package de.xyzerstudios.moneymanager.activities.add;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.fragments.KeyboardFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class AddBudgetActivity extends AppCompatActivity implements KeyboardFragment.KeyboardListener {

    private final Utils utils = new Utils(this);
    private ImageView closeActivityAddBudget, addBudget;
    private FrameLayout chooserBudgetCategory;
    private TextView textViewBudgetCategory, textViewBudgetAmount;
    private LinearLayout displayCategoryColorBudget, keyboardContainer;
    private FrameLayout amountChooser;
    private int categoryId = 9;
    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            String name = result.getData().getStringExtra("categoryName");
                            int color = result.getData().getIntExtra("categoryColor", getColor(R.color.ui_text_faded));
                            categoryId = result.getData().getIntExtra("categoryId", 9);
                            textViewBudgetCategory.setText(name);

                            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
                            backgroundDrawableIndicator.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

                            displayCategoryColorBudget.setBackground(backgroundDrawableIndicator);
                        }
                    }
                }
            });
    private int amount = 0;
    private Date date;
    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        initGui();
        initObjects();
        setClickListeners();
        manipulateGui();
    }

    private void initObjects() {
        date = new Date();
    }

    private void initGui() {
        closeActivityAddBudget = findViewById(R.id.closeActivityAddBudget);
        addBudget = findViewById(R.id.addBudget);
        chooserBudgetCategory = findViewById(R.id.chooserBudgetCategory);
        textViewBudgetCategory = findViewById(R.id.textViewBudgetCategory);
        textViewBudgetAmount = findViewById(R.id.textViewBudgetAmount);
        displayCategoryColorBudget = findViewById(R.id.displayCategoryColorBudget);

        keyboardContainer = findViewById(R.id.keyboardContainerBudget);
        amountChooser = findViewById(R.id.amountChooser);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerBudget, fragment);
        fragmentTransaction.commit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((double) displayMetrics.heightPixels / 2.8));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        keyboardContainer.setLayoutParams(layoutParams);

        setSoftKeyboardOpened(softKeyboardOpened);
    }

    @Override
    public void onBackPressed() {
        if (softKeyboardOpened)
            setSoftKeyboardOpened(false);
        else
            super.onBackPressed();
    }

    private void setSoftKeyboardOpened(boolean opened) {
        softKeyboardOpened = opened;
        if (opened) {
            TransitionManager.beginDelayedTransition(keyboardContainer);
            keyboardContainer.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainer);
            keyboardContainer.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {

        amountChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!softKeyboardOpened)
                    setSoftKeyboardOpened(true);
            }
        });

        closeActivityAddBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooserBudgetCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBudgetActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                startForResult.launch(intent);
            }
        });

        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0)
                    return;

                BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(AddBudgetActivity.this);
                Cursor cursor = budgetsDatabaseHelper.readEntriesByCategoryId(categoryId);

                if (cursor.getCount() != 0) {
                    Toast.makeText(AddBudgetActivity.this, getString(R.string.cant_add_two_budgets_same_cat), Toast.LENGTH_SHORT).show();
                    return;
                }

                budgetsDatabaseHelper.addNewEntry(categoryId, amount);

                finish();
            }
        });

    }


    private void manipulateGui() {
        CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(this, this);
        Cursor categoryCursor = categoriesDatabaseHelper.readCategoryById(categoryId);
        while (categoryCursor.moveToNext()) {
            textViewBudgetCategory.setText(categoryCursor.getString(1));
            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
            backgroundDrawableIndicator.setColorFilter(categoryCursor.getInt(3), PorterDuff.Mode.SRC_ATOP);
            displayCategoryColorBudget.setBackground(backgroundDrawableIndicator);
        }

        textViewBudgetAmount.setText(utils.formatCurrency(amount));
    }

    @Override
    public void keyPressed(int key) {
        if (key == 11) {
            setSoftKeyboardOpened(false);
            return;
        }

        String s = amount + "";

        if (key == 10)
            amount = amount / 10;

        if (s.length() >= 12)
            return;

        switch (key) {
            case 0:
                amount = amount * 10;
                break;
            case 1:
                amount = amount * 10 + 1;
                break;
            case 2:
                amount = amount * 10 + 2;
                break;
            case 3:
                amount = amount * 10 + 3;
                break;
            case 4:
                amount = amount * 10 + 4;
                break;
            case 5:
                amount = amount * 10 + 5;
                break;
            case 6:
                amount = amount * 10 + 6;
                break;
            case 7:
                amount = amount * 10 + 7;
                break;
            case 8:
                amount = amount * 10 + 8;
                break;
            case 9:
                amount = amount * 10 + 9;
                break;
        }
        textViewBudgetAmount.setText(utils.formatCurrency(amount));
    }

}