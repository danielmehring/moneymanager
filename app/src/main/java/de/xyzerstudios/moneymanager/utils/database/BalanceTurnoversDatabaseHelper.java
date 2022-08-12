package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BalanceTurnoversDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "BalanceTurnoversDatabaseHelper";

    private static final String DATABASE_NAME = "BalancesTurnovers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "balances_turnovers";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_BALANCE_ID = "balance_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TYPE_IS_EXPENSE = "is_expense"; // 1=expense or 0=revenue
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_CATEGORY_COLOR = "category_color";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private final Context context;


    public BalanceTurnoversDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BALANCE_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_TYPE_IS_EXPENSE + " INTEGER, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_CATEGORY_COLOR + " INTEGER, " +
                COLUMN_PAYMENT_METHOD + " TEXT);";
        database.execSQL(query);

        Log.d(tag, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewEntry(int balanceId, String name, int amount, String timestamp,
                            TurnoverType turnoverType, String category, int categoryColor) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_BALANCE_ID, balanceId);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_TYPE_IS_EXPENSE, turnoverType == TurnoverType.EXPENSE ? 1 : 0);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_CATEGORY_COLOR, categoryColor);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void addNewEntry(int balanceId, String name, int amount, String timestamp,
                            TurnoverType turnoverType, String paymentMethod, String category, int categoryColor) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_BALANCE_ID, balanceId);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_TYPE_IS_EXPENSE, turnoverType == TurnoverType.EXPENSE ? 1 : 0);
        contentValues.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_CATEGORY_COLOR, categoryColor);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void updateEntry(int balanceEntryId, String name, int amount, String timestamp,
                            TurnoverType turnoverType, String paymentMethod, String category, int categoryColor) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_TYPE_IS_EXPENSE, turnoverType == TurnoverType.EXPENSE ? 1 : 0);
        contentValues.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_CATEGORY_COLOR, categoryColor);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateEntry(int balanceEntryId, String name, int amount, String timestamp,
                            TurnoverType turnoverType, String category, int categoryColor) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_TYPE_IS_EXPENSE, turnoverType == TurnoverType.EXPENSE ? 1 : 0);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_CATEGORY_COLOR, categoryColor);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateEntry(int balanceEntryId, int balanceId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_BALANCE_ID, balanceId);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }


    public void deleteEntry(int balanceEntryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(balanceEntryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry deleted.");
        }
    }

    public Cursor sumEntriesGroupedByType(int balanceId, TurnoverType turnoverType) {
        String query = "SELECT Sum(" + COLUMN_AMOUNT + ") as sum_cat" +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_BALANCE_ID + "=" + balanceId +
                " AND " + COLUMN_TYPE_IS_EXPENSE + "=" + (turnoverType == TurnoverType.EXPENSE ? "1" : "0") +
                " GROUP BY " + COLUMN_TYPE_IS_EXPENSE;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
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

    public Cursor readEntryById(int balanceEntryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + balanceEntryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readEntriesByBalanceId(int balanceId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_BALANCE_ID + "=" + balanceId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

}
