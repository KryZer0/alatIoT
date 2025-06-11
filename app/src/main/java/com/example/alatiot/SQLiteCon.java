package com.example.alatiot;

import android.provider.BaseColumns;

public class SQLiteCon {
    private SQLiteCon() {}

    static class DataColumns implements BaseColumns {
        static final String TABLE_DATA = "data";
        static final String COLUMN_ID = "id_data";
        static final String COLUMN_GASCO = "gasco";
        static final String COLUMN_GASCO2 = "gasco2";
        static final String COLUMN_GASHC = "gashc";
        static final String COLUMN_HUMIDITY = "humidity";
        static final String COLUMN_TEMPERATURE = "temperature";
        static final String COLUMN_LED = "led";
        static final String COLUMN_KETERANGAN = "keterangan";

        static final String TABLE_USERS = "users";
        static final String COLUMN_ID_USERS = "id_users";
        static final String COLUMN_USERNAME = "username";
        static final String COLUMN_PASSWORD = "password";
    }
}
