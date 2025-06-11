package com.example.alatiot;

import static com.example.alatiot.SQLiteCon.DataColumns.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2; // Naikkan versi DB
    private static final String DATABASE_NAME = "DbData";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DATA_TABLE =
                "CREATE TABLE " + TABLE_DATA + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_GASCO + " REAL, " +
                        COLUMN_GASCO2 + " REAL, " +
                        COLUMN_GASHC + " REAL, " +
                        COLUMN_HUMIDITY + " REAL, " +
                        COLUMN_TEMPERATURE + " REAL, " +
                        COLUMN_LED + " TEXT, " +
                        COLUMN_KETERANGAN + " TEXT)";

        String SQL_CREATE_USERS_TABLE =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COLUMN_ID_USERS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME + " TEXT, " +
                        COLUMN_PASSWORD + " TEXT)";

        db.execSQL(SQL_CREATE_DATA_TABLE);
        db.execSQL(SQL_CREATE_USERS_TABLE);
        insertDummyData(db);

        Log.d("SQLiteHelper", "Executing query: " + SQL_CREATE_DATA_TABLE);
        Log.d("SQLiteHelper", "Executing query: " + SQL_CREATE_USERS_TABLE);
    }

    public void insertDummyData(SQLiteDatabase db) {
        String insertData = "INSERT INTO " + TABLE_DATA + " (" +
                COLUMN_GASCO + ", " +
                COLUMN_GASCO2 + ", " +
                COLUMN_GASHC + ", " +
                COLUMN_HUMIDITY + ", " +
                COLUMN_TEMPERATURE + ", " +
                COLUMN_LED + ", " +
                COLUMN_KETERANGAN + ") VALUES " +
                "(10.5, 20.3, 5.6, 55.0, 28.5, '0', 'Normal'), " +
                "(12.8, 18.7, 6.1, 65.0, 30.0, '1', 'Warning'), " +
                "(9.3, 21.1, 4.9, 50.0, 27.0, '0', 'Safe');";

        String insertUsers = "INSERT INTO " + TABLE_USERS + " (" +
                COLUMN_USERNAME + ", " + COLUMN_PASSWORD + ") VALUES " +
                "('admin', 'password123'), " +
                "('ahmad', 'ahmad'), " +
                "('user2', 'securepass');";

        db.execSQL(insertData);
        db.execSQL(insertUsers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_DATA_TABLE = "DROP TABLE IF EXISTS " + TABLE_DATA;
        String SQL_DELETE_USERS_TABLE = "DROP TABLE IF EXISTS " + TABLE_USERS;
        db.execSQL(SQL_DELETE_DATA_TABLE);
        db.execSQL(SQL_DELETE_USERS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}