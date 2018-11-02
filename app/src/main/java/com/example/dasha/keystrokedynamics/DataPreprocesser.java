package com.example.dasha.keystrokedynamics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataPreprocesser {

    String TAG = "myLog";

    //ArrayList<Double> preprocessedData;

    public DataPreprocesser () {}

    /*public void preprocAndInsert (List<String> rawData, String login) {

        preprocessedData = preprocAndReturn(rawData);
        insertInDatabase(login, preprocessedData);
        Log.d(TAG, "+");
    }*/

    public ArrayList<Double> preprocAndReturn (List<String> rawData) {

        ArrayList<Double> resultFeatureVector = new ArrayList<>();

        ArrayList<Double> holds = new ArrayList<>();
        ArrayList<Double> xOrient = new ArrayList<>();
        ArrayList<Double> yOrient = new ArrayList<>();
        ArrayList<Double> zOrient = new ArrayList<>();
        ArrayList<Double> xGravity = new ArrayList<>();
        ArrayList<Double> yGravity = new ArrayList<>();
        ArrayList<Double> zGravity= new ArrayList<>();
        //ArrayList<Double> xGyro = new ArrayList<>();
        //ArrayList<Double> yGyro = new ArrayList<>();
        //ArrayList<Double> zGyro = new ArrayList<>();
        ArrayList<Double> xAcc = new ArrayList<>();
        ArrayList<Double> yAcc = new ArrayList<>();
        ArrayList<Double> zAcc= new ArrayList<>();
        long lastUp = -1;
        double hold = 0;
        double UD = 0;
        double DD = 0;
        for ( int i = 0; i < rawData.size(); i++ ) {

            String charSeq = rawData.get(i);
            String[] myVec = charSeq.split(";");

            // add x, y coordinates
            resultFeatureVector.add(Double.parseDouble(myVec[14]));
            resultFeatureVector.add(Double.parseDouble(myVec[15]));

            // add sensors to own arrays
            xOrient.add(Double.parseDouble(myVec[2]));
            yOrient.add(Double.parseDouble(myVec[3]));
            zOrient.add(Double.parseDouble(myVec[4]));
            xAcc.add(Double.parseDouble(myVec[5]));
            yAcc.add(Double.parseDouble(myVec[6]));
            zAcc.add(Double.parseDouble(myVec[7]));
            xGravity.add(Double.parseDouble(myVec[8]));
            yGravity.add(Double.parseDouble(myVec[9]));
            zGravity.add(Double.parseDouble(myVec[10]));
            holds.add(hold);

            // add flight time and DD time
            if (i > 0) {
                hold = getHoldTime(myVec);
                UD = Long.parseLong(myVec[1]) - lastUp;
                DD = UD + hold;
                resultFeatureVector.add(UD);
                resultFeatureVector.add(DD);
            }

            lastUp = Long.parseLong(myVec[16]);

        }

        // Calculate mean values
        resultFeatureVector.add(getMean(xOrient));
        resultFeatureVector.add(getMean(yOrient));
        resultFeatureVector.add(getMean(zOrient));
        resultFeatureVector.add(getMean(xAcc));
        resultFeatureVector.add(getMean(yAcc));
        resultFeatureVector.add(getMean(zAcc));
        resultFeatureVector.add(getMean(xGravity));
        resultFeatureVector.add(getMean(yGravity));
        resultFeatureVector.add(getMean(zGravity));
        resultFeatureVector.add(getMean(holds));
        // Calculate std
        resultFeatureVector.add(getSTD(xOrient));
        resultFeatureVector.add(getSTD(yOrient));
        resultFeatureVector.add(getSTD(zOrient));
        resultFeatureVector.add(getSTD(xAcc));
        resultFeatureVector.add(getSTD(yAcc));
        resultFeatureVector.add(getSTD(zAcc));
        resultFeatureVector.add(getSTD(xGravity));
        resultFeatureVector.add(getSTD(yGravity));
        resultFeatureVector.add(getSTD(zGravity));
        resultFeatureVector.add(getSTD(holds));
        // Add all data from sensors to final array
        resultFeatureVector.addAll(xOrient);
        resultFeatureVector.addAll(yOrient);
        resultFeatureVector.addAll(zOrient);
        resultFeatureVector.addAll(xAcc);
        resultFeatureVector.addAll(yAcc);
        resultFeatureVector.addAll(zAcc);
        resultFeatureVector.addAll(xGravity);
        resultFeatureVector.addAll(yGravity);
        resultFeatureVector.addAll(zGravity);
        resultFeatureVector.addAll(holds);

        Log.d(TAG, resultFeatureVector.toString());

        return resultFeatureVector;
    }

    private double getHoldTime(String[] vector) {
        Long result = Long.parseLong(vector[16]) - Long.parseLong(vector[1]);
        return result.doubleValue();
    }

    private double getMean(ArrayList<Double> array) {
        double sum = 0;
        for (double element : array)
            sum += element;
        return sum / (double) array.size();
    }

    private double getSTD(ArrayList<Double> array) {
        double mean = getMean(array);
        double sum = 0;
        for (double element : array)
            sum += Math.pow(element - mean, 2);
        double meanOfDiffs = sum / (double) array.size();
        return Math.sqrt(meanOfDiffs);
    }

    /*public void insertInDatabase (String login, ArrayList<Double> row) {

        ContentValues cv = new ContentValues();
        cv.put("login", login);
        cv.put("password", row.toString());
        db = dbHelper.getWritableDatabase();
        db.insert("passwordData",null, cv);

    }

    private class DBPassword extends SQLiteOpenHelper {

        public DBPassword (Context context) {
            super(context, "Passwords", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table passwordData ("
                    + "id integer primary key autoincrement,"
                    + "login text,"
                    + "password text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

    public void clearDatabase () {
        db = dbHelper.getWritableDatabase();
        //CLEAR
        Log.d(TAG, "--- Clear database: ---");
        // удаляем все записи
        int clearCount = db.delete("passwordData", null, null);
        Log.d(TAG, "deleted rows count = " + clearCount);
    }

    public void showDatabase () {
        db = dbHelper.getWritableDatabase();
        //READ ALL
        Log.d(TAG, "--- Rows in passwordTable: ---");
        Cursor c = db.query("passwordData", null, null,
                null, null, null, null);

        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex("id");
            int loginColIndex = c.getColumnIndex("login");
            int passwordColIndex = c.getColumnIndex("password");

            do {
                Log.d(TAG, "ID = " + c.getInt(idColIndex) +
                        ", login = " + c.getString(loginColIndex) +
                        ", password = " + c.getString(passwordColIndex));

            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        c.close();
    } */
}
