package de.xyzerstudios.moneymanager.asynctasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.utils.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.PortfolioAdapter;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class LoadPortfoliosAsyncTask extends AsyncTask<Integer, String, ArrayList<BalancePortfolioItem>> {

    private WeakReference<PortfoliosActivity> activityWeakReference;
    private PortfoliosActivity activity;

    public LoadPortfoliosAsyncTask(PortfoliosActivity activity) {
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
    protected ArrayList<BalancePortfolioItem> doInBackground(Integer... integers) {


        //Reading Name and Saldo of Portfolio
        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);
        Cursor cursor = portfolioDatabaseHelper.readAllData();

        if (cursor.getCount() == 0) {
            return new ArrayList<>();
        }

        ArrayList<BalancePortfolioItem> portfolioItems = new ArrayList<>();

        while (cursor.moveToNext()) {
            portfolioItems.add(new BalancePortfolioItem(cursor.getString(1), cursor.getInt(5), cursor.getInt(0)));
        }


        return portfolioItems;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        PortfoliosActivity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }
        Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(ArrayList<BalancePortfolioItem> a) {
        super.onPostExecute(a);
        PortfoliosActivity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }

        activity.swipeRefreshPortfolio.setRefreshing(false);
        activity.portfolioItems = a;
        activity.portfolioAdapter = new PortfolioAdapter(activity, activity, activity.portfolioItems, activity.loadPortfolioIdFromSharedPrefs());
        activity.portfolioRecyclerView.setAdapter(activity.portfolioAdapter);
        activity.portfolioAdapter.notifyDataSetChanged();

    }

}
