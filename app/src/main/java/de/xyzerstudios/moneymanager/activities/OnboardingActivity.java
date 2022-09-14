package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnLeftOutListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.WaitFragment;

public class OnboardingActivity extends AppCompatActivity {

    private boolean openedFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Bundle bundle = getIntent().getExtras();
        try {
            openedFirstTime = bundle.getBoolean("openedFirstTime");
        } catch (Exception e) {
            finish();
            throw new IllegalArgumentException(this.toString() + " must pass openedFirstTime (boolean) argument using intent.putExtra");
        }

        PaperOnboardingPage scr1 = new PaperOnboardingPage(getString(R.string.menu_title_portfolios),
                getString(R.string.portfolios_explanation),
                Color.parseColor("#678FB4"), R.drawable.nothing, R.drawable.smallicon_selector);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(getString(R.string.menu_title_statistics),
                getString(R.string.statistics_explanation),
                Color.parseColor("#65B0B4"), R.drawable.nothing, R.drawable.smallicon_selector);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(getString(R.string.menu_title_bilanzen),
                getString(R.string.balance_explanation),
                Color.parseColor("#9B90BC"), R.drawable.nothing, R.drawable.smallicon_selector);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(getString(R.string.menu_title_budgets),
                getString(R.string.budgets_explanation),
                Color.parseColor("#678FB4"), R.drawable.nothing, R.drawable.smallicon_selector);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr3);
        elements.add(scr2);
        elements.add(scr4);

        PaperOnboardingFragment paperOnboardingFragment = PaperOnboardingFragment.newInstance(elements);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containerOnboarding, paperOnboardingFragment);
        fragmentTransaction.commit();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(elements.get(0).getBgColor());

        paperOnboardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                if (openedFirstTime) {
                    WaitFragment waitFragment = new WaitFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.containerOnboarding, waitFragment);
                    fragmentTransaction.commit();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getColor(R.color.ui_light_background));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    Intent intent = new Intent(OnboardingActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        paperOnboardingFragment.setOnLeftOutListener(new PaperOnboardingOnLeftOutListener() {
            @Override
            public void onLeftOut() {
                if (!openedFirstTime) {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        paperOnboardingFragment.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int from, int to) {
                int color = elements.get(to).getBgColor();
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        window.setStatusBarColor(color);
                    }
                }, 450);
            }
        });
    }
}