package de.xyzerstudios.moneymanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.ExpensesActivity;
import de.xyzerstudios.moneymanager.activities.IncomeActivity;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseActivity;
import de.xyzerstudios.moneymanager.activities.add.AddIncomeActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPieChartsAsyncTask;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.charting.CategoryAdapter;
import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;


public class DashboardFragment extends Fragment {

    public PieChart pieChartIncome, pieChartExpenses;
    public RecyclerView incomeRecyclerView, expensesRecyclerView;
    public RecyclerView.Adapter incomeRecyclerViewAdapter, expensesRecyclerViewAdapter;

    public TextView portfolioNameDisplay, dashboardSaldo, dashboardIncome, dashboardExpenses;
    public TextView centerTextIncomeChart, centerTextExpensesChart;

    public LinearLayout buttonShowAllExpenses, buttonShowAllIncome, linearLayoutExpenses, linearLayoutIncome;

    public LinearLayout buttonCurrentMonth, buttonAddExpense, buttonAddRevenue;
    public TextView textViewCurrentMonth;

    public int currentMonth = 1;
    public int currentYear = 2022;

    public DashboardFragment() {

    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pieChartIncome = view.findViewById(R.id.pieChartIncome);
        pieChartExpenses = view.findViewById(R.id.pieChartExpenses);

        incomeRecyclerView = view.findViewById(R.id.recyclerViewIncomeCategories);
        expensesRecyclerView = view.findViewById(R.id.recyclerViewExpensesCategories);

        portfolioNameDisplay = view.findViewById(R.id.portfolioNameDisplay);
        dashboardSaldo = view.findViewById(R.id.dashboardSaldo);
        dashboardIncome = view.findViewById(R.id.dashboardIncome);
        dashboardExpenses = view.findViewById(R.id.dashboardExpenses);

        centerTextIncomeChart = view.findViewById(R.id.centerTextViewIncomeChart);
        centerTextExpensesChart = view.findViewById(R.id.centerTextViewExpensesChart);

        buttonShowAllExpenses = view.findViewById(R.id.buttonShowAllExpenses);
        buttonShowAllIncome = view.findViewById(R.id.buttonShowAllIncome);
        linearLayoutExpenses = view.findViewById(R.id.linearLayoutExpenses);
        linearLayoutIncome = view.findViewById(R.id.linearLayoutIncome);

        buttonCurrentMonth = view.findViewById(R.id.buttonCurrentMonth);
        textViewCurrentMonth = view.findViewById(R.id.textViewCurrentMonth);
        buttonAddExpense = view.findViewById(R.id.buttonAddExpenseDashboard);
        buttonAddRevenue = view.findViewById(R.id.buttonAddRevenueDashboard);

        Button testButton = view.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonAddRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddIncomeActivity.class);
                startActivity(intent);
            }
        });

        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        buttonShowAllExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        buttonShowAllIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IncomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        linearLayoutExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        linearLayoutIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IncomeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        currentMonth = date.getMonth() + 1;
        currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

        textViewCurrentMonth.setText(getMonth(currentMonth));
        buttonCurrentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        int formattedSelectedMonth = selectedMonth + 1;
                        currentMonth = formattedSelectedMonth;
                        currentYear = selectedYear;
                        String suffixString = "";
                        if (Integer.valueOf(Utils.yearDateFormat.format(date)) != currentYear) {
                            suffixString = ", " + currentYear;
                        }
                        textViewCurrentMonth.setText(getMonth(formattedSelectedMonth) + suffixString);
                        loadPieChartsAndPortfolio();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(currentMonth - 1)
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(currentYear)
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });

        setupPieCharts();
        loadIncomePieChartData();
        loadExpensesPieChartData();
        inflateCategoriesIncome();
        inflateCategoriesExpenses();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPieChartsAndPortfolio();
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

    private void loadPieChartsAndPortfolio() {
        new LoadPieChartsAsyncTask(getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs(),
                currentMonth, currentYear);
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void setupPieCharts() {
        pieChartIncome.setDrawHoleEnabled(true);
        pieChartIncome.setHoleColor(getResources().getColor(R.color.ui_light_background, null));
        pieChartIncome.setHoleRadius(90);
        pieChartIncome.setDrawEntryLabels(false);
        pieChartIncome.setDrawSliceText(false);
        pieChartIncome.getDescription().setEnabled(false);
        pieChartIncome.setTouchEnabled(false);
        pieChartIncome.getLegend().setEnabled(false);
        pieChartIncome.setRotationAngle(90);

        pieChartExpenses.setDrawHoleEnabled(true);
        pieChartExpenses.setHoleColor(getResources().getColor(R.color.ui_light_background, null));
        pieChartExpenses.setHoleRadius(90);
        pieChartExpenses.setDrawEntryLabels(false);
        pieChartExpenses.setDrawSliceText(false);
        pieChartExpenses.getDescription().setEnabled(false);
        pieChartExpenses.setTouchEnabled(false);
        pieChartExpenses.getLegend().setEnabled(false);
        pieChartExpenses.setRotationAngle(90);
    }

    private void loadExpensesPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1f, getString(R.string.category)));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.ui_lime_green, null));

        PieDataSet dataSet = new PieDataSet(entries, "Kategorien");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChartIncome.setData(data);
        pieChartIncome.invalidate();
    }

    private void loadIncomePieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1f, getString(R.string.category)));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.ui_lime_green, null));

        PieDataSet dataSet = new PieDataSet(entries, "Kategorien");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChartExpenses.setData(data);
        pieChartExpenses.invalidate();
    }

    public void loadExpensesPieChartData(ArrayList<CategoryItem> categories) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (CategoryItem categoryItem : categories) {
            entries.add(new PieEntry((float) categoryItem.getCategoryPercentage() / 100f, ""));
            colors.add(categoryItem.getIndicatorColor());
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChartExpenses.setData(data);
        pieChartExpenses.invalidate();
    }

    public void loadIncomePieChartData(ArrayList<CategoryItem> categories) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (CategoryItem categoryItem : categories) {
            entries.add(new PieEntry((float) categoryItem.getCategoryPercentage() / 100f, ""));
            colors.add(categoryItem.getIndicatorColor());
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChartIncome.setData(data);
        pieChartIncome.invalidate();
    }

    private void inflateCategoriesIncome() {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem(getResources().getColor(R.color.ui_lime_green, null), getString(R.string.category), 100));

        incomeRecyclerView.setHasFixedSize(false);
        incomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        incomeRecyclerViewAdapter = new CategoryAdapter(getActivity(), categoryItems);

        incomeRecyclerView.setAdapter(incomeRecyclerViewAdapter);
    }

    private void inflateCategoriesExpenses() {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem(getResources().getColor(R.color.ui_lime_green, null), getString(R.string.category), 100));

        expensesRecyclerView.setHasFixedSize(false);
        expensesRecyclerViewAdapter = new CategoryAdapter(getContext(), categoryItems);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        expensesRecyclerView.setAdapter(expensesRecyclerViewAdapter);
    }

    public void inflateCategoriesIncome(ArrayList<CategoryItem> categories) {
        incomeRecyclerView.setHasFixedSize(false);
        incomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        incomeRecyclerViewAdapter = new CategoryAdapter(getActivity(), categories);

        incomeRecyclerView.setAdapter(incomeRecyclerViewAdapter);
    }


    public void inflateCategoriesExpenses(ArrayList<CategoryItem> categories) {
        expensesRecyclerView.setHasFixedSize(false);
        expensesRecyclerViewAdapter = new CategoryAdapter(getActivity(), categories);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        expensesRecyclerView.setAdapter(expensesRecyclerViewAdapter);
    }
}