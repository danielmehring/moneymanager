package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddBalanceActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.adapters.BalanceAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.BudgetsAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalanceItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.BudgetItem;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;

public class BalancesActivity extends AppCompatActivity {

    private ImageButton buttonBalancesGoBack;
    private LinearLayout buttonAddNewBalance;
    private RecyclerView recyclerViewBalances;
    private BalanceAdapter balanceAdapter;
    private SwipeRefreshLayout swipeRefreshBalance;

    public ArrayList<BalanceItem> balanceItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);

        buttonBalancesGoBack = findViewById(R.id.buttonBalancesGoBack);
        buttonAddNewBalance = findViewById(R.id.buttonAddNewBalance);
        recyclerViewBalances = findViewById(R.id.recyclerViewBalances);
        swipeRefreshBalance = findViewById(R.id.swipeRefreshBalances);

        balanceItems = new ArrayList<>();

        recyclerViewBalances.setHasFixedSize(true);
        recyclerViewBalances.setLayoutManager(new LinearLayoutManager(this));
        balanceAdapter = new BalanceAdapter(this, this, balanceItems);

        recyclerViewBalances.setAdapter(balanceAdapter);


        swipeRefreshBalance.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshBalance.setRefreshing(false);
            }
        });

        buttonAddNewBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BalancesActivity.this, AddBalanceActivity.class);
                startActivity(intent);
            }
        });

        buttonBalancesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadBalances();
    }

    private void loadBalances() {
        new balancesAsyncTask(BalancesActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
    }

    public class balancesAsyncTask extends AsyncTask<Integer, String, ArrayList<BalanceItem>> {
        private final WeakReference<BalancesActivity> activityWeakReference;

        balancesAsyncTask(BalancesActivity activity) {
            activityWeakReference = new WeakReference<BalancesActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BalancesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected ArrayList<BalanceItem> doInBackground(Integer... integers) {
            ArrayList<BalanceItem> balanceItems = new ArrayList<>();

            BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(BalancesActivity.this);
            Cursor cursor = balanceDatabaseHelper.readAllData();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);

                int revenue = 0;
                int expenses = 0;

                BalanceTurnoversDatabaseHelper balanceTurnovers = new BalanceTurnoversDatabaseHelper(getBaseContext());

                Cursor revenueCursor = balanceTurnovers.sumEntriesGroupedByType(id, TurnoverType.REVENUE);
                while (revenueCursor.moveToNext()) revenue = revenueCursor.getInt(0);

                Cursor expensesCursor = balanceTurnovers.sumEntriesGroupedByType(id, TurnoverType.EXPENSE);
                while (expensesCursor.moveToNext()) expenses = expensesCursor.getInt(0);

                int saldo = revenue - expenses;

                balanceItems.add(new BalanceItem(title, saldo, id));
            }


            return balanceItems;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            BalancesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(ArrayList<BalanceItem> a) {
            super.onPostExecute(a);
            BalancesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.balanceItems = a;
            activity.balanceAdapter = new BalanceAdapter(BalancesActivity.this, BalancesActivity.this, a);
            activity.recyclerViewBalances.swapAdapter(activity.balanceAdapter, false);
            activity.swipeRefreshBalance.setRefreshing(false);
        }
    }
}