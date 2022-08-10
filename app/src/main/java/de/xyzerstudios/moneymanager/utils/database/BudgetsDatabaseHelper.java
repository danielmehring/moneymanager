package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class BudgetsDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "BudgetsDatabaseHelper";

    private static final String DATABASE_NAME = "Budgets.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "budgets";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_AMOUNT_LIMIT = "amount_limit";
    private final Context context;


    public BudgetsDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_ID + " INTEGER, " +
                COLUMN_AMOUNT_LIMIT + " INTEGER);";
        database.execSQL(query);

        Log.d(tag, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewEntry(int categoryId, int amountLimit) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_AMOUNT_LIMIT, amountLimit);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void updateEntry(int budgetEntryId, int amountLimit) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_AMOUNT_LIMIT, amountLimit);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(budgetEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateEntry(int budgetEntryId, int categoryId, int amountLimit) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_AMOUNT_LIMIT, amountLimit);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(budgetEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }



    public void deleteEntry(int budgetEntryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(budgetEntryId)});
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

    public Cursor readEntryById(int budgetEntryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + budgetEntryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByCategoryId(int categoryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY_ID + "=" + categoryId;
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
