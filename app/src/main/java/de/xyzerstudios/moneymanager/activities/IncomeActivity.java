package de.xyzerstudios.moneymanager.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseActivity;
import de.xyzerstudios.moneymanager.activities.add.AddIncomeActivity;
import de.xyzerstudios.moneymanager.utils.RepeatedItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.ExpensesAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.IncomeAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.RepeatedIncomeAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.ExpensesItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.IncomeItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.RepeatedIncomeItem;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;

public class IncomeActivity extends AppCompatActivity {

    public ImageButton buttonIncomeGoBack;

    public LinearLayout buttonAddNewIncome, buttonIncomeFilter;
    public SwipeRefreshLayout swipeRefresh, swipeRefreshRepeated;
    public RecyclerView recyclerViewIncome, recyclerViewRepeated;
    public IncomeAdapter recyclerAdapter;
    public RepeatedIncomeAdapter recyclerAdapterRepeated;
    public TextView repeatedIncomeHeading;
    private Date date;

    public ArrayList<IncomeItem> incomeItems;
    public ArrayList<RepeatedIncomeItem> repeatedIncomeItems;

    public int selectedMonth;
    public int selectedYear;

    public String filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        selectedMonth = bundle.getInt("month", 0);
        selectedYear = bundle.getInt("year", 0);

        initGui();
        initObjects();
        setOnClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    private void initGui() {
        buttonIncomeGoBack = findViewById(R.id.buttonIncomeGoBack);
        buttonAddNewIncome = findViewById(R.id.buttonAddNewIncome);
        buttonIncomeFilter = findViewById(R.id.buttonIncomeFilter);
        swipeRefresh = findViewById(R.id.swipeRefreshIncome);
        recyclerViewIncome = findViewById(R.id.recyclerViewIncome);
        swipeRefreshRepeated = findViewById(R.id.swipeRefreshRepeatedIncome);
        recyclerViewRepeated = findViewById(R.id.recyclerViewRepeatedIncome);
        repeatedIncomeHeading = findViewById(R.id.repeatedIncomeHeading);
    }

    private void initObjects() {
        date = new Date();
        incomeItems = new ArrayList<>();
        repeatedIncomeItems = new ArrayList<>();
        filterCategory = "";
    }

