package de.xyzerstudios.moneymanager.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.AlarmReceiver;
import de.xyzerstudios.moneymanager.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton buttonSettingsGoBack;
    private Switch switchDisplayCurWU, switchEnableNotification;
    private ViewGroup chooserNotificationTime;
    private LinearLayout chooserSettingsCurrency;
    private TextView textViewSettingsCurrency, textViewSettingsNotificationTime;
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
    private boolean notificationEnabled = false;
    private Calendar calendarNotification;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        loadCalendarFromSharedPrefs();

        buttonSettingsGoBack = findViewById(R.id.buttonSettingsGoBack);
        switchDisplayCurWU = findViewById(R.id.switchDisplayCurWU);
        chooserSettingsCurrency = findViewById(R.id.chooserSettingsCurrency);
        textViewSettingsCurrency = findViewById(R.id.textViewSettingsCurrency);
        switchEnableNotification = findViewById(R.id.switchEnableNotification);
        chooserNotificationTime = findViewById(R.id.chooserNotificationTime);
        textViewSettingsNotificationTime = findViewById(R.id.textViewSettingsNotificationTime);

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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.SPS_CURRENCY_DISPLAYED_WITH_UNICODE, isChecked);
                editor.apply();
            }
        });

        switchEnableNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                notificationEnabled = isChecked;
                updateVisibilityNotificationChooser();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.SPS_DAILY_NOTIFICATION_ENABLED, isChecked);
                editor.apply();
                if (!isChecked) {
                    cancelAlarm();
                }
            }
        });

        chooserSettingsCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CurrenciesActivity.class);
                startForResult.launch(intent);
            }
        });

        chooserNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        loadUIFromSharedPrefs();
    }

    private void loadCalendarFromSharedPrefs() {
        calendarNotification = Calendar.getInstance();
        calendarNotification.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(Utils.SPS_DAILY_NOTIFICATION_TIME_HOUR, 20));
        calendarNotification.set(Calendar.MINUTE, sharedPreferences.getInt(Utils.SPS_DAILY_NOTIFICATION_TIME_MINUTE, 0));
        calendarNotification.set(Calendar.SECOND, 0);
        calendarNotification.set(Calendar.MILLISECOND, 0);
    }

    private void loadUIFromSharedPrefs() {
        textViewSettingsCurrency.setText(sharedPreferences.getString(Utils.SPS_CURRENCY_ISO_CODE, "USD"));
        switchDisplayCurWU.setChecked(sharedPreferences.getBoolean(Utils.SPS_CURRENCY_DISPLAYED_WITH_UNICODE, true));
        switchEnableNotification.setChecked(sharedPreferences.getBoolean(Utils.SPS_DAILY_NOTIFICATION_ENABLED, false));
        showTimeTextView(sharedPreferences.getInt(Utils.SPS_DAILY_NOTIFICATION_TIME_HOUR, -1),
                sharedPreferences.getInt(Utils.SPS_DAILY_NOTIFICATION_TIME_MINUTE, -1));
    }

    private void updateVisibilityNotificationChooser() {
        TransitionManager.beginDelayedTransition(chooserNotificationTime);
        chooserNotificationTime.setVisibility(notificationEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Utils.SPS_DAILY_NOTIFICATION_TIME_HOUR, selectedHour);
                editor.putInt(Utils.SPS_DAILY_NOTIFICATION_TIME_MINUTE, selectedMinute);
                editor.apply();

                showTimeTextView(selectedHour, selectedMinute);

                calendarNotification.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendarNotification.set(Calendar.MINUTE, selectedMinute);
                calendarNotification.set(Calendar.SECOND, 0);
                calendarNotification.set(Calendar.MILLISECOND, 0);

                setAlarm();
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendarNotification.get(Calendar.HOUR_OF_DAY), calendarNotification.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void showTimeTextView(int hour, int minute) {
        if (hour == -1 || minute == -1) {
            textViewSettingsNotificationTime.setText("--:--");
            return;
        }
        String hourString = hour < 10 ? ("0" + hour) : ("" + hour);
        String minuteString = minute < 10 ? ("0" + minute) : ("" + minute);
        textViewSettingsNotificationTime.setText(hourString + ":" + minuteString);
    }

    private void setAlarm() {
        if (sharedPreferences.getBoolean(Utils.SPS_DAILY_NOTIFICATION_ENABLED, false)) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendarNotification.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
    }
}