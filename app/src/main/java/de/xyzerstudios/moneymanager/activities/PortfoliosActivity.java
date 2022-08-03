package de.xyzerstudios.moneymanager.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddPortfolioActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.adapters.PortfolioAdapter;
import de.xyzerstudios.moneymanager.utils.Utils;

public class PortfoliosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton buttonPortfolioGoBack;
    private LinearLayout buttonAddNewPortfolioActivity;

    public RecyclerView portfolioRecyclerView;
    public RecyclerView.Adapter portfolioAdapter;
    public SwipeRefreshLayout swipeRefreshPortfolio;
    public static ArrayList<BalancePortfolioItem> portfolioItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolios);

        buttonPortfolioGoBack = findViewById(R.id.buttonPortfolioGoBack);
        buttonAddNewPortfolioActivity = findViewById(R.id.buttonAddNewPortfolioActivity);

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

        portfolioRecyclerView = findViewById(R.id.recyclerViewPortfoliosActivity);
        swipeRefreshPortfolio = findViewById(R.id.swipeRefreshPortfoliosActivity);

        portfolioItems = new ArrayList<>();

        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioAdapter = new PortfolioAdapter(this, this, portfolioItems, loadPortfolioIdFromSharedPrefs());

        portfolioRecyclerView.setAdapter(portfolioAdapter);

        swipeRefreshPortfolio.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadPortfoliosAsyncTask(PortfoliosActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs());
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new LoadPortfoliosAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            new LoadPortfoliosAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loadPortfolioIdFromSharedPrefs());
        }
    }

    public int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void savePortfolioId(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, id);
        editor.apply();
    }
}