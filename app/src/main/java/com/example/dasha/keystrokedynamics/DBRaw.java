package com.example.dasha.keystrokedynamics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBRaw extends SQLiteOpenHelper {

    public DBRaw (Context context) {
        super(context, "Passwords", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table rawData ("
                + "id integer primary key autoincrement,"
                + "text text" + ");");

        db.execSQL("create table passwordData ("
                + "id integer primary key autoincrement,"
                + "login text,"
                + "password text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}