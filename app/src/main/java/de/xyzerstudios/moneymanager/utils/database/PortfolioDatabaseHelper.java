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

public class PortfolioDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "PortfolioDatabaseHelper";

    private static final String DATABASE_NAME = "Portfolios.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "portfolios";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIMESTAMP_CREATED = "timestamp_created";
    private static final String COLUMN_REVENUES_SUM = "revenue_sum";
    private static final String COLUMN_EXPENSES_SUM = "expense_sum";
    private static final String COLUMN_SALDO = "saldo_sum";
    private static final String COLUMN_DESCRIPTION = "description";
    private final Context context;


    public PortfolioDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIMESTAMP_CREATED + " TEXT, " +
                COLUMN_REVENUES_SUM + " INTEGER, " +
                COLUMN_EXPENSES_SUM + " INTEGER, " +
                COLUMN_SALDO + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT);";
        database.execSQL(query);

        Log.d(tag, "Database created.");

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, "Portfolio 1");
        Date date = new Date();
        contentValues.put(COLUMN_TIMESTAMP_CREATED, Utils.isoDateFormat.format(date));
        contentValues.put(COLUMN_REVENUES_SUM, 0);
        contentValues.put(COLUMN_EXPENSES_SUM, 0);
        contentValues.put(COLUMN_SALDO, 0);
        contentValues.put(COLUMN_DESCRIPTION, "Default portfolio.");

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewPortfolio(String name, String timestampOfCreation, String description) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TIMESTAMP_CREATED, timestampOfCreation);
        contentValues.put(COLUMN_REVENUES_SUM, 0);
        contentValues.put(COLUMN_EXPENSES_SUM, 0);
        contentValues.put(COLUMN_SALDO, 0);
        contentValues.put(COLUMN_DESCRIPTION, description);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }

    public void updatePortfolio(int portfolioId, String name, String description) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DESCRIPTION, description);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(portfolioId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updatePortfolio(int portfolioId, int revenuesSum, int expensesSum, int saldoTotal) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_REVENUES_SUM, revenuesSum);
        contentValues.put(COLUMN_EXPENSES_SUM, expensesSum);
        contentValues.put(COLUMN_SALDO, saldoTotal);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(portfolioId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void deletePortfolio(int portfolioId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(portfolioId)});
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

    public Cursor readPortfolioById(int portfolioId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + portfolioId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readPortfolioByIdMonthAndYear(int portfolioId, int month, int year) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + portfolioId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

}
