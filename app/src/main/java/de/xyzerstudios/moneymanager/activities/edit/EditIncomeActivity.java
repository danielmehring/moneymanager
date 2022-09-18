package de.xyzerstudios.moneymanager.activities.edit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
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

public class EditIncomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        IntervalPickerDialog.IntervalPickerDialogListener, KeyboardFragment.KeyboardListener {

    private final Utils utils = new Utils(this);
    public EditText editTextIncomeName;
    public ImageView closeActivityAddIncome, editIncome;
    public TextView textViewIncomeAmount, textViewIncomeTimestamp, textViewIncomeCategory, textViewIncomeInterval,
            textViewRepeatedIncome;
    public FrameLayout chooserIncomeTimestamp, chooserIncomeCategory, chooserIncomeInterval, amountChooser;
    public LinearLayout displayCategoryColor, buttonEditIncomeConvert, keyboardContainer;
    public FloatingActionButton deleteIncomeEntry;

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
    private int incomeEntryId;
    private int amount = 0;
    private Date date;

    private boolean wasRepeated;
    private String interval = "1_m";

    private boolean repeated;
    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        incomeEntryId = bundle.getInt("incomeEntryId");

        setValues();
        initGui();
        initObjects();
        setClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    private void setValues() {
        RepeatedIncomeDatabaseHelper repeatedIncomeDatabase = new RepeatedIncomeDatabaseHelper(this);
        Cursor cursor = repeatedIncomeDatabase.readEntriesByIncomeEntryId(incomeEntryId);
        if (cursor.getCount() == 0) {
            wasRepeated = false;
            return;
        }
        wasRepeated = true;
        while (cursor.moveToNext()) {
            interval = cursor.getString(4);
        }
        repeated = wasRepeated;
    }

    private void initObjects() {
        date = new Date();
    }

    private void initGui() {
        editTextIncomeName = findViewById(R.id.editTextIncomeNameEdit);
        closeActivityAddIncome = findViewById(R.id.closeActivityEditIncome);
        editIncome = findViewById(R.id.editIncome);
        textViewIncomeAmount = findViewById(R.id.textViewIncomeAmountEdit);
        textViewIncomeTimestamp = findViewById(R.id.textViewIncomeTimestampEdit);
        textViewIncomeCategory = findViewById(R.id.textViewIncomeCategoryEdit);
        chooserIncomeTimestamp = findViewById(R.id.chooserIncomeTimestampEdit);
        chooserIncomeCategory = findViewById(R.id.chooserIncomeCategoryEdit);
        displayCategoryColor = findViewById(R.id.displayCategoryColorEditIncome);
        deleteIncomeEntry = findViewById(R.id.deleteIncomeEntry);

        switchRepeatedIncome = findViewById(R.id.switchRepeatedIncomeEdit);
        containerAddIncomeInterval = findViewById(R.id.containerAddIncomeIntervalEdit);
        chooserIncomeInterval = containerAddIncomeInterval.findViewById(R.id.chooserIncomeIntervalEdit);
        textViewIncomeInterval = containerAddIncomeInterval.findViewById(R.id.textViewIncomeIntervalEdit);
        textViewRepeatedIncome = findViewById(R.id.textViewRepeatedIncomeEdit);
        buttonEditIncomeConvert = findViewById(R.id.buttonEditIncomeConvert);

        keyboardContainer = findViewById(R.id.keyboardContainerIncomeEdit);
        amountChooser = findViewById(R.id.amountChooser);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerIncomeEdit, fragment);
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
            deleteIncomeEntry.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainer);
            keyboardContainer.setVisibility(View.GONE);
            deleteIncomeEntry.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(EditIncomeActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "income");
                startForResult.launch(intent);
            }
        });

        buttonEditIncomeConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditIncomeActivity.this, ConvertCurrencyActivity.class);
                startForResultConvertCurrency.launch(intent);
            }
        });

        editIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }
                IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(EditIncomeActivity.this);
                incomeDatabaseHelper.updateEntry(incomeEntryId, editTextIncomeName.getText().toString().trim(),
                        amount, categoryId, Utils.isoDateFormat.format(date), Integer.valueOf(Utils.monthDateFormat.format(date)),
                        Integer.valueOf(Utils.yearDateFormat.format(date)));

                RepeatedIncomeDatabaseHelper repeatedIncomeDatabase = new RepeatedIncomeDatabaseHelper(EditIncomeActivity.this);
                if (wasRepeated && !repeated) {
                    repeatedIncomeDatabase.deleteEntryByIncomeEnrtyId(incomeEntryId);
                } else if (wasRepeated && repeated) {
                    repeatedIncomeDatabase.updateEntryByIncomeEntryId(incomeEntryId, amount, interval, Utils.isoDateFormat.format(date), categoryId);
                } else if (!wasRepeated && repeated) {
                    repeatedIncomeDatabase.addNewEntry(incomeEntryId, amount, loadPortfolioIdFromSharedPrefs(), interval, Utils.isoDateFormat.format(date), categoryId);
                }
                finish();
            }
        });

        deleteIncomeEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditIncomeActivity.this);
                builder.setMessage(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.delete_confirmation) + "</font>"))
                        .setPositiveButton(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.yes) + "</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(EditIncomeActivity.this);
                                incomeDatabaseHelper.deleteEntry(incomeEntryId);
                                if (!wasRepeated) {
                                    finish();
                                    return;
                                }
                                RepeatedIncomeDatabaseHelper repeatedIncomeDatabase = new RepeatedIncomeDatabaseHelper(EditIncomeActivity.this);
                                repeatedIncomeDatabase.deleteEntryByIncomeEnrtyId(incomeEntryId);
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
        IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(this);
        Cursor cursor = incomeDatabaseHelper.readEntryById(incomeEntryId);

        while (cursor.moveToNext()) {
            try {
                amount = cursor.getInt(6);
                SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                date = simpleDateFormat.parse(cursor.getString(3));
                categoryId = cursor.getInt(7);
                editTextIncomeName.setText(cursor.getString(2));
                textViewIncomeTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
                textViewIncomeAmount.setText(utils.formatCurrency(amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private void showIntervalPicker() {
        IntervalPickerDialog intervalPickerDialog = new IntervalPickerDialog(interval);
        intervalPickerDialog.show(getSupportFragmentManager(), "Interval Picker Dialog");
    }

    private void showDialogDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(false);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
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
        if (interval.matches(""))
            return;

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