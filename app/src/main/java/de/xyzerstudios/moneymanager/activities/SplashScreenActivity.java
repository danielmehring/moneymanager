package de.xyzerstudios.moneymanager.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.currency.Currencies;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;

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
        initNotificationChannel();
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

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Notification Channel";
            String description = "Channel for a daily notification.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("dailyNotification", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startAsyncTask() {
        new splashScreenAsyncTask(this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
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
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean(Utils.SHARED_PREFS_IS_FIRST_START, true)) {
                Intent intent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
                intent.putExtra("openedFirstTime", true);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.SHARED_PREFS_IS_FIRST_START, false);
                editor.apply();
                finish();
                return;
            }
            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

}