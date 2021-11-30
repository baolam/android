package com.example.chinh_thuc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name_text;
    private EditText password_text;
    private TextView nof_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name_text = findViewById(R.id.username);
        password_text = findViewById(R.id.password);
        nof_login = findViewById(R.id.nof_login);

        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.Register);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

        Intent data = getIntent();
        if (data != null) {
            register.setEnabled(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {
                handleLogin();
                break;
            }
            case R.id.Register: {
                startRegisterActivity();
                break;
            }
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    private void handleLogin() {
        String name = name_text.getText()
                .toString();
        String password = password_text.getText()
                .toString();
        if (checkValid(name, password)) {
            JSONObject config_srv = new JSONObject();
            JSONObject information = new JSONObject();

            try {
                 config_srv.put("url", MainActivity.server + "/user/login");
                 config_srv.put("method", "POST");

                 information.put("name", name);
                 information.put("password", password);

                 new HTTPPostHandler() {
                     @Override
                     public void onResponseData(final String response) {
                         super.onResponseData(response);

                         try {
                             JSONObject resp = new JSONObject(response);

                             boolean err = resp.getBoolean("err");
                             final String msg = resp.getString("msg");

                             if (err) {
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         nof_login.setText(msg);
                                     }
                                 });
                                 return;
                             }

                             Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                             startActivity(intent);
                         } catch (JSONException e) {
                             Log.d("ERROR", e.toString());
                             e.printStackTrace();
                         }

                     }
                 }.execute(config_srv, information);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            nof_login.setText("Thông tin đăng nhập sai");
        }
    }

    private boolean checkValid(String name, String password) {
        return ! name.equals("")
                && ! password.equals("")
                && name.indexOf('@') != -1;
    }
}