package com.example.suitcase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Create variables for database name, database table, and columns
    public static final String DB_NAME = "SuitCase.db";
    public static final int DB_VERSION = 1;
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_TABLE_NAME = "Users";
    public static final String ITEM_TABLE_NAME = "Items";
    // table columns
    public static final String ITEM_COLUMN_ID = "id";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_PRICE = "price"; // Corrected datatype to REAL
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_IMAGE = "image";
    public static final String ITEM_PURCHASED = "purchased";

    // Context for connecting other classes
    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating database sqlQuery
    @Override
    public void onCreate(SQLiteDatabase db) {
        // User Create method
        String userTableSqlQuery = "CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_EMAIL + " TEXT PRIMARY KEY, " +
                USER_PASSWORD + " TEXT)";

        // Item create method
        String itemTableSqlQuery = "CREATE TABLE " + ITEM_TABLE_NAME + " (" +
                ITEM_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT NOT NULL, " +
                ITEM_PRICE + " REAL NOT NULL, " + // Corrected datatype to REAL
                ITEM_DESCRIPTION + " TEXT, " +
                ITEM_IMAGE + " TEXT, " +
                ITEM_PURCHASED + " INTEGER)";
        try {
            db.execSQL(itemTableSqlQuery);
            db.execSQL(userTableSqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);

        onCreate(db);
    }

    // Save user detail in database (create new users)
    public Boolean insertUsers(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = sqLiteDatabase.insert("Users", null, contentValues);
        sqLiteDatabase.close(); // Close database connection
        return result != -1;
    }

    // Update password
    public Boolean updatePassword(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        int result = sqLiteDatabase.update("Users", contentValues, "email=?", new String[]{email});
        sqLiteDatabase.close(); // Close database connection
        return result != -1;
    }

    // Verify email for forgot password
    public Boolean checkEmail(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Close cursor
        sqLiteDatabase.close(); // Close database connection
        return exists;
    }

    // Check email and password for login
    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_EMAIL + "=? AND " + USER_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Close cursor
        sqLiteDatabase.close(); // Close database connection
        return exists;
    }

    // Query data with custom SQL query
    public Cursor queryData(String sqlQuery) {
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sqlQuery, null);
    }

    // Insert item into database
    public Boolean insertItem(String name,
                              double price,
                              String description,
                              String image,
                              boolean purchased) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + ITEM_TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, name);
        statement.bindDouble(2, price);
        statement.bindString(3, description);
        statement.bindString(4, image);
        statement.bindLong(5, purchased ? 1 : 0);
        long result = statement.executeInsert();
        database.close(); // Close database connection
        return result != -1;
    }

    // Get item by ID
    public Cursor getItemById(int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sqlQuery = "SELECT * FROM " + ITEM_TABLE_NAME + " WHERE " + ITEM_COLUMN_ID + "=?";
        return database.rawQuery(sqlQuery, new String[]{String.valueOf(id)});
    }

    // Get all items
    public Cursor getAllItem() {
        SQLiteDatabase database = getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + ITEM_TABLE_NAME;
        return database.rawQuery(sqlQuery, null);
    }

    // Update item
    public Boolean updateItem(int id,
                              String name,
                              double price,
                              String description,
                              String image,
                              boolean purchased) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_NAME, name);
        contentValues.put(ITEM_PRICE, price);
        contentValues.put(ITEM_DESCRIPTION, description);
        contentValues.put(ITEM_IMAGE, image);
        contentValues.put(ITEM_PURCHASED, purchased ? 1 : 0);
        int result = database.update(ITEM_TABLE_NAME, contentValues, ITEM_COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        database.close(); // Close database connection
        return result != -1;
    }

    // Delete item
    public void deleteItem(long id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(ITEM_TABLE_NAME, ITEM_COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        database.close(); // Close database connection
    }
}
