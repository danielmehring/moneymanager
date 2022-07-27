package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.ExpensesAdapter;
import de.xyzerstudios.moneymanager.utils.ExpensesItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;

public class ExpensesActivity extends AppCompatActivity {

    public ImageButton buttonExpensesGoBack;
    public LinearLayout buttonAddNewExpense, buttonMonthPicker;
    public SwipeRefreshLayout swipeRefreshExpenses;
    public RecyclerView recyclerViewExpenses;
    public RecyclerView.Adapter recyclerAdapter;
    private Date date;

    public ArrayList<ExpensesItem> expensesItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        initGui();
        initObjects();
        setOnClickListeners();
        manipulateGui();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new ExpensesActivity.expensesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private void manipulateGui() {
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new ExpensesAdapter(this, this, expensesItems);

        swipeRefreshExpenses.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ExpensesActivity.expensesAsyncTask(ExpensesActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                swipeRefreshExpenses.setRefreshing(false);
            }
        });
    }

    private void setOnClickListeners() {
        buttonExpensesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonAddNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initObjects() {
        date = new Date();
        expensesItems = new ArrayList<>();
    }

    private void initGui() {
        buttonExpensesGoBack = findViewById(R.id.buttonExpensesGoBack);
        buttonAddNewExpense = findViewById(R.id.buttonAddNewExpense);
        buttonMonthPicker = findViewById(R.id.buttonMonthPicker);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);
        swipeRefreshExpenses = findViewById(R.id.swipeRefreshExpenses);
    }

    private class expensesAsyncTask extends AsyncTask<String, ArrayList<ExpensesItem>, String> {
        private final WeakReference<ExpensesActivity> activityWeakReference;
        private final Activity activity;

        expensesAsyncTask(ExpensesActivity activity) {
            activityWeakReference = new WeakReference<ExpensesActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ExpensesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            ArrayList<ExpensesItem> expensesItems = new ArrayList<>();

            ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(activity);
            Cursor expensesCursor = expensesDatabaseHelper.readEntriesByPortfolioIdSortedByDate(loadPortfolioIdFromSharedPrefs());

            while (expensesCursor.moveToNext()) {
                try {
                    SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                    Date date = simpleDateFormat.parse(expensesCursor.getString(3));

                    CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                    Cursor categoriesCursor = categoriesDatabaseHelper.readCategoryById(expensesCursor.getInt(7));
                    String categoryName = "";
                    while (categoriesCursor.moveToNext()) categoryName = categoriesCursor.getString(1);

                    expensesItems.add(new ExpensesItem(expensesCursor.getInt(0), expensesCursor.getString(2), expensesCursor.getInt(6),
                            DateFormat.getDateInstance().format(date), expensesCursor.getString(8), categoryName));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            publishProgress(expensesItems);

            return "";
        }

        @Override
        protected void onProgressUpdate(ArrayList<ExpensesItem>... values) {
            super.onProgressUpdate(values);
            ExpensesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.recyclerAdapter = new ExpensesAdapter(activity, activity, values[0]);
            activity.recyclerViewExpenses.setAdapter(activity.recyclerAdapter);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ExpensesActivity activity = activityWeakReference.get();
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