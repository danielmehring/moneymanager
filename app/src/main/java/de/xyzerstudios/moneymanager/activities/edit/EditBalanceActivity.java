package de.xyzerstudios.moneymanager.activities.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import de.xyzerstudios.moneymanager.R;

public class EditBalanceActivity extends AppCompatActivity {

    private int balanceId;

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
    }
}