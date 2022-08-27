package de.xyzerstudios.moneymanager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Currencies;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView mmLogo, xsLogo;

    private BalanceDatabaseHelper balanceDatabase;
    private BalanceTurnoversDatabaseHelper balanceTurnoversDatabase;
    private ExpensesDatabaseHelper expensesDatabase;
    private IncomeDatabaseHelper incomeDatabase;
    private RepeatedIncomeDatabaseHelper repeatedIncomeDatabase;
    private PortfolioDatabaseHelper portfolioDatabase;
    private CategoriesDatabaseHelper categoriesDatabase;
    private BudgetsDatabaseHelper budgetsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initAndCreateDatabases();
        initGuiObjects();
        Currencies.init();
        manipulateGuiObjects();
        startAsyncTask();
    }

    private void initAndCreateDatabases() {
        balanceDatabase = new BalanceDatabaseHelper(this);
        balanceTurnoversDatabase = new BalanceTurnoversDatabaseHelper(this);
        expensesDatabase = new ExpensesDatabaseHelper(this);
        incomeDatabase = new IncomeDatabaseHelper(this);
        repeatedIncomeDatabase = new RepeatedIncomeDatabaseHelper(this);
        portfolioDatabase = new PortfolioDatabaseHelper(this);
        categoriesDatabase = new CategoriesDatabaseHelper(this, this);
        budgetsDatabase = new BudgetsDatabaseHelper(this);

        balanceDatabase.getReadableDatabase();
        balanceTurnoversDatabase.getReadableDatabase();
        expensesDatabase.getReadableDatabase();
        incomeDatabase.getReadableDatabase();
        repeatedIncomeDatabase.getReadableDatabase();
        portfolioDatabase.getReadableDatabase();
        categoriesDatabase.getReadableDatabase();
        budgetsDatabase.getReadableDatabase();
    }

    private void manipulateGuiObjects() {
        Animation animationFadeImageViewIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_textview);
        Animation animationFadeInMoveUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_move_up);
        mmLogo.startAnimation(animationFadeImageViewIn);
        xsLogo.startAnimation(animationFadeInMoveUp);
    }

    private void initGuiObjects() {
        mmLogo = findViewById(R.id.imageViewMmLogo);
        xsLogo = findViewById(R.id.imageViewXsLogo);
    }

    private void startAsyncTask() {
        new splashScreenAsyncTask(this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        new splashScreenNetworkAsyncTask(this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "en");
    }

    private class splashScreenAsyncTask extends AsyncTask<String, String, String> {
        private final WeakReference<SplashScreenActivity> activityWeakReference;
        private final Context context;

        splashScreenAsyncTask(SplashScreenActivity activity) {
            activityWeakReference = new WeakReference<SplashScreenActivity>(activity);
            context = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Thread.sleep(2200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private class splashScreenNetworkAsyncTask extends AsyncTask<String, String, String> {
        private final WeakReference<SplashScreenActivity> activityWeakReference;
        private final Context context;

        splashScreenNetworkAsyncTask(SplashScreenActivity activity) {
            activityWeakReference = new WeakReference<SplashScreenActivity>(activity);
            context = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String language = strings[0];

            ArrayList<String> allCurrencies = Currencies.getIsoCodes();
            ArrayList<String> mostUsedCurrencies = Currencies.getIsoCodesOfCurrenciesUsedMost();
            for (String isoCode : mostUsedCurrencies) {
                allCurrencies.remove(isoCode);
            }

            HashMap<String, String> translatedHashMap = new HashMap<>();
            final boolean isNetworkAvaiable = Utils.isNetworkAvailable(context);

            if (isNetworkAvaiable) {
                for (String isoCode : mostUsedCurrencies) {
                    try {
                        String name = translateUsingGoogle("de", language, Currencies.getIsoToName().get(isoCode));
                        translatedHashMap.put(isoCode, name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (String isoCode : allCurrencies) {
                    try {
                        String name = translateUsingGoogle("de", language, Currencies.getIsoToName().get(isoCode));
                        translatedHashMap.put(isoCode, name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            translatedHashMap.forEach((key, value) -> {
                Log.d("SplashScreen___", value);
            });

            Currencies.setIsoToNameTranslated(translatedHashMap);

            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SplashScreenActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        public String translateUsingGoogle(String langFrom, String langTo, String text) throws IOException {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .build();

            String urlStr = "https://script.google.com/macros/s/" +
                    "AKfycbwCUpWzH3lxoAOauRQxssx0hieNIy8_XkyfJONzCQwPVr6j-aylrkrMn34Kt4HgnculOQ/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;
            Request request = new Request.Builder().url(urlStr)
                    .build();
            return okHttpClient.newCall(request).execute().body().string();
        }
    }
}