package com.example.dasha.keystrokedynamics;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AnomalyDetector {

    private double[] meanVector;
    private double[] stdVector;

    private int numberOfRows;
    private double thresholdLOF;
    private double thresholdEuclidean;
    private double thresholdManhattan;

    private double scoreManhattan;
    private double scoreEuclidean;
    private double scoreLOF;

    private ArrayList<double []> featureVectorsList;
    private int featuresNumber;
    private double inf = Double.POSITIVE_INFINITY;

    private Context context;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private String login;
    private String TAG = "myLog";

    public AnomalyDetector (String login, Context context) {
        this.login = login;
        this.context = context;
        this.featureVectorsList = cursorToArrayList();
        this.featuresNumber = featureVectorsList.get(0).length;
        this.meanVector = calculateMeanVector();
        //show(meanVector);
        this.stdVector = calculateStdVector();
        show(stdVector);
        Log.d(TAG,"numberForThreshold=" + getNumberForThreshold());
    }

    private ArrayList<double []> cursorToArrayList() {
        DBRaw dbHelper = new DBRaw(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();;
        Cursor cursor;
        String[] columns = new String[] {"password"};
        String selection = "login = ?";
        String[] selectionArgs = new String[] { login };
        cursor = db.query("passwordData", columns, selection,
                selectionArgs, null, null, null);
        ArrayList<double []> featureVectorsList = new ArrayList<>();
        numberOfRows = 0;

        if (cursor != null) {
            double [] featureVector;
            if (cursor.moveToFirst()) {
                int passwordColIndex = cursor.getColumnIndex("password");
                do {
                    featureVector = fromString(cursor.getString(passwordColIndex));
                    featureVectorsList.add(featureVector);
                    numberOfRows++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else
            Log.d(TAG, "Cursor is null");

        return featureVectorsList;
    }

    public boolean makeDecision (ArrayList<Double> data) {
        if (numberOfRows - getNumberForThreshold() > 0) {

            thresholdLOF = countThresholdLOF();
            thresholdEuclidean = countThresholdEuclidean();
            thresholdManhattan = countThresholdManhattan();

            setThreshold("Euclidean", thresholdEuclidean);
            setThreshold("Manhattan", thresholdManhattan);
            setThreshold("LOF", thresholdLOF);

            setNumberForThreshold(numberOfRows);

        }
        else {
            thresholdManhattan = getThreshold("Manhattan");
            thresholdEuclidean = getThreshold("Euclidean");
            thresholdLOF = getThreshold("LOF");
        }

        scoreManhattan = countManhattanDistance(data);
        Log.d(TAG, "ThresManh=" + thresholdManhattan);
        Log.d(TAG, "scoreManhattan=" + scoreManhattan);
        scoreEuclidean = countEuclideanDistance(data);
        Log.d(TAG, "ThresEuclid=" + thresholdEuclidean);
        Log.d(TAG, "scoreEuclidean=" + scoreEuclidean);
        scoreLOF = countLocalOutlierFactor(data);
        Log.d(TAG, "thresLOF=" + thresholdLOF);
        Log.d(TAG, "scoreLOF = " + scoreLOF);

        boolean trueEuclidean = scoreEuclidean < thresholdEuclidean;
        boolean trueManhattan = scoreManhattan < thresholdManhattan;
        boolean trueLOF = scoreLOF < thresholdLOF;

        int sum = 0;
        sum += trueEuclidean ? 1 : 0;
        sum += trueManhattan ? 1 : 0;
        sum += trueLOF ? 1 : 0;

        Log.d(TAG, "SUM = " + sum);

        return sum >= 2;
    }

    public String returnScores () {
        return "Score / Threshold\nManh: " + new DecimalFormat("##.##").format(scoreManhattan) +
                " / " + new DecimalFormat("##.##").format(thresholdManhattan) +
                "\nEuclid: " + new DecimalFormat("##.##").format(scoreEuclidean) +
                " / " + new DecimalFormat("##.##").format(thresholdEuclidean) +
                "\nLOF: " + new DecimalFormat("##.##").format(scoreLOF) +
                " / " + new DecimalFormat("##.##").format(thresholdLOF);

    }

    private double countManhattanDistance (ArrayList<Double> data) {

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

    private double countManhattanDistance (double[] data) {

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

    private double countEuclideanDistance (ArrayList<Double> data) {

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

    private int getNumberForThreshold () {
        int num = 0;
        sharedPref = context.getSharedPreferences("numberForThreshold", Context.MODE_PRIVATE);
        if (sharedPref.contains(login))
            num = sharedPref.getInt(login, 0);
        return num;
    }

    private void setNumberForThreshold (int num) {
        sharedPref = context.getSharedPreferences("numberForThreshold", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putInt(login, num);
        editor.commit();
    }

    private double getThreshold (String method) {
        float threshold = 0;
        sharedPref = context.getSharedPreferences("numberForThreshold", Context.MODE_PRIVATE);
        if (sharedPref.contains(login+method))
            threshold = sharedPref.getFloat(login+method, 0);
        return (double) threshold;
    }

    private void setThreshold (String method, double threshold) {
        sharedPref = context.getSharedPreferences("numberForThreshold", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putFloat(login+method, (float) threshold);
        editor.commit();
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

    private double countLocalOutlierFactor (ArrayList<Double> data) {
        int kNeighbours = 3;
        // metric = Euclidean
        double LOF = 1000;
        // count self density and find neighbours
        String passwordVector;
        double value;
        double min1 = inf;
        double min2 = inf;
        double min3 = inf;
        double[] resultMin1 = null;
        double[] resultMin2 = null;
        double[] resultMin3 = null;

        for (double[] featureVector : featureVectorsList) {
            value = countEuclideanDistance(featureVector, data);
            if (value < min3) {
                if (value < min2) {
                    if (value < min1) {
                        min3 = min2;
                        min2 = min1;
                        min1 = value;

                        resultMin3 = resultMin2;
                        resultMin2 = resultMin1;
                        resultMin1 = featureVector;
                    }
                    else {
                        min3 = min2;
                        min2 = value;

                        resultMin3 = resultMin2;
                        resultMin2 = featureVector;
                    }
                }
                else {
                    min3 = value;

                    resultMin3 = featureVector;
                }
            }
        }
        double selfDensity = (double) kNeighbours / (min1 + min2 + min3);

        double density1;
        double density2;
        double density3;

        ArrayList<Double> values1 = new ArrayList<>();
        ArrayList<Double> values2 = new ArrayList<>();
        ArrayList<Double> values3 = new ArrayList<>();

        double value1;
        double value2;
        double value3;

        for (double[] featureVector : featureVectorsList) {
            value1 = countEuclideanDistance(featureVector, resultMin1);
            value2 = countEuclideanDistance(featureVector, resultMin2);
            value3 = countEuclideanDistance(featureVector, resultMin3);

            values1.add(value1);
            values2.add(value2);
            values3.add(value3);

        }

        //there would be 0 at the first place (distance with self)
        Collections.sort(values1);
        Collections.sort(values2);
        Collections.sort(values3);

        density1 = (double) kNeighbours / (values1.get(1) + values1.get(2) + values1.get(3));
        density2 = (double) kNeighbours / (values2.get(1) + values2.get(2) + values2.get(3));
        density3 = (double) kNeighbours / (values3.get(1) + values3.get(2) + values3.get(3));

        LOF = (density1 + density2 + density3) / (double) kNeighbours / selfDensity;

        return LOF;
    }

    private double countLocalOutlierFactorBySelf (double[] data) {
        int kNeighbours = 3;
        // metric = Euclidean
        double LOF = 1000;
        // count self density and find neighbours
        double value;
        double min1 = inf;
        double min2 = inf;
        double min3 = inf;
        double[] resultMin1 = null;
        double[] resultMin2 = null;
        double[] resultMin3 = null;


        for (double[] featureVector : featureVectorsList) {
            value = countEuclideanDistance(featureVector, data);
            if (value < min3 && value != 0) {
                if (value < min2) {
                    if (value < min1) {
                        min3 = min2;
                        min2 = min1;
                        min1 = value;

                        resultMin3 = resultMin2;
                        resultMin2 = resultMin1;
                        resultMin1 = featureVector;
                    }
                    else {
                        min3 = min2;
                        min2 = value;

                        resultMin3 = resultMin2;
                        resultMin2 = featureVector;
                    }
                }
                else {
                    min3 = value;

                    resultMin3 = featureVector;
                }
            }
        }

        double selfDensity = (double) kNeighbours / (min1 + min2 + min3);

        double density1;
        double density2;
        double density3;

        //count densities of neighbours
        ArrayList<Double> values1 = new ArrayList<>();
        ArrayList<Double> values2 = new ArrayList<>();
        ArrayList<Double> values3 = new ArrayList<>();

        double value1;
        double value2;
        double value3;

        for (double[] featureVector : featureVectorsList) {

            value1 = countEuclideanDistance(featureVector, resultMin1);
            value2 = countEuclideanDistance(featureVector, resultMin2);
            value3 = countEuclideanDistance(featureVector, resultMin3);

            values1.add(value1);
            values2.add(value2);
            values3.add(value3);

        }
        //there would be 0 at the first place (distance with self)
        Collections.sort(values1);
        Collections.sort(values2);
        Collections.sort(values3);

        density1 = (double) kNeighbours / (values1.get(1) + values1.get(2) + values1.get(3));
        density2 = (double) kNeighbours / (values2.get(1) + values2.get(2) + values2.get(3));
        density3 = (double) kNeighbours / (values3.get(1) + values3.get(2) + values3.get(3));

        LOF = (density1 + density2 + density3) / (double) kNeighbours / selfDensity;
        return LOF;
    }

    private double countThresholdEuclidean(int percentile) {
        double threshold;
        ArrayList<Double> values = new ArrayList<>();
        for (double[] featureVector : featureVectorsList) {
            values.add(countEuclideanDistance(featureVector));
        }
        threshold = Percentile(values, 100 - percentile);
        return threshold;
    }

    private double countThresholdEuclidean() { return countThresholdEuclidean(10); }

    private double countThresholdManhattan(int percentile) {
        double threshold;
        ArrayList<Double> values = new ArrayList<>();
        for (double[] featureVector : featureVectorsList){
            values.add(countManhattanDistance(featureVector));
        }
        threshold = Percentile(values, 100 - percentile);
        return threshold;
    }

    private double countThresholdManhattan() { return countThresholdManhattan(10); }

    private double countThresholdLOF() { return countThresholdLOF(10); }

    private double countThresholdLOF (int percentile) {
        double threshold;
        ArrayList<Double> values = new ArrayList<>();
        for (double[] featureVector : featureVectorsList) {
            values.add(countLocalOutlierFactorBySelf(featureVector));
        }
        threshold = Percentile(values, 100 - percentile);
        return threshold;
    }

    private double Percentile(ArrayList<Double> values, double percentile) {
        Collections.sort(values);
        int index = (int) Math.ceil(((double) percentile / (double) 100) * (double) values.size());
        return values.get(index-1);
    }

    private double[] calculateMeanVector() {
        double[] meanVector = new double[featuresNumber];
        Arrays.fill(meanVector, 0);
        for (double[] featureVector : featureVectorsList) {
            meanVector = sumArrays(meanVector, featureVector);
        }
        meanVector = divideBy(meanVector, numberOfRows);
        Log.d(TAG, "numberOfRows = " + numberOfRows);
        return meanVector;
    }

    private double[] calculateStdVector() {
        double [] stdVector = new double[featuresNumber];
        Arrays.fill(stdVector, 0);
        for (double [] featureVector : featureVectorsList) {
            stdVector = sumArrays(stdVector, powArray(minusArrays(featureVector, this.meanVector), 2));
        }
        stdVector = divideBy(stdVector, numberOfRows);
        stdVector = powArray(stdVector, 0.5);
        return stdVector;
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
