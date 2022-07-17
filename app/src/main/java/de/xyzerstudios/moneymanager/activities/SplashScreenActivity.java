package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.xyzerstudios.moneymanager.R;

public class SplashScreenActivity extends AppCompatActivity {

    private Button addEntry, readEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        addEntry = findViewById(R.id.addEntry);
        readEntries = findViewById(R.id.readEntries);

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        readEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}