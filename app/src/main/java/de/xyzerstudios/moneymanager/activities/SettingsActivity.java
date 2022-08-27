package de.xyzerstudios.moneymanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton buttonSettingsGoBack;
    private Switch switchDisplayCurWU;
    private LinearLayout chooserSettingsCurrency;
    private TextView textViewSettingsCurrency;
    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            String isoCode = result.getData().getStringExtra("isoCodeCurrency");
                            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Utils.SPS_CURRENCY_ISO_CODE, isoCode);
                            editor.apply();
                            textViewSettingsCurrency.setText(isoCode);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonSettingsGoBack = findViewById(R.id.buttonSettingsGoBack);
        switchDisplayCurWU = findViewById(R.id.switchDisplayCurWU);
        chooserSettingsCurrency = findViewById(R.id.chooserSettingsCurrency);
        textViewSettingsCurrency = findViewById(R.id.textViewSettingsCurrency);

        buttonSettingsGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        switchDisplayCurWU.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.SPS_CURRENCY_DISPLAYED_WITH_UNICODE, isChecked);
                editor.apply();
            }
        });

        chooserSettingsCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CurrenciesActivity.class);
                startForResult.launch(intent);
            }
        });

        loadUIFromSharedPrefs();
    }

    private void loadUIFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        textViewSettingsCurrency.setText(sharedPreferences.getString(Utils.SPS_CURRENCY_ISO_CODE, "EUR"));
        switchDisplayCurWU.setChecked(sharedPreferences.getBoolean(Utils.SPS_CURRENCY_DISPLAYED_WITH_UNICODE, true));
    }
}