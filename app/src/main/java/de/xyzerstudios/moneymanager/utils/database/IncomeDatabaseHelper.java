package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class IncomeDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "IncomeDatabaseHelper";

    private static final String DATABASE_NAME = "Income.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "income";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PORTFOLIO_ID = "portfolio_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY_ID = "category_id";
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
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_CATEGORY_ID + " INTEGER);";
        database.execSQL(query);

        Log.d(tag, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewEntry(int portfolioId, String name, int amount, int categoryId, String timestamp,
                            int month, int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public int addNewEntryAndReturnId(int portfolioId, String name, int amount, int categoryId, String timestamp,
                            int month, int year) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_YEAR, year);

        long result = writableDatabase.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
            return 0;
        } else {
            Log.d(tag, "Added new entry.");
        }

        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME
                + " ORDER BY " + COLUMN_ID + " DESC"
                + " LIMIT 1";
        SQLiteDatabase readableDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        if (readableDatabase != null)
            cursor = readableDatabase.rawQuery(query, null);
        if (cursor == null)
            return 0;

        int entryId = 0;

        while (cursor.moveToNext()) entryId = cursor.getInt(0);

        return entryId;
    }

    public void updateEntry(int incomeEntryId, String name, int amount, int categoryId, String timestamp,
                            int month, int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateEntry(int incomeEntryId, int portfolioId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(incomeEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }


    public void deleteEntry(int incomeEntryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(incomeEntryId)});
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

    public Cursor readEntryById(int incomeEntryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + incomeEntryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByPortfolioId(int portfolioId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByPortfolioIdSortedByDate(int portfolioId, int month, int year) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_PORTFOLIO_ID + "=" + portfolioId
                + " AND " + COLUMN_MONTH + "=" + month
                + " AND " + COLUMN_YEAR + "=" + year
                + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByRangeSortedByDate(int portfolioId, int yearFrom, int yearTo) {
        String query = "SELECT Sum(" + COLUMN_AMOUNT + ") as total_sum, "
                + COLUMN_MONTH + ", " + COLUMN_YEAR + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId
                + " AND " + COLUMN_YEAR + " BETWEEN " + yearFrom + " AND " + yearTo
                + " GROUP BY " + COLUMN_MONTH + ", " + COLUMN_YEAR
                + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByPortfolioIdSortedByCategoriesDesc(int portfolioId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId + " ORDER BY " + COLUMN_CATEGORY_ID + " DESC";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumAllEntriesByPortfolioId(int portfolioId, int month, int year) {
        String query = "SELECT Sum(" + COLUMN_AMOUNT + ") as total_sum FROM " + TABLE_NAME + " WHERE "
                + COLUMN_PORTFOLIO_ID + "=" + portfolioId +
                " AND " + COLUMN_MONTH + "=" + month +
                " AND " + COLUMN_YEAR + "=" + year;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumEntriesGroupedByCategory(int portfolioId, int month, int year) {
        String query = "SELECT Sum(" + COLUMN_AMOUNT + ") as sum_cat, " +
                COLUMN_CATEGORY_ID +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId +
                " AND " + COLUMN_MONTH + "=" + month +
                " AND " + COLUMN_YEAR + "=" + year +
                " GROUP BY " + COLUMN_CATEGORY_ID;

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
