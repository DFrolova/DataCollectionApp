package com.example.dasha.keystrokedynamics;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword;
    TextView tvWelcome;
    TextView tvPassword;
    TextView tvResult;
    Button btnLogout;
    Button btnDone;
    Button btnReturn;

    MyKeyboard keyboard;
    DBRaw dbHelper;

    String login;
    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnReturn = (Button) findViewById(R.id.btnReturn);

        btnLogout.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        etPassword = (EditText) findViewById(R.id.etPassword);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvPassword = (TextView) findViewById(R.id.tvPassword);
        tvResult = (TextView) findViewById(R.id.tvResult);

        tvResult.setText("Typed " + count + " times");
        tvWelcome.setText("Welcome, " + login);

        keyboard = (MyKeyboard) findViewById(R.id.keyboard);
        etPassword.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etPassword.setTextIsSelectable(true);

        InputConnection ic = etPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        keyboard.setLogin(login);

        dbHelper = new DBRaw(this);

    }

    @Override
    public void onClick(View v) {
        String realPassword;
        String correctPassword = "tie5.Roanl";
        String correctSequence = "tie5.$Roanl";
        String realSequence;
        String TAG = "myLog";

        ArrayList<Double> preprocessedData = new ArrayList<>();

        SQLiteDatabase db;

        switch (v.getId()) {
            case R.id.btnDone:
                if (TextUtils.isEmpty(etPassword.getText().toString()))
                    return;

                realPassword = etPassword.getText().toString();
                etPassword.setText("");
                realSequence = keyboard.getCharSequence();

                DataPreprocesser preprocesser = new DataPreprocesser(this, login);
                preprocessedData = preprocesser.preprocAndReturn(keyboard.getRawData());

                db = dbHelper.getWritableDatabase();

                if (realPassword.equals(correctPassword)) {
                    if (realSequence.equals(correctSequence)) {
                        count += 1;
                        tvResult.setText("Typed " + count + " times");
                        List<String> rawData= keyboard.getRawData();
                        keyboard.clearData();

                        ContentValues cv = new ContentValues();
                        cv.put("text", "login=" + login);
                        db.insert("rawData", null, cv);
                        Log.d(TAG, "INSERTED: " + "login=" + login);

                        for (int i = 0; i < 11; i += 1) {

                            String charSeq = rawData.get(i);
                            cv.put("text", charSeq.toString());
                            db.insert("rawData", null, cv);
                            Log.d(TAG, charSeq.toString());
                        }

                        // TODO preproc and put into database

                    }
                    else
                        tvResult.setText("Incorrect typing!\nTyped " + count + " times");
                }
                else
                    tvResult.setText("Incorrect password!\nTyped " + count + " times");

                if (realPassword.equals("show data")) {
                    //READ ALL
                    Log.d(TAG, "--- Rows in mytable: ---");
                    Cursor c = db.query("rawData", null, null,
                            null, null, null, null);

                    if (c.moveToFirst()) {

                        int idColIndex = c.getColumnIndex("id");
                        int textColIndex = c.getColumnIndex("text");

                        do {
                            Log.d(TAG, "ID = " + c.getInt(idColIndex) +
                                    ", text = " + c.getString(textColIndex));
                        } while (c.moveToNext());
                    } else
                        Log.d(TAG, "0 rows");
                    c.close();
                }
                /*if (realPassword.equals("delete data")) {
                    //CLEAR
                    Log.d(TAG, "--- Clear mytable: ---");
                    // удаляем все записи
                    int clearCount = db.delete("rawData", null, null);
                    Log.d(TAG, "deleted rows count = " + clearCount);
                }*/


                break;
            case R.id.btnReturn:
                Intent intentReturn = new Intent(this, ChooseActivity.class);
                intentReturn.putExtra("login", login);
                startActivity(intentReturn);
                break;
            case R.id.btnLogout:
                Intent intentLogout = new Intent(this, LoginActivity.class);
                startActivity(intentLogout);
                break;
            default:
                break;
        }
    }

    private class DBRaw extends SQLiteOpenHelper {

        public DBRaw (Context context) {
            super(context, "Passwords", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table rawData ("
                    + "id integer primary key autoincrement,"
                    + "text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

}
