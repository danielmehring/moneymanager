package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class RepeatedIncomeDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "IncomeDatabaseHelper";

    private static final String DATABASE_NAME = "RepeatedIncome.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "repeated_income";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_INCOME_ENRTRY_ID = "income_entry_id";
    private static final String COLUMN_INCOME_ENRTRY_PORTFOLIO_ID = "income_entry_portfolio_id";
    private static final String COLUMN_INCOME_ENRTRY_AMOUNT = "income_entry_amount";
    private static final String COLUMN_INTERVAL = "interval"; //format: 5_d; 3_w; 4_m; 1_y
    private static final String COLUMN_TIMESTAMP_START_TIME = "timestamp_start_time";
    private static final String COLUMN_INCOME_ENRTRY_CATEGORY_ID = "income_category_id";
    private final Context context;


    public RepeatedIncomeDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCOME_ENRTRY_ID + " INTEGER, " +
                COLUMN_INCOME_ENRTRY_PORTFOLIO_ID + " INTEGER, " +
                COLUMN_INCOME_ENRTRY_AMOUNT + " INTEGER, " +
                COLUMN_INTERVAL + " TEXT, " +
                COLUMN_TIMESTAMP_START_TIME + " TEXT, " +
                COLUMN_INCOME_ENRTRY_CATEGORY_ID + " INTEGER);";
        database.execSQL(query);

        Log.d(tag, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewEntry(int incomeEntryId, int amount, int incomeEntryPortfolioId, String interval,
                            String timestamp, int categoryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_INCOME_ENRTRY_ID, incomeEntryId);
        contentValues.put(COLUMN_INCOME_ENRTRY_AMOUNT, amount);
        contentValues.put(COLUMN_INCOME_ENRTRY_PORTFOLIO_ID, incomeEntryPortfolioId);
        contentValues.put(COLUMN_INTERVAL, interval);
        contentValues.put(COLUMN_TIMESTAMP_START_TIME, timestamp);
        contentValues.put(COLUMN_INCOME_ENRTRY_CATEGORY_ID, categoryId);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void updateEntryByIncomeEntryId(int incomeEntryId, int amount, String interval, String timestamp, int categoryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_INTERVAL, interval);
        contentValues.put(COLUMN_INCOME_ENRTRY_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP_START_TIME, timestamp);
        contentValues.put(COLUMN_INCOME_ENRTRY_CATEGORY_ID, categoryId);

        long result = database.update(TABLE_NAME, contentValues, COLUMN_INCOME_ENRTRY_ID + "=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }


    public void deleteEntry(int entryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(entryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry deleted.");
        }
    }

    public void deleteEntryByIncomeEnrtyId(int incomeEntryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME,  COLUMN_INCOME_ENRTRY_ID + "=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry deleted.");
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

    public Cursor readAllDataForPortfolio(int portfolioId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_INCOME_ENRTRY_PORTFOLIO_ID + "=" + portfolioId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntryById(int entryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + entryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByIncomeEntryId(int incomeEntryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_INCOME_ENRTRY_ID + "=" + incomeEntryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumEntriesByIntervalFromPortfolioId(int portfolioId) {
        String query = "SELECT Sum(" + COLUMN_INCOME_ENRTRY_AMOUNT + "), " + COLUMN_INTERVAL + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_INCOME_ENRTRY_PORTFOLIO_ID + "=" + portfolioId
                + " GROUP BY " + COLUMN_INTERVAL;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor customQuery(String query) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }
}
