package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OkActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvOk;
    Button btnTryAgain;
    Button btnLogout;

    String login;
    double score = 10000;
    boolean decision;
    boolean correctPassword;
    boolean correctFeatureVector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        decision = intent.getBooleanExtra("decision", false);
        correctPassword = intent.getBooleanExtra("correctPassword", false);
        correctFeatureVector = intent.getBooleanExtra("correctFeatureVector", false);
        score = intent.getDoubleExtra("score", 10000);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        btnLogout.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);

        tvOk = (TextView) findViewById(R.id.tvOk);

        if (! correctPassword)
            tvOk.setText("Incorrect password! You are not authenticated!");
        else if (! correctFeatureVector)
            tvOk.setText("Incorrect typing of password! SMS is sent to authenticate you");
        else if (! decision)
            tvOk.setText("You type not like " + login + "! SMS is sent to authenticate you\n" + "score = " + score);
        else
            tvOk.setText(login + ", you are now authenticated!\n" + "score = " + score);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnTryAgain:
                Intent intentAuth = new Intent(this, AuthActivity.class);
                intentAuth.putExtra("login", login);
                intentAuth.putExtra("authOk", false);
                startActivity(intentAuth);
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
