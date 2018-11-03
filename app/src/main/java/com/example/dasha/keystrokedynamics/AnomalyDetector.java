package com.example.dasha.keystrokedynamics;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class AnomalyDetector {

    double[] meanVector;
    double[] stdVector;

    DBRaw dbHelper;
    SQLiteDatabase db;
    Cursor c;

    String login;
    String TAG = "myLog";

    public AnomalyDetector (String login, Context context) {
        this.login = login;
        dbHelper = new DBRaw(context);
        this.meanVector = calculateMeanVector();
        show(meanVector);
        this.stdVector = calculateStdVector();
        //show(stdVector);
    }

    public double countManhattanDistance (ArrayList<Double> data) {

        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.size(); i++) {
            centered = data.get(i) - meanVector[i];
            scaled = centered / stdVector[i];
            sum += Math.abs(scaled);
        }
        return sum;
    }

    public double countEuclideanDistance (ArrayList<Double> data) {

        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.size(); i++) {
            centered = data.get(i) - meanVector[i];
            scaled = centered / stdVector[i];
            sum += scaled * scaled;
        }
        return Math.sqrt(sum);
    }

    public double countLocalOutlierFactor () {

        return 0;
    }

    private double[] calculateMeanVector() {
        db = dbHelper.getWritableDatabase();
        double[] result = null;
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);
        if (c != null) {
            String passwordVector;

            if (c.moveToFirst()) {
                int numberOfRows = 0;
                int passwordColIndex = c.getColumnIndex("password");
                passwordVector = c.getString(passwordColIndex);
                //Log.d(TAG, "password = " + passwordVector);
                numberOfRows++;

                result = fromString(passwordVector);

                while (c.moveToNext()) {
                    passwordVector = c.getString(passwordColIndex);
                    //Log.d(TAG, "password = " + passwordVector);
                    numberOfRows++;

                    result = sumArrays(result, fromString(passwordVector));
                }

                result = divideBy(result, numberOfRows);
            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");

        return result;

    }

    private double[] calculateStdVector() {
        db = dbHelper.getWritableDatabase();
        double[] result = null;
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);
        if (c != null) {
            String passwordVector;

            if (c.moveToFirst()) {
                int numberOfRows = 0;
                int passwordColIndex = c.getColumnIndex("password");
                passwordVector = c.getString(passwordColIndex);
                //Log.d(TAG, "password = " + passwordVector);
                numberOfRows++;

                result = powArray(minusArrays(fromString(passwordVector), this.meanVector), 2);

                while (c.moveToNext()) {
                    passwordVector = c.getString(passwordColIndex);
                    //Log.d(TAG, "password = " + passwordVector);
                    numberOfRows++;

                    result = sumArrays(result, powArray(minusArrays(fromString(passwordVector), this.meanVector), 2));
                }

                result = divideBy(result, numberOfRows);
                Log.d(TAG, "number=" + numberOfRows);
                result = powArray(result, 0.5);
            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");

        return result;

    }

    private double[] fromString(String string) {
        String[] strings = string.replace("[", "")
                .replace("]", "").split(", ");
        double result[] = new double[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(strings[i]);
        }
        return result;
    }

    private double[] sumArrays(double[] array1, double[] array2) {
        if (array1.length != array2.length)
            Log.d(TAG, "Different lengths!");
        double[] result = new double[array1.length];

        for ( int i = 0; i < array1.length; i++)
            result[i] = array1[i] + array2[i];
        return result;
    }

    private double[] minusArrays(double[] array1, double[] array2) {
        if (array1.length != array2.length)
            Log.d(TAG, "Different lengths!");
        double[] result = new double[array1.length];

        for ( int i = 0; i < array1.length; i++)
            result[i] = array1[i] - array2[i];
        return result;
    }

    private double[] divideBy(double[] array, double value) {
        double[] result = new double[array.length];

        for (int i = 0; i < array.length; i++ )
            result[i] = array[i] / (double) value;

        return result;
    }

    private double[] powArray(double[] array, double value) {
        double[] result = new double[array.length];

        for (int i = 0; i < array.length; i++ )
            result[i] = Math.pow(array[i], (double) value);

        return result;
    }

    private void show(double[] array) {
        Log.d(TAG, "ARRAY");
        for (int i = 0; i < array.length; i++) {
            if (i % 4 == 0)
                Log.d(TAG, " ");
            Log.d(TAG, " " + array[i]);
        }
    }

}
