package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTrain;
    Button btnAuth;
    Button btnPassword;

    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btnTrain = (Button) findViewById(R.id.btnTrain);
        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnPassword = (Button) findViewById(R.id.btnPassword);

        btnTrain.setOnClickListener(this);
        btnAuth.setOnClickListener(this);
        btnPassword.setOnClickListener(this);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPassword:
                Intent intentPassword = new Intent(this, PasswordActivity.class);
                intentPassword.putExtra("login", login);
                startActivity(intentPassword);
                break;
            case R.id.btnAuth:
                Intent intentAuth = new Intent(this, AuthActivity.class);
                intentAuth.putExtra("login", login);
                startActivity(intentAuth);
                break;
            case R.id.btnTrain:
                Intent intentTrain = new Intent(this, TrainActivity.class);
                intentTrain.putExtra("login", login);
                startActivity(intentTrain);
            default:
                break;
        }
    }
}
