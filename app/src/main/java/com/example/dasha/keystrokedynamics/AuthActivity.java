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

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        String realPassword;
        String correctPassword = "tie5.Roanl";
        String correctSequence = "tie5.$Roanl";
        String realSequence;
        String rawDataString;

        double thresholdManhattan = 240;
        double thresholdEuclidean = 27;

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

                        double scoreManhattan = countManhattanDist(preprocessedData);
                        Log.d(TAG, "scoreManhattan=" + scoreManhattan);
                        double scoreEuclidean = countEuclideanDist(preprocessedData);
                        Log.d(TAG, "scoreEuclidean=" + scoreEuclidean);
                        intentOk.putExtra("scoreManhattan", scoreManhattan);
                        intentOk.putExtra("scoreEuclidean", scoreEuclidean);
                        intentOk.putExtra("prerocessedData", preprocessedData.toString());

                        if (scoreEuclidean < thresholdEuclidean)
                            intentOk.putExtra("decision", true);
                        else
                            intentOk.putExtra("decision", false);

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

    double[] meanVector = { 5.69000000e+01,  7.27250000e+01,  4.57250000e+01,  6.63750000e+01,
            1.64650000e+02,  2.82050000e+02,  7.15750000e+01,  1.11625000e+02,
            9.75000000e+01,  2.13600000e+02,  5.03250000e+01,  8.24250000e+01,
            1.22950000e+02,  2.51650000e+02,  4.54750000e+01,  5.84000000e+01,
            5.62100000e+02,  6.83700000e+02,  1.51150000e+02,  1.08150000e+02,
            3.24200000e+02,  4.29850000e+02,  6.52750000e+01,  7.67500000e+01,
            4.96250000e+02,  6.46000000e+02,  4.00750000e+01,  6.43250000e+01,
            2.87500000e+02,  3.96600000e+02,  3.03250000e+01,  9.39250000e+01,
            1.00750000e+02,  2.05250000e+02,  6.34750000e+01,  8.69750000e+01,
            4.94500000e+01,  1.93900000e+02,  4.70000000e+01,  8.59250000e+01,
            1.10200000e+02,  2.19350000e+02,  1.10910296e+02, -3.22090367e+01,
            7.91485748e+00, -3.45323537e-02,  2.57315715e-03, -1.72931558e-01,
            -1.11424809e+00,  5.15827476e+00,  8.22316382e+00,  1.19490909e+02,
            6.13353532e+01,  4.18677518e+00,  2.61577694e+00,  1.38040895e-01,
            1.63711625e-01,  3.55551626e-01,  2.92450981e-01,  5.41623972e-01,
            3.85442503e-01,  1.89179105e+01,  1.18481978e+02,  4.90322367e+01,
            1.32804442e+01,  1.18275859e+02,  1.19458393e+02,  1.36789965e+02,
            1.34960490e+02,  1.36445436e+02,  1.36595260e+02,  1.37217757e+02,
            1.19475443e+02, -3.61593832e+01, -3.31537567e+01, -3.46049986e+01,
            -3.91197014e+01, -2.80270688e+01, -2.89154385e+01, -3.80825230e+01,
            -2.89953707e+01, -2.86178845e+01, -2.92991477e+01, -2.93241305e+01,
            9.51092410e+00,  6.68887755e+00,  5.75034658e+00,  1.14954466e+01,
            5.90255558e+00,  7.14251536e+00,  1.32895235e+01,  7.90372264e+00,
            6.92070343e+00,  6.82483279e+00,  5.63398416e+00, -7.43075403e-02,
            -5.72969763e-02,  7.27766185e-02, -1.06339413e-01,  2.57293696e-02,
            -3.77552492e-02, -8.81810547e-02, -4.70704849e-02,  4.63653748e-02,
            7.55713879e-03, -1.21333673e-01,  9.63138825e-02, -3.70377789e-02,
            1.04632140e-01,  3.85621542e-02, -1.37100459e-01,  1.60983434e-02,
            1.38409471e-01, -1.31260467e-01,  1.01723993e-01, -5.70926553e-02,
            -1.04943895e-01, -2.53866746e-01, -5.86035275e-01, -7.64229043e-02,
            -3.29828597e-01, -1.15272809e-01,  3.35634518e-01, -3.36874819e-01,
            -2.11216356e-01,  1.78482842e-01, -1.56614349e-01, -3.50232648e-01,
            -1.28335611e+00, -9.35915403e-01, -8.67658693e-01, -1.46024460e+00,
            -8.87037694e-01, -1.05712216e+00, -1.70461360e+00, -1.17724307e+00,
            -1.05679376e+00, -1.03189178e+00, -7.94852148e-01,  5.71249795e+00,
            5.26561934e+00,  5.47139591e+00,  6.07988542e+00,  4.58666020e+00,
            4.75520863e+00,  5.88989502e+00,  4.75149984e+00,  4.65299350e+00,
            4.80370352e+00,  4.77166303e+00,  7.84802581e+00,  8.20684467e+00,
            8.07874289e+00,  7.53315180e+00,  8.60834538e+00,  8.49938847e+00,
            7.64103731e+00,  8.48636893e+00,  8.55681033e+00,  8.47631365e+00,
            8.51977272e+00,  1.17400000e+02,  1.16100000e+02,  1.28700000e+02,
            1.21600000e+02,  1.05650000e+02,  1.49750000e+02,  1.09100000e+02,
            1.04500000e+02,  1.44450000e+02,  1.09150000e+02,  1.08000000e+02};

    double[] stdVector = {1.38740765e+01, 1.56696163e+01, 2.47120795e+01, 2.07454061e+01,
            4.13935683e+01, 4.35022701e+01, 2.15280486e+01, 1.99097181e+01,
            3.67756713e+01, 3.58767334e+01, 1.58313573e+01, 1.94899173e+01,
            5.73127167e+01, 5.21145613e+01, 1.37935447e+01, 1.88186078e+01,
            8.61741841e+01, 9.00083885e+01, 3.15979034e+01, 1.45508591e+01,
            7.95880644e+01, 8.09056704e+01, 1.49134796e+01, 1.60977483e+01,
            7.30943739e+01, 7.70116874e+01, 2.14075191e+01, 1.82614861e+01,
            3.87717681e+01, 4.18430400e+01, 1.17060615e+01, 1.52342337e+01,
            3.00830102e+01, 2.96460368e+01, 1.52163358e+01, 1.46034885e+01,
            2.14253938e+01, 2.44579231e+01, 2.07189527e+01, 1.87671755e+01,
            1.28397819e+01, 1.36831100e+01, 7.14010672e+01, 1.87435285e+00,
            1.74576218e+00, 5.03359031e-02, 4.95665113e-02, 8.04188497e-02,
            2.39531553e-01, 2.61694697e-01, 1.76621974e-01, 3.62855981e+00,
            6.11159407e+01, 8.35742324e-01, 4.09781919e-01, 3.44136792e-02,
            3.93958181e-02, 7.80800913e-02, 4.20268479e-02, 9.65048772e-02,
            7.09443620e-02, 4.55928112e+00, 1.03533570e+02, 1.51486311e+02,
            1.58920250e+02, 1.02293221e+02, 1.03908673e+02, 7.85308860e+01,
            7.56716841e+01, 7.95336268e+01, 7.83029269e+01, 7.94074570e+01,
            1.04106156e+02, 3.05983242e+00, 2.36257422e+00, 2.69514437e+00,
            3.42125206e+00, 2.43193487e+00, 2.42011074e+00, 2.34557513e+00,
            1.73253481e+00, 2.01491901e+00, 1.94453665e+00, 2.06762865e+00,
            1.71461873e+00, 1.87019854e+00, 2.29716173e+00, 2.17126567e+00,
            1.99455354e+00, 1.75439347e+00, 2.84317449e+00, 2.13512396e+00,
            1.98062758e+00, 1.87603290e+00, 2.14451292e+00, 1.27735380e-01,
            1.79799566e-01, 1.62934279e-01, 1.74732769e-01, 1.07251471e-01,
            1.17099047e-01, 1.48008531e-01, 8.89007822e-02, 1.08066345e-01,
            1.35309922e-01, 1.35909503e-01, 1.09167822e-01, 2.10678464e-01,
            1.20437863e-01, 1.30894562e-01, 1.58454714e-01, 2.09451434e-01,
            1.00064848e-01, 1.34558657e-01, 1.45775039e-01, 1.32771062e-01,
            1.09197094e-01, 3.28359910e-01, 3.37460468e-01, 4.02007105e-01,
            3.12629943e-01, 2.45915549e-01, 2.17630761e-01, 2.22401228e-01,
            2.42629963e-01, 2.45349974e-01, 2.77008139e-01, 1.94945982e-01,
            2.40602861e-01, 2.39157843e-01, 2.69474694e-01, 2.87815136e-01,
            2.89476503e-01, 2.39513466e-01, 2.82839359e-01, 3.11515060e-01,
            2.73494572e-01, 2.83580402e-01, 2.94950036e-01, 3.90134179e-01,
            3.30141688e-01, 3.07380193e-01, 3.84677986e-01, 3.52302095e-01,
            3.27943111e-01, 2.58711268e-01, 2.59664677e-01, 2.84775092e-01,
            2.72622445e-01, 2.66567538e-01, 2.98577254e-01, 2.21841604e-01,
            2.19537975e-01, 3.06393909e-01, 1.85949982e-01, 1.90668687e-01,
            2.03553720e-01, 1.57514641e-01, 1.68310991e-01, 1.65479752e-01,
            1.58899174e-01, 1.22368297e+01, 1.28953480e+01, 1.44813673e+01,
            1.30935098e+01, 1.41466427e+01, 1.80385005e+01, 9.86863719e+00,
            1.37640837e+01, 1.35995404e+01, 1.07437191e+01, 9.66436754e+00};

    private double countEuclideanDist(ArrayList<Double> data) {
        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.size(); i++) {
            centered = data.get(i) - meanVector[i];
            scaled = centered / stdVector[i];
            sum += scaled * scaled;
        }
        return Math.sqrt(sum);
    }

    private double countManhattanDist(ArrayList<Double> data) {
        int sum = 0;
        double centered = 0;
        double scaled = 0;
        for (int i = 0; i < data.size(); i++) {
            centered = data.get(i) - meanVector[i];
            scaled = centered / stdVector[i];
            sum += Math.abs(scaled);
        }
        return sum;
    }

}
