package de.xyzerstudios.moneymanager.utils.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import de.xyzerstudios.moneymanager.utils.Utils;

public class CategoriesDatabaseHelper extends SQLiteOpenHelper {

    private static final String tag = "CategoriesDatabaseHelper";

    private static final String DATABASE_NAME = "Categories.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "categories";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type"; //expense or income
    private static final String COLUMN_COLOR = "color";
    private final Context context;
    private final Activity activity;


    public CategoriesDatabaseHelper(@Nullable Context context, Activity activity) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_COLOR + " INTEGER);";
        database.execSQL(query);

        Log.d(tag, "Database created.");

        Categories categoriesExpenses = new Categories(activity).expenses();
        ArrayList<String> categoriesExpensesNames = categoriesExpenses.getCategories();
        ArrayList<Integer> categoriesExpensesColors = categoriesExpenses.getColors();

        for (int i = 0; i < categoriesExpensesNames.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, categoriesExpensesNames.get(i));
            contentValues.put(COLUMN_COLOR, categoriesExpensesColors.get(i));
            contentValues.put(COLUMN_TYPE, "expense");
            long result = database.insert(TABLE_NAME, null, contentValues);
            Log.d(tag, "Result of category entry: " + result);
        }

        Categories categoriesIncome = new Categories(activity).income();
        ArrayList<String> categoriesIncomeNames = categoriesIncome.getCategories();
        ArrayList<Integer> categoriesIncomeColors = categoriesIncome.getColors();

        for (int i = 0; i < categoriesIncomeNames.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, categoriesIncomeNames.get(i));
            contentValues.put(COLUMN_COLOR, categoriesIncomeColors.get(i));
            contentValues.put(COLUMN_TYPE, "income");
            long result = database.insert(TABLE_NAME, null, contentValues);
            Log.d(tag, "Result of category entry: " + result);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addNewCategory(String name, String type, int color) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_COLOR, color);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Added new entry.");
        }
    }


    public void updateCategory(int categoryId, String name, int color) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_COLOR, color);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(categoryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void updateCategory(int categoryId, String name, String type, int color) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_COLOR, color);

        long result = database.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(categoryId)});
        if (result == -1) {
            Log.d(tag, "Something went wrong.");
        } else {
            Log.d(tag, "Entry updated.");
        }
    }

    public void deleteCategory(int categoryId) {
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(categoryId)});
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

    public Cursor readCategoryById(int categoryId) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + categoryId;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readCategoriesByType(String type) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TYPE + "=\"" + type + "\"";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

}
