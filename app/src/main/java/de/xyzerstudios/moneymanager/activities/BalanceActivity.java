package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseToBalanceActivity;
import de.xyzerstudios.moneymanager.activities.add.AddIncomeToBalanceActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.BalanceTurnoversAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.TurnoverItem;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;

public class BalanceActivity extends AppCompatActivity {

    private ImageButton buttonBalanceGoBack;
    private TextView balanceName, saldoOfBalance, turnoverTypeFilter;
    private LinearLayout buttonAddNewAmountToBalance, chooserTurnoverTypeFilter;
    private SwipeRefreshLayout swipeRefreshBalance;
    private RecyclerView recyclerViewBalance;
    private RecyclerView.Adapter recyclerAdapter;
    private ArrayList<TurnoverItem> turnoverItems;

    private int balanceId;
    private TurnoverType turnoverTypeFilters = TurnoverType.REVENUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        balanceId = bundle.getInt("balanceId");

        turnoverItems = new ArrayList<>();

        initGui();

        buttonBalanceGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonAddNewAmountToBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialogTurnover();
            }
        });

        chooserTurnoverTypeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialogDisplay();
            }
        });

        swipeRefreshBalance.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoadingAsyncTask(true);
            }
        });


        recyclerViewBalance.setHasFixedSize(true);
        recyclerViewBalance.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new BalanceTurnoversAdapter(this, this, turnoverItems);

        recyclerViewBalance.setAdapter(recyclerAdapter);

        reloadTypeChooser();
    }

    private void initGui() {
        buttonBalanceGoBack = findViewById(R.id.buttonBalanceGoBack);
        balanceName = findViewById(R.id.balanceName);
        saldoOfBalance = findViewById(R.id.saldoOfBalance);
        buttonAddNewAmountToBalance = findViewById(R.id.buttonAddNewAmountToBalance);
        swipeRefreshBalance = findViewById(R.id.swipeRefreshBalance);
        recyclerViewBalance = findViewById(R.id.recyclerViewBalance);
        chooserTurnoverTypeFilter = findViewById(R.id.chooserTurnoverTypeFilter);
        turnoverTypeFilter = findViewById(R.id.turnoverTypeFilter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startLoadingAsyncTask(true);
    }

    private void showBottomDialogTurnover() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_choose_turnover_type);

        LinearLayout buttonExpense = dialog.findViewById(R.id.buttonBSExpense);
        LinearLayout buttonRevenue = dialog.findViewById(R.id.buttonBSRevenue);

        buttonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(BalanceActivity.this, AddExpenseToBalanceActivity.class);
                intent.putExtra("balanceId", balanceId);
                startActivity(intent);
            }
        });

        buttonRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(BalanceActivity.this, AddIncomeToBalanceActivity.class);
                intent.putExtra("balanceId", balanceId);
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showBottomDialogDisplay() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_choose_display_type);

        LinearLayout buttonExpense = dialog.findViewById(R.id.buttonBSExpenseDisplay);
        LinearLayout buttonRevenue = dialog.findViewById(R.id.buttonBSRevenueDisplay);

        buttonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                turnoverTypeFilters = TurnoverType.EXPENSE;
                reloadTypeChooser();
                startLoadingAsyncTask(false);
            }
        });

        buttonRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                turnoverTypeFilters = TurnoverType.REVENUE;
                reloadTypeChooser();
                startLoadingAsyncTask(false);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void reloadTypeChooser() {
        turnoverTypeFilter.setText(turnoverTypeFilters == TurnoverType.EXPENSE ? getString(R.string.expenses) : getString(R.string.revenue));
    }

    private void startLoadingAsyncTask(boolean reloadNameAndSaldo) {
        new BalanceActivity.loadBalanceAsyncTask(this, reloadNameAndSaldo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, balanceId);
    }

    private class loadBalanceAsyncTask extends AsyncTask<Integer, String, ArrayList<TurnoverItem>> {
        private final WeakReference<BalanceActivity> activityWeakReference;
        private final BalanceActivity activity;
        private Utils utils;
        private final boolean reloadNameAndSaldo;
        private final TurnoverType turnoverTypeFilter;

        loadBalanceAsyncTask(BalanceActivity activity, boolean reloadNameAndSaldo) {
            activityWeakReference = new WeakReference<BalanceActivity>(activity);
            this.activity = activity;
            this.reloadNameAndSaldo = reloadNameAndSaldo;
            utils = new Utils(BalanceActivity.this);
            this.turnoverTypeFilter = BalanceActivity.this.turnoverTypeFilters;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BalanceActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected ArrayList<TurnoverItem> doInBackground(Integer... integers) {
            int balanceId = integers[0];
            int saldoAmount = 0;
            String textBalanceName = "";
            String textSaldo = "";
            ArrayList<TurnoverItem> turnoverItems = new ArrayList<>();


            BalanceTurnoversDatabaseHelper turnoverDatabase = new BalanceTurnoversDatabaseHelper(BalanceActivity.this);
            Cursor cursorTurnover = turnoverDatabase.readEntriesByBalanceIdAndType(balanceId, turnoverTypeFilter);

            if (cursorTurnover.getCount() != 0) {

                CategoriesDatabaseHelper categoriesDatabase = new CategoriesDatabaseHelper(BalanceActivity.this, BalanceActivity.this);

                try {
                    while (cursorTurnover.moveToNext()) {
                        TurnoverType turnoverType = cursorTurnover.getInt(5) == 1 ? TurnoverType.EXPENSE : TurnoverType.REVENUE;
                        if (turnoverType == TurnoverType.EXPENSE) {
                            int entryId = cursorTurnover.getInt(0);
                            String name = cursorTurnover.getString(2);
                            int amount = cursorTurnover.getInt(4);
                            SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                            Date dateObj = simpleDateFormat.parse(cursorTurnover.getString(3));
                            String date = Utils.timestampDateDisplayFormat.format(dateObj);
                            String paymentMethodCode = cursorTurnover.getString(7);
                            int categoryId = cursorTurnover.getInt(6);
                            Cursor categoryCursor = categoriesDatabase.readCategoryById(categoryId);
                            String category = "";
                            while (categoryCursor.moveToNext())
                                category = categoryCursor.getString(1);

                            turnoverItems.add(new TurnoverItem(entryId, name, amount, date, paymentMethodCode, category));
                        } else {
                            int entryId = cursorTurnover.getInt(0);
                            String name = cursorTurnover.getString(2);
                            int amount = cursorTurnover.getInt(4);
                            SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                            Date dateObj = simpleDateFormat.parse(cursorTurnover.getString(3));
                            String date = Utils.timestampDateDisplayFormat.format(dateObj);
                            int categoryId = cursorTurnover.getInt(6);
                            Cursor categoryCursor = categoriesDatabase.readCategoryById(categoryId);
                            String category = "";
                            while (categoryCursor.moveToNext())
                                category = categoryCursor.getString(1);

                            turnoverItems.add(new TurnoverItem(entryId, name, amount, date, category));
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            if (!reloadNameAndSaldo)
                return turnoverItems;

            int expenses = 0;
            int revenues = 0;

            Cursor expenseCursor = turnoverDatabase.sumEntriesGroupedByType(balanceId, TurnoverType.EXPENSE);
            while (expenseCursor.moveToNext()) expenses = expenseCursor.getInt(0);

            Cursor revenueCursor = turnoverDatabase.sumEntriesGroupedByType(balanceId, TurnoverType.REVENUE);
            while (revenueCursor.moveToNext()) revenues = revenueCursor.getInt(0);

            int saldo = revenues - expenses;

            BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(activity);
            balanceDatabaseHelper.updateBalance(balanceId, revenues, expenses, saldo);

            Cursor cursor = balanceDatabaseHelper.readBalanceById(balanceId);
            while (cursor.moveToNext()) {
                textBalanceName = cursor.getString(1);
                saldoAmount = cursor.getInt(6);
            }

            if (saldoAmount < 0) {
                saldoAmount = saldoAmount * (-1);
                textSaldo = "- " + utils.formatCurrency(saldoAmount);
            } else {
                textSaldo = "+ " + utils.formatCurrency(saldoAmount);
            }
            publishProgress(textBalanceName, textSaldo);

            return turnoverItems;
        }

        @Override
        protected void onProgressUpdate(String... strings) {
            super.onProgressUpdate(strings);
            BalanceActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            balanceName.setText(strings[0]);
            saldoOfBalance.setText(strings[1]);
            if (strings[1].contains("+"))
                saldoOfBalance.setTextColor(getColor(R.color.ui_money_text_green));
            else
                saldoOfBalance.setTextColor(getColor(R.color.ui_money_text_red));
        }

        @Override
        protected void onPostExecute(ArrayList<TurnoverItem> a) {
            super.onPostExecute(a);
            BalanceActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.turnoverItems = a;
            activity.recyclerAdapter = new BalanceTurnoversAdapter(activity, activity, a);
            activity.recyclerViewBalance.swapAdapter(activity.recyclerAdapter, false);
            activity.swipeRefreshBalance.setRefreshing(false);
        }
    }
}