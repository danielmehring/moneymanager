package de.xyzerstudios.moneymanager.activities.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.activities.ConvertCurrencyActivity;
import de.xyzerstudios.moneymanager.fragments.KeyboardFragment;
import de.xyzerstudios.moneymanager.fragments.WaitFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;
import de.xyzerstudios.moneymanager.utils.dialogs.PaymentMethodDialog;

public class AddExpenseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        PaymentMethodDialog.PaymentMethodDialogListener, KeyboardFragment.KeyboardListener{

    private final Utils utils = new Utils(this);
    public EditText editTextExpenseName;
    public ImageView closeActivityAddExpense, addExpense;
    public TextView textViewExpenseAmount, textViewExpenseTimestamp, textViewExpenseCategory, textViewExpensePaymentMethod;
    public FrameLayout chooserExpenseTimestamp, chooserExpenseCategory, chooserExpensePaymentMethod, amountChooser;
    public LinearLayout displayCategoryColor, buttonAddExpenseConvert, keyboardContainerAddExpense;
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
                            textViewExpenseCategory.setText(name);

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
                            textViewExpenseAmount.setText(utils.formatCurrency(amount));
                        }
                    }
                }
            });
    private String paymentMethod = "";
    private int amount = 0;
    private Date date;

    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initGui();
        initObjects();
        setClickListeners();
        manipulateGui();

    }

    private void initObjects() {
        date = new Date();
    }

    private void initGui() {
        closeActivityAddExpense = findViewById(R.id.closeActivityAddExpense);
        addExpense = findViewById(R.id.addExpense);
        textViewExpenseAmount = findViewById(R.id.textViewExpenseAmount);
        editTextExpenseName = findViewById(R.id.editTextExpenseName);
        textViewExpenseTimestamp = findViewById(R.id.textViewExpenseTimestamp);
        textViewExpenseCategory = findViewById(R.id.textViewExpenseCategory);
        chooserExpenseTimestamp = findViewById(R.id.chooserExpenseTimestamp);
        chooserExpenseCategory = findViewById(R.id.chooserExpenseCategory);
        chooserExpensePaymentMethod = findViewById(R.id.chooserExpensePaymentMethod);
        displayCategoryColor = findViewById(R.id.displayCategoryColor);
        textViewExpensePaymentMethod = findViewById(R.id.textViewExpensePaymentMethod);
        buttonAddExpenseConvert = findViewById(R.id.buttonAddExpenseConvert);
        amountChooser = findViewById(R.id.amountChooser);
        keyboardContainerAddExpense = findViewById(R.id.keyboardContainerAddExpense);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerAddExpense, fragment);
        fragmentTransaction.commit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) ((double)displayMetrics.heightPixels / 2.8));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        keyboardContainerAddExpense.setLayoutParams(layoutParams);

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
            TransitionManager.beginDelayedTransition(keyboardContainerAddExpense);
            keyboardContainerAddExpense.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainerAddExpense);
            keyboardContainerAddExpense.setVisibility(View.GONE);
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

        closeActivityAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom);
            }
        });

        chooserExpenseTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDatePicker();
            }
        });

        chooserExpenseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddExpenseActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                startForResult.launch(intent);
            }
        });

        buttonAddExpenseConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddExpenseActivity.this, ConvertCurrencyActivity.class);
                startForResultConvertCurrency.launch(intent);
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }
                ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(AddExpenseActivity.this);
                expensesDatabaseHelper.addNewEntry(loadPortfolioIdFromSharedPrefs(), editTextExpenseName.getText().toString().trim(),
                        amount, Utils.isoDateFormat.format(date), Integer.valueOf(Utils.monthDateFormat.format(date)),
                        Integer.valueOf(Utils.yearDateFormat.format(date)), categoryId, paymentMethod);
                finish();
            }
        });

        chooserExpensePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPaymentMethod();
            }
        });
    }


    private void manipulateGui() {
        CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(this, this);
        Cursor categoryCursor = categoriesDatabaseHelper.readCategoryById(categoryId);
        while (categoryCursor.moveToNext()) {
            textViewExpenseCategory.setText(categoryCursor.getString(1));
            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
            backgroundDrawableIndicator.setColorFilter(categoryCursor.getInt(3), PorterDuff.Mode.SRC_ATOP);
            displayCategoryColor.setBackground(backgroundDrawableIndicator);
        }

        textViewExpenseTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
        textViewExpenseAmount.setText(utils.formatCurrency(amount));
    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void showDialogDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(false);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    private void showDialogPaymentMethod() {
        PaymentMethodDialog paymentMethodDialog = new PaymentMethodDialog();
        paymentMethodDialog.show(getSupportFragmentManager(), "Payment Method Dialog");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        textViewExpenseTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
    }

    private void updatePaymentMethodTextView() {
        String paymentMethodString = "";
        if (paymentMethod.matches("CC")) {
            paymentMethodString = getString(R.string.credit_card);
        } else if (paymentMethod.matches("EC")) {
            paymentMethodString = getString(R.string.ec_card);
        } else if (paymentMethod.matches("CASH")) {
            paymentMethodString = getString(R.string.cash);
        } else if (paymentMethod.matches("BT")) {
            paymentMethodString = getString(R.string.bank_transfer);
        } else if (paymentMethod.matches("OP")) {
            paymentMethodString = getString(R.string.online_payment);
        }
        textViewExpensePaymentMethod.setText(paymentMethodString);
    }

    @Override
    public void applyPaymentMethod(String paymentMethodCode) {
        paymentMethod = paymentMethodCode;
        updatePaymentMethodTextView();
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
        textViewExpenseAmount.setText(utils.formatCurrency(amount));
    }
}