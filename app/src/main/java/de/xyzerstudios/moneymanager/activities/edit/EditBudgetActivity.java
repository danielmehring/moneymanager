package de.xyzerstudios.moneymanager.activities.edit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class EditBudgetActivity extends AppCompatActivity {

    private int budgetEntryId;

    private final Utils utils = new Utils(this);
    private ImageView closeActivityAddBudget, editBudget;
    private FrameLayout chooserBudgetCategory;
    private TextView textViewBudgetCategory, textViewBudgetAmount;
    private LinearLayout displayCategoryColorBudget, deleteBudgetEntry;
    private EditText editTextBudgetAmount;
    private int categoryId = 9;
    private int amount = 0;
    private Date date;

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
        initObjects();
        setClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    private void initObjects() {
        date = new Date();
    }

    private void initGui() {
        closeActivityAddBudget = findViewById(R.id.closeActivityEditBudget);
        editBudget = findViewById(R.id.editBudget);
        chooserBudgetCategory = findViewById(R.id.chooserBudgetCategoryEdit);
        textViewBudgetCategory = findViewById(R.id.textViewBudgetCategoryEdit);
        textViewBudgetAmount = findViewById(R.id.textViewBudgetAmountEdit);
        displayCategoryColorBudget = findViewById(R.id.displayCategoryColorBudgetEdit);
        editTextBudgetAmount = findViewById(R.id.editTextBudgetAmountEdit);
        deleteBudgetEntry = findViewById(R.id.deleteBudgetEntry);
    }

    private void setClickListeners() {
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

    private void setOtherListeners() {

        editTextBudgetAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {


                    String s = amount + "";

                    if (i == KeyEvent.KEYCODE_DEL)
                        amount = amount / 10;

                    if (s.length() >= 12)
                        return false;

                    switch (i) {
                        case KeyEvent.KEYCODE_0:
                            amount = amount * 10;
                            break;
                        case KeyEvent.KEYCODE_1:
                            amount = amount * 10 + 1;
                            break;
                        case KeyEvent.KEYCODE_2:
                            amount = amount * 10 + 2;
                            break;
                        case KeyEvent.KEYCODE_3:
                            amount = amount * 10 + 3;
                            break;
                        case KeyEvent.KEYCODE_4:
                            amount = amount * 10 + 4;
                            break;
                        case KeyEvent.KEYCODE_5:
                            amount = amount * 10 + 5;
                            break;
                        case KeyEvent.KEYCODE_6:
                            amount = amount * 10 + 6;
                            break;
                        case KeyEvent.KEYCODE_7:
                            amount = amount * 10 + 7;
                            break;
                        case KeyEvent.KEYCODE_8:
                            amount = amount * 10 + 8;
                            break;
                        case KeyEvent.KEYCODE_9:
                            amount = amount * 10 + 9;
                            break;
                    }
                    textViewBudgetAmount.setText(utils.formatCurrency(amount));
                }
                return false;
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

}