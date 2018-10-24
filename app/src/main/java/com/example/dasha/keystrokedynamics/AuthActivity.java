package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword;
    TextView tvWelcome;
    TextView tvPassword;
    Button btnLogout;
    Button btnDone;

    MyKeyboard keyboard;

    String login;

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

        keyboard = (MyKeyboard) findViewById(R.id.keyboard);
        etPassword.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etPassword.setTextIsSelectable(true);

        InputConnection ic = etPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        keyboard.setLogin(login);

    }

    @Override
    public void onClick(View v) {
        String realPassword;
        String correctPassword = "tie5.Roanl";
        String correctSequence = "tie5.$Roanl";
        String realSequence;

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
                intentOk.putExtra("decision", true); // TODO make decision
                intentOk.putExtra("correctFeatureVector", realSequence.equals(correctSequence));
                intentOk.putExtra("correctPassword", realPassword.equals(correctPassword));
                startActivity(intentOk);
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
