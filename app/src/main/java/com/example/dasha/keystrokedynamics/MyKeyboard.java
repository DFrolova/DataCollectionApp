package com.example.dasha.keystrokedynamics;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Calendar;

import static android.content.Context.SENSOR_SERVICE;

public class MyKeyboard extends LinearLayout implements View.OnClickListener,
        View.OnTouchListener {

    private Button button1, button2, button3, button4,
            button5, button6, button7, button8,
            button9, button0, buttonDelete,
            buttonQ, buttonW, buttonE, buttonR, buttonT,
            buttonY, buttonU, buttonI, buttonO, buttonP,
            buttonA, buttonS, buttonD, buttonF, buttonG,
            buttonH, buttonJ, buttonK, buttonL, buttonQuest,
            buttonShift, buttonZ, buttonX, buttonC, buttonV,
            buttonN, buttonB, buttonM, buttonDot, buttonExcl,
            buttonComma, buttonSpace;

    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    private boolean isShifted = false;
    private boolean isUpper = false;

    SensorManager sensorManager;
    Sensor sensorGyro, sensorMagnet, sensorAccel;
    Sensor sensorLinAccel, sensorGravity;

    DBHelper dbHelper;

    String textCode, textDown, textUp, textForFile;
    String TAG = "myLog";
    String login;
    String sequence;

    public MyKeyboard(Context context) {
        this(context, null, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);

        button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(this);
        button1.setOnTouchListener(this);
        button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(this);
        button2.setOnTouchListener(this);
        button3 = (Button) findViewById(R.id.button_3);
        button3.setOnClickListener(this);
        button3.setOnTouchListener(this);
        button4 = (Button) findViewById(R.id.button_4);
        button4.setOnClickListener(this);
        button4.setOnTouchListener(this);
        button5 = (Button) findViewById(R.id.button_5);
        button5.setOnClickListener(this);
        button5.setOnTouchListener(this);
        button6 = (Button) findViewById(R.id.button_6);
        button6.setOnClickListener(this);
        button6.setOnTouchListener(this);
        button7 = (Button) findViewById(R.id.button_7);
        button7.setOnClickListener(this);
        button7.setOnTouchListener(this);
        button8 = (Button) findViewById(R.id.button_8);
        button8.setOnClickListener(this);
        button8.setOnTouchListener(this);
        button9 = (Button) findViewById(R.id.button_9);
        button9.setOnClickListener(this);
        button9.setOnTouchListener(this);
        button0 = (Button) findViewById(R.id.button_0);
        button0.setOnClickListener(this);
        button0.setOnTouchListener(this);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);
        buttonDelete.setOnTouchListener(this);
        buttonQ = (Button) findViewById(R.id.button_q);
        buttonQ.setOnClickListener(this);
        buttonQ.setOnTouchListener(this);
        buttonW = (Button) findViewById(R.id.button_w);
        buttonW.setOnClickListener(this);
        buttonW.setOnTouchListener(this);
        buttonE = (Button) findViewById(R.id.button_e);
        buttonE.setOnClickListener(this);
        buttonE.setOnTouchListener(this);
        buttonR = (Button) findViewById(R.id.button_r);
        buttonR.setOnClickListener(this);
        buttonR.setOnTouchListener(this);
        buttonT = (Button) findViewById(R.id.button_t);
        buttonT.setOnClickListener(this);
        buttonT.setOnTouchListener(this);
        buttonY = (Button) findViewById(R.id.button_y);
        buttonY.setOnClickListener(this);
        buttonY.setOnTouchListener(this);
        buttonU = (Button) findViewById(R.id.button_u);
        buttonU.setOnClickListener(this);
        buttonU.setOnTouchListener(this);
        buttonI = (Button) findViewById(R.id.button_i);
        buttonI.setOnClickListener(this);
        buttonI.setOnTouchListener(this);
        buttonO = (Button) findViewById(R.id.button_o);
        buttonO.setOnClickListener(this);
        buttonO.setOnTouchListener(this);
        buttonP = (Button) findViewById(R.id.button_p);
        buttonP.setOnClickListener(this);
        buttonP.setOnTouchListener(this);
        buttonA = (Button) findViewById(R.id.button_a);
        buttonA.setOnClickListener(this);
        buttonA.setOnTouchListener(this);
        buttonS = (Button) findViewById(R.id.button_s);
        buttonS.setOnClickListener(this);
        buttonS.setOnTouchListener(this);
        buttonD = (Button) findViewById(R.id.button_d);
        buttonD.setOnClickListener(this);
        buttonD.setOnTouchListener(this);
        buttonF = (Button) findViewById(R.id.button_f);
        buttonF.setOnClickListener(this);
        buttonF.setOnTouchListener(this);
        buttonG = (Button) findViewById(R.id.button_g);
        buttonG.setOnClickListener(this);
        buttonG.setOnTouchListener(this);
        buttonH = (Button) findViewById(R.id.button_h);
        buttonH.setOnClickListener(this);
        buttonH.setOnTouchListener(this);
        buttonJ = (Button) findViewById(R.id.button_j);
        buttonJ.setOnClickListener(this);
        buttonJ.setOnTouchListener(this);
        buttonK = (Button) findViewById(R.id.button_k);
        buttonK.setOnClickListener(this);
        buttonK.setOnTouchListener(this);
        buttonL = (Button) findViewById(R.id.button_l);
        buttonL.setOnClickListener(this);
        buttonL.setOnTouchListener(this);
        buttonQuest = (Button) findViewById(R.id.button_quest);
        buttonQuest.setOnClickListener(this);
        buttonQuest.setOnTouchListener(this);
        buttonShift = (Button) findViewById(R.id.button_shift);
        buttonShift.setOnClickListener(this);
        buttonShift.setOnTouchListener(this);
        buttonZ = (Button) findViewById(R.id.button_z);
        buttonZ.setOnClickListener(this);
        buttonZ.setOnTouchListener(this);
        buttonX = (Button) findViewById(R.id.button_x);
        buttonX.setOnClickListener(this);
        buttonX.setOnTouchListener(this);
        buttonC = (Button) findViewById(R.id.button_c);
        buttonC.setOnClickListener(this);
        buttonC.setOnTouchListener(this);
        buttonV = (Button) findViewById(R.id.button_v);
        buttonV.setOnClickListener(this);
        buttonV.setOnTouchListener(this);
        buttonB = (Button) findViewById(R.id.button_b);
        buttonB.setOnClickListener(this);
        buttonB.setOnTouchListener(this);
        buttonN = (Button) findViewById(R.id.button_n);
        buttonN.setOnClickListener(this);
        buttonN.setOnTouchListener(this);
        buttonM = (Button) findViewById(R.id.button_m);
        buttonM.setOnClickListener(this);
        buttonM.setOnTouchListener(this);
        buttonDot = (Button) findViewById(R.id.button_dot);
        buttonDot.setOnClickListener(this);
        buttonDot.setOnTouchListener(this);
        buttonExcl = (Button) findViewById(R.id.button_exclam);
        buttonExcl.setOnClickListener(this);
        buttonExcl.setOnTouchListener(this);
        buttonComma = (Button) findViewById(R.id.button_comma);
        buttonComma.setOnClickListener(this);
        buttonComma.setOnTouchListener(this);
        buttonSpace = (Button) findViewById(R.id.button_space);
        buttonSpace.setOnClickListener(this);
        buttonSpace.setOnTouchListener(this);

        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");

        keyValues.put(R.id.button_q, "q");
        keyValues.put(R.id.button_w, "w");
        keyValues.put(R.id.button_e, "e");
        keyValues.put(R.id.button_r, "r");
        keyValues.put(R.id.button_t, "t");
        keyValues.put(R.id.button_y, "y");
        keyValues.put(R.id.button_u, "u");
        keyValues.put(R.id.button_i, "i");
        keyValues.put(R.id.button_o, "o");
        keyValues.put(R.id.button_p, "p");

        keyValues.put(R.id.button_a, "a");
        keyValues.put(R.id.button_s, "s");
        keyValues.put(R.id.button_d, "d");
        keyValues.put(R.id.button_f, "f");
        keyValues.put(R.id.button_g, "g");
        keyValues.put(R.id.button_h, "h");
        keyValues.put(R.id.button_j, "j");
        keyValues.put(R.id.button_k, "k");
        keyValues.put(R.id.button_l, "l");
        keyValues.put(R.id.button_quest, "?");
        keyValues.put(R.id.button_z, "z");
        keyValues.put(R.id.button_x, "x");
        keyValues.put(R.id.button_c, "c");
        keyValues.put(R.id.button_v, "v");
        keyValues.put(R.id.button_b, "b");
        keyValues.put(R.id.button_n, "n");
        keyValues.put(R.id.button_m, "m");
        keyValues.put(R.id.button_dot, ".");
        keyValues.put(R.id.button_exclam, "!");
        keyValues.put(R.id.button_comma, ",");
        keyValues.put(R.id.button_space, " ");

        keyValues.put(R.id.button_shift, "$");
        keyValues.put(R.id.button_delete, "@");

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorGyro, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorLinAccel, SensorManager.SENSOR_DELAY_FASTEST);

        dbHelper = new DBHelper(context);

        sequence = "";

    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        int code = v.getId();
        String value = keyValues.get(code);
        switch (code) {

            case R.id.button_delete:
                CharSequence selectedText = inputConnection.getSelectedText(0);

                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else
                    inputConnection.commitText("", 1);
                if (isUpper)
                    toLowerCase();
                isUpper = false;
                break;
            case R.id.button_shift:
                isShifted = !isShifted;

                if (isShifted) {
                    toUpperCase();
                    isUpper = true;
                } else {
                    toLowerCase();
                    isUpper = false;
                }
                break;
            default:
                if (isUpper) {
                    value = value.toUpperCase();
                    toLowerCase();
                }
                inputConnection.commitText(value, 1);
                isUpper = false;
                break;
        }
        textCode = value + ";";
        textForFile = textCode + textDown + textUp;
        //INSERT ROW
        ContentValues cv = new ContentValues();
        cv.put("rawText", textForFile);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("rawData", null, cv);
        Log.d(TAG, "ROW raw inserted=" + textForFile);

        sequence += value;
        Log.d(TAG, "SEQ=" + sequence);
    }

    float[] rot = new float[9];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];
    float[] valuesAccel = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesOrient = new float[3];
    float[] valuesGyro = new float[3];

    float x;
    float y;

    Long currentTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                x = event.getX();
                y = event.getY();
                //pressure = event.getPressure();
                //Log.v(TAG, "Pressure = " + pressure);
                //fingerArea = event.getSize();
                //Log.v(TAG, "Finger area = " + fingerArea);

                currentTime = Calendar.getInstance().getTimeInMillis();
                getDeviceOrientation();

                textDown = currentTime + ";"
                        + valuesOrient[0] + ";" + valuesOrient[1] + ";"
                        + valuesOrient[2] + ";" + valuesLinAccel[0] + ";"
                        + valuesLinAccel[1] + ";" + valuesLinAccel[2] + ";"
                        + valuesGravity[0] + ";" + valuesGravity[1] + ";"
                        + valuesGravity[2] + ";" + valuesGyro[0] + ";"
                        + valuesGyro[1] + ";" + valuesGyro[2] + ";"
                        + x + ";" + y;
                break;
            case MotionEvent.ACTION_UP:

                currentTime = Calendar.getInstance().getTimeInMillis();
                textUp = ";" + currentTime;

                break;
        }

        return false;
    }

    void getDeviceOrientation() {
        SensorManager.getRotationMatrix(rot, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(rot, valuesOrient);

        valuesOrient[0] = (float) Math.toDegrees(valuesOrient[0]);
        valuesOrient[1] = (float) Math.toDegrees(valuesOrient[1]);
        valuesOrient[2] = (float) Math.toDegrees(valuesOrient[2]);
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }

                case Sensor.TYPE_ACCELEROMETER:
                    for (int i=0; i < 3; i++){
                        valuesAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i=0; i < 3; i++){
                        valuesMagnet[i] = event.values[i];
                    }
                case Sensor.TYPE_GYROSCOPE:
                    for (int i=0; i < 3; i++){
                        valuesGyro[i] = event.values[i];
                    }
                    break;
            }
        }
    };

    public void setInputConnection(InputConnection ic) { inputConnection = ic; }

    public void setLogin(String log) { login = log; }

    public String getCharSequence () {

        int length = sequence.length();
        if (length <= 11)
            return sequence;
        return sequence.substring(length - 11);
    }

    private void toUpperCase() {

        buttonQ.setText("Q");
        buttonW.setText("W");
        buttonE.setText("E");
        buttonR.setText("R");
        buttonT.setText("T");
        buttonY.setText("Y");
        buttonU.setText("U");
        buttonI.setText("I");
        buttonO.setText("O");
        buttonP.setText("P");
        buttonA.setText("A");
        buttonS.setText("S");
        buttonD.setText("D");
        buttonF.setText("F");
        buttonG.setText("G");
        buttonH.setText("H");
        buttonJ.setText("J");
        buttonK.setText("K");
        buttonL.setText("L");
        buttonZ.setText("Z");
        buttonX.setText("X");
        buttonC.setText("C");
        buttonV.setText("V");
        buttonN.setText("N");
        buttonB.setText("B");
        buttonM.setText("M");
    }

    private void toLowerCase() {

        buttonQ.setText("q");
        buttonW.setText("w");
        buttonE.setText("e");
        buttonR.setText("r");
        buttonT.setText("t");
        buttonY.setText("y");
        buttonU.setText("u");
        buttonI.setText("i");
        buttonO.setText("o");
        buttonP.setText("p");
        buttonA.setText("a");
        buttonS.setText("s");
        buttonD.setText("d");
        buttonF.setText("f");
        buttonG.setText("g");
        buttonH.setText("h");
        buttonJ.setText("j");
        buttonK.setText("k");
        buttonL.setText("l");
        buttonZ.setText("z");
        buttonX.setText("x");
        buttonC.setText("c");
        buttonV.setText("v");
        buttonN.setText("n");
        buttonB.setText("b");
        buttonM.setText("m");
    }

}