package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.Categories;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView mmLogo, xsLogo;

    private BalanceDatabaseHelper balanceDatabase;
    private BalanceTurnoversDatabaseHelper balanceTurnoversDatabase;
    private ExpensesDatabaseHelper expensesDatabase;
    private IncomeDatabaseHelper incomeDatabase;
    private PortfolioDatabaseHelper portfolioDatabase;
    private CategoriesDatabaseHelper categoriesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initAndCreateDatabases();
        initGuiObjects();
        manipulateGuiObjects();
        startAsyncTask();
    }

    private void initAndCreateDatabases() {
        balanceDatabase = new BalanceDatabaseHelper(this);
        balanceTurnoversDatabase = new BalanceTurnoversDatabaseHelper(this);
        expensesDatabase = new ExpensesDatabaseHelper(this);
        incomeDatabase = new IncomeDatabaseHelper(this);
        portfolioDatabase = new PortfolioDatabaseHelper(this);
        categoriesDatabase = new CategoriesDatabaseHelper(this, this);

        balanceDatabase.getReadableDatabase();
        balanceTurnoversDatabase.getReadableDatabase();
        expensesDatabase.getReadableDatabase();
        incomeDatabase.getReadableDatabase();
        portfolioDatabase.getReadableDatabase();
        categoriesDatabase.getReadableDatabase();
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
        new splashScreenAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private class splashScreenAsyncTask extends AsyncTask<String, String, String> {
        private final WeakReference<SplashScreenActivity> activityWeakReference;

        splashScreenAsyncTask(SplashScreenActivity activity) {
            activityWeakReference = new WeakReference<SplashScreenActivity>(activity);
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
}