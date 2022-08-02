package de.xyzerstudios.moneymanager.activities.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import de.xyzerstudios.moneymanager.R;

public class EditCategoryActivity extends AppCompatActivity {

    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        categoryId = bundle.getInt("categoryId");
    }
}