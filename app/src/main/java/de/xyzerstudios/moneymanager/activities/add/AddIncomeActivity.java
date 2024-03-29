package de.xyzerstudios.moneymanager.activities.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.activities.ConvertCurrencyActivity;
import de.xyzerstudios.moneymanager.fragments.KeyboardFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.RepeatedIncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;
import de.xyzerstudios.moneymanager.utils.dialogs.IntervalPickerDialog;

public class AddIncomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        IntervalPickerDialog.IntervalPickerDialogListener, KeyboardFragment.KeyboardListener {

    private final Utils utils = new Utils(this);
    public EditText editTextIncomeName;
    public ImageView closeActivityAddIncome, addIncome;
    public TextView textViewIncomeAmount, textViewIncomeTimestamp, textViewIncomeCategory, textViewIncomeInterval,
            textViewRepeatedIncome;
    public FrameLayout chooserIncomeTimestamp, chooserIncomeCategory, chooserIncomeInterval, amountChooser;
    public LinearLayout displayCategoryColor, buttonAddIncomeConvert, keyboardContainerAddIncome;
    public ViewGroup containerAddIncomeInterval;
    public Switch switchRepeatedIncome;
    private int categoryId = 38;

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
    private String interval = "1_m";
    private int amount = 0;
    public ActivityResultLauncher<Intent> startForResultConvertCurrency = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            amount = result.getData().getIntExtra("exchangedCurrency", amount);
                            textViewIncomeAmount.setText(utils.formatCurrency(amount));
                        }
                    }
                }
            });
    private Date date;

    private boolean repeated = false;

    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

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
        editTextIncomeName = findViewById(R.id.editTextIncomeName);
        closeActivityAddIncome = findViewById(R.id.closeActivityAddIncome);
        addIncome = findViewById(R.id.addIncome);
        textViewIncomeAmount = findViewById(R.id.textViewIncomeAmount);
        textViewIncomeTimestamp = findViewById(R.id.textViewIncomeTimestamp);
        textViewIncomeCategory = findViewById(R.id.textViewIncomeCategory);
        chooserIncomeTimestamp = findViewById(R.id.chooserIncomeTimestamp);
        chooserIncomeCategory = findViewById(R.id.chooserIncomeCategory);
        displayCategoryColor = findViewById(R.id.displayCategoryColorAddIncome);
        switchRepeatedIncome = findViewById(R.id.switchRepeatedIncome);

        containerAddIncomeInterval = findViewById(R.id.containerAddIncomeInterval);
        textViewIncomeInterval = containerAddIncomeInterval.findViewById(R.id.textViewIncomeInterval);
        chooserIncomeInterval = containerAddIncomeInterval.findViewById(R.id.chooserIncomeInterval);
        containerAddIncomeInterval.setVisibility(View.INVISIBLE);
        textViewRepeatedIncome = findViewById(R.id.textViewRepeatedIncomeAdd);
        buttonAddIncomeConvert = findViewById(R.id.buttonAddIncomeConvert);
        amountChooser = findViewById(R.id.amountChooser);
        keyboardContainerAddIncome = findViewById(R.id.keyboardContainerAddIncome);
        amountChooser = findViewById(R.id.amountChooser);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerAddIncome, fragment);
        fragmentTransaction.commit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) ((double)displayMetrics.heightPixels / 2.8));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        keyboardContainerAddIncome.setLayoutParams(layoutParams);

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
            TransitionManager.beginDelayedTransition(keyboardContainerAddIncome);
            keyboardContainerAddIncome.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainerAddIncome);
            keyboardContainerAddIncome.setVisibility(View.GONE);
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

        closeActivityAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom);
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
                Intent intent = new Intent(AddIncomeActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "income");
                startForResult.launch(intent);
            }
        });

        buttonAddIncomeConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddIncomeActivity.this, ConvertCurrencyActivity.class);
                startForResultConvertCurrency.launch(intent);
            }
        });

        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }
                IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(AddIncomeActivity.this);
                int incomeEntryId = incomeDatabaseHelper.addNewEntryAndReturnId(loadPortfolioIdFromSharedPrefs(), editTextIncomeName.getText().toString().trim(),
                        amount, categoryId, Utils.isoDateFormat.format(date), Integer.valueOf(Utils.monthDateFormat.format(date)),
                        Integer.valueOf(Utils.yearDateFormat.format(date)));
                if (!repeated) {
                    finish();
                    return;
                }
                RepeatedIncomeDatabaseHelper repeatedIncomeDatabase = new RepeatedIncomeDatabaseHelper(AddIncomeActivity.this);
                repeatedIncomeDatabase.addNewEntry(incomeEntryId, amount, loadPortfolioIdFromSharedPrefs(), interval, Utils.isoDateFormat.format(date), categoryId);
                finish();
            }
        });

        chooserIncomeInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIntervalPicker();
            }
        });
    }

    private void setOtherListeners() {
        switchRepeatedIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switched) {
                TransitionManager.beginDelayedTransition(containerAddIncomeInterval);
                containerAddIncomeInterval.setVisibility(switched ? View.VISIBLE : View.INVISIBLE);
                repeated = switched;
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
        switchRepeatedIncome.setChecked(repeated);
        containerAddIncomeInterval.setVisibility(repeated ? View.VISIBLE : View.INVISIBLE);
        applyInterval(interval);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            switchRepeatedIncome.setVisibility(View.GONE);
            textViewRepeatedIncome.setTextColor(getColor(R.color.ui_lime_red));
            textViewRepeatedIncome.setText(getString(R.string.repeated_income_not_available));
        }
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void showIntervalPicker() {
        IntervalPickerDialog intervalPickerDialog = new IntervalPickerDialog(interval);
        intervalPickerDialog.show(getSupportFragmentManager(), "Interval Picker Dialog");
    }

    private void showDialogDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(false);
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

    @Override
    public void applyInterval(String interval) {
        this.interval = interval;
        int number = Integer.valueOf(interval.split("_")[0]);
        String unit = interval.split("_")[1];

        String displayText = "";
        switch (unit) {
            case "d":
                displayText = number > 1 ? getString(R.string.days) : getString(R.string.day);
                break;
            case "w":
                displayText = number > 1 ? getString(R.string.weeks) : getString(R.string.week);
                break;
            case "m":
                displayText = number > 1 ? getString(R.string.months) : getString(R.string.month);
                break;
            case "y":
                displayText = number > 1 ? getString(R.string.years) : getString(R.string.year);
                break;
        }
        textViewIncomeInterval.setText(number + " " + displayText);
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
        textViewIncomeAmount.setText(utils.formatCurrency(amount));
    }

}