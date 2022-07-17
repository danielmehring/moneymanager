package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class IncomeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Income.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "income";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PORTFOLIO_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_AMOUNT = "amount";
    private final Context context;


    public IncomeDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PORTFOLIO_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_AMOUNT + " INTEGER);";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewEntry(int portfolioId, String name, int amount, String timestamp, int month, int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added new entry.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateEntry(int incomeEntryId, String name, int amount, String timestamp, int month,
                            int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Entry updated.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateEntry(int incomeEntryId, int portfolioId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Entry updated.", Toast.LENGTH_SHORT).show();
        }
    }


    public void deleteEntry(int incomeEntryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Entry deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntryById(int incomeEntryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + incomeEntryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

}
