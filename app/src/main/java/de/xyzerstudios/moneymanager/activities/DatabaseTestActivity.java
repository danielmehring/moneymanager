package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;

public class DatabaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
    }

    public void sumup(View view) {
        ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(this);
        Date date = new Date();
        Cursor cursor = expensesDatabaseHelper.sumEntriesGroupedByCategory(1,
                Integer.parseInt(Utils.monthDateFormat.format(date)),
                Integer.parseInt(Utils.yearDateFormat.format(date)));

        while (cursor.moveToNext()) {
            Toast.makeText(this, cursor.getString(0) + " ; " + cursor.getString(1) + " ; " + cursor.getString(2), Toast.LENGTH_SHORT).show();
        }
    }

    public void newentry(View view) {
        int randomNumber = Math.round((float) Math.random() * 1000f);

        String category = Math.random() < .5 ? "Category 1" : "Category 2";

        ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(this);
        Date date = new Date();
        expensesDatabaseHelper.addNewEntry(1, "Entry", randomNumber,
                Utils.isoDateFormat.format(date), Integer.parseInt(Utils.monthDateFormat.format(date)),
                Integer.parseInt(Utils.yearDateFormat.format(date)), 1);

        Toast.makeText(this, "Entry added " + randomNumber + " " + category, Toast.LENGTH_SHORT).show();
    }
}