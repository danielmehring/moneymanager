package de.xyzerstudios.moneymanager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.SimpleStatisticsItem;
import de.xyzerstudios.moneymanager.utils.StatisticsItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.currency.Currencies;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {

    private ImageButton buttonStatisticsGoBack;
    private ViewGroup buttonStatisticsMonthFrom, buttonStatisticsMonthTo;
    private TextView textViewMonthFrom, textViewMonthTo;
    private LineChart lineChartStatistics;
    private BarChart barChartStatistics;
    private LinearLayout lineChartStatisticsLegend, barChartStatisticsLegend;

    private Calendar calendarFrom;
    private Calendar calendarTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();

        if (calendarTo.get(Calendar.MONTH) > 1)
            calendarFrom.set(Calendar.MONTH, (calendarTo.get(Calendar.MONTH) - 2));

        buttonStatisticsGoBack = findViewById(R.id.buttonStatisticsGoBack);
        buttonStatisticsMonthFrom = findViewById(R.id.buttonStatisticsMonthFrom);
        buttonStatisticsMonthTo = findViewById(R.id.buttonStatisticsMonthTo);
        textViewMonthFrom = buttonStatisticsMonthFrom.findViewById(R.id.textViewMonthFrom);
        textViewMonthTo = buttonStatisticsMonthTo.findViewById(R.id.textViewMonthTo);
        lineChartStatistics = findViewById(R.id.lineChartStatistics);
        barChartStatistics = findViewById(R.id.barChartStatistics);
        lineChartStatisticsLegend = findViewById(R.id.lineChartStatisticsLegend);
        barChartStatisticsLegend = findViewById(R.id.barChartStatisticsLegend);

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
        setupBarChart();


        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(1500);
        lineChartStatisticsLegend.startAnimation(alphaAnimation);
        barChartStatisticsLegend.startAnimation(alphaAnimation);
    }

    private void setupBarChart() {
        barChartStatistics.setNoDataTextColor(getColor(R.color.ui_text));
        barChartStatistics.getXAxis().setAxisMinimum(0f);
        barChartStatistics.getXAxis().setGranularity(1);
        barChartStatistics.getXAxis().setCenterAxisLabels(true);
        barChartStatistics.getDescription().setEnabled(false);
        barChartStatistics.setTouchEnabled(true);
        barChartStatistics.getLegend().setEnabled(false);
        barChartStatistics.setDrawGridBackground(false);
        barChartStatistics.setDrawBorders(false);
        barChartStatistics.getAxisLeft().setDrawGridLines(false);
        barChartStatistics.getAxisRight().setDrawGridLines(false);
        barChartStatistics.getXAxis().setDrawGridLines(false);
        barChartStatistics.getAxisLeft().setDrawAxisLine(false);
        barChartStatistics.getAxisRight().setDrawAxisLine(false);
        barChartStatistics.getXAxis().setDrawAxisLine(false);
        barChartStatistics.getAxisLeft().setDrawZeroLine(true);
        barChartStatistics.getAxisLeft().setZeroLineWidth(1f);
        barChartStatistics.getAxisLeft().setZeroLineColor(getColor(R.color.ui_lime_grey));
        barChartStatistics.setFocusable(false);
        barChartStatistics.setDoubleTapToZoomEnabled(false);
        barChartStatistics.getXAxis().setTextColor(getColor(R.color.ui_text));
        barChartStatistics.getXAxis().setTextSize(13f);
        barChartStatistics.getXAxis().setTypeface(ResourcesCompat.getFont(StatisticsActivity.this, R.font.poppins_regular));
        barChartStatistics.setExtraTopOffset(25);
        barChartStatistics.getAxisLeft().setTextColor(getColor(R.color.ui_light_background));
        barChartStatistics.getAxisRight().setTextColor(getColor(R.color.ui_light_background));
        barChartStatistics.setScaleYEnabled(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((double) displayMetrics.heightPixels / 1.8));
        barChartStatistics.setLayoutParams(layoutParams);
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
        lineChartStatistics.getAxisLeft().setZeroLineWidth(1f);
        lineChartStatistics.getAxisLeft().setZeroLineColor(getColor(R.color.ui_lime_grey));
        lineChartStatistics.getViewPortHandler().setMaximumScaleY(1);
        lineChartStatistics.getXAxis().setTextColor(getColor(R.color.ui_light_background));
        lineChartStatistics.getAxisLeft().setTextColor(getColor(R.color.ui_light_background));
        lineChartStatistics.getAxisRight().setTextColor(getColor(R.color.ui_light_background));
        lineChartStatistics.setNoDataTextColor(getColor(R.color.ui_text));
        lineChartStatistics.setDoubleTapToZoomEnabled(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((double) displayMetrics.heightPixels / 1.8));
        lineChartStatistics.setLayoutParams(layoutParams);
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

            HashMap<String, Integer> repeatedIncomeMap = new HashMap<>();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                int currentYear = yearFrom;
                int currentMonth = monthFrom;

                for (;;) {
                    final String key = currentMonth + "_" + currentYear;
                    final int value = sumOfRepeatedIncomeInMonth(currentMonth, currentYear, portfolioId);
                    repeatedIncomeMap.put(key, value);
                    if (currentYear == yearTo) {
                        if (currentMonth < monthTo) {
                            currentMonth++;
                            continue;
                        } else {
                            break;
                        }
                    }
                    if (currentMonth < 12) {
                        currentMonth++;
                    } else {
                        currentMonth = 1;
                        currentYear++;
                    }
                }
            }


            expenseMap.forEach((key, value) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, value.getYear());
                calendar.set(Calendar.MONTH, value.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                statisticsItems.add(new StatisticsItem(value.getMonth(), value.getYear(), value.getAmount(),
                        (incomeMap.getOrDefault(key, new SimpleStatisticsItem(0, 0, 0)).getAmount())
                        + (repeatedIncomeMap.getOrDefault(key, 0)),
                        calendar.getTime()));
                incomeMap.remove(key);
                repeatedIncomeMap.remove(key);
            });


            incomeMap.forEach((key, value) -> {
                final int year = value.getYear();
                final int month = value.getMonth();
                final int amount = value.getAmount();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);

                statisticsItems.add(new StatisticsItem(month, year, 0, amount
                        + (repeatedIncomeMap.getOrDefault(key, 0)),
                        calendar.getTime()));
                repeatedIncomeMap.remove(key);
            });

            repeatedIncomeMap.forEach((key, value) -> {
                int month = Integer.valueOf(key.split("_")[0]);
                int year = Integer.valueOf(key.split("_")[1]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                if (value > 0) {
                    statisticsItems.add(new StatisticsItem(month, year, 0, value, calendar.getTime()));
                }
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

            float lowestValue = 1;

            final ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            final ArrayList<Entry> entriesExpenses = new ArrayList<>();
            final ArrayList<Entry> entriesIncome = new ArrayList<>();
            final ArrayList<Entry> entriesBalance = new ArrayList<>();

            final ArrayList<BarEntry> barEntriesExpenses = new ArrayList<>();
            final ArrayList<BarEntry> barEntriesIncome = new ArrayList<>();
            final ArrayList<BarEntry> barEntriesBalance = new ArrayList<>();

            float groupSpace = 0.15f;
            float barSpace = 0.02f;
            float barWidth = 0.263f;

            final int sizeOfArray = a.size();

            final ArrayList<String> labels = new ArrayList<>();
            for (int i = 0; i < sizeOfArray; i++) {
                StatisticsItem statisticsItem = a.get(i);

                float expensesValue = ((float) statisticsItem.getExpenseAmount() / 100f);
                float incomeValue = ((float) statisticsItem.getIncomeAmount() / 100f);
                float balanceValue = (((float) (statisticsItem.getIncomeAmount() - statisticsItem.getExpenseAmount())) / 100f);

                entriesExpenses.add(new Entry(i, expensesValue));
                entriesIncome.add(new Entry(i, incomeValue));
                entriesBalance.add(new Entry(i, balanceValue));

                barEntriesExpenses.add(new BarEntry(i, expensesValue));
                barEntriesIncome.add(new BarEntry(i, incomeValue));
                barEntriesBalance.add(new BarEntry(i, balanceValue));

                if (expensesValue < lowestValue) {
                    lowestValue = expensesValue;
                }

                if (incomeValue < lowestValue) {
                    lowestValue = incomeValue;
                }

                if (balanceValue < lowestValue) {
                    lowestValue = balanceValue;
                }

                labels.add(Utils.yearMonthDateFormat.format(statisticsItem.getTimestamp()));
            }

            Log.d("test___", entriesExpenses.size() + ";" + labels.size());

            LineDataSet lineDataSetExpenses = new LineDataSet(entriesExpenses, getString(R.string.expenses));
            LineDataSet lineDataSetIncome = new LineDataSet(entriesIncome, getString(R.string.income));
            LineDataSet lineDataSetBalance = new LineDataSet(entriesBalance, getString(R.string.saldo));

            BarDataSet barDataSetExpenses = new BarDataSet(barEntriesExpenses, getString(R.string.expenses));
            BarDataSet barDataSetIncome = new BarDataSet(barEntriesIncome, getString(R.string.income));
            BarDataSet barDataSetBalance = new BarDataSet(barEntriesBalance, getString(R.string.saldo));

            int colorAlpha = 190;

            int colorExpenses = getColor(R.color.ui_lime_red);
            barDataSetExpenses.setColor(Color.argb(colorAlpha, Color.red(colorExpenses), Color.green(colorExpenses), Color.blue(colorExpenses)));
            barDataSetExpenses.setHighlightEnabled(false);
            barDataSetExpenses.setValueTextSize(10f);
            lineDataSetExpenses.setLineWidth(1.5f);
            lineDataSetExpenses.setColor(getColor(R.color.ui_lime_red));
            lineDataSetExpenses.setValueTextSize(0f);
            lineDataSetExpenses.setDrawCircles(false);
            lineDataSetExpenses.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetExpenses.setCubicIntensity(0.06f);
            lineDataSetExpenses.setHighlightEnabled(false);

            lineDataSetExpenses.setDrawFilled(true);
            lineDataSetExpenses.setFillColor(getColor(R.color.ui_lime_red));
            lineDataSetExpenses.setFillAlpha(55);

            int colorIncome = getColor(R.color.ui_lime_green);
            barDataSetIncome.setColor(Color.argb(colorAlpha, Color.red(colorIncome), Color.green(colorIncome), Color.blue(colorIncome)));
            barDataSetIncome.setHighlightEnabled(false);
            barDataSetIncome.setValueTextSize(10f);
            lineDataSetIncome.setLineWidth(1.5f);
            lineDataSetIncome.setColor(getColor(R.color.ui_lime_green));
            lineDataSetIncome.setValueTextSize(0f);
            lineDataSetIncome.setDrawCircles(false);
            lineDataSetIncome.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetIncome.setCubicIntensity(0.06f);
            lineDataSetIncome.setHighlightEnabled(false);

            lineDataSetIncome.setDrawFilled(true);
            lineDataSetIncome.setFillColor(getColor(R.color.ui_lime_green));
            lineDataSetIncome.setFillAlpha(55);

            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            String isoCode = sharedPreferences.getString(Utils.SPS_CURRENCY_ISO_CODE, "USD");
            String currencySymbol = Currencies.getIsoToUnicode().getOrDefault(isoCode, isoCode);

            int colorBalance = getColor(R.color.ui_lime_grey);
            barDataSetBalance.setColor(Color.argb(colorAlpha, Color.red(colorBalance), Color.green(colorBalance), Color.blue(colorBalance)));
            barDataSetBalance.setHighlightEnabled(false);
            barDataSetBalance.setValueTextSize(10f);
            lineDataSetBalance.setLineWidth(1.9f);
            lineDataSetBalance.setColor(getColor(R.color.ui_lime_grey));
            lineDataSetBalance.setValueTextSize(13f);
            lineDataSetBalance.setValueTypeface(ResourcesCompat.getFont(StatisticsActivity.this, R.font.poppins_light));
            lineDataSetBalance.setValueTextColor(getColor(R.color.ui_text_faded));
            lineDataSetBalance.setDrawCircles(false);
            lineDataSetBalance.setHighLightColor(getColor(R.color.ui_text));
            lineDataSetBalance.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSetBalance.setCubicIntensity(0.06f);
            lineDataSetBalance.setValueFormatter(new ValueFormatter() {
                @Override
                public String getPointLabel(Entry entry) {
                    int index = (int) entry.getX();
                    StatisticsItem statisticsItem = a.get(index);
                    float value = (statisticsItem.getIncomeAmount() - statisticsItem.getExpenseAmount()) / 100;
                    String display = (int) value + currencySymbol + " " + Utils.yearMonthDateFormat.format(statisticsItem.getTimestamp());
                    if (sizeOfArray > 9) {
                        display = "";
                    } else if (sizeOfArray > 5) {
                        display = Utils.yearMonthDateFormat.format(statisticsItem.getTimestamp());
                    }
                    return display;
                }
            });

            lineDataSetBalance.setDrawFilled(true);
            lineDataSetBalance.setFillColor(getColor(R.color.ui_lime_grey));
            lineDataSetBalance.setFillAlpha(0);
            lineDataSetBalance.setHighlightEnabled(false);

            dataSets.add(lineDataSetExpenses);
            dataSets.add(lineDataSetIncome);
            dataSets.add(lineDataSetBalance);

            if (sizeOfArray > 3) {
                barDataSetBalance.setValueTextSize(0);
                barDataSetExpenses.setValueTextSize(0);
                barDataSetIncome.setValueTextSize(0);
            }

            BarData barData = new BarData(barDataSetBalance, barDataSetExpenses, barDataSetIncome);
            barData.setValueTextColor(getColor(R.color.ui_text));
            barData.setValueTypeface(ResourcesCompat.getFont(StatisticsActivity.this, R.font.poppins_light));
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getBarLabel(BarEntry barEntry) {
                    return (int) barEntry.getY() + currencySymbol;
                }
            });
            barData.setBarWidth(barWidth);

            activity.barChartStatistics.setData(barData);
            activity.barChartStatistics.getXAxis().setAxisMaximum(labels.size());
            activity.barChartStatistics.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            activity.barChartStatistics.groupBars(0f, groupSpace, barSpace);

            LineData lineData = new LineData(dataSets);
            if (sizeOfArray > 1) {
                activity.lineChartStatistics.setData(lineData);
            } else {
                activity.lineChartStatistics.clear();
            }

            activity.lineChartStatistics.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            activity.lineChartStatistics.getXAxis().setAxisMinimum(lineData.getXMin() - 0.07f);
            activity.lineChartStatistics.getXAxis().setAxisMaximum(lineData.getXMax() + 0.07f);

            if (lowestValue > 0) {
                activity.barChartStatistics.getAxisLeft().setAxisMinimum(-0f);
                activity.barChartStatistics.getAxisRight().setAxisMinimum(-0f);
                activity.lineChartStatistics.getAxisLeft().setAxisMinimum(-0f);
                activity.lineChartStatistics.getAxisRight().setAxisMinimum(-0f);
            }

            activity.barChartStatistics.getAxisLeft().resetAxisMinimum();
            activity.barChartStatistics.getAxisRight().resetAxisMinimum();
            activity.lineChartStatistics.getAxisLeft().resetAxisMinimum();
            activity.lineChartStatistics.getAxisRight().resetAxisMinimum();

            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(1500);
            activity.lineChartStatistics.startAnimation(alphaAnimation);
            activity.lineChartStatistics.animateY(500, Easing.EaseInOutCirc);
            activity.barChartStatistics.startAnimation(alphaAnimation);
            activity.barChartStatistics.animateY(500, Easing.EaseInOutCirc);
            activity.lineChartStatisticsLegend.startAnimation(alphaAnimation);
            activity.barChartStatisticsLegend.startAnimation(alphaAnimation);
            activity.barChartStatistics.invalidate();
        }

        private int loadPortfolioIdFromSharedPrefs() {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private int sumOfRepeatedIncomeInMonth(int month, int year, int portfolioId) {
            RepeatedIncomeDatabaseHelper repeatedIncomeDatabaseHelper = new RepeatedIncomeDatabaseHelper(activity);
            Cursor repeatedCursor = repeatedIncomeDatabaseHelper.readAllDataForPortfolio(portfolioId);

            int amount = 0;

            while (repeatedCursor.moveToNext()) {
                int amountRepeated = repeatedCursor.getInt(3);
                String interval = repeatedCursor.getString(4);
                String timestampOfEntry = repeatedCursor.getString(5);

                int intervalNumber = Integer.valueOf(interval.split("_")[0]);
                String intervalUnit = interval.split("_")[1];

                int amountToAdd = 0;
                amountToAdd = amountInMonth(timestampOfEntry, month, year, intervalUnit, intervalNumber, amountRepeated);

                if (amountToAdd <= 0) {
                    continue;
                }

                amount = amount + amountToAdd;
            }

            return amount;
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
}