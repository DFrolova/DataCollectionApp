package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TrainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDone;
    Button btnLogout;
    Button btnReturn;
    TextView tvWelcome;
    TextView tvTrain;
    EditText etTrain;

    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnReturn = (Button) findViewById(R.id.btnReturn);

        btnLogout.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        etTrain = (EditText) findViewById(R.id.etTrain);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvTrain = (TextView) findViewById(R.id.tvTrain);

        tvWelcome.setText("Welcome, " + login); // + login); // TODO shared preferences

        //login
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDone:
                etTrain.setText("");
                break;
            case R.id.btnLogout:
                Intent intentLogout = new Intent(this, LoginActivity.class);
                startActivity(intentLogout);
                break;
            case R.id.btnReturn:
                Intent intentReturn = new Intent(this, ChooseActivity.class);
                intentReturn.putExtra("login", login);
                startActivity(intentReturn);
                break;
            default:
                break;
        }
    }
}
