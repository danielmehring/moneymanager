package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import de.xyzerstudios.moneymanager.R;

public class IncomeActivity extends AppCompatActivity {

    public ImageButton buttonIncomeGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        buttonIncomeGoBack = findViewById(R.id.buttonIncomeGoBack);
        buttonIncomeGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}