package de.xyzerstudios.moneymanager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.SimpleStatisticsItem;
import de.xyzerstudios.moneymanager.utils.StatisticsItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {

    private ImageButton buttonStatisticsGoBack;
    private ViewGroup buttonStatisticsMonthFrom, buttonStatisticsMonthTo;
    private TextView textViewMonthFrom, textViewMonthTo;
    private LineChart lineChartStatistics;

    private Calendar calendarFrom;
    private Calendar calendarTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();

        calendarFrom.set(Calendar.YEAR, (calendarTo.get(Calendar.YEAR) - 1));

        buttonStatisticsGoBack = findViewById(R.id.buttonStatisticsGoBack);
        buttonStatisticsMonthFrom = findViewById(R.id.buttonStatisticsMonthFrom);
        buttonStatisticsMonthTo = findViewById(R.id.buttonStatisticsMonthTo);
        textViewMonthFrom = buttonStatisticsMonthFrom.findViewById(R.id.textViewMonthFrom);
        textViewMonthTo = buttonStatisticsMonthTo.findViewById(R.id.textViewMonthTo);
        lineChartStatistics = findViewById(R.id.lineChartStatistics);

        lineChartStatistics.setNoDataTextColor(getColor(R.color.ui_text));

        buttonStatisticsMonthFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(StatisticsActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        calendarFrom.set(Calendar.YEAR, selectedYear);
                        calendarFrom.set(Calendar.MONTH, selectedMonth);
                        int formattedSelectedMonth = selectedMonth + 1;
                        TransitionManager.beginDelayedTransition(buttonStatisticsMonthFrom);
                        textViewMonthFrom.setText(getMonth(formattedSelectedMonth) + ", " + selectedYear);
                        startAsyncTask();
                    }
                }, calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH));

                Date date = new Date();
                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(calendarFrom.get(Calendar.MONTH))
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(calendarFrom.get(Calendar.YEAR))
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });

        buttonStatisticsMonthTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(StatisticsActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        calendarTo.set(Calendar.YEAR, selectedYear);
                        calendarTo.set(Calendar.MONTH, selectedMonth);
                        int formattedSelectedMonth = selectedMonth + 1;
                        TransitionManager.beginDelayedTransition(buttonStatisticsMonthTo);
                        textViewMonthTo.setText(getMonth(formattedSelectedMonth) + ", " + selectedYear);
                        startAsyncTask();
                    }
                }, calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH));

                Date date = new Date();
                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(calendarTo.get(Calendar.MONTH))
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(calendarTo.get(Calendar.YEAR))
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });

        buttonStatisticsGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        updateTextViews();
        startAsyncTask();
        setupLineChart();
    }

    private void setupLineChart() {
        lineChartStatistics.getDescription().setEnabled(false);
        lineChartStatistics.setTouchEnabled(true);
        lineChartStatistics.getLegend().setEnabled(false);
        lineChartStatistics.setDrawGridBackground(false);
        lineChartStatistics.getAxisLeft().setDrawGridLines(false);
        lineChartStatistics.getAxisRight().setDrawGridLines(false);
        lineChartStatistics.getXAxis().setDrawGridLines(false);
        lineChartStatistics.setDrawBorders(false);
        lineChartStatistics.getAxisLeft().setDrawAxisLine(false);
        lineChartStatistics.getAxisRight().setDrawAxisLine(false);
        lineChartStatistics.getXAxis().setDrawAxisLine(false);
        lineChartStatistics.getAxisLeft().setDrawZeroLine(true);
        lineChartStatistics.getAxisLeft().setZeroLineWidth(2f);
        lineChartStatistics.getAxisLeft().setZeroLineColor(getColor(R.color.ui_text));
        lineChartStatistics.getViewPortHandler().setMaximumScaleY(1);
        lineChartStatistics.getXAxis().setTextColor(getColor(R.color.ui_text));
        lineChartStatistics.getAxisLeft().setTextColor(getColor(R.color.ui_light_background));
        lineChartStatistics.getAxisRight().setTextColor(getColor(R.color.ui_light_background));


    }

    private void startAsyncTask() {
        new statisticsAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                calendarFrom.get(Calendar.MONTH), calendarFrom.get(Calendar.YEAR),
                calendarTo.get(Calendar.MONTH), calendarTo.get(Calendar.YEAR));
    }

    private void updateTextViews() {
        textViewMonthFrom.setText(getMonth((calendarFrom.get(Calendar.MONTH) + 1)) + ", " + calendarFrom.get(Calendar.YEAR));
        textViewMonthTo.setText(getMonth((calendarTo.get(Calendar.MONTH) + 1)) + ", " + calendarTo.get(Calendar.YEAR));
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

    private class statisticsAsyncTask extends AsyncTask<Integer, String, ArrayList<StatisticsItem>> {
        private final WeakReference<StatisticsActivity> activityWeakReference;
        private final Activity activity;

        statisticsAsyncTask(StatisticsActivity activity) {
            activityWeakReference = new WeakReference<StatisticsActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StatisticsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected ArrayList<StatisticsItem> doInBackground(Integer... integers) {
            ArrayList<StatisticsItem> statisticsItems = new ArrayList<>();
            int portfolioId = loadPortfolioIdFromSharedPrefs();
            int monthFrom = integers[0] + 1;
            int yearFrom = integers[1];
            int monthTo = integers[2] + 1;
            int yearTo = integers[3];

            ExpensesDatabaseHelper expensesDatabase = new ExpensesDatabaseHelper(activity);
            Cursor expenseCursor = expensesDatabase.readEntriesByRangeSortedByDate(portfolioId, yearFrom, yearTo);

            HashMap<String, SimpleStatisticsItem> expenseMap = new HashMap<>();

            while (expenseCursor.moveToNext()) {
                if (expenseCursor.getInt(2) == yearFrom && expenseCursor.getInt(1) < monthFrom) {
                    continue;
                }
                if (expenseCursor.getInt(2) == yearTo && expenseCursor.getInt(1) > monthTo) {
                    continue;
                }
                String timestampMonthYear = expenseCursor.getString(1) + "_" + expenseCursor.getString(2);
                expenseMap.put(timestampMonthYear, new SimpleStatisticsItem(expenseCursor.getInt(1),
                        expenseCursor.getInt(2), expenseCursor.getInt(0)));
            }

            IncomeDatabaseHelper incomeDatabase = new IncomeDatabaseHelper(activity);
            Cursor incomeCursor = incomeDatabase.readEntriesByRangeSortedByDate(portfolioId, yearFrom, yearTo);

            HashMap<String, SimpleStatisticsItem> incomeMap = new HashMap<>();

            while (incomeCursor.moveToNext()) {
                if (incomeCursor.getInt(2) == yearFrom && incomeCursor.getInt(1) < monthFrom) {
                    continue;
                }
                if (incomeCursor.getInt(2) == yearTo && incomeCursor.getInt(1) > monthTo) {
                    continue;
                }
                String timestampMonthYear = incomeCursor.getString(1) + "_" + incomeCursor.getString(2);
                incomeMap.put(timestampMonthYear, new SimpleStatisticsItem(incomeCursor.getInt(1),
                        incomeCursor.getInt(2), incomeCursor.getInt(0)));
            }

            expenseMap.forEach((key, value) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, value.getYear());
                calendar.set(Calendar.MONTH, value.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                statisticsItems.add(new StatisticsItem(value.getMonth(), value.getYear(), value.getAmount(),
                        incomeMap.getOrDefault(key, new SimpleStatisticsItem(0, 0, 0)).getAmount(),
                        calendar.getTime()));
                incomeMap.remove(key);
            });

            incomeMap.forEach((key, value) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, value.getYear());
                calendar.set(Calendar.MONTH, value.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                statisticsItems.add(new StatisticsItem(value.getMonth(), value.getYear(), 0, value.getAmount(),
                        calendar.getTime()));
            });

            statisticsItems.sort(new Comparator<StatisticsItem>() {
                @Override
                public int compare(StatisticsItem s1, StatisticsItem s2) {
                    return s1.getTimestamp().compareTo(s2.getTimestamp());
                }
            });

            return statisticsItems;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            StatisticsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(ArrayList<StatisticsItem> a) {
            super.onPostExecute(a);
            StatisticsActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

            final ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            final ArrayList<Entry> entriesExpenses = new ArrayList<>();
            final ArrayList<Entry> entriesIncome = new ArrayList<>();
            final ArrayList<Entry> entriesBalance = new ArrayList<>();
            final ArrayList<String> labels = new ArrayList<>();
            for (int i = 0; i < a.size(); i++) {
                StatisticsItem statisticsItem = a.get(i);
                entriesExpenses.add(new Entry(i, ((float) statisticsItem.getExpenseAmount() / 100f)));
                entriesIncome.add(new Entry(i, ((float) statisticsItem.getIncomeAmount() / 100f)));
                entriesBalance.add(new Entry(i, (((float) (statisticsItem.getIncomeAmount() - statisticsItem.getExpenseAmount())) / 100f)));
                labels.add(Utils.yearMonthDateFormat.format(statisticsItem.getTimestamp()));
            }

            Log.d("test___", entriesExpenses.size() + ";" + labels.size());

            LineDataSet lineDataSetExpenses = new LineDataSet(entriesExpenses, getString(R.string.expenses));
            LineDataSet lineDataSetIncome = new LineDataSet(entriesIncome, getString(R.string.income));
            LineDataSet lineDataSetBalance = new LineDataSet(entriesBalance, getString(R.string.saldo));

            lineDataSetExpenses.setLineWidth(4f);
            lineDataSetExpenses.setColor(getColor(R.color.ui_lime_red));
            lineDataSetExpenses.setValueTextSize(0f);
            lineDataSetExpenses.setDrawCircles(false);
            lineDataSetExpenses.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetExpenses.setCubicIntensity(0.01f);
            lineDataSetExpenses.setDrawVerticalHighlightIndicator(false);
            lineDataSetExpenses.setDrawHorizontalHighlightIndicator(false);

            lineDataSetExpenses.setDrawFilled(true);
            lineDataSetExpenses.setFillColor(getColor(R.color.ui_lime_red));
            lineDataSetExpenses.setFillAlpha(55);

            lineDataSetIncome.setLineWidth(4f);
            lineDataSetIncome.setColor(getColor(R.color.ui_lime_green));
            lineDataSetIncome.setValueTextSize(0f);
            lineDataSetIncome.setDrawCircles(false);
            lineDataSetIncome.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetIncome.setCubicIntensity(0.01f);
            lineDataSetIncome.setDrawVerticalHighlightIndicator(false);
            lineDataSetIncome.setDrawHorizontalHighlightIndicator(false);

            lineDataSetIncome.setDrawFilled(true);
            lineDataSetIncome.setFillColor(getColor(R.color.ui_lime_green));
            lineDataSetIncome.setFillAlpha(55);


            lineDataSetBalance.setLineWidth(2f);
            lineDataSetBalance.setColor(getColor(R.color.ui_lime_grey));
            lineDataSetBalance.setValueTextSize(12f);
            lineDataSetBalance.setValueTextColor(getColor(R.color.ui_text));
            lineDataSetBalance.setDrawCircles(false);
            lineDataSetBalance.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetBalance.setCubicIntensity(0.01f);

            lineDataSetBalance.setDrawFilled(true);
            lineDataSetBalance.setFillColor(getColor(R.color.ui_lime_grey));
            lineDataSetBalance.setFillAlpha(0);



            dataSets.add(lineDataSetExpenses);
            dataSets.add(lineDataSetIncome);
            dataSets.add(lineDataSetBalance);

            LineData lineData = new LineData(dataSets);
            activity.lineChartStatistics.setData(lineData);
            activity.lineChartStatistics.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            activity.lineChartStatistics.getXAxis().setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    try {
                        return labels.get((int) value);
                    } catch (Exception e) {
                        return "";
                    }
                }
            });

            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(1000);
            activity.lineChartStatistics.startAnimation(alphaAnimation);
        }

        private int loadPortfolioIdFromSharedPrefs() {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
        }
    }
}