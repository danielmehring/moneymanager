package de.xyzerstudios.moneymanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.DatabaseTestActivity;
import de.xyzerstudios.moneymanager.activities.ExpensesActivity;
import de.xyzerstudios.moneymanager.activities.IncomeActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPieChartsAsyncTask;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfolioAsyncTask;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.charting.CategoryAdapter;
import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;


public class DashboardFragment extends Fragment {

    public PieChart pieChartIncome, pieChartExpenses;
    public RecyclerView incomeRecyclerView, expensesRecyclerView;
    public RecyclerView.Adapter incomeRecyclerViewAdapter, expensesRecyclerViewAdapter;

    public TextView portfolioNameDisplay, dashboardSaldo, dashboardIncome, dashboardExpenses;
    public TextView centerTextIncomeChart, centerTextExpensesChart;

    public LinearLayout buttonShowAllExpenses, buttonShowAllIncome;

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

        Button testButton = view.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DatabaseTestActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        loadPieCharts();
    }

    private void loadPieCharts() {
        Date date = new Date();
        new LoadPieChartsAsyncTask(getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs(),
                Integer.parseInt(Utils.monthDateFormat.format(date)), Integer.parseInt(Utils.yearDateFormat.format(date)));
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
        entries.add(new PieEntry(1f, getResources().getString(R.string.category)));

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
        entries.add(new PieEntry(1f, getResources().getString(R.string.category)));

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
        categoryItems.add(new CategoryItem(getResources().getColor(R.color.ui_lime_green, null), getResources().getString(R.string.category), 100));

        incomeRecyclerView.setHasFixedSize(false);
        incomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        incomeRecyclerViewAdapter = new CategoryAdapter(getActivity(), categoryItems);

        incomeRecyclerView.setAdapter(incomeRecyclerViewAdapter);
    }

    private void inflateCategoriesExpenses() {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem(getResources().getColor(R.color.ui_lime_green, null), getResources().getString(R.string.category), 100));

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