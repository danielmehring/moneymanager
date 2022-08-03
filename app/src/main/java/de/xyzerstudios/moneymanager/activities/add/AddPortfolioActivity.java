package de.xyzerstudios.moneymanager.activities.add;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;

public class AddPortfolioActivity extends AppCompatActivity {

    private ImageView closeActivityAddPortfolio, addPortfolio;
    private EditText namePortfolio, descriptionPortfolio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_portfolio);

        closeActivityAddPortfolio = findViewById(R.id.closeActivityAddPortfolio);
        addPortfolio = findViewById(R.id.addPortfolio);
        namePortfolio = findViewById(R.id.editTextPortfolioName);
        descriptionPortfolio = findViewById(R.id.editTextPortfolioDescription);

        closeActivityAddPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = namePortfolio.getText().toString().trim();

                if (name.matches("")) {
                    Toast.makeText(AddPortfolioActivity.this, getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.contains(";")) {
                    Toast.makeText(AddPortfolioActivity.this, getString(R.string.only_letters_numbers), Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = descriptionPortfolio.getText().toString().trim();
                Date date = new Date();
                PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(AddPortfolioActivity.this);
                portfolioDatabaseHelper.addNewPortfolio(name, Utils.isoDateFormat.format(date), description);

                finish();
            }
        });

    }
}