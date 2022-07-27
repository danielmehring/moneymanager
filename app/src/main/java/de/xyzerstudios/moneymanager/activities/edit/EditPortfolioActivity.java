package de.xyzerstudios.moneymanager.activities.edit;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;

public class EditPortfolioActivity extends AppCompatActivity {

    private ImageView closeActivityEditPortfolio, editPortfolio;
    private EditText namePortfolio, descriptionPortfolio;

    private int portfolioId;
    private String loadedName;
    private String loadedDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portfolio);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        portfolioId = bundle.getInt("portfolioId");

        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(this);
        Cursor cursor = portfolioDatabaseHelper.readPortfolioById(portfolioId);

        if (cursor.getCount() == 0) {
            finish();
            return;
        }

        while (cursor.moveToNext()) {
            loadedName = cursor.getString(1);
            loadedDesc = cursor.getString(6);
        }

        closeActivityEditPortfolio = findViewById(R.id.closeActivityEditPortfolio);
        editPortfolio = findViewById(R.id.editPortfolio);
        namePortfolio = findViewById(R.id.editTextPortfolioNameEdit);
        descriptionPortfolio = findViewById(R.id.editTextPortfolioDescriptionEdit);

        namePortfolio.setText(loadedName);
        descriptionPortfolio.setText(loadedDesc);

        closeActivityEditPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = namePortfolio.getText().toString().trim();

                if (name.matches("")) {
                    Toast.makeText(EditPortfolioActivity.this, getResources().getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.contains(";")) {
                    Toast.makeText(EditPortfolioActivity.this, getResources().getString(R.string.only_letters_numbers), Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = descriptionPortfolio.getText().toString().trim();
                Date date = new Date();
                PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(EditPortfolioActivity.this);
                portfolioDatabaseHelper.updatePortfolio(portfolioId, name, description);

                finish();
            }
        });

    }
}