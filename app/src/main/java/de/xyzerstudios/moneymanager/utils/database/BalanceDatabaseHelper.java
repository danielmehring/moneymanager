package de.xyzerstudios.moneymanager.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;

import de.xyzerstudios.moneymanager.utils.Utils;

public class BalanceDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "BalanceDatabaseHelper";

    private static final String DATABASE_NAME = "Balances.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "balances";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIMESTAMP_CREATED = "timestamp_created";
    private static final String COLUMN_PORTFOLIO_ID = "portfolio_id";
    private static final String COLUMN_REVENUES_SUM = "revenue_sum";
    private static final String COLUMN_EXPENSES_SUM = "expense_sum";
    private static final String COLUMN_SALDO = "saldo_sum";
    private static final String COLUMN_MONTH = "month_created";
    private static final String COLUMN_YEAR = "created_created";
    private final Context context;


    public BalanceDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIMESTAMP_CREATED + " TEXT, " +
                COLUMN_PORTFOLIO_ID + " INTEGER, " +
                COLUMN_REVENUES_SUM + " INTEGER, " +
                COLUMN_EXPENSES_SUM + " INTEGER, " +
                COLUMN_SALDO + " INTEGER, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_YEAR + " INTEGER);";
        database.execSQL(query);

        Log.d(tag, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewBalance(String name, String timestampOfCreation, int portfolioId, int month, int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TIMESTAMP_CREATED, timestampOfCreation);
        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);
        contentValues.put(COLUMN_REVENUES_SUM, 0);
        contentValues.put(COLUMN_EXPENSES_SUM, 0);
        contentValues.put(COLUMN_SALDO, 0);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void updateBalance(int balanceId, String name, int portfolioId, String timestamp, int month, int year) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);
        contentValues.put(COLUMN_TIMESTAMP_CREATED, timestamp);
        contentValues.put(COLUMN_MONTH, month);
        contentValues.put(COLUMN_YEAR, year);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateBalance(int balanceId, String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateBalance(int balanceId, int portfolioId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PORTFOLIO_ID, portfolioId);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateBalance(int balanceId, int revenuesSum, int expensesSum, int saldoTotal) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_REVENUES_SUM, revenuesSum);
        contentValues.put(COLUMN_EXPENSES_SUM, expensesSum);
        contentValues.put(COLUMN_SALDO, saldoTotal);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(balanceId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void deleteBalance(int balanceId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(balanceId)});
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

    public Cursor readBalanceById(int balanceId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + balanceId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumAllByPortfolioId(int portfolioId) {
        String query = "SELECT Sum(" + COLUMN_REVENUES_SUM + ") as sum_revenues, " +
                " Sum(" + COLUMN_EXPENSES_SUM + ") as sum_expenses, " +
                " Sum(" + COLUMN_SALDO + ") as sum_saldo" +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId +
                " GROUP BY " + COLUMN_PORTFOLIO_ID;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumAllByPortfolioIdAndDate(int portfolioId, int month, int year) {
        String query = "SELECT Sum(" + COLUMN_REVENUES_SUM + ") as sum_revenues, " +
                " Sum(" + COLUMN_EXPENSES_SUM + ") as sum_expenses, " +
                " Sum(" + COLUMN_SALDO + ") as sum_saldo" +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId +
                " AND " + COLUMN_MONTH + "=" + month +
                " AND " + COLUMN_YEAR + "=" + year +
                " GROUP BY " + COLUMN_PORTFOLIO_ID;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor sumAllSaldosByPortfolioId(int portfolioId) {
        String query = "SELECT Sum(" + COLUMN_SALDO + ") as summe" +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PORTFOLIO_ID + "=" + portfolioId +
                " GROUP BY " + COLUMN_PORTFOLIO_ID;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

}
