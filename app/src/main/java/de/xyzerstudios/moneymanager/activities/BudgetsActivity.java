package de.xyzerstudios.moneymanager.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddBudgetActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.BudgetsAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.BudgetItem;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.InformationDialog;

public class BudgetsActivity extends AppCompatActivity {

    private ImageButton buttonBudgetsGoBack;
    private LinearLayout buttonBudgetsFilter, buttonAddNewBudget, buttonInfoBudgets, containerBudgetsSumAndLeft;
    private BudgetsAdapter budgetsAdapter;
    private RecyclerView recyclerViewBudgets;
    private ArrayList<BudgetItem> budgetItems;
    private TextView availableAmountBudget, sumOfBudget, textViewBudgetExceededLeft, exceededOrLeftBudget;
    private Space budgetsSpacerBottom1, budgetsSpacerBottom2;

    private SwipeRefreshLayout budgetsSwipeRefresh;

    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);

        Date date = new Date();
        month = Integer.valueOf(Utils.monthDateFormat.format(date));
        year = Integer.valueOf(Utils.yearDateFormat.format(date));

        budgetItems = new ArrayList<>();

        buttonBudgetsGoBack = findViewById(R.id.buttonBudgetsGoBack);
        buttonBudgetsFilter = findViewById(R.id.buttonBudgetsFilter);
        recyclerViewBudgets = findViewById(R.id.recyclerViewBudgets);
        budgetsSwipeRefresh = findViewById(R.id.swipeRefreshBudgets);
        buttonAddNewBudget = findViewById(R.id.buttonAddNewBudget);
        availableAmountBudget = findViewById(R.id.availableAmountBudget);
        buttonInfoBudgets = findViewById(R.id.buttonInfoBudgets);
        containerBudgetsSumAndLeft = findViewById(R.id.containerBudgetsSumAndLeft);

        sumOfBudget = findViewById(R.id.sumOfBudget);
        textViewBudgetExceededLeft = findViewById(R.id.textViewBudgetExceededLeft);
        exceededOrLeftBudget = findViewById(R.id.exceededOrLeftBudget);

        budgetsSpacerBottom1 = findViewById(R.id.budgetsSpacerBottom1);
        budgetsSpacerBottom2 = findViewById(R.id.budgetsSpacerBottom2);

        recyclerViewBudgets.setHasFixedSize(true);
        recyclerViewBudgets.setLayoutManager(new LinearLayoutManager(this));
        budgetsAdapter = new BudgetsAdapter(this, this, budgetItems);

        recyclerViewBudgets.setAdapter(budgetsAdapter);

        buttonBudgetsGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonBudgetsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        buttonAddNewBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetsActivity.this, AddBudgetActivity.class);
                startActivity(intent);
            }
        });

        budgetsSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBudgetItem(month, year);
            }
        });

        buttonInfoBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformationDialog informationDialog = new InformationDialog(getString(R.string.menu_title_budgets), getString(R.string.budgets_explanation));
                informationDialog.show(getSupportFragmentManager(), "Information Budgets");
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadBudgetItem(month, year);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_filter_budgets);

        LinearLayout chooserMonthAndYear = dialog.findViewById(R.id.chooserMonthAndYearBudget);
        TextView textViewMonthAndYear = dialog.findViewById(R.id.textViewMonthAndYearBudget);


        textViewMonthAndYear.setText(getMonth(month) + ", " + year);

        chooserMonthAndYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(BudgetsActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        int formattedSelectedMonth = selectedMonth + 1;
                        month = formattedSelectedMonth;
                        year = selectedYear;
                        textViewMonthAndYear.setText(getMonth(formattedSelectedMonth) + ", " + selectedYear);

                        loadBudgetItem(month, year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(month - 1)
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(year)
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    private String getMonth(int selectedMonth) {
        String month = "";
        switch (selectedMonth) {
            case 1:
                month = getString(R.string.january);
                break;
            case 2:
                month = getString(R.string.february);
                break;
            case 3:
                month = getString(R.string.march);
                break;
            case 4:
                month = getString(R.string.april);
                break;
            case 5:
                month = getString(R.string.may);
                break;
            case 6:
                month = getString(R.string.june);
                break;
            case 7:
                month = getString(R.string.july);
                break;
            case 8:
                month = getString(R.string.august);
                break;
            case 9:
                month = getString(R.string.september);
                break;
            case 10:
                month = getString(R.string.october);
                break;
            case 11:
                month = getString(R.string.november);
                break;
            case 12:
                month = getString(R.string.december);
                break;
        }
        return month;
    }

    private void loadBudgetItem(int month, int year) {
        new budgetsActivity(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs(), month, year);
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private class budgetsActivity extends AsyncTask<Integer, String, ArrayList<BudgetItem>> {
        private final WeakReference<BudgetsActivity> activityWeakReference;
        private final Utils utils;

        budgetsActivity(BudgetsActivity activity) {
            activityWeakReference = new WeakReference<BudgetsActivity>(activity);
            utils = new Utils(BudgetsActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BudgetsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected ArrayList<BudgetItem> doInBackground(Integer... integers) {
            int portfolioId = integers[0];
            int month = integers[1];
            int year = integers[2];
            ArrayList<BudgetItem> budgetItems = new ArrayList<>();


            BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(BudgetsActivity.this);
            Cursor cursor = budgetsDatabaseHelper.readAllData();

            while (cursor.moveToNext()) {
                int budgetEntryId = cursor.getInt(0);
                int categoryId = cursor.getInt(1);
                int amountLimit = cursor.getInt(2);
                ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(BudgetsActivity.this);
                Cursor cursorAmount = expensesDatabaseHelper.sumEntriesOfCategory(portfolioId, categoryId, month, year);
                int amountSpent = 0;
                while (cursorAmount.moveToNext()) amountSpent = cursorAmount.getInt(0);

                CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(BudgetsActivity.this, BudgetsActivity.this);
                Cursor cursorCategory = categoriesDatabaseHelper.readCategoryById(categoryId);
                String categoryName = "";
                while (cursorCategory.moveToNext()) categoryName = cursorCategory.getString(1);

                budgetItems.add(new BudgetItem(budgetEntryId, categoryId, categoryName, amountLimit, amountSpent));
            }

            Cursor sumCursor = budgetsDatabaseHelper.sumAllEntries();
            int sumOfAllBudets = 0;
            while (sumCursor.moveToNext()) sumOfAllBudets = sumCursor.getInt(0);

            RepeatedIncomeDatabaseHelper repeatedIncomeDatabaseHelper = new RepeatedIncomeDatabaseHelper(BudgetsActivity.this);
            Cursor cursorRepeated = repeatedIncomeDatabaseHelper.sumEntriesByIntervalFromPortfolioId(portfolioId);

            if (cursorRepeated.getCount() == 0) {
                publishProgress(utils.formatCurrency(0));
                return budgetItems;
            }

            int availableAmount = 0;

            while (cursorRepeated.moveToNext()) {
                int amountInRow = cursorRepeated.getInt(0);
                String interval = cursorRepeated.getString(1);
                int quantity = Integer.valueOf(interval.split("_")[0]);
                String unit = interval.split("_")[1];
                double multiplier = 1;
                switch (unit) {
                    case "d":
                        multiplier = 30.44;
                        break;
                    case "w":
                        multiplier = 4.28;
                        break;
                    case "m":
                    default:
                        multiplier = 1;
                        break;
                    case "y":
                        multiplier = 0.08333;
                        break;
                }
                availableAmount += Math.round((double) amountInRow / (double) quantity * multiplier);
            }

            if (cursor.getCount() == 0 || cursorRepeated.getCount() == 0) {
                publishProgress(utils.formatCurrency(availableAmount));
                return budgetItems;
            }

            int amountLeftOrExceeded = availableAmount - sumOfAllBudets;

            String budgetLeftExceeded = "";
            if (amountLeftOrExceeded < 0) {
                budgetLeftExceeded = getString(R.string.amount_exceeded);
                amountLeftOrExceeded = amountLeftOrExceeded * (-1);
            } else {
                budgetLeftExceeded = getString(R.string.amount_left);
            }

            publishProgress(utils.formatCurrency(availableAmount), utils.formatCurrency(sumOfAllBudets),
                    budgetLeftExceeded, utils.formatCurrency(amountLeftOrExceeded));

            return budgetItems;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            BudgetsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.availableAmountBudget.setText(values[0]);
            if (values.length == 1) {
                activity.containerBudgetsSumAndLeft.setVisibility(View.GONE);
                activity.budgetsSpacerBottom1.setVisibility(View.GONE);
                activity.budgetsSpacerBottom2.setVisibility(View.GONE);
                return;
            }
            activity.containerBudgetsSumAndLeft.setVisibility(View.VISIBLE);
            activity.budgetsSpacerBottom1.setVisibility(View.VISIBLE);
            activity.budgetsSpacerBottom2.setVisibility(View.VISIBLE);
            activity.sumOfBudget.setText(values[1]);
            activity.textViewBudgetExceededLeft.setText(values[2]);
            activity.exceededOrLeftBudget.setText(values[3]);
            if (values[2].matches(getString(R.string.amount_exceeded))) {
                activity.exceededOrLeftBudget.setTextColor(getColor(R.color.ui_money_text_red));
            } else {
                activity.exceededOrLeftBudget.setTextColor(getColor(R.color.ui_money_text_green));
            }
        }

        @Override
        protected void onPostExecute(ArrayList<BudgetItem> a) {
            super.onPostExecute(a);
            BudgetsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.budgetItems = a;
            activity.budgetsAdapter = new BudgetsAdapter(BudgetsActivity.this, BudgetsActivity.this, a);
            activity.recyclerViewBudgets.swapAdapter(activity.budgetsAdapter, false);
            activity.budgetsSwipeRefresh.setRefreshing(false);
        }
    }
}