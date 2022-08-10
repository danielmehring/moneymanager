package de.xyzerstudios.moneymanager.activities.add;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class AddBudgetActivity extends AppCompatActivity {

    private final Utils utils = new Utils();
    private ImageView closeActivityAddBudget, addBudget;
    private FrameLayout chooserBudgetCategory;
    private TextView textViewBudgetCategory, textViewBudgetAmount;
    private LinearLayout displayCategoryColorBudget;
    private EditText editTextBudgetAmount;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

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
        closeActivityAddBudget = findViewById(R.id.closeActivityAddBudget);
        addBudget = findViewById(R.id.addBudget);
        chooserBudgetCategory = findViewById(R.id.chooserBudgetCategory);
        textViewBudgetCategory = findViewById(R.id.textViewBudgetCategory);
        textViewBudgetAmount = findViewById(R.id.textViewBudgetAmount);
        displayCategoryColorBudget = findViewById(R.id.displayCategoryColorBudget);
        editTextBudgetAmount = findViewById(R.id.editTextBudgetAmount);
    }

    private void setClickListeners() {

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