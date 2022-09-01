package de.xyzerstudios.moneymanager.activities.edit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
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

public class EditExpenseOfBalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, PaymentMethodDialog.PaymentMethodDialogListener {

    private final Utils utils = new Utils(this);
    public EditText editTextExpenseAmount, editTextExpenseName;
    public ImageView closeActivityAddExpense, editExpense;
    public TextView textViewExpenseAmount, textViewExpenseTimestamp, textViewExpenseCategory, textViewExpensePaymentMethod;
    public FrameLayout chooserExpenseTimestamp, chooserExpenseCategory, chooserExpensePaymentMethod;
    public LinearLayout displayCategoryColor, buttonEditExpenseConvert;
    public FloatingActionButton deleteExpenseEntry;
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
    private int balanceEntryId;
    private int amount = 0;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        balanceEntryId = bundle.getInt("balanceEntryId");

        initGui();
        initObjects();
        setClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    private void initObjects() {
        date = new Date();
    }

    private void setOtherListeners() {
        editTextExpenseAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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

    private void initGui() {
        editTextExpenseAmount = findViewById(R.id.editTextExpenseAmountEdit);
        closeActivityAddExpense = findViewById(R.id.closeActivityEditExpense);
        editExpense = findViewById(R.id.editExpense);
        textViewExpenseAmount = findViewById(R.id.textViewExpenseAmountEdit);
        editTextExpenseName = findViewById(R.id.editTextExpenseNameEdit);
        textViewExpenseTimestamp = findViewById(R.id.textViewExpenseTimestampEdit);
        textViewExpenseCategory = findViewById(R.id.textViewExpenseCategoryEdit);
        chooserExpenseTimestamp = findViewById(R.id.chooserExpenseTimestampEdit);
        chooserExpenseCategory = findViewById(R.id.chooserExpenseCategoryEdit);
        chooserExpensePaymentMethod = findViewById(R.id.chooserExpensePaymentMethodEdit);
        displayCategoryColor = findViewById(R.id.displayCategoryColorEdit);
        textViewExpensePaymentMethod = findViewById(R.id.textViewExpensePaymentMethodEdit);
        deleteExpenseEntry = findViewById(R.id.deleteExpenseEntry);
        buttonEditExpenseConvert = findViewById(R.id.buttonEditExpenseConvert);
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
                Intent intent = new Intent(EditExpenseOfBalanceActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                startForResult.launch(intent);
            }
        });

        buttonEditExpenseConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditExpenseOfBalanceActivity.this, ConvertCurrencyActivity.class);
                startForResultConvertCurrency.launch(intent);
            }
        });

        editExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == 0) {
                    return;
                }
                BalanceTurnoversDatabaseHelper turnoversDatabaseHelper = new BalanceTurnoversDatabaseHelper(EditExpenseOfBalanceActivity.this);
                turnoversDatabaseHelper.updateEntry(balanceEntryId, editTextExpenseName.getText().toString().trim(),
                        amount, Utils.isoDateFormat.format(date), TurnoverType.EXPENSE, paymentMethod, categoryId);

                finish();
            }
        });

        deleteExpenseEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditExpenseOfBalanceActivity.this);
                builder.setMessage(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.delete_confirmation) + "</font>"))
                        .setPositiveButton(Html.fromHtml("<font color='"
                                + String.format("#%06X", (0xFFFFFF & getColor(R.color.ui_text)))
                                + "'>" + getString(R.string.yes) + "</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BalanceTurnoversDatabaseHelper turnoversDatabaseHelper = new BalanceTurnoversDatabaseHelper(EditExpenseOfBalanceActivity.this);
                                turnoversDatabaseHelper.deleteEntry(balanceEntryId);
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

        chooserExpensePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPaymentMethod();
            }
        });
    }

    private void manipulateGui() {
        BalanceTurnoversDatabaseHelper turnoversDatabaseHelper = new BalanceTurnoversDatabaseHelper(this);
        Cursor cursor = turnoversDatabaseHelper.readEntryById(balanceEntryId);

        while (cursor.moveToNext()) {
            try {
                amount = cursor.getInt(4);
                SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                date = simpleDateFormat.parse(cursor.getString(3));
                categoryId = cursor.getInt(6);
                paymentMethod = cursor.getString(7);
                editTextExpenseName.setText(cursor.getString(2));
                textViewExpenseTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
                textViewExpenseAmount.setText(utils.formatCurrency(amount));
                updatePaymentMethodTextView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(this, this);
        Cursor categoryCursor = categoriesDatabaseHelper.readCategoryById(categoryId);
        while (categoryCursor.moveToNext()) {
            textViewExpenseCategory.setText(categoryCursor.getString(1));
            Drawable backgroundDrawableIndicator = getDrawable(R.drawable.circle);
            backgroundDrawableIndicator.setColorFilter(categoryCursor.getInt(3), PorterDuff.Mode.SRC_ATOP);
            displayCategoryColor.setBackground(backgroundDrawableIndicator);
        }


    }

    private int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
    }

    private void showDialogPaymentMethod() {
        PaymentMethodDialog paymentMethodDialog = new PaymentMethodDialog();
        paymentMethodDialog.show(getSupportFragmentManager(), "Payment Method Dialog");
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