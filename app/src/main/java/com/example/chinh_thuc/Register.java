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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    EditText name, pass, emailKP;
    Button btnRegis;

    private int length = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        pass = findViewById(R.id.password);
        btnRegis = findViewById(R.id.register);
        emailKP = findViewById(R.id.emailKP);
//
        OnClick();
        GetActivity();
    }

    private void GetActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();

                String name_intent = intent.getStringExtra("name");
                String pass_intent = intent.getStringExtra("password");

                assert name_intent != null && pass_intent != null;
                    if(name_intent.length() != 0 && pass_intent.length() != 0) {
                        name.setText(name_intent);
                        pass.setText(pass_intent);
                    }
            }
        });
    }

    private void OnClick() {
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                String email = emailKP.getText().toString();
                final String nameRead = name.getText().toString();
                String passRead = pass.getText().toString();

                final Context context = getApplicationContext();

                if(email.length() == 0 && nameRead.length() == 0 && passRead.length() == 0) {
                    CharSequence nof = "Email is not valid" + "\n" + "Name is not valid" + "\n" + "Pass is not valid";
                    Toast toast = Toast.makeText(context, nof, length);
                    toast.show();
                }
                else if(nameRead.length() == 0) {
                    CharSequence nof = "Name is not valid";
                    Toast toast = Toast.makeText(context, nof, length);
                    toast.show();
                }
                else if(passRead.length() == 0) {
                    CharSequence nof = "Pass is not valid";
                    Toast toast = Toast.makeText(context, nof, length);
                    toast.show();
                }
                else if(email.length() == 0) {
                    CharSequence nof = "Email is not valid";
                    Toast toast = Toast.makeText(context, nof, length);
                    toast.show();
                }
                else {
                    JSONObject data = new JSONObject();
                    JSONObject host_url = new JSONObject();

                    try {
                        data.put("name", nameRead);
                        data.put("password", passRead);
                        data.put("email", email);

                        host_url.put("url", VariableConfig.host + ":" + VariableConfig.port + VariableConfig.url_register);
                        host_url.put("method", "POST");

                        Log.d("TESING", "RUN");
                        Log.d("TESTING", VariableConfig.host + ":" + VariableConfig.port + VariableConfig.url_register);

                        new HTTPPostHandler() {
                            @Override
                            public void onResponseData(String response) {
                                super.onResponseData(response);
                                assert response != null;
                                Log.d("TESTING", response);
                                try {
                                    JSONObject data = new JSONObject(response);
                                    boolean error = data.getBoolean("error");
                                    if(error) {
                                        String message = data.getString("message");
                                        Toast toast = Toast.makeText(context, message, length);
                                        toast.show();

                                        emailKP.setText("");
                                        name.setText("");
                                        pass.setText("");

                                    }
                                    else {
                                        boolean state = data.getBoolean("state");
                                        String message = data.getString("message");
                                        String name = data.getString("name");

                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        intent.putExtra("state", "register");
                                        intent.putExtra("message", message);
                                        intent.putExtra("QR", ! state);
                                        intent.putExtra("name", name);

                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute(host_url, data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}