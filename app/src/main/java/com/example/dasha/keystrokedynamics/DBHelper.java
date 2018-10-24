package com.example.dasha.keystrokedynamics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "rawData", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table rawData ("
                + "id integer primary key autoincrement,"
                + "login,"
                + "text" + ");");

        db.execSQL("create table passwordData ("
                + "id integer primary key autoincrement,"
                + "login,"
                + "password" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}