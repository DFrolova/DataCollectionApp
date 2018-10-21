package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword;
    TextView tvWelcome;
    TextView tvPassword;
    Button btnLogout;
    Button btnDone;

    String login;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnDone = (Button) findViewById(R.id.btnDone);

        btnLogout.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        etPassword = (EditText) findViewById(R.id.etPassword);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        tvWelcome.setText("Welcome, " + login);

    }

    @Override
    public void onClick(View v) {
        String password;

        Intent intentWrong;
        Intent intentOk;

        switch (v.getId()) {
            case R.id.btnDone:
                if (TextUtils.isEmpty(etPassword.getText().toString()))
                    return;

                password = etPassword.getText().toString();
                etPassword.setText("");

                if (password.equals("tie5.Roanl")) {
                    // TODO check correctness of feature vector
                    // TODO check if not anomaly
                    intentOk = new Intent(this, OkActivity.class);
                    intentOk.putExtra("login", login);
                    intentOk.putExtra("decision", true);
                    intentOk.putExtra("correctFeatureVector", true);
                    intentOk.putExtra("correctPassword", true);
                    startActivity(intentOk);
                }
                else {
                    intentWrong = new Intent(this, OkActivity.class);
                    intentWrong.putExtra("login", login);
                    intentWrong.putExtra("decision", false);
                    intentWrong.putExtra("correctFeatureVector", false);
                    intentWrong.putExtra("correctPassword", false);
                    startActivity(intentWrong);
                }
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
