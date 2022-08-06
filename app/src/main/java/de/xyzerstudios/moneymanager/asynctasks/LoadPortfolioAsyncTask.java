package de.xyzerstudios.moneymanager.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.DashboardFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class LoadPortfolioAsyncTask extends AsyncTask<Integer, String, String> {

    private WeakReference<Activity> activityWeakReference;
    private Activity activity;
    private DashboardFragment dashboardFragment;

    public LoadPortfolioAsyncTask(Activity activity, DashboardFragment dashboardFragment) {
        activityWeakReference = new WeakReference<Activity>(activity);
        this.activity = activity;
        this.dashboardFragment = dashboardFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

    }

    @Override
    protected String doInBackground(Integer... integers) {


        int portfolioId = integers[0];

        //Reading Name, and Turnovers of Portfolio
        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);
        Cursor cursor = portfolioDatabaseHelper.readPortfolioById(portfolioId);

        String name = "";
        int saldo = 0;
        int revenuesSum = 0;
        int expensesSum = 0;

        if (cursor.getCount() == 0) {
            return "";
        }
        while (cursor.moveToNext()) {
            name = cursor.getString(1);
            revenuesSum = cursor.getInt(3);
            expensesSum = cursor.getInt(4);
            saldo = cursor.getInt(5);
        }

        return name + ";" + saldo + ";" + revenuesSum + ";" + expensesSum;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }
        Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }
        String[] strings = s.split(";");
        dashboardFragment.portfolioNameDisplay.setText(strings[0]);
        int saldo = Integer.valueOf(strings[1]);
        int revenue = Integer.valueOf(strings[2]);
        int expenses = Integer.valueOf(strings[3]);
        Utils utils = new Utils();
        if (saldo < 0) {
            saldo = saldo * (-1);
            dashboardFragment.dashboardSaldo.setText("- " + utils.formatCurrency(saldo) );
        } else {
            dashboardFragment.dashboardSaldo.setText("+ " + utils.formatCurrency(saldo) );
        }

        dashboardFragment.dashboardIncome.setText("+ " + utils.formatCurrency(revenue) );

        dashboardFragment.dashboardExpenses.setText("- " + utils.formatCurrency(expenses) );

        dashboardFragment.centerTextIncomeChart.setText(utils.formatCurrency(revenue) );
        dashboardFragment.centerTextExpensesChart.setText(utils.formatCurrency(expenses) );
    }

}
