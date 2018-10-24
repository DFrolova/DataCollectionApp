package com.example.dasha.keystrokedynamics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    Button btnRegister;
    Button btnLogin;
    EditText etLogin;
    TextView tvLogin;

    String key = "key";
    String TAG = "myLog";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        etLogin = (EditText) findViewById(R.id.etLogin);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setText("Enter login or register");
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> loginArrayList;
        String login;
        Intent intentChoose;

        if (TextUtils.isEmpty(etLogin.getText().toString()))
            return;

        login = etLogin.getText().toString();
        etLogin.setText("");

        loginArrayList = readPref();

        Log.d(TAG, loginArrayList.toString());

        switch (v.getId()) {
            case R.id.btnRegister:
                if (loginArrayList.contains(login))
                    tvLogin.setText("This login is already taken!");
                else {
                    loginArrayList.add(login);
                    saveToPref(loginArrayList);
                    intentChoose = new Intent(this, ChooseActivity.class);
                    intentChoose.putExtra("login", login);
                    startActivity(intentChoose);
                }
                break;
            case R.id.btnLogin:
                if (loginArrayList.contains(login)) {
                    intentChoose = new Intent(this, ChooseActivity.class);
                    intentChoose.putExtra("login", login);
                    startActivity(intentChoose);
                }
                else
                    tvLogin.setText("No user with this login!");
                break;
        }
    }

    private void saveToPref(ArrayList loginArrayList) {
        sharedPref = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(loginArrayList);

        editor = sharedPref.edit();
        editor.putString(key, json);
        editor.commit();
    }

    private ArrayList<String> readPref() {
        sharedPref = getPreferences(MODE_PRIVATE);
        ArrayList<String> loginArrayList;
        String jsonCart = sharedPref.getString(key, null);
        Gson gson = new Gson();

        if (sharedPref.contains(key)) {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            loginArrayList = gson.fromJson(jsonCart, type);
            if (loginArrayList == null)
                loginArrayList = new ArrayList<String>();
        }
        else
            loginArrayList = new ArrayList<String>();
        return loginArrayList;
    }

}
