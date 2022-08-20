package de.xyzerstudios.moneymanager.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.HomeActivity;
import de.xyzerstudios.moneymanager.fragments.DashboardFragment;
import de.xyzerstudios.moneymanager.utils.RepeatedItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;


public class LoadPieChartsAsyncTask extends AsyncTask<Integer, ArrayList<CategoryItem>, Integer> {

    private final WeakReference<Activity> activityWeakReference;
    private final Activity activity;
    private final HomeActivity homeActivity;
    private final DashboardFragment dashboardFragment;

    public LoadPieChartsAsyncTask(Activity activity, HomeActivity homeActivity, DashboardFragment dashboardFragment) {
        activityWeakReference = new WeakReference<Activity>(activity);
        this.activity = activity;
        this.homeActivity = homeActivity;
        this.dashboardFragment = dashboardFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

    }

    @Override
    protected Integer doInBackground(Integer... integers) {

        int portfolioId = integers[0];
        int month = integers[1];
        int year = integers[2];

        ArrayList<CategoryItem> categoryItemsExpenses = new ArrayList<>();
        ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(activity);
        //publishProgress[0] for expenses

        ArrayList<CategoryItem> categoryItemsIncome = new ArrayList<>();
        IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(activity);
        //publishProgress[1] for income

        Cursor cursorSumAllExpenses = expensesDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);
        Cursor cursorSumCategoriesExpenses = expensesDatabaseHelper.sumEntriesGroupedByCategory(portfolioId, month, year);

