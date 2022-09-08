package de.xyzerstudios.moneymanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.AboutUsFragment;
import de.xyzerstudios.moneymanager.fragments.DashboardFragment;
import de.xyzerstudios.moneymanager.fragments.DonateFragment;
import de.xyzerstudios.moneymanager.fragments.YourDataFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.drawermenu.DrawerAdapter;
import de.xyzerstudios.moneymanager.utils.drawermenu.DrawerItem;
import de.xyzerstudios.moneymanager.utils.drawermenu.HeadingItem;
import de.xyzerstudios.moneymanager.utils.drawermenu.SimpleItem;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final String tag = "HomeActivity";

    public static final int POS_PORTFOLIOS = 2;
    public static final int POS_BILANZEN = 3;
    public static final int POS_BUDGET = 4;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_DONATE = 6;
    private static final int POS_SETTINGS = 7;
    private static final int POS_YOUR_DATA = 9;
    private static final int POS_ABOUT_US = 10;

    private static final int RESOURCE_DASHBOARD = 0;
    private static final int RESOURCE_PORTFOLIOS = 1;
    private static final int RESOURCE_BILANZEN = 2;
    private static final int RESOURCE_BUDGET = 3;
    private static final int RESOURCE_DONATE = 4;
    private static final int RESOURCE_SETTINGS = 5;
    private static final int RESOURCE_YOUR_DATA = 6;
    private static final int RESOURCE_ABOUT_US = 7;

    private static final int RESOURCE_HEADING_GENERAL = 0;
    private static final int RESOURCE_HEADING_ACTIONS = 1;
    private static final int RESOURCE_HEADING_OTHER = 2;

    boolean hasBackPressed = false;

    private String[] simpleItemTitles;
    private Drawable[] simpleItemIcons;
    private String[] headingItemTitles;

    private SlidingRootNav slidingRootNav;
    private DrawerAdapter adapter;
    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private Configuration mPrevConfig;

    private ReviewInfo reviewInfo;
    private ReviewManager reviewManager;

    public static boolean darkModeChanged(Configuration mPrevConfig, Configuration configuration) {
        if (mPrevConfig != null) {
            boolean prevDarkModeTrue = (mPrevConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            boolean newDarkModeTrue = (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            return prevDarkModeTrue != newDarkModeTrue;
        } else {
            return true;
        }
    }

    public static boolean isOnDarkMode(Configuration configuration) {
        return (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activateReviewInfo();
        addCreatedCount();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withRootViewScale(0.84f)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        loadSlidingRootNav();

        if (getCreatedCount() % 10 == 0) {
            startReviewFlow();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
    }

    public void loadSlidingRootNav() {

        simpleItemTitles = loadSimpleItemTitles();
        simpleItemIcons = loadSimpleItemIcons();
        headingItemTitles = loadHeadingItemTitles();

        List<DrawerItem> drawerItems = new ArrayList<>();

        drawerItems.add(createNewHeadingItem(RESOURCE_HEADING_GENERAL));
        drawerItems.add(createNewDrawerItem(RESOURCE_DASHBOARD).hideNotification().setChecked(true));
        drawerItems.add(createNewDrawerItem(RESOURCE_PORTFOLIOS).hideNotification());
        drawerItems.add(createNewDrawerItem(RESOURCE_BILANZEN).hideNotification());
        drawerItems.add(createNewDrawerItem(RESOURCE_BUDGET).hideNotification());

        drawerItems.add(createNewHeadingItem(RESOURCE_HEADING_ACTIONS));

        drawerItems.add(createNewDrawerItem(RESOURCE_DONATE).showNotification());
        drawerItems.add(createNewDrawerItem(RESOURCE_SETTINGS).hideNotification());

        drawerItems.add(createNewHeadingItem(RESOURCE_HEADING_OTHER));

        drawerItems.add(createNewDrawerItem(RESOURCE_YOUR_DATA).hideNotification());
        drawerItems.add(createNewDrawerItem(RESOURCE_ABOUT_US).hideNotification());

        adapter = new DrawerAdapter(drawerItems);
        adapter.setListener(this);

        recyclerView = findViewById(R.id.list);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setSelected(POS_DASHBOARD);

        Fragment dashboardFragment = new DashboardFragment();
        showFragment(dashboardFragment);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadSlidingRootNav();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configurationChanged(newConfig);
        mPrevConfig = new Configuration(newConfig);
    }

    protected void configurationChanged(Configuration newConfig) {
        if (darkModeChanged(mPrevConfig, newConfig)) {
            Fragment dashboardFragment = new DashboardFragment();
            showFragment(dashboardFragment);
            recreate();
            Log.d("NIGHT_MODE", "Night mode changed. Night mode: " + isOnDarkMode(newConfig));
        }
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private int getCreatedCount() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_COUNT_HOMEACTIVITY_CREATED, 0);
    }

    private void addCreatedCount() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int newCount = getCreatedCount() + 1;
        editor.putInt(Utils.SHARED_PREFS_COUNT_HOMEACTIVITY_CREATED, newCount);
        editor.apply();
        Log.d(tag, "HomeActivity created count changed to: " + newCount);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in_fast,
                        R.anim.fade_out_fast
                )
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private SimpleItem createNewDrawerItem(int positionOfResourcesInArray) {
        return new SimpleItem(simpleItemIcons[positionOfResourcesInArray], simpleItemTitles[positionOfResourcesInArray],
                this, positionOfResourcesInArray == RESOURCE_BUDGET)
                .withIconTint(color(R.color.ui_side_menu))
                .withTextTint(color(R.color.ui_side_menu))
                .withSelectedIconTint(color(R.color.ui_side_menu))
                .withSelectedTextTint(color(R.color.ui_side_menu));
    }

    private HeadingItem createNewHeadingItem(int positionOfResourcesInArray) {
        return new HeadingItem(headingItemTitles[positionOfResourcesInArray])
                .withTextTint(getColor(R.color.ui_text_faded));
    }

    private String[] loadSimpleItemTitles() {
        return getResources().getStringArray(R.array.srn_titles);
    }

    private String[] loadHeadingItemTitles() {
        return getResources().getStringArray(R.array.srn_headings);
    }

    private Drawable[] loadSimpleItemIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.srn_icons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onBackPressed() {
        if (hasBackPressed) {
            finish();
        } else {
            hasBackPressed = true;
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hasBackPressed = false;
                }
            }, 1700);
        }
    }

    @Override
    public void onItemSelected(int position) {
        switch (position) {
            case POS_DASHBOARD:
                loadPortfolioIdFromSharedPrefs();
                Fragment dashboardFragment = new DashboardFragment();
                showFragment(dashboardFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_PORTFOLIOS:
                slidingRootNav.closeMenu();
                Intent intent = new Intent(HomeActivity.this, PortfoliosActivity.class);
                intent.putExtra("choosePortfolio", false);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case POS_BILANZEN:
                slidingRootNav.closeMenu();
                Intent intent2 = new Intent(HomeActivity.this, BalancesActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case POS_BUDGET:
                slidingRootNav.closeMenu();
                Intent intent3 = new Intent(HomeActivity.this, BudgetsActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case POS_DONATE:
                Fragment donateFragment = new DonateFragment();
                showFragment(donateFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_SETTINGS:
                slidingRootNav.closeMenu();
                Intent intent4 = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case POS_ABOUT_US:
                Fragment aboutUsFragment = new AboutUsFragment();
                showFragment(aboutUsFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_YOUR_DATA:
                Fragment yourDataFragment = new YourDataFragment();
                showFragment(yourDataFragment);
                slidingRootNav.closeMenu();
                break;
        }
    }

    private void activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            } else {
                Log.e(tag, "Review Dialog failed to start.");
            }
        });
    }

    private void startReviewFlow() {
        Log.d(tag, "Trying to start review flow.");
        if (reviewInfo != null) {
            reviewManager.launchReviewFlow(this, reviewInfo);
        }
    }

}