package de.xyzerstudios.moneymanager.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.currency.Currencies;
import de.xyzerstudios.moneymanager.utils.adapters.CurrencyAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.CurrencyItem;

public class CurrenciesActivity extends AppCompatActivity {

    private ImageButton buttonCurrenciesGoBack;
    private RecyclerView recyclerViewCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies);

        buttonCurrenciesGoBack = findViewById(R.id.buttonCurrenciesGoBack);
        recyclerViewCurrencies = findViewById(R.id.recyclerViewCurrencies);

        buttonCurrenciesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewCurrencies.setHasFixedSize(true);
        recyclerViewCurrencies.setLayoutManager(new LinearLayoutManager(this));

        loadCurrencyItemsIntoAdapter();

    }

    private void loadCurrencyItemsIntoAdapter() {
        new CurrenciesActivity.currenciesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "en");
    }

    private class currenciesAsyncTask extends AsyncTask<String, String, CurrencyAdapter> {

        private final WeakReference<CurrenciesActivity> activityWeakReference;
        private final CurrenciesActivity activity;

        currenciesAsyncTask(CurrenciesActivity activity) {
            activityWeakReference = new WeakReference<CurrenciesActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CurrenciesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected CurrencyAdapter doInBackground(String... strings) {
            ArrayList<CurrencyItem> currencyItems = new ArrayList<>();


            ArrayList<String> allCurrencies = Currencies.getIsoCodes();
            ArrayList<String> mostUsedCurrencies = Currencies.getIsoCodesOfCurrenciesUsedMost();

            for (String isoCode : mostUsedCurrencies) {
                allCurrencies.remove(isoCode);
                currencyItems.add(new CurrencyItem(isoCode, Currencies.getIsoToName().get(isoCode),
                        Currencies.getIsoToUnicode().getOrDefault(isoCode, "")));
            }

            allCurrencies.sort(new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    return s.compareTo(t1);
                }
            });

            for (String isoCode : allCurrencies) {
                currencyItems.add(new CurrencyItem(isoCode, Currencies.getIsoToName().get(isoCode),
                        Currencies.getIsoToUnicode().getOrDefault(isoCode, "")));
            }

            return new CurrencyAdapter(activity, currencyItems);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            CurrenciesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

        }

        @Override
        protected void onPostExecute(CurrencyAdapter ca) {
            super.onPostExecute(ca);
            CurrenciesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            this.activity.recyclerViewCurrencies.setAdapter(ca);
        }
    }
}