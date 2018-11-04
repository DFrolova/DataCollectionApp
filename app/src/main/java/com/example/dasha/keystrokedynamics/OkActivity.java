package com.example.dasha.keystrokedynamics;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OkActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvOk;
    TextView tvQuestion;
    TextView tvScores;
    Button btnYes;
    Button btnNo;

    DBRaw dbHelper;

    String login;
    String preprocessedData;
    String TAG = "myLog";
    String rawDataString;
    String scores;
    boolean decision;

    String[] rawData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        Log.d(TAG, "login = " + login);
        preprocessedData = intent.getStringExtra("prerocessedData");
        Log.d(TAG, "prerocessedData = " + preprocessedData);
        rawDataString = intent.getStringExtra("rawData");
        decision = intent.getBooleanExtra("decision", false);
        scores = intent.getStringExtra("scores");

        btnYes = (Button) findViewById(R.id.btnYes);
        btnNo = (Button) findViewById(R.id.btnNo);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        dbHelper = new DBRaw(this);

        tvOk = (TextView) findViewById(R.id.tvOk);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvScores = (TextView) findViewById(R.id.tvScores);

        tvQuestion.setText("Was it " + login + "?");
        tvScores.setText(scores);

        if (! decision)
            tvOk.setText("You type not like " + login + "! SMS is sent to authenticate you");
        else
            tvOk.setText(login + ", you are now authenticated!");
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnYes) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // insert to rawData table
            ContentValues cv = new ContentValues();
            cv.put("text", "login=" + login);
            db.insert("rawData", null, cv);

            rawData = rawDataString.split(" ");
            for (int i = 0; i < 11; i += 1) {

                String charSeq = rawData[i];
                cv.put("text", charSeq.toString());
                db.insert("rawData", null, cv);
            }

            //insert to passwordData table
            ContentValues cvPass = new ContentValues();
            cvPass.put("login", login);
            cvPass.put("password", preprocessedData);
            db.insert("passwordData", null, cvPass);
        }

        Intent intentAuth = new Intent(this, AuthActivity.class);
        intentAuth.putExtra("login", login);
        startActivity(intentAuth);
    }
}
