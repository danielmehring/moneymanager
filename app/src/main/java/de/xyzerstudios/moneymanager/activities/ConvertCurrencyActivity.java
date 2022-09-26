package de.xyzerstudios.moneymanager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.fragments.KeyboardFragment;
import de.xyzerstudios.moneymanager.utils.Json;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.dialogs.DatePickerFragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ConvertCurrencyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        KeyboardFragment.KeyboardListener {

    private ImageButton buttonConvertGoBack;
    private ViewGroup buttonExchangeCurrency;
    private LinearLayout chooserConvertCurrencyFrom, chooserConvertCurrencyDate, keyboardContainerConvertCurrency;
    private TextView textViewFromCurrency, textViewToCurrency, textViewDateConvertCurrency,
            textViewExchangeRate, textViewExchangeAmount, textViewEqualsToAmount;
    private FrameLayout amountChooser;

    private int amount = 0;
    private int amountExchanged = 0;
    private double exchangeRate = 1;
    private Utils utils;
    private Date date;
    private String currencyFrom = "";
    private String currencyTo = "";
    private boolean error = false;
    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            String isoCode = result.getData().getStringExtra("isoCodeCurrency");
                            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Utils.SPS_CURRENCY_CONVERT_USED_LAST, isoCode);
                            editor.apply();
                            currencyFrom = isoCode;
                            updateCurrenciesToUi();
                            updateAmountToUi();
                            loadExchangeRate();
                        }
                    }
                }
            });

    private boolean softKeyboardOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_currency);

        initGui();
        initObjects();
        loadValues();
        setClickListeners();
        manipulateGui();
        loadExchangeRate();
        setSoftKeyboardOpened(true);
    }

    private void loadValues() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, MODE_PRIVATE);
            currencyFrom = sharedPreferences.getString(Utils.SPS_CURRENCY_CONVERT_USED_LAST, "USD");
            currencyTo = sharedPreferences.getString(Utils.SPS_CURRENCY_ISO_CODE, "EUR");
            if (!sharedPreferences.getString(Utils.SPS_CURRENCY_CONVERT_LAST_DATE, "").matches("")) {
                Date dateObj = Utils.isoDateFormatCurrency.parse(sharedPreferences.getString(Utils.SPS_CURRENCY_CONVERT_LAST_DATE,
                        Utils.isoDateFormatCurrency.format(new Date())));
                date = dateObj;
            }
            updateDateToUi();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initObjects() {
        date = new Date();
        utils = new Utils(this);
    }

    private void initGui() {
        buttonConvertGoBack = findViewById(R.id.buttonConvertGoBack);
        chooserConvertCurrencyFrom = findViewById(R.id.chooserConvertCurrencyFrom);
        chooserConvertCurrencyDate = findViewById(R.id.chooserConvertCurrencyDate);
        textViewFromCurrency = findViewById(R.id.textViewFromCurrency);
        textViewToCurrency = findViewById(R.id.textViewToCurrency);
        textViewDateConvertCurrency = findViewById(R.id.textViewDateConvertCurrency);
        textViewExchangeRate = findViewById(R.id.textViewExchangeRate);
        textViewExchangeAmount = findViewById(R.id.textViewExchangeAmount);
        textViewEqualsToAmount = findViewById(R.id.textViewEqualsToAmount);
        buttonExchangeCurrency = findViewById(R.id.buttonExchangeCurrency);
        amountChooser = findViewById(R.id.amountChooser);
        keyboardContainerConvertCurrency = findViewById(R.id.keyboardContainerConvertCurrency);

        KeyboardFragment fragment = new KeyboardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.keyboardContainerConvertCurrency, fragment);
        fragmentTransaction.commit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) ((double)displayMetrics.heightPixels / 2.8));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        keyboardContainerConvertCurrency.setLayoutParams(layoutParams);

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
            TransitionManager.beginDelayedTransition(keyboardContainerConvertCurrency);
            keyboardContainerConvertCurrency.setVisibility(View.VISIBLE);
        } else {
            TransitionManager.beginDelayedTransition(keyboardContainerConvertCurrency);
            keyboardContainerConvertCurrency.setVisibility(View.GONE);
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

        buttonConvertGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom);
            }
        });

        chooserConvertCurrencyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDatePicker();
            }
        });


        chooserConvertCurrencyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConvertCurrencyActivity.this, CurrenciesActivity.class);
                startForResult.launch(intent);
            }
        });

        buttonExchangeCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (error) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("exchangedCurrency", amountExchanged);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void manipulateGui() {
        updateAmountToUi();
        updateCurrenciesToUi();
        updateDateToUi();
        updateExchangeCurrencyButton();
    }

    private void showDialogDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(true);
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        date = calendar.getTime();
        boolean isLastest = Utils.isoDateFormat.format(date).matches(Utils.isoDateFormat.format(new Date()));
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isLastest) {
            editor.putString(Utils.SPS_CURRENCY_CONVERT_LAST_DATE, "");
        } else {
            editor.putString(Utils.SPS_CURRENCY_CONVERT_LAST_DATE, Utils.isoDateFormatCurrency.format(date));
        }
        editor.apply();
        updateDateToUi();
        loadExchangeRate();
    }

    private void updateAmountToUi() {
        if (error) {
            textViewExchangeAmount.setText("--,--");
            textViewEqualsToAmount.setText("--,--");
            return;
        }
        textViewExchangeAmount.setText(utils.formatCurrency(amount, currencyFrom, false));
        textViewEqualsToAmount.setText(utils.formatCurrency(amountExchanged, currencyTo, false));
    }

    private void updateCurrenciesToUi() {
        textViewFromCurrency.setText(currencyFrom);
        textViewToCurrency.setText(currencyTo);
    }

    private void updateDateToUi() {
        textViewDateConvertCurrency.setText(Utils.timestampDateDisplayFormat.format(date));
    }

    private void updateExchangeCurrencyButton() {
        TransitionManager.beginDelayedTransition(buttonExchangeCurrency);
        buttonExchangeCurrency.setVisibility(error ? View.INVISIBLE : View.VISIBLE);
        buttonExchangeCurrency.setEnabled(!error);
        amountChooser.setEnabled(!error);
        chooserConvertCurrencyDate.setEnabled(!error);
    }

    private void loadExchangeRate() {
        new exchangeRateAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                currencyFrom, currencyTo, Utils.isoDateFormatCurrency.format(date));
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
        amountExchanged = (int) ((double) amount * exchangeRate);
        updateAmountToUi();
    }

    private class exchangeRateAsyncTask extends AsyncTask<String, String, Double> {

        private final WeakReference<ConvertCurrencyActivity> activityWeakReference;
        private final Context context;

        exchangeRateAsyncTask(ConvertCurrencyActivity activity) {
            activityWeakReference = new WeakReference<ConvertCurrencyActivity>(activity);
            context = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ConvertCurrencyActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.textViewExchangeRate.setText("loading...");
        }

        @Override
        protected Double doInBackground(String... strings) {
            String currencyFrom = strings[0];
            String currencyTo = strings[1];
            String dateIso = strings[2];
            Date date = new Date();
            if (dateIso.matches(Utils.isoDateFormatCurrency.format(date))) {
                dateIso = "latest";
            }

            OkHttpClient okHttpClient = new OkHttpClient();
            String url = "https://www.frankfurter.app/" + dateIso +
                    "?from=" + currencyFrom +
                    "&to=" + currencyTo;
            try {
                Request request = new Request.Builder().url(url).build();
                String response = okHttpClient.newCall(request).execute().body().string();
                JsonNode jsonNode = Json.parse(response);
                return jsonNode.get("rates").get(currencyTo).asDouble();
            } catch (IOException e) {
                return -1.0;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ConvertCurrencyActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

        }

        @Override
        protected void onPostExecute(Double d) {
            super.onPostExecute(d);
            ConvertCurrencyActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            if (d == -1.0) {
                activity.textViewExchangeRate.setText("Error");
                activity.error = true;
                updateAmountToUi();
            } else {
                activity.exchangeRate = d;
                activity.amountExchanged = (int) ((double) activity.amount * activity.exchangeRate);
                activity.textViewExchangeRate.setText(d + "");
                activity.error = false;
                updateAmountToUi();
            }
            updateExchangeCurrencyButton();
        }
    }
}