        Cursor cursorSumAllIncome = incomeDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);
        Cursor cursorSumCategoriesIncome = incomeDatabaseHelper.sumEntriesGroupedByCategory(portfolioId, month, year);

        int sumOfAllExpenses = 0;
        int sumOfAllIncome = 0;


        boolean expensesEntriesExist = false;
        boolean incomeEntriesExist = false;
        boolean isRepeatedIncomeInThisMonth = false;
        boolean isBalanceExpensesDisplayed = false;
        boolean isBalanceIncomeDisplayed = false;


        int sumRevenuesBalances = 0;
        int sumExpensesBalances = 0;

        int saldoBalances = 0;

        BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(activity);
        Cursor balanceCursor = balanceDatabaseHelper.sumAllByPortfolioId(portfolioId);

        while (balanceCursor.moveToNext()) {
            sumRevenuesBalances = balanceCursor.getInt(0);
            sumExpensesBalances = balanceCursor.getInt(1);
            saldoBalances = balanceCursor.getInt(2);
        }

        if (sumExpensesBalances != 0) {
            isBalanceExpensesDisplayed = true;
        }

        if (sumRevenuesBalances != 0) {
            isBalanceIncomeDisplayed = true;
        }

        HashMap<Integer, Integer> categoryIdAndSumOfRepeatedIncomes = new HashMap<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ArrayList<RepeatedItem> repeatedItems = new ArrayList<>();
            RepeatedIncomeDatabaseHelper repeatedIncomeDatabaseHelper = new RepeatedIncomeDatabaseHelper(activity);
            Cursor repeatedCursor = repeatedIncomeDatabaseHelper.readAllDataForPortfolio(portfolioId);

            while (repeatedCursor.moveToNext()) {
                int categoryId = repeatedCursor.getInt(6);
                int amount = repeatedCursor.getInt(3);
                String interval = repeatedCursor.getString(4);
                String timestampOfEntry = repeatedCursor.getString(5);

                int intervalNumber = Integer.valueOf(interval.split("_")[0]);
                String intervalUnit = interval.split("_")[1];

                int amountToAdd = amountInMonth(timestampOfEntry, month, year, intervalUnit, intervalNumber, amount);
                if (amountToAdd <= 0) {
                    continue;
                }
                repeatedItems.add(new RepeatedItem(categoryId, amountToAdd));
            }
            isRepeatedIncomeInThisMonth = repeatedItems.size() > 0;

            if (isRepeatedIncomeInThisMonth) {
                categoryIdAndSumOfRepeatedIncomes = categoryIdAndSumOfAmounts(repeatedItems);
            }

        }




        if (cursorSumAllExpenses.getCount() > 0 || cursorSumCategoriesExpenses.getCount() > 0) {
            expensesEntriesExist = true;
        }

        if (cursorSumAllIncome.getCount() > 0 || cursorSumCategoriesIncome.getCount() > 0) {
            incomeEntriesExist = true;
        }


        boolean budgetExceededChanged = false;

        int sumExpensesDatabase = 0;

        int sumIncomeDatabase = 0;
        int sumIncomeRepeated = 0;

        if (expensesEntriesExist) {
            while (cursorSumAllExpenses.moveToNext()) {
                sumExpensesDatabase = cursorSumAllExpenses.getInt(0);
            }
        }

        if (incomeEntriesExist) {
            while (cursorSumAllIncome.moveToNext()) {
                sumIncomeDatabase = cursorSumAllIncome.getInt(0);
            }
        }

        sumOfAllExpenses = sumExpensesDatabase + sumExpensesBalances;

        boolean isBudgetExceedSomewhere = false;
        if (expensesEntriesExist) {
            while (cursorSumCategoriesExpenses.moveToNext()) {
                int categoryId = cursorSumCategoriesExpenses.getInt(1);
                CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);

                Cursor cursor = categoriesDatabaseHelper.readCategoryById(categoryId);
                int indicatorColor = 0;
                String categoryText = "";
                while (cursor.moveToNext()) {
                    indicatorColor = cursor.getInt(3);
                    categoryText = cursor.getString(1);
                }

                BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(activity);
                int amountLimit = 0;
                boolean isBudgetExceeded = false;
                Cursor cursorBudgets = budgetsDatabaseHelper.readEntriesByCategoryId(categoryId);
                while (cursorBudgets.moveToNext()) {
                    amountLimit = cursorBudgets.getInt(2);
                }
                if (sumOfAllExpenses > amountLimit && amountLimit != 0) {
                    isBudgetExceeded = true;
                    isBudgetExceedSomewhere = true;
                }

                categoryItemsExpenses.add(new CategoryItem(indicatorColor, categoryText,
                        (int) ((float) cursorSumCategoriesExpenses.getInt(0) / (float) sumOfAllExpenses * 100f), isBudgetExceeded));
            }
        }

        if (isBalanceExpensesDisplayed) {
            categoryItemsExpenses.add(new CategoryItem(activity.getColor(R.color.static_neutral_grey),
                    activity.getString(R.string.menu_title_bilanzen), (int) (((float) sumExpensesBalances / (float) sumOfAllExpenses) * 100f), false));
        }

        budgetExceededChanged = isBudgetExceedSomewhere != isBudgetExceeded();
        setBudgetExceeded(isBudgetExceedSomewhere);


        if (incomeEntriesExist) {
            while (cursorSumAllIncome.moveToNext()) {
                sumIncomeDatabase = cursorSumAllIncome.getInt(0);
            }
        }

        if (isRepeatedIncomeInThisMonth) {;
            int tempAmount = 0;
            for (int amountInCategory : categoryIdAndSumOfRepeatedIncomes.values()) {
                tempAmount += amountInCategory;
            }
            sumIncomeRepeated = tempAmount;
        }

        sumOfAllIncome = sumRevenuesBalances + sumIncomeDatabase + sumIncomeRepeated;

        if (incomeEntriesExist) {
            while (cursorSumCategoriesIncome.moveToNext()) {
                int categoryId = cursorSumCategoriesIncome.getInt(1);
                int sumOfCategory = cursorSumCategoriesIncome.getInt(0);

                CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                Cursor cursor = categoriesDatabaseHelper.readCategoryById(categoryId);

                int indicatorColor = 0;
                String categoryText = "";
                while (cursor.moveToNext()) {
                    indicatorColor = cursor.getInt(3);
                    categoryText = cursor.getString(1);
                }

                int repeatedIncomeForCategory = 0;
                if (isRepeatedIncomeInThisMonth) {
                    if (categoryIdAndSumOfRepeatedIncomes.containsKey(categoryId)) {
                        int amountOfCategory = categoryIdAndSumOfRepeatedIncomes.get(categoryId);
                        repeatedIncomeForCategory = amountOfCategory;
                        categoryIdAndSumOfRepeatedIncomes.remove(categoryId);
                    }
                }

                categoryItemsIncome.add(new CategoryItem(indicatorColor, categoryText,
                        (int) (((float) sumOfCategory + (float) repeatedIncomeForCategory) / (float) sumOfAllIncome * 100f)));
            }
        }



        if (isRepeatedIncomeInThisMonth && categoryIdAndSumOfRepeatedIncomes.size() > 0) {
            for (Map.Entry<Integer, Integer> entry : categoryIdAndSumOfRepeatedIncomes.entrySet()) {
                int categoryId = entry.getKey();
                int amountOfCat = entry.getValue();
                CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                Cursor cursor = categoriesDatabaseHelper.readCategoryById(categoryId);
                int indicatorColor = 0;
                String categoryText = "";
                while (cursor.moveToNext()) {
                    indicatorColor = cursor.getInt(3);
                    categoryText = cursor.getString(1);
                }
                categoryItemsIncome.add(new CategoryItem(indicatorColor, categoryText,
                        (int) ((float) amountOfCat / (float) sumOfAllIncome * 100f), false));
            }
        }

        if (isBalanceIncomeDisplayed) {
            categoryItemsIncome.add(new CategoryItem(activity.getColor(R.color.static_neutral_grey),
                    activity.getString(R.string.menu_title_bilanzen), (int) (((float) sumRevenuesBalances / (float) sumOfAllIncome) * 100f)));
        }

        if (sumOfAllIncome == 0) {
            categoryItemsIncome.add(new CategoryItem(activity.getColor(R.color.ui_lime_green),
                    activity.getString(R.string.category), 100));
        }

        if (sumOfAllExpenses == 0) {
            categoryItemsExpenses.add(new CategoryItem(activity.getColor(R.color.ui_lime_green),
                    activity.getString(R.string.category), 100));
        }

        int saldoOffAll = sumOfAllIncome - sumOfAllExpenses;

        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);
        portfolioDatabaseHelper.updatePortfolio(portfolioId, sumOfAllIncome,
                sumOfAllExpenses, saldoOffAll);

        if (budgetExceededChanged) {
            publishProgress(categoryItemsExpenses, categoryItemsIncome, new ArrayList<>());
        } else {
            publishProgress(categoryItemsExpenses, categoryItemsIncome);
        }

        return portfolioId;
    }

    @Override
    protected void onProgressUpdate(ArrayList<CategoryItem>... values) {
        super.onProgressUpdate(values);
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

        dashboardFragment.loadExpensesPieChartData(values[0]);
        dashboardFragment.loadIncomePieChartData(values[1]);

        dashboardFragment.inflateCategoriesExpenses(values[0]);
        dashboardFragment.inflateCategoriesIncome(values[1]);

        if (values.length > 2) {
            homeActivity.loadSlidingRootNav();
        }
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

        new LoadPortfolioAsyncTask(activity, dashboardFragment).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
    }

    private HashMap<Integer, Integer> categoryIdAndSumOfAmounts(ArrayList<RepeatedItem> repeatedItems) {
        HashMap<Integer, Integer> categoryIdAndSumOfAmounts = new HashMap<>();
        for (RepeatedItem item : repeatedItems) {
            int categoryId = item.getCategoryId();
            int currentValue = categoryIdAndSumOfAmounts.getOrDefault(categoryId, 0);
            currentValue = currentValue + item.getAmountToAdd();
            categoryIdAndSumOfAmounts.put(categoryId, currentValue);
        }
        return categoryIdAndSumOfAmounts;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int amountInMonth(String timestampOfEntry, int month, int year, String intervalUnit, int intervalNumber, int amount) {
        int amountInMonth = 0; // returns -1 when selected month is before timestamp.

        String[] timestampSplit = timestampOfEntry.split("_");
        int yearTimestamp = Integer.valueOf(timestampSplit[0]);
        int monthTimestamp = Integer.valueOf(timestampSplit[1]);
        int dayTimestamp = Integer.valueOf(timestampSplit[2]);

        String firstDayOfMonth = year + "_" + ((month < 10) ? ("0" + month) : month) + "_01";
        String lastDayOfMonth = year + "_" + ((month < 10) ? ("0" + month) : month) + "_" + getDaysInMonth(month, year);

        switch (intervalUnit) {
            case "d":
                if (monthTimestamp == month && yearTimestamp == year) {
                    int multiplierAmountInMonth = -1;
                    int daysInCurrentMonth = getDaysInMonth(month, year);
                    for (int i = dayTimestamp; i <= daysInCurrentMonth; i += intervalNumber) {
                        ++multiplierAmountInMonth;
                    }
                    amountInMonth = amount * multiplierAmountInMonth;
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");

                    LocalDate dateTimestamp = LocalDate.parse(timestampOfEntry, dtf);
                    LocalDate dateStartCurrentMonth = LocalDate.parse(firstDayOfMonth, dtf);
                    LocalDate dateEndCurrentMonth = LocalDate.parse(lastDayOfMonth, dtf);

                    int rangeStart = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateStartCurrentMonth));
                    int rangeEnd = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateEndCurrentMonth));

                    if (rangeStart < 0 || rangeEnd < 0) {
                        amountInMonth = -1;
                        break;
                    }
                    int multiplier = 0;
                    for (int i = rangeStart; i <= rangeEnd; ++i) {
                        if (i % intervalNumber == 0) multiplier++;
                    }
                    amountInMonth = amount * multiplier;
                }
                break;
            case "w":
                intervalNumber *= 7;
                if (monthTimestamp == month && yearTimestamp == year) {
                    int multiplierAmountInMonth = -1;
                    int daysInCurrentMonth = getDaysInMonth(month, year);
                    for (int i = dayTimestamp; i <= daysInCurrentMonth; i += intervalNumber) {
                        ++multiplierAmountInMonth;
                    }
                    amountInMonth = amount * multiplierAmountInMonth;
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");

                    String endOfThisMonth = year + "_" + ((month < 10) ? ("0" + month) : month) + "_" + getDaysInMonth(month, year);

                    LocalDate dateTimestamp = LocalDate.parse(timestampOfEntry, dtf);
                    LocalDate dateStartCurrentMonth = LocalDate.parse(firstDayOfMonth, dtf);
                    LocalDate dateEndCurrentMonth = LocalDate.parse(endOfThisMonth, dtf);

                    int rangeStart = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateStartCurrentMonth));
                    int rangeEnd = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateEndCurrentMonth));

                    if (rangeStart < 0 || rangeEnd < 0) {
                        amountInMonth = -1;
                        break;
                    }

                    int multiplier = 0;
                    for (int i = rangeStart; i <= rangeEnd; ++i) {
                        if (i % intervalNumber == 0) multiplier++;
                    }
                    amountInMonth = amount * multiplier;
                }
                break;
            case "m":
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
                String timestampModified = timestampOfEntry.split("_")[0] + "_" + timestampOfEntry.split("_")[1] + "_01";

                LocalDate dateTimestampMonth = LocalDate.parse(timestampModified, dtf);
                LocalDate dateSelectedMonth = LocalDate.parse(firstDayOfMonth, dtf);

                int monthsBetween = Math.toIntExact(ChronoUnit.MONTHS.between(dateTimestampMonth, dateSelectedMonth));
                if (monthsBetween == 0) {
                    amountInMonth = 0;
                    break;
                }
                if (monthsBetween < 0) {
                    amountInMonth = -1;
                    break;
                }
                if (monthsBetween % intervalNumber == 0) {
                    amountInMonth = amount;
                }
                break;
            case "y":
                try {
                    SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                    Date dateTimestamp = simpleDateFormat.parse(timestampOfEntry);
                    int timestampYear = Integer.valueOf(Utils.yearDateFormat.format(dateTimestamp));
                    int timestampMonth = Integer.valueOf(Utils.monthDateFormat.format(dateTimestamp));
                    if (timestampMonth == month && year > timestampYear) {
                        amountInMonth = amount;
                    } else if (year < timestampYear || (year == timestampYear && month < timestampMonth)) {
                        amountInMonth = -1;
                    } else {
                        amountInMonth = 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        return amountInMonth;
    }

    private boolean isBudgetExceeded() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Utils.SHARED_PREFS_IS_BUDGET_EXCEEDED, false);
    }

    private void setBudgetExceeded(boolean isExceeded) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utils.SHARED_PREFS_IS_BUDGET_EXCEEDED, isExceeded);
        editor.apply();
    }

    private int getDaysInMonth(int month, int year) {
        int length;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                length = 31;
                break;
            default:
            case 4:
            case 6:
            case 9:
            case 11:
                length = 30;
                break;
            case 2:
                length = checkLeapYear(year) ? 29 : 28;
                break;
        }
        return length;
    }

    private boolean checkLeapYear(int year) {
        if (year % 400 == 0) {
            return true;
        }
        if (year % 100 == 0) {
            return false;
        }
        return year % 4 == 0;
    }

}
