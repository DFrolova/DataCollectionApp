package com.example.dasha.keystrokedynamics;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword;
    TextView tvWelcome;
    TextView tvPassword;
    Button btnLogout;
    Button btnDone;

    MyKeyboard keyboard;
    DBRaw dbHelper;

    String login;
    String passwordTyping;
    String TAG = "myLog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        passwordTyping = intent.getStringExtra("passwordTyping");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnDone = (Button) findViewById(R.id.btnDone);

        btnLogout.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        etPassword = (EditText) findViewById(R.id.etPassword);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        tvWelcome.setText("Welcome, " + login);
        tvPassword.setText("Type password tie5.Roanl");

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
        String rawDataString;

        ArrayList<Double> preprocessedData;

        Intent intentOk;

        switch (v.getId()) {
            case R.id.btnDone:
                if (TextUtils.isEmpty(etPassword.getText().toString()))
                    return;

                realPassword = etPassword.getText().toString();
                etPassword.setText("");
                realSequence = keyboard.getCharSequence();
                intentOk = new Intent(this, OkActivity.class);
                intentOk.putExtra("login", login);

                if (realPassword.equals(correctPassword)) {
                    if (realSequence.equals(correctSequence)) {
                        DataPreprocesser preprocesser = new DataPreprocesser();
                        List<String> rawData = keyboard.getRawData();
                        keyboard.clearData();

                        rawDataString = rawData.get(0);
                        for (int i = 1; i < rawData.size(); i++ )
                            rawDataString = rawDataString + " " + rawData.get(i);

                        intentOk.putExtra("rawData", rawDataString);

                        preprocessedData = preprocesser.preproc(rawData);

                        intentOk.putExtra("prerocessedData", preprocessedData.toString());

                        AnomalyDetector detector = new AnomalyDetector(login, this);

                        intentOk.putExtra("decision", detector.makeDecision(preprocessedData));
                        intentOk.putExtra("scores", detector.returnScores());

                        tvPassword.setText("Type password tie5.Roanl");
                        startActivity(intentOk);
                    }
                    else
                        tvPassword.setText("Incorrect typing!\nType password tie5.Roanl");
                }
                else
                    tvPassword.setText("Incorrect password!\nType password tie5.Roanl");

                break;
            case R.id.btnLogout:
                Intent intentLogout = new Intent(this, LoginActivity.class);
                startActivity(intentLogout);
                break;
            default:
                break;
        }
    }

}
