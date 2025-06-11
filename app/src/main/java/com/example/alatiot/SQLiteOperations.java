package com.example.alatiot;

import static com.example.alatiot.SQLiteCon.DataColumns.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class SQLiteOperations
{
    SQLiteHelper sqliteHelper;

    public SQLiteOperations(Context context)
    {
        this.sqliteHelper = new SQLiteHelper(context);
    }

    void addUser(DataModel dataModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GASCO, dataModel.getGasCo());
        values.put(COLUMN_GASCO2, dataModel.getGasCo2());
        values.put(COLUMN_GASHC, dataModel.getGasHc());
        values.put(COLUMN_KETERANGAN, dataModel.getKeterangan());

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_USERS, null, values);

        db.close();
    }

    void addData(Double humidity, Double temp, String ledStatus) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_TEMPERATURE, temp);
        values.put(COLUMN_LED, ledStatus.equals("0") ? 0 : 1); // Simpan sebagai integer 0 atau 1

        // Default values jika nilai sensor gas belum tersedia
        values.put(COLUMN_GASCO, 0.0);
        values.put(COLUMN_GASCO2, 0.0);
        values.put(COLUMN_GASHC, 0.0);
        values.put(COLUMN_KETERANGAN, "N/A");

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.insert(TABLE_DATA, null, values);
        db.close();
    }


    DataModel getData(int id) {
        DataModel dataItem = new DataModel();

        String[] projection = {
                COLUMN_ID,
                COLUMN_GASCO,
                COLUMN_GASCO2,
                COLUMN_GASHC,
                COLUMN_KETERANGAN
        };
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                dataItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                dataItem.setGasCo(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GASCO))));
                dataItem.setGasCo2(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GASCO2))));
                dataItem.setGasHc(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GASHC))));
                dataItem.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN)));

                Log.d("getUser(" + id + ")", dataItem.toString());
            }
            cursor.close();
        }
        return dataItem;
    }

    List<DataModel> getAllData() {
        List<DataModel> dataModels = new LinkedList<DataModel>();
        String query = "SELECT * FROM " + TABLE_DATA;
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            DataModel dataItems = null;
            do {
                dataItems = new DataModel();
                dataItems.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                dataItems.setGasCo(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GASCO)));
                dataItems.setGasCo2(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GASCO2)));
                dataItems.setGasHc(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GASHC)));
                dataItems.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KETERANGAN)));
                dataModels.add(dataItems);
            } while (cursor.moveToNext());
        }
        Log.d("getAllUsers()", "Total users count: " + dataModels.size());
        return dataModels;
    }

    public int getUserCount() {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }


    void updateData(DataModel userItem, String userItemId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GASCO, userItem.getGasCo());
        values.put(COLUMN_GASCO2, userItem.getGasCo2());
        values.put(COLUMN_GASHC, userItem.getGasHc());
        values.put(COLUMN_KETERANGAN, userItem.getKeterangan());

        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.update(
                TABLE_DATA,
                values,
                COLUMN_ID + " = ?",
                new String[]{userItemId}
        );
        db.close();
    }

    void deleteData(DataModel userItem) {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(
                TABLE_DATA,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userItem.getId())}
        );
        db.close();
    }
    void deleteAllUsers() {
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(
                TABLE_DATA,
                null,
                null
        );
    }
    public UserModel getUser(String username, String password) {
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + ", " + COLUMN_PASSWORD +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        UserModel user = null;
        if (cursor.moveToFirst()) {
            user = new UserModel(
                    cursor.getString(0),
                    cursor.getString(1)
            );
        }
        cursor.close();
        db.close();
        return user;
    }
}
