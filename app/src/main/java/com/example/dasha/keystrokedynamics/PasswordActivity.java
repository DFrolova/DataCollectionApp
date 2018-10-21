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

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword;
    TextView tvWelcome;
    TextView tvPassword;
    TextView tvResult;
    Button btnLogout;
    Button btnDone;
    Button btnReturn;

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

    }

    @Override
    public void onClick(View v) {
        String password;

        switch (v.getId()) {
            case R.id.btnDone:
                if (TextUtils.isEmpty(etPassword.getText().toString()))
                    return;

                password = etPassword.getText().toString();
                etPassword.setText("");
                if (password.equals("tie5.Roanl")) {
                    count += 1;
                    tvResult.setText("Typed " + count + " times");
                }
                else
                    tvResult.setText("Incorrect password! Retype, please\nTyped " + count + " times");
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

}
