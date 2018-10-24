package com.example.dasha.keystrokedynamics;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataPreprocesser {

    String login;
    DBPassword dbHelper;
    ArrayList<Float> preprocessedData;
    SQLiteDatabase db;

    public DataPreprocesser (Context context, String log) {

        login = log;
        dbHelper = new DBPassword(context);
    }

    public void preprocAndInsert (List<String> rawData) {

        preprocessedData = preprocAndReturn(rawData);
        insertInDatabase(login, preprocessedData);
    }

    public ArrayList<Float> preprocAndReturn (List<String> rawData) {

        // TODO

        for (int i = 0; i < 11; i += 1) {

            String charSeq = rawData.get(i);

        }



        return preprocessedData;
    }

    public void insertInDatabase (String login, ArrayList<Float> row) {

        ContentValues cv = new ContentValues();
        cv.put("login", login);
        cv.put("password", row.toString());
        db = dbHelper.getWritableDatabase();
        db.insert("passwordData", null, cv);

    }

    private class DBPassword extends SQLiteOpenHelper {

        public DBPassword (Context context) {
            super(context, "Passwords", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table passwordData ("
                    + "id integer primary key autoincrement,"
                    + "login,"
                    + "password" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}
