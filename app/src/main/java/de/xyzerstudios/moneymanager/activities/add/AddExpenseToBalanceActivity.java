package de.xyzerstudios.moneymanager.activities.add;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.activities.ConvertCurrencyActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceTurnoversDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;
import de.xyzerstudios.moneymanager.utils.dialogs.PaymentMethodDialog;

public class AddExpenseToBalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, PaymentMethodDialog.PaymentMethodDialogListener {

    private final Utils utils = new Utils(this);
    public EditText editTextExpenseAmount, editTextExpenseName;
    public ImageView closeActivityAddExpense, addExpense;
    public TextView textViewExpenseAmount, textViewExpenseTimestamp, textViewExpenseCategory, textViewExpensePaymentMethod;
    public FrameLayout chooserExpenseTimestamp, chooserExpenseCategory, chooserExpensePaymentMethod;
    public LinearLayout displayCategoryColor, buttonAddExpenseConvert;
    private int balanceId;
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
    private String paymentMethod = "";
    private int amount = 0;
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
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

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

    private void initGui() {
        editTextExpenseAmount = findViewById(R.id.editTextExpenseAmount);
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
    }

    private void setClickListeners() {
        closeActivityAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                Intent intent = new Intent(AddExpenseToBalanceActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                startForResult.launch(intent);
            }
        });

        buttonAddExpenseConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddExpenseToBalanceActivity.this, ConvertCurrencyActivity.class);
                startForResultConvertCurrency.launch(intent);
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }
                BalanceTurnoversDatabaseHelper balanceTurnoversDatabaseHelper = new BalanceTurnoversDatabaseHelper(AddExpenseToBalanceActivity.this);
                balanceTurnoversDatabaseHelper.addNewEntry(balanceId, editTextExpenseName.getText().toString().trim(),
                        amount, Utils.isoDateFormat.format(date), TurnoverType.EXPENSE, paymentMethod, categoryId);
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

    private void setOtherListeners() {

        editTextExpenseAmount.setOnKeyListener(new View.OnKeyListener() {
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
                    textViewExpenseAmount.setText(utils.formatCurrency(amount));
                }
                return false;
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
}