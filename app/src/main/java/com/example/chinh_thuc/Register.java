package com.example.chinh_thuc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private EditText name;
    private EditText pass;
    private EditText emailKP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        pass = findViewById(R.id.password);
        emailKP = findViewById(R.id.emailKP);

        Button btnRegis = findViewById(R.id.register);
        btnRegis.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            handleRegister();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void handleRegister() {
        String name_str = name.getText()
                .toString();
        String pass_str = pass.getText()
                .toString();
        String email = emailKP.getText()
                .toString();
        if (checkValid(name_str, pass_str, email)) {

            JSONObject config_srv = new JSONObject();
            JSONObject information = new JSONObject();

            try {
                config_srv.put("url", MainActivity.server + "/user/register");
                config_srv.put("method", "POST");

                information.put("name", name_str);
                information.put("password", pass_str);
                information.put("email", email);

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
//                                        nof_login.setText(msg);
                                         Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }

                            Intent intent = new Intent(Register.this, LoginActivity.class);
                            intent.putExtra("block_register", "ok");
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute(config_srv, information);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Thông tin sai", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkValid(String name_str, String pass_str, String email) {
        return ! name_str.equals("")
                && ! pass_str.equals("")
                && ! email.equals("");
    }
}