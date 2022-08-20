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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.CategoriesActivity;
import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;

public class AddBalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public EditText editTextBalanceName;
    public ImageView closeActivityAddBalance, addBalance;
    public TextView textViewBalanceTimestamp, textViewBalancePortfolio;
    public FrameLayout chooserBalanceTimestamp, chooserBalancePortfolio;
    public LinearLayout removeAddFilterPortfolio;

    private final Utils utils = new Utils();

    private int portfolioId = 0;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        initGui();
        initObjects();
        setClickListeners();
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
                            String name = result.getData().getStringExtra("portfolioName");
                            portfolioId = result.getData().getIntExtra("portfolioId", 0);
                            textViewBalancePortfolio.setText(name);
                        }
                    }
                }
            });

    private void initGui() {
        editTextBalanceName = findViewById(R.id.editTextBalanceName);
        closeActivityAddBalance = findViewById(R.id.closeActivityAddBalance);
        addBalance = findViewById(R.id.addBalance);
        textViewBalanceTimestamp = findViewById(R.id.textViewBalanceTimestamp);
        textViewBalancePortfolio = findViewById(R.id.textViewBalancePortfolio);
        chooserBalanceTimestamp = findViewById(R.id.chooserBalanceTimestamp);
        chooserBalancePortfolio = findViewById(R.id.chooserBalancePortfolio);
        removeAddFilterPortfolio = findViewById(R.id.removeAddFilterPortfolio);
    }

    private void setClickListeners() {
        closeActivityAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooserBalanceTimestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDatePicker();
            }
        });

        chooserBalancePortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBalanceActivity.this, PortfoliosActivity.class);
                intent.putExtra("choosePortfolio", true);
                startForResult.launch(intent);
            }
        });

        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextBalanceName.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(AddBalanceActivity.this, getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(AddBalanceActivity.this);
                balanceDatabaseHelper.addNewBalance(name, Utils.isoDateFormat.format(date), portfolioId);

                finish();
            }
        });

        removeAddFilterPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portfolioId = 0;
                updatePortfolioChooser();
            }
        });

    }


    private void manipulateGui() {
        textViewBalanceTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
        updatePortfolioChooser();
    }

    public void updatePortfolioChooser() {
        if (portfolioId != 0) {
            PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(AddBalanceActivity.this);
            Cursor cursor = portfolioDatabaseHelper.readPortfolioById(portfolioId);
            String portfolioName = "";
            while (cursor.moveToNext()) {
                portfolioName = cursor.getString(1);
            }
            textViewBalancePortfolio.setText(portfolioName);
            removeAddFilterPortfolio.setVisibility(View.VISIBLE);
        } else {
            textViewBalancePortfolio.setText("");
            removeAddFilterPortfolio.setVisibility(View.GONE);
        }
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
        textViewBalanceTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
    }


}