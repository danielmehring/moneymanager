package de.xyzerstudios.moneymanager.activities.edit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.activities.add.AddBalanceActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;

public class EditBalanceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private int balanceId;

    public EditText editTextBalanceName;
    public ImageView closeActivityEditBalance, editBalance;
    public TextView textViewBalanceTimestamp, textViewBalancePortfolio;
    public FrameLayout chooserBalanceTimestamp, chooserBalancePortfolio;

    private final Utils utils = new Utils();

    private int portfolioId = 0;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_balance);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        balanceId = bundle.getInt("balanceId");

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
        editTextBalanceName = findViewById(R.id.editTextBalanceNameEdit);
        closeActivityEditBalance = findViewById(R.id.closeActivityEditBalance);
        editBalance = findViewById(R.id.editBalance);
        textViewBalanceTimestamp = findViewById(R.id.textViewBalanceTimestampEdit);
        textViewBalancePortfolio = findViewById(R.id.textViewBalancePortfolioEdit);
        chooserBalanceTimestamp = findViewById(R.id.chooserBalanceTimestampEdit);
        chooserBalancePortfolio = findViewById(R.id.chooserBalancePortfolioEdit);
    }

    private void setClickListeners() {
        closeActivityEditBalance.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(EditBalanceActivity.this, PortfoliosActivity.class);
                intent.putExtra("choosePortfolio", true);
                startForResult.launch(intent);
            }
        });

        editBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextBalanceName.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(EditBalanceActivity.this, getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(EditBalanceActivity.this);
                balanceDatabaseHelper.updateBalance(balanceId, name, portfolioId, Utils.isoDateFormat.format(date));

                finish();
            }
        });

    }


    private void manipulateGui() {
        BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(this);
        Cursor cursor = balanceDatabaseHelper.readBalanceById(balanceId);

        while (cursor.moveToNext()) {
            try {
                SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                date = simpleDateFormat.parse(cursor.getString(2));
                editTextBalanceName.setText(cursor.getString(1));

                portfolioId = cursor.getInt(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (portfolioId != 0) {
            PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(EditBalanceActivity.this);
            Cursor cursor2 = portfolioDatabaseHelper.readPortfolioById(portfolioId);
            String portfolioName = "";
            while (cursor2.moveToNext()) {
                portfolioName = cursor2.getString(1);
            }
            textViewBalancePortfolio.setText(portfolioName);
        }

        textViewBalanceTimestamp.setText(Utils.timestampDateDisplayFormat.format(date));
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