package com.example.dasha.keystrokedynamics;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class AnomalyDetector {

    private double[] meanVector;
    private double[] stdVector;

    private DBRaw dbHelper;
    private SQLiteDatabase db;
    private Cursor c;

    private String login;
    private String TAG = "myLog";

    public AnomalyDetector (String login, Context context) {
        this.login = login;
        dbHelper = new DBRaw(context);
        this.meanVector = calculateMeanVector();
        //show(meanVector);
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

    public double countManhattanDistance (double[] data) {

        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.length; i++) {
            centered = data[i] - meanVector[i];
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

    private double countEuclideanDistance (double[] data) {

        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.length; i++) {
            centered = data[i] - meanVector[i];
            scaled = centered / stdVector[i];
            sum += scaled * scaled;
        }
        return Math.sqrt(sum);
    }

    private double countEuclideanDistance (double[] array1, ArrayList<Double> array2) {
        if (array1.length != array2.size())
            Log.d(TAG, "Different lengths!");
        int sum = 0;
        double difference;
        for (int i = 0; i < array1.length; i++ ) {
            difference = array1[i] - array2.get(i);
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    private double countEuclideanDistance (double[] array1, double[] array2) {
        if (array1.length != array2.length)
            Log.d(TAG, "Different lengths!");
        int sum = 0;
        double difference;
        for (int i = 0; i < array1.length; i++ ) {
            difference = array1[i] - array2[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    public double countLocalOutlierFactor (ArrayList<Double> data) {
        int kNeighbours = 3;
        // metric = Euclidean

        db = dbHelper.getWritableDatabase();
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);

        double[] result;
        double LOF = 1000;

        // count self density and find neighbours
        if (c != null) {
            String passwordVector;
            double value;
            double min1 = 1000000;
            double min2 = 1000000;
            double min3 = 1000000;
            double[] resultMin1 = null;
            double[] resultMin2 = null;
            double[] resultMin3 = null;

            if (c.moveToFirst()) {
                int passwordColIndex = c.getColumnIndex("password");

                do {
                    passwordVector = c.getString(passwordColIndex);
                    result = fromString(passwordVector);
                    value = countEuclideanDistance(result, data);
                    if (value < min3) {
                        if (value < min2) {
                            if (value < min1) {
                                min3 = min2;
                                min2 = min1;
                                min1 = value;

                                resultMin3 = resultMin2;
                                resultMin2 = resultMin1;
                                resultMin1 = result;
                            }
                            else {
                                min3 = min2;
                                min2 = value;

                                resultMin3 = resultMin2;
                                resultMin2 = result;
                            }
                        }
                        else {
                            min3 = value;

                            resultMin3 = result;
                        }
                    }
                } while (c.moveToNext());

                double selfDensity = (double) kNeighbours / (min1 + min2 + min3);

                double density1 = 0;
                double density2 = 0;
                double density3 = 0;

                //count densities of neighbours
                c = db.query("passwordData", columns, selection,
                        selectionArgs, null, null, null);

                ArrayList<Double> values1 = new ArrayList<>();
                ArrayList<Double> values2 = new ArrayList<>();
                ArrayList<Double> values3 = new ArrayList<>();

                if (c.moveToFirst()) {
                    passwordColIndex = c.getColumnIndex("password");

                    double value1;
                    double value2;
                    double value3;

                    do {
                        passwordVector = c.getString(passwordColIndex);
                        result = fromString(passwordVector);

                        value1 = countEuclideanDistance(result, resultMin1);
                        value2 = countEuclideanDistance(result, resultMin2);
                        value3 = countEuclideanDistance(result, resultMin3);

                        values1.add(value1);
                        values2.add(value2);
                        values3.add(value3);

                    } while (c.moveToNext());
                }

                //there would be 0 at the first place (distance with self)
                Collections.sort(values1);
                Collections.sort(values2);
                Collections.sort(values3);

                density1 = (double) kNeighbours / (values1.get(1) + values1.get(2) + values1.get(3));
                density2 = (double) kNeighbours / (values2.get(1) + values2.get(2) + values2.get(3));
                density3 = (double) kNeighbours / (values3.get(1) + values3.get(2) + values3.get(3));

                LOF = (density1 + density2 + density3) / (double) kNeighbours / selfDensity;
            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");
        return LOF;
    }

    public double countLocalOutlierFactorBySelf (double[] data) {
        int kNeighbours = 3;
        // metric = Euclidean

        db = dbHelper.getWritableDatabase();
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);

        double[] result;
        double LOF = 1000;

        // count self density and find neighbours
        if (c != null) {
            String passwordVector;
            double value;
            double min1 = 1000000;
            double min2 = 1000000;
            double min3 = 1000000;
            double[] resultMin1 = null;
            double[] resultMin2 = null;
            double[] resultMin3 = null;

            if (c.moveToFirst()) {
                int passwordColIndex = c.getColumnIndex("password");

                do {
                    passwordVector = c.getString(passwordColIndex);
                    result = fromString(passwordVector);
                    value = countEuclideanDistance(result, data);
                    if (value < min3 && value != 0) {
                        if (value < min2) {
                            if (value < min1) {
                                min3 = min2;
                                min2 = min1;
                                min1 = value;

                                resultMin3 = resultMin2;
                                resultMin2 = resultMin1;
                                resultMin1 = result;
                            }
                            else {
                                min3 = min2;
                                min2 = value;

                                resultMin3 = resultMin2;
                                resultMin2 = result;
                            }
                        }
                        else {
                            min3 = value;

                            resultMin3 = result;
                        }
                    }
                } while (c.moveToNext());

                double selfDensity = (double) kNeighbours / (min1 + min2 + min3);

                double density1 = 0;
                double density2 = 0;
                double density3 = 0;

                //count densities of neighbours
                c = db.query("passwordData", columns, selection,
                        selectionArgs, null, null, null);

                ArrayList<Double> values1 = new ArrayList<>();
                ArrayList<Double> values2 = new ArrayList<>();
                ArrayList<Double> values3 = new ArrayList<>();

                if (c.moveToFirst()) {
                    passwordColIndex = c.getColumnIndex("password");

                    double value1;
                    double value2;
                    double value3;

                    do {
                        passwordVector = c.getString(passwordColIndex);
                        result = fromString(passwordVector);

                        value1 = countEuclideanDistance(result, resultMin1);
                        value2 = countEuclideanDistance(result, resultMin2);
                        value3 = countEuclideanDistance(result, resultMin3);

                        values1.add(value1);
                        values2.add(value2);
                        values3.add(value3);

                    } while (c.moveToNext());
                }

                //there would be 0 at the first place (distance with self)
                Collections.sort(values1);
                Collections.sort(values2);
                Collections.sort(values3);

                density1 = (double) kNeighbours / (values1.get(1) + values1.get(2) + values1.get(3));
                density2 = (double) kNeighbours / (values2.get(1) + values2.get(2) + values2.get(3));
                density3 = (double) kNeighbours / (values3.get(1) + values3.get(2) + values3.get(3));

                LOF = (density1 + density2 + density3) / (double) kNeighbours / selfDensity;

            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");
        return LOF;
    }

    public double countThresholdEuclidean(int percentile) {
        db = dbHelper.getWritableDatabase();
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);

        double threshold = 1000000;
        double[] result;

        if (c != null) {
            String passwordVector;
            ArrayList<Double> values = new ArrayList<>();

            if (c.moveToFirst()) {
                int passwordColIndex = c.getColumnIndex("password");

                do {
                    passwordVector = c.getString(passwordColIndex);
                    result = fromString(passwordVector);
                    values.add(countEuclideanDistance(result));
                } while (c.moveToNext());
                threshold = Percentile(values, 100 - percentile);
            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");
        return threshold;
    }

    public double countThresholdEuclidean() { return countThresholdEuclidean(10); }

    public double countThresholdManhattan(int percentile) {
        db = dbHelper.getWritableDatabase();
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        c = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);

        double threshold = 1000000;
        double[] result;

        if (c != null) {
            String passwordVector;
            ArrayList<Double> values = new ArrayList<>();

            if (c.moveToFirst()) {
                int passwordColIndex = c.getColumnIndex("password");

                do {
                    passwordVector = c.getString(passwordColIndex);
                    result = fromString(passwordVector);
                    values.add(countManhattanDistance(result));
                } while (c.moveToNext());
                threshold = Percentile(values, 100 - percentile);
            }
            c.close();
        } else
            Log.d(TAG, "Cursor is null");
        return threshold;
    }

    public double countThresholdManhattan() { return countThresholdManhattan(10); }

    public double countThresholdLOF() { return countThresholdLOF(10); }

    public double countThresholdLOF (int percentile) {
        db = dbHelper.getWritableDatabase();
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        Cursor cAll = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);

        double threshold = 1000000;
        double[] result;

        if (cAll != null) {
            String passwordVector;
            ArrayList<Double> values = new ArrayList<>();

            if (cAll.moveToFirst()) {
                int passwordColIndex = cAll.getColumnIndex("password");

                do {
                    passwordVector = cAll.getString(passwordColIndex);
                    result = fromString(passwordVector);
                    values.add(countLocalOutlierFactorBySelf(result));
                } while (cAll.moveToNext());
                threshold = Percentile(values, 100 - percentile);
            }
            cAll.close();
        } else
            Log.d(TAG, "Cursor is null");
        return threshold;
    }

    private double Percentile(ArrayList<Double> values, double percentile) {
        Collections.sort(values);
        int index = (int)Math.ceil(((double) percentile / (double) 100) * (double) values.size());
        return values.get(index-1);
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
