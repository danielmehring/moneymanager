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
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseActivity;
import de.xyzerstudios.moneymanager.activities.add.AddIncomeActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.ExpensesAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.IncomeAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.ExpensesItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.IncomeItem;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;

public class IncomeActivity extends AppCompatActivity {

    public ImageButton buttonIncomeGoBack;

    public LinearLayout buttonAddNewIncome, buttonIncomeFilter;
    public SwipeRefreshLayout swipeRefresh;
    public RecyclerView recyclerViewIncome;
    public IncomeAdapter recyclerAdapter;
    private Date date;

    public static ArrayList<IncomeItem> incomeItems;

    public int selectedMonth;
    public int selectedYear;

    public String filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

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
    }

    private void initObjects() {
        date = new Date();
        incomeItems = new ArrayList<>();
        selectedMonth = Integer.valueOf(Utils.monthDateFormat.format(date));
        selectedYear = Integer.valueOf(Utils.yearDateFormat.format(date));
        filterCategory = "";
    }

    private void manipulateGui() {
        recyclerViewIncome.setHasFixedSize(true);
        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new IncomeAdapter(this, this, incomeItems);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new IncomeActivity.incomeAsyncTask(IncomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory);
    }

    private void setOtherListeners() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new IncomeActivity.incomeAsyncTask(IncomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        selectedMonth + "", selectedYear + "", filterCategory);
                swipeRefresh.setRefreshing(false);
            }
        });
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

                        Toast.makeText(IncomeActivity.this, selectedMonth + ";" + selectedYear, Toast.LENGTH_SHORT).show();

                        new IncomeActivity.incomeAsyncTask(IncomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                formattedSelectedMonth + "", selectedYear + "", filterCategory);
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
        new IncomeActivity.incomeAsyncTask(IncomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory);
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

    private class incomeAsyncTask extends AsyncTask<String, ArrayList<IncomeItem>, String> {
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
        protected String doInBackground(String... strings) {

            ArrayList<IncomeItem> incomeItems = new ArrayList<>();

            IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(activity);
            Cursor incomeCursor = incomeDatabaseHelper.readEntriesByPortfolioIdSortedByDate(loadPortfolioIdFromSharedPrefs(),
                    Integer.valueOf(strings[0]), Integer.valueOf(strings[1]));

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

            return "";
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            IncomeActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        private int loadPortfolioIdFromSharedPrefs() {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
        }
    }
    
}