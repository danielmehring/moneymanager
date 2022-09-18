package de.xyzerstudios.moneymanager.activities.edit;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.KeyboardFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class EditBudgetActivity extends AppCompatActivity implements KeyboardFragment.KeyboardListener {

    private final Utils utils = new Utils(this);
    private int budgetEntryId;
    private ImageView closeActivityAddBudget, editBudget;
    private FrameLayout chooserBudgetCategory;
    private TextView textViewBudgetCategory, textViewBudgetAmount;
    private LinearLayout displayCategoryColorBudget, keyboardContainer;
    private FrameLayout amountChooser;
    private FloatingActionButton deleteBudgetEntry;
    private int categoryId = 9;
    private int amount = 0;
    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        budgetEntryId = bundle.getInt("budgetEntryId");
        initGui();
        setClickListeners();
        manipulateGui();
    }

    private void initGui() {
        closeActivityAddBudget = findViewById(R.id.closeActivityEditBudget);
        editBudget = findViewById(R.id.editBudget);
        chooserBudgetCategory = findViewById(R.id.chooserBudgetCategoryEdit);
        textViewBudgetCategory = findViewById(R.id.textViewBudgetCategoryEdit);
        textViewBudgetAmount = findViewById(R.id.textViewBudgetAmountEdit);
        displayCategoryColorBudget = findViewById(R.id.displayCategoryColorBudgetEdit);
        deleteBudgetEntry = findViewById(R.id.deleteBudgetEntry);

        keyboardContainer = findViewById(R.id.keyboardContainerBudgetEdit);
        amountChooser = findViewById(R.id.amountChooser);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerBudgetEdit, fragment);
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
            deleteBudgetEntry.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainer);
            keyboardContainer.setVisibility(View.GONE);
            deleteBudgetEntry.setVisibility(View.VISIBLE);
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

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(EditBudgetActivity.this);

                budgetsDatabaseHelper.updateEntry(budgetEntryId, amount);

                finish();
            }
        });

        deleteBudgetEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditBudgetActivity.this);
                builder.setMessage(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.delete_confirmation) + "</font>"))
                        .setPositiveButton(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.yes) + "</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(EditBudgetActivity.this);
                                budgetsDatabaseHelper.deleteEntry(budgetEntryId);
                                finish();
                            }
                        })
                        .setNegativeButton(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.cancel) + "</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setBackground(getDrawable(R.drawable.dialog_background))
                        .show();


            }
        });

    }


    private void manipulateGui() {
        BudgetsDatabaseHelper budgetsDatabaseHelper = new BudgetsDatabaseHelper(this);
        Cursor budgetCursor = budgetsDatabaseHelper.readEntryById(budgetEntryId);
        while (budgetCursor.moveToNext()) {
            categoryId = budgetCursor.getInt(1);
            amount = budgetCursor.getInt(2);
        }

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