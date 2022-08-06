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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.IncomeDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;

public class EditIncomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final Utils utils = new Utils();
    public EditText editTextIncomeAmount, editTextIncomeName;
    public ImageView closeActivityAddIncome, editIncome;
    public TextView textViewIncomeAmount, textViewIncomeTimestamp, textViewIncomeCategory;
    public FrameLayout chooserIncomeTimestamp, chooserIncomeCategory;
    public LinearLayout displayCategoryColor, deleteIncomeEntry;
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
    private int incomeEntryId;
    private int amount = 0;
    private Date date;

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
        editTextIncomeAmount = findViewById(R.id.editTextIncomeAmountEdit);
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
                Intent intent = new Intent(EditIncomeActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "income");
                startForResult.launch(intent);
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
                finish();
            }
        });

        deleteIncomeEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditIncomeActivity.this);
                builder.setMessage(getString(R.string.delete_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IncomeDatabaseHelper incomeDatabaseHelper = new IncomeDatabaseHelper(EditIncomeActivity.this);
                                incomeDatabaseHelper.deleteEntry(incomeEntryId);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void setOtherListeners() {

        editTextIncomeAmount.setOnKeyListener(new View.OnKeyListener() {
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
                    textViewIncomeAmount.setText(utils.formatCurrency(amount));
                }
                return false;
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