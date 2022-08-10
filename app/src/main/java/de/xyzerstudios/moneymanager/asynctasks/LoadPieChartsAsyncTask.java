package de.xyzerstudios.moneymanager.asynctasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.DashboardFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class LoadPieChartsAsyncTask extends AsyncTask<Integer, ArrayList<CategoryItem>, Integer> {

    private WeakReference<Activity> activityWeakReference;
    private Activity activity;
    private DashboardFragment dashboardFragment;

    public LoadPieChartsAsyncTask(Activity activity, DashboardFragment dashboardFragment) {
        activityWeakReference = new WeakReference<Activity>(activity);
        this.activity = activity;
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

        Cursor cursorSumTotalExpenses = expensesDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);
        Cursor cursorSumCategoriesExpenses = expensesDatabaseHelper.sumEntriesGroupedByCategory(portfolioId, month, year);

        Cursor cursorSumTotalIncome = incomeDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);
        Cursor cursorSumCategoriesIncome = incomeDatabaseHelper.sumEntriesGroupedByCategory(portfolioId, month, year);

        int sumTotalExpenses = 0;
        int sumTotalIncome = 0;

        boolean executeExpenses = true;
        boolean executeIncome = true;


        if (cursorSumTotalExpenses.getCount() == 0 || cursorSumCategoriesExpenses.getCount() == 0) {
            executeExpenses = false;
            categoryItemsExpenses.add(new CategoryItem(activity.getColor(R.color.ui_lime_green),
                    activity.getString(R.string.category), 100, false));
        }

        if (cursorSumTotalIncome.getCount() == 0 ||cursorSumCategoriesIncome.getCount() == 0) {
            executeIncome = false;
            categoryItemsIncome.add(new CategoryItem(activity.getColor(R.color.ui_lime_green),
                    activity.getString(R.string.category), 100, false));
        }

        if (executeExpenses) {
            while (cursorSumTotalExpenses.moveToNext()) {
                sumTotalExpenses = cursorSumTotalExpenses.getInt(0);
            }


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
                if (sumTotalExpenses > amountLimit && amountLimit != 0)
                    isBudgetExceeded = true;

                categoryItemsExpenses.add(new CategoryItem(indicatorColor, categoryText,
                        (int) ((float) cursorSumCategoriesExpenses.getInt(0) / (float) sumTotalExpenses * 100f), isBudgetExceeded));
            }
        }

        if (executeIncome) {
            while (cursorSumTotalIncome.moveToNext()) {
                sumTotalIncome = cursorSumTotalIncome.getInt(0);
            }

            while (cursorSumCategoriesIncome.moveToNext()) {
                int categoryId = cursorSumCategoriesIncome.getInt(1);
                CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                Cursor cursor = categoriesDatabaseHelper.readCategoryById(categoryId);
                int indicatorColor = 0;
                String categoryText = "";
                while (cursor.moveToNext()) {
                    indicatorColor = cursor.getInt(3);
                    categoryText = cursor.getString(1);
                }

                categoryItemsIncome.add(new CategoryItem(indicatorColor, categoryText,
                        (int) ((float) cursorSumCategoriesIncome.getInt(0) / (float) sumTotalIncome * 100f), false));
            }
        }

        int saldoTotal = sumTotalIncome - sumTotalExpenses;

        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);
        portfolioDatabaseHelper.updatePortfolio(portfolioId, sumTotalIncome, sumTotalExpenses, saldoTotal);

        publishProgress(categoryItemsExpenses, categoryItemsIncome);

        return portfolioId;
    }

    @Override
    protected void onProgressUpdate(ArrayList<CategoryItem>... values) {
        super.onProgressUpdate(values);
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

        for (CategoryItem categoryItem : values[0])
            Log.d("categoryItem__Expenses", categoryItem.getIndicatorColor() + ";" +
                    categoryItem.getCategoryText() + ";" + categoryItem.getCategoryPercentage());

        for (CategoryItem categoryItem : values[1])
            Log.d("categoryItem__Income", categoryItem.getIndicatorColor() + ";" +
                    categoryItem.getCategoryText() + ";" + categoryItem.getCategoryPercentage());

        Log.d("categoryItem__Color", "" + activity.getColor(R.color.ui_lime_green));


        dashboardFragment.loadExpensesPieChartData(values[0]);
        dashboardFragment.loadIncomePieChartData(values[1]);

        dashboardFragment.inflateCategoriesExpenses(values[0]);
        dashboardFragment.inflateCategoriesIncome(values[1]);
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

}