    private void manipulateGui() {
        recyclerViewIncome.setHasFixedSize(true);
        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new IncomeAdapter(this, this, incomeItems);
        recyclerViewIncome.setAdapter(recyclerAdapter);

        recyclerViewRepeated.setHasFixedSize(true);
        recyclerViewRepeated.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapterRepeated = new RepeatedIncomeAdapter(this, repeatedIncomeItems);
        recyclerViewRepeated.setAdapter(recyclerAdapterRepeated);

        if (repeatedIncomeItems.size() == 0) {
            swipeRefreshRepeated.setVisibility(View.GONE);
            repeatedIncomeHeading.setVisibility(View.GONE);
        } else {
            swipeRefreshRepeated.setVisibility(View.VISIBLE);
            repeatedIncomeHeading.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners() {
        buttonIncomeGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonAddNewIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomeActivity.this, AddIncomeActivity.class);
                startActivity(intent);
            }
        });

        buttonIncomeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
    }

    private void setOtherListeners() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startAsyncTask();
                swipeRefresh.setRefreshing(false);
            }
        });
        swipeRefreshRepeated.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startAsyncTask();
                swipeRefreshRepeated.setRefreshing(false);
            }
        });
    }

    private void startAsyncTask() {
        new IncomeActivity.incomeAsyncTask(IncomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startAsyncTask();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_filter_income);



        LinearLayout chooserMonthAndYear = dialog.findViewById(R.id.chooserMonthAndYearIncome);
        LinearLayout chooserCategory = dialog.findViewById(R.id.chooserCategoryIncome);

        LinearLayout removeFilterCategory = dialog.findViewById(R.id.removeFilterCategoryBSIncome);

        TextView textViewMonthAndYear = dialog.findViewById(R.id.textViewMonthAndYearIncome);
        TextView textViewCategory = dialog.findViewById(R.id.textViewCategoryIncome);



        if (filterCategory.matches("")) {
            removeFilterCategory.setVisibility(View.GONE);
        }

        textViewMonthAndYear.setText(getMonth(selectedMonth) + ", " + selectedYear);

        if (!filterCategory.matches("")) {
            textViewCategory.setText(filterCategory);
        }

        chooserMonthAndYear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(IncomeActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        int formattedSelectedMonth = selectedMonth + 1;
                        IncomeActivity.this.selectedMonth = formattedSelectedMonth;
                        IncomeActivity.this.selectedYear = selectedYear;
                        textViewMonthAndYear.setText(getMonth(formattedSelectedMonth) + ", " + selectedYear);
                        startAsyncTask();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(selectedMonth - 1)
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(selectedYear)
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });


        chooserCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "income");
                startForResult.launch(intent);
                dialog.dismiss();
            }
        });

        removeFilterCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCategory = "";
                updateCategory();
                dialog.dismiss();
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

    private void updateCategory() {
        startAsyncTask();
    }

    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            filterCategory = result.getData().getStringExtra("categoryName");
                            updateCategory();

                        }
                    }
                }
            });

    private class incomeAsyncTask extends AsyncTask<String, ArrayList<IncomeItem>, ArrayList<RepeatedIncomeItem>> {
        private final WeakReference<IncomeActivity> activityWeakReference;
        private final Activity activity;

        incomeAsyncTask(IncomeActivity activity) {
            activityWeakReference = new WeakReference<IncomeActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            IncomeActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected ArrayList<RepeatedIncomeItem> doInBackground(String... strings) {
            int month = Integer.valueOf(strings[0]);
            int year = Integer.valueOf(strings[1]);


            ArrayList<IncomeItem> incomeItems = new ArrayList<>();
            ArrayList<RepeatedIncomeItem> repeatedIncomeItems = new ArrayList<>();

            IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(activity);
            Cursor incomeCursor = incomeDatabaseHelper.readEntriesByPortfolioIdSortedByDate(loadPortfolioIdFromSharedPrefs(),
                    month, year);

            boolean filterCategory = false;

            if (strings.length > 2 && !strings[2].matches(""))
                filterCategory = true;

            String categoryFilter = "";
            if (filterCategory)
                categoryFilter = strings[2];



            while (incomeCursor.moveToNext()) {
                try {
                    SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                    Date date = simpleDateFormat.parse(incomeCursor.getString(3));

                    CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                    Cursor categoriesCursor = categoriesDatabaseHelper.readCategoryById(incomeCursor.getInt(7));
                    String categoryName = "";
                    while (categoriesCursor.moveToNext()) categoryName = categoriesCursor.getString(1);

                    if (filterCategory && !categoryName.matches(categoryFilter))
                        continue;

                    incomeItems.add(new IncomeItem(incomeCursor.getInt(0),
                            incomeCursor.getString(2), incomeCursor.getInt(6),
                            DateFormat.getDateInstance().format(date), categoryName));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            publishProgress(incomeItems);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RepeatedIncomeDatabaseHelper repeatedIncomeDatabaseHelper = new RepeatedIncomeDatabaseHelper(activity);
                Cursor repeatedCursor = repeatedIncomeDatabaseHelper.readAllDataForPortfolio(loadPortfolioIdFromSharedPrefs());

                while (repeatedCursor.moveToNext()) {
                    int entryId = repeatedCursor.getInt(0);
                    int incomeEntryId = repeatedCursor.getInt(1);
                    int amount = repeatedCursor.getInt(3);
                    String interval = repeatedCursor.getString(4);
                    String timestamp = repeatedCursor.getString(5);
                    String firstOfMonthAsDate = year + "_" + ((month < 10) ? ("0" + month) : month) + "_01";

                    int intervalNumber = Integer.valueOf(interval.split("_")[0]);
                    String intervalUnit = interval.split("_")[1];

                    int amountToAdd = 0;

                    switch (intervalUnit) {
                        case "d":
                            String[] timestampSplit = timestamp.split("_");
                            int yearTimestamp = Integer.valueOf(timestampSplit[0]);
                            int monthTimestamp = Integer.valueOf(timestampSplit[1]);
                            if (monthTimestamp == month && yearTimestamp == year) {
                                int dayTimestamp = Integer.valueOf(timestampSplit[2]);
                                int multiplierAmountInMonth = -1;
                                int daysInCurrentMonth = getDaysInMonth(month, year);
                                for (int i = dayTimestamp; i <= daysInCurrentMonth; i += intervalNumber) {
                                    ++multiplierAmountInMonth;
                                }
                                amountToAdd = amount * multiplierAmountInMonth;
                            } else {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");

                                String endOfThisMonth = year + "_" + ((month < 10) ? ("0" + month) : month) + "_" + getDaysInMonth(month, year);

                                LocalDate dateTimestamp = LocalDate.parse(timestamp, dtf);
                                LocalDate dateStartCurrentMonth = LocalDate.parse(firstOfMonthAsDate, dtf);
                                LocalDate dateEndCurrentMonth = LocalDate.parse(endOfThisMonth, dtf);

                                int rangeStart = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateStartCurrentMonth));
                                int rangeEnd = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateEndCurrentMonth));

                                if (rangeStart < 0 || rangeEnd < 0) {
                                    break;
                                }

                                int multiplier = 0;
                                for (int i = rangeStart; i <= rangeEnd; ++i) {
                                    if (i % intervalNumber == 0) multiplier++;
                                }
                                amountToAdd = amount * multiplier;
                            }
                            break;
                        case "w":
                            intervalNumber *= 7;
                            String[] timestampSplitW = timestamp.split("_");
                            int yearTimestampW = Integer.valueOf(timestampSplitW[0]);
                            int monthTimestampW = Integer.valueOf(timestampSplitW[1]);
                            if (monthTimestampW == month && yearTimestampW == year) {
                                int dayTimestamp = Integer.valueOf(timestampSplitW[2]);
                                int multiplierAmountInMonth = -1;
                                int daysInCurrentMonth = getDaysInMonth(month, year);
                                for (int i = dayTimestamp; i <= daysInCurrentMonth; i += intervalNumber) {
                                    ++multiplierAmountInMonth;
                                }
                                amountToAdd = amount * multiplierAmountInMonth;
                            } else {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");

                                String endOfThisMonth = year + "_" + ((month < 10) ? ("0" + month) : month) + "_" + getDaysInMonth(month, year);

                                LocalDate dateTimestamp = LocalDate.parse(timestamp, dtf);
                                LocalDate dateStartCurrentMonth = LocalDate.parse(firstOfMonthAsDate, dtf);
                                LocalDate dateEndCurrentMonth = LocalDate.parse(endOfThisMonth, dtf);

                                int rangeStart = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateStartCurrentMonth));
                                int rangeEnd = Math.toIntExact(ChronoUnit.DAYS.between(dateTimestamp, dateEndCurrentMonth));

                                if (rangeStart < 0 || rangeEnd < 0) {
                                    break;
                                }

                                int multiplier = 0;
                                for (int i = rangeStart; i <= rangeEnd; ++i) {
                                    if (i % intervalNumber == 0) multiplier++;
                                }
                                amountToAdd = amount * multiplier;
                            }
                            break;
                        case "m":
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
                            String timestampModified = timestamp.split("_")[0] + "_" + timestamp.split("_")[1] + "_01";

                            LocalDate dateTimestampMonth = LocalDate.parse(timestampModified, dtf);
                            LocalDate dateSelectedMonth = LocalDate.parse(firstOfMonthAsDate, dtf);

                            int monthsBetween = Math.toIntExact(ChronoUnit.MONTHS.between(dateTimestampMonth, dateSelectedMonth));
                            if (monthsBetween <= 0) {
                                break;
                            }
                            if (monthsBetween % intervalNumber == 0) {
                                amountToAdd = amount;
                            }
                            break;
                        case "y":
                            try {
                                SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                                Date dateTimestamp = simpleDateFormat.parse(timestamp);
                                int timestampYear = Integer.valueOf(Utils.yearDateFormat.format(dateTimestamp));
                                int timestampMonth = Integer.valueOf(Utils.monthDateFormat.format(dateTimestamp));
                                if (timestampMonth == month && year > timestampYear) {
                                    amountToAdd = amount;
                                } else {
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                    Cursor cursorIncomeDB = incomeDatabaseHelper.readEntryById(incomeEntryId);
                    String name = "";
                    while (cursorIncomeDB.moveToNext()) name = cursorIncomeDB.getString(2);
                    if (amountToAdd != 0)
                        repeatedIncomeItems.add(new RepeatedIncomeItem(entryId, incomeEntryId, name, amountToAdd));
                }
            }

            return repeatedIncomeItems;
        }

        @Override
        protected void onProgressUpdate(ArrayList<IncomeItem>... values) {
            super.onProgressUpdate(values);
            IncomeActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.incomeItems = values[0];
            activity.recyclerAdapter = new IncomeAdapter(activity, activity, values[0]);
            activity.recyclerViewIncome.swapAdapter(activity.recyclerAdapter, false);
        }

        @Override
        protected void onPostExecute(ArrayList<RepeatedIncomeItem> a) {
            super.onPostExecute(a);
            IncomeActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.repeatedIncomeItems = a;
            activity.recyclerAdapterRepeated = new RepeatedIncomeAdapter(activity, a);
            activity.recyclerViewRepeated.swapAdapter(activity.recyclerAdapterRepeated, false);
            activity.recyclerViewRepeated.scrollToPosition(0);
            if (activity.repeatedIncomeItems.size() == 0) {
                activity.swipeRefreshRepeated.setVisibility(View.GONE);
                activity.repeatedIncomeHeading.setVisibility(View.GONE);
            } else {
                activity.swipeRefreshRepeated.setVisibility(View.VISIBLE);
                activity.repeatedIncomeHeading.setVisibility(View.VISIBLE);
            }
            activity.swipeRefreshRepeated.setEnabled(false);
            if (a.size() == 1) {
                activity.swipeRefreshRepeated.getLayoutParams().height = dpToPx(60);
            } else if (a.size() == 2) {
                activity.swipeRefreshRepeated.getLayoutParams().height = dpToPx(110);
            } else if (a.size() == 3){
                activity.swipeRefreshRepeated.getLayoutParams().height = dpToPx(160);
            } else {
                activity.swipeRefreshRepeated.getLayoutParams().height = dpToPx(210);
                activity.swipeRefreshRepeated.setEnabled(true);
            }
        }

        private int loadPortfolioIdFromSharedPrefs() {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
        }

        public int dpToPx(int dp) {
            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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