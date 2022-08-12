package de.xyzerstudios.moneymanager.asynctasks;

import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.utils.adapters.PortfolioChooseAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.adapters.PortfolioAdapter;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class LoadPortfoliosAsyncTask extends AsyncTask<Boolean, String, ArrayList<BalancePortfolioItem>> {

    private WeakReference<PortfoliosActivity> activityWeakReference;
    private PortfoliosActivity activity;
    private boolean choosePortfolio = false;

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
    protected ArrayList<BalancePortfolioItem> doInBackground(Boolean... booleans) {

        choosePortfolio = booleans[0];

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
        if (choosePortfolio)
            activity.portfolioAdapter = new PortfolioChooseAdapter(activity, activity, activity.portfolioItems);
        else
            activity.portfolioAdapter = new PortfolioAdapter(activity, activity, activity.portfolioItems, activity.loadPortfolioIdFromSharedPrefs());
        activity.portfolioRecyclerView.swapAdapter(activity.portfolioAdapter, false);

    }

}
