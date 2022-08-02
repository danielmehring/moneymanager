package de.xyzerstudios.moneymanager.activities.edit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.PaymentMethodDialog;

public class EditExpenseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, PaymentMethodDialog.PaymentMethodDialogListener {

    public EditText editTextExpenseAmount, editTextExpenseName;
    public ImageView closeActivityAddExpense, editExpense;
    public TextView textViewExpenseAmount, textViewExpenseTimestamp, textViewExpenseCategory, textViewExpensePaymentMethod;
    public FrameLayout chooserExpenseTimestamp, chooserExpenseCategory, chooserExpensePaymentMethod;
    public LinearLayout displayCategoryColor, deleteExpenseEntry;

    private final Utils utils = new Utils();

    private int categoryId = 9;
    private String paymentMethod = "";

    private int expensesEntryId;

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

        expensesEntryId = bundle.getInt("expensesEntryId");

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
                    textViewExpenseAmount.setText(utils.formatCurrency(amount) + " €");
                }
                return false;
            }

        });
    }

    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            String name = result.getData().getStringExtra("categoryName");
                            int color = result.getData().getIntExtra("categoryColor", getResources().getColor(R.color.ui_text_faded, null));
                            categoryId = result.getData().getIntExtra("categoryId", 9);
                            textViewExpenseCategory.setText(name);

                            Drawable backgroundDrawableIndicator = getResources().getDrawable(R.drawable.circle, null);
                            backgroundDrawableIndicator.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

                            displayCategoryColor.setBackground(backgroundDrawableIndicator);
                        }
                    }
                }
            });

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
                Intent intent = new Intent(EditExpenseActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                intent.putExtra("removefilter", false);
                startForResult.launch(intent);
            }
        });

        editExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categoryId == 0) {
                    return;
                }
                ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(EditExpenseActivity.this);
                expensesDatabaseHelper.updateEntry(expensesEntryId, editTextExpenseName.getText().toString().trim(),
                        amount, Utils.isoDateFormat.format(date), Integer.valueOf(Utils.monthDateFormat.format(date)),
                        categoryId, Integer.valueOf(Utils.yearDateFormat.format(date)), paymentMethod);
                finish();
            }
        });

        deleteExpenseEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditExpenseActivity.this);
                builder.setMessage(getResources().getString(R.string.delete_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(EditExpenseActivity.this);
                                expensesDatabaseHelper.deleteEntry(expensesEntryId);
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
        ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(this);
        Cursor cursor = expensesDatabaseHelper.readEntryById(expensesEntryId);

        while (cursor.moveToNext()) {
            try {
                amount = cursor.getInt(6);
                SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                date = simpleDateFormat.parse(cursor.getString(3));
                categoryId = cursor.getInt(7);
                paymentMethod = cursor.getString(8);
                editTextExpenseName.setText(cursor.getString(2));
                textViewExpenseTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
                textViewExpenseAmount.setText(utils.formatCurrency(amount) + " €");
                updatePaymentMethodTextView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(this, this);
        Cursor categoryCursor = categoriesDatabaseHelper.readCategoryById(categoryId);
        while (categoryCursor.moveToNext()) {
            textViewExpenseCategory.setText(categoryCursor.getString(1));
            Drawable backgroundDrawableIndicator = getResources().getDrawable(R.drawable.circle, null);
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
        textViewExpenseTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
    }

    private void updatePaymentMethodTextView() {
        String paymentMethodString = "";
        if (paymentMethod.matches("CC")) {
            paymentMethodString = getResources().getString(R.string.credit_card);
        } else if (paymentMethod.matches("EC")) {
            paymentMethodString = getResources().getString(R.string.ec_card);
        } else if (paymentMethod.matches("CASH")) {
            paymentMethodString = getResources().getString(R.string.cash);
        }
        textViewExpensePaymentMethod.setText(paymentMethodString);
    }

    @Override
    public void applyPaymentMethod(String paymentMethodCode) {
        paymentMethod = paymentMethodCode;
        updatePaymentMethodTextView();
    }
}