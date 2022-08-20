package de.xyzerstudios.moneymanager.activities.add;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;

public class AddIncomeToBalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public EditText editTextIncomeAmount, editTextIncomeName;
    public ImageView closeActivityAddIncome, addIncome;
    public TextView textViewIncomeAmount, textViewIncomeTimestamp, textViewIncomeCategory;
    public FrameLayout chooserIncomeTimestamp, chooserIncomeCategory;
    public LinearLayout displayCategoryColor;

    private final Utils utils = new Utils();

    private int balanceId;
    private int categoryId = 38;

    private int amount = 0;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_to_balance);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        balanceId = bundle.getInt("balanceId");

        initGui();
        initObjects();
        setClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    private void initObjects() {
        date = new Date();
    }

    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            String name = result.getData().getStringExtra("categoryName");
                            int color = result.getData().getIntExtra("categoryColor", getColor(R.color.ui_text_faded));
                            categoryId = result.getData().getIntExtra("categoryId", 9);
                            textViewIncomeCategory.setText(name);

                            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
                            backgroundDrawableIndicator.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

                            displayCategoryColor.setBackground(backgroundDrawableIndicator);
                        }
                    }
                }
            });

    private void initGui() {
        editTextIncomeAmount = findViewById(R.id.editTextIncomeAmount);
        editTextIncomeName = findViewById(R.id.editTextIncomeName);
        closeActivityAddIncome = findViewById(R.id.closeActivityAddIncome);
        addIncome = findViewById(R.id.addIncome);
        textViewIncomeAmount = findViewById(R.id.textViewIncomeAmount);
        textViewIncomeTimestamp = findViewById(R.id.textViewIncomeTimestamp);
        textViewIncomeCategory = findViewById(R.id.textViewIncomeCategory);
        chooserIncomeTimestamp = findViewById(R.id.chooserIncomeTimestamp);
        chooserIncomeCategory = findViewById(R.id.chooserIncomeCategory);
        displayCategoryColor = findViewById(R.id.displayCategoryColorAddIncome);
    }

    private void setClickListeners() {
        closeActivityAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooserIncomeTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDatePicker();
            }
        });

        chooserIncomeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddIncomeToBalanceActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "income");
                startForResult.launch(intent);
            }
        });

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }

                BalanceTurnoversDatabaseHelper balanceTurnoversDatabaseHelper = new BalanceTurnoversDatabaseHelper(AddIncomeToBalanceActivity.this);
                balanceTurnoversDatabaseHelper.addNewEntry(balanceId, editTextIncomeName.getText().toString().trim(),
                        amount, Utils.isoDateFormat.format(date), TurnoverType.REVENUE, categoryId);
                finish();

                finish();
            }
        });
    }

    private void setOtherListeners() {

        editTextIncomeAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == keyEvent.ACTION_UP) {


                    String s = amount + "";

                    if(i == KeyEvent.KEYCODE_DEL)
                        amount = amount / 10;

                    if(s.length() >= 12)
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
                    textViewIncomeAmount.setText(utils.formatCurrency(amount));
                }
                return false;
            }

        });
    }


    private void manipulateGui() {
        CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(this, this);
        Cursor categoryCursor = categoriesDatabaseHelper.readCategoryById(categoryId);

        while (categoryCursor.moveToNext()) {
            textViewIncomeCategory.setText(categoryCursor.getString(1));
            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
            backgroundDrawableIndicator.setColorFilter(categoryCursor.getInt(3), PorterDuff.Mode.SRC_ATOP);
            displayCategoryColor.setBackground(backgroundDrawableIndicator);
        }

        textViewIncomeTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
        textViewIncomeAmount.setText(utils.formatCurrency(amount));
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void showDialogDatePicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        textViewIncomeTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
    }
}