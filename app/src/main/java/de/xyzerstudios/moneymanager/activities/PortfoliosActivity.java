package de.xyzerstudios.moneymanager.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddPortfolioActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.adapters.PortfolioChooseAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.adapters.PortfolioAdapter;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.InformationDialog;

public class PortfoliosActivity extends AppCompatActivity {

    private ImageButton buttonPortfolioGoBack;
    private LinearLayout buttonAddNewPortfolioActivity, buttonInfoPortfolios;

    public RecyclerView portfolioRecyclerView;
    public RecyclerView.Adapter portfolioAdapter;
    public SwipeRefreshLayout swipeRefreshPortfolio;
    public static ArrayList<BalancePortfolioItem> portfolioItems;

    private boolean choosePortfolio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolios);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        choosePortfolio = bundle.getBoolean("choosePortfolio");

        buttonPortfolioGoBack = findViewById(R.id.buttonPortfolioGoBack);
        buttonAddNewPortfolioActivity = findViewById(R.id.buttonAddNewPortfolioActivity);
        buttonInfoPortfolios = findViewById(R.id.buttonInfoPortfolios);

        buttonPortfolioGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonAddNewPortfolioActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PortfoliosActivity.this, AddPortfolioActivity.class);
                startActivity(intent);
            }
        });

        buttonInfoPortfolios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformationDialog informationDialog = new InformationDialog(getString(R.string.menu_title_portfolios), getString(R.string.portfolios_explanation));
                informationDialog.show(getSupportFragmentManager(), "Information Portfolios");
            }
        });

        portfolioRecyclerView = findViewById(R.id.recyclerViewPortfoliosActivity);
        swipeRefreshPortfolio = findViewById(R.id.swipeRefreshPortfoliosActivity);

        portfolioItems = new ArrayList<>();

        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (choosePortfolio) {
            portfolioAdapter = new PortfolioChooseAdapter(this, this, portfolioItems);
        } else {
            portfolioAdapter = new PortfolioAdapter(this, this, portfolioItems, loadPortfolioIdFromSharedPrefs());
        }

        portfolioRecyclerView.setAdapter(portfolioAdapter);

        swipeRefreshPortfolio.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateAndLoadPortfoliosAsyncTask(PortfoliosActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, choosePortfolio);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new updateAndLoadPortfoliosAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, choosePortfolio);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            new updateAndLoadPortfoliosAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, choosePortfolio);
        }
    }

    public int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private class updateAndLoadPortfoliosAsyncTask extends AsyncTask<Boolean, String, Boolean> {
        private final WeakReference<PortfoliosActivity> activityWeakReference;
        private final Activity activity;

        updateAndLoadPortfoliosAsyncTask(PortfoliosActivity activity) {
            activityWeakReference = new WeakReference<PortfoliosActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PortfoliosActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {

            PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);

            ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(activity);
            IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(activity);

            Date date = new Date();
            int month = Integer.valueOf(Utils.monthDateFormat.format(date));
            int year = Integer.valueOf(Utils.yearDateFormat.format(date));

            Cursor portfolioCursor = portfolioDatabaseHelper.readAllData();
            while (portfolioCursor.moveToNext()) {
                int portfolioId = portfolioCursor.getInt(0);

                int sumTotalExpense = 0;
                int sumTotalIncome = 0;

                Cursor cursorSumTotalExpenses = expensesDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);
                Cursor cursorSumTotalIncome = incomeDatabaseHelper.sumAllEntriesByPortfolioId(portfolioId, month, year);

                if (cursorSumTotalExpenses.getCount() != 0) {
                    while (cursorSumTotalExpenses.moveToNext()) {
                        sumTotalExpense = cursorSumTotalExpenses.getInt(0);
                    }
                }

                if (cursorSumTotalIncome.getCount() != 0) {
                    while (cursorSumTotalIncome.moveToNext()) {
                        sumTotalIncome = cursorSumTotalIncome.getInt(0);
                    }
                }

                int revenuesBalances = 0;
                int expensesBalances = 0;

                BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(activity);
                Cursor balanceCursor = balanceDatabaseHelper.sumAllByPortfolioId(portfolioId);
                while (balanceCursor.moveToNext()) {
                    revenuesBalances = balanceCursor.getInt(0);
                    expensesBalances = balanceCursor.getInt(1);
                }

                sumTotalExpense = sumTotalExpense + expensesBalances;
                sumTotalIncome = sumTotalIncome + revenuesBalances;

                int saldo = sumTotalIncome - sumTotalExpense;
                portfolioDatabaseHelper.updatePortfolio(portfolioId, sumTotalIncome, sumTotalExpense, saldo);
            }

            return booleans[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PortfoliosActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            PortfoliosActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            new LoadPortfoliosAsyncTask(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, b);
        }
    }
}