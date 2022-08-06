package de.xyzerstudios.moneymanager.activities;

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

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.AboutUsFragment;
import de.xyzerstudios.moneymanager.fragments.BalancesFragment;
import de.xyzerstudios.moneymanager.fragments.BudgetsFragment;
import de.xyzerstudios.moneymanager.fragments.DashboardFragment;
import de.xyzerstudios.moneymanager.fragments.DonateFragment;
import de.xyzerstudios.moneymanager.fragments.PortfoliosFragment;
import de.xyzerstudios.moneymanager.fragments.PremiumFragment;
import de.xyzerstudios.moneymanager.utils.PublicValues;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.drawermenu.DrawerAdapter;
import de.xyzerstudios.moneymanager.utils.drawermenu.HeadingItem;
import de.xyzerstudios.moneymanager.utils.drawermenu.SimpleItem;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_DASHBOARD = 1;
    private static final int POS_PORTFOLIOS = 2;
    private static final int POS_BILANZEN = 3;
    private static final int POS_BUDGET = 4;
    private static final int POS_PREMIUM = 6;
    private static final int POS_DONATE = 7;
    private static final int POS_ABOUT_US = 9;

    private static final int RESOURCE_DASHBOARD = 0;
    private static final int RESOURCE_PORTFOLIOS = 1;
    private static final int RESOURCE_BILANZEN = 2;
    private static final int RESOURCE_BUDGET = 3;
    private static final int RESOURCE_PREMIUM = 4;
    private static final int RESOURCE_DONATE = 5;
    private static final int RESOURCE_ABOUT_US = 6;

    private static final int RESOURCE_HEADING_GENERAL = 0;
    private static final int RESOURCE_HEADING_ACTIONS = 1;
    private static final int RESOURCE_HEADING_OTHER = 2;

    boolean hasBackPressed = false;

    private String[] simpleItemTitles;
    private Drawable[] simpleItemIcons;
    private String[] headingItemTitles;

    private SlidingRootNav slidingRootNav;
    private DrawerAdapter adapter;

    private Toolbar toolbar;
    private Configuration mPrevConfig;


    public static boolean darkModeChanged(Configuration mPrevConfig, Configuration configuration) {
        if (mPrevConfig != null) {
            boolean prevDarkModeTrue = (mPrevConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            boolean newDarkModeTrue = (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            return prevDarkModeTrue != newDarkModeTrue;
        } else {
            return true;
        }
    }

    public static boolean isOnLightMode(Configuration configuration) {
        return (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO;
    }

    public static boolean isOnDarkMode(Configuration configuration) {
        return (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withRootViewScale(0.8f)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        loadSlidingRootNav();

    }


    private void loadSlidingRootNav() {

        simpleItemTitles = loadSimpleItemTitles();
        simpleItemIcons = loadSimpleItemIcons();
        headingItemTitles = loadHeadingItemTitles();

        adapter = new DrawerAdapter(Arrays.asList(
                createNewHeadingItem(RESOURCE_HEADING_GENERAL),

                createNewDrawerItem(RESOURCE_DASHBOARD).hideNotification().setChecked(true),
                createNewDrawerItem(RESOURCE_PORTFOLIOS).hideNotification(),
                createNewDrawerItem(RESOURCE_BILANZEN).hideNotification(),
                createNewDrawerItem(RESOURCE_BUDGET).hideNotification(),

                createNewHeadingItem(RESOURCE_HEADING_ACTIONS),

                createNewDrawerItem(RESOURCE_PREMIUM).showNotification(),
                createNewDrawerItem(RESOURCE_DONATE).hideNotification(),

                createNewHeadingItem(RESOURCE_HEADING_OTHER),

                createNewDrawerItem(RESOURCE_ABOUT_US).hideNotification()));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);


        adapter.setSelected(POS_DASHBOARD);

        Fragment dashboardFragment = new DashboardFragment();
        showFragment(dashboardFragment);
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
        return new SimpleItem(simpleItemIcons[positionOfResourcesInArray], simpleItemTitles[positionOfResourcesInArray])
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
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case POS_BILANZEN:
                Fragment bilanzenFragment = new BalancesFragment();
                showFragment(bilanzenFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_BUDGET:
                Fragment budgetsFragment = new BudgetsFragment();
                showFragment(budgetsFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_PREMIUM:
                Fragment premiumFragment = new PremiumFragment();
                showFragment(premiumFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_DONATE:
                Fragment donateFragment = new DonateFragment();
                showFragment(donateFragment);
                slidingRootNav.closeMenu();
                break;
            case POS_ABOUT_US:
                Fragment aboutUsFragment = new AboutUsFragment();
                showFragment(aboutUsFragment);
                slidingRootNav.closeMenu();
                break;
        }
    }
}