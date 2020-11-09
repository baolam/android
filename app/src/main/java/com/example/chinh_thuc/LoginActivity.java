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

public class LoginActivity extends AppCompatActivity {
    EditText name_text, password_text;
    TextView nof_login;
    Button login, register;

    private int length = Toast.LENGTH_SHORT;
    private int tryToLogin = 5;
    private String name_pri;
    private String pass_pri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name_text = findViewById(R.id.username);
        password_text = findViewById(R.id.password);
        nof_login = findViewById(R.id.nof_login);
        login = findViewById(R.id.login);
        register = findViewById(R.id.Register);

        register.setEnabled(false);

        login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint(value = "StaticFieldLeak")
            @Override
             public void onClick(View v) {
                String name = name_text.getText().toString();
                String pass = password_text.getText().toString();

                name_pri = name;
                pass_pri = pass;

                if(name.length() == 0 && pass.length() == 0) {
                    final CharSequence nof = "Name is not valid" + '\n' + "Password is not valid";
                    Log.d("DEBUG", nof.toString());
                    nof_login.post(new Runnable() {
                        @Override
                        public void run() {
                            nof_login.setText(nof);
                        }
                    });
                }
                else if(name.length() == 0) {
                    final CharSequence nof = "Name is not valid";
                    Log.d("DEBUG", nof.toString());
                    nof_login.post(new Runnable() {
                        @Override
                        public void run() {
                             //nof_login.setTextColor(Integer.parseInt("#FF0000"));
                            nof_login.setText(nof);
                        }
                    });
                }
                else if(pass.length() == 0) {
                    final CharSequence nof = "Password is not valid";
                    Log.d("DEBUG", nof.toString());
                    nof_login.post(new Runnable() {
                        @Override
                        public void run() {
                            //nof_login.setTextColor(Integer.parseInt("#FF0000"));
                            nof_login.setText(nof);
                        }
                    });
                }
                else {
                    JSONObject host_url = new JSONObject();
                    JSONObject data = new JSONObject();

                    try {
                        host_url.put("url", VariableConfig.host + ":" + VariableConfig.port + VariableConfig.url_login);
                        host_url.put("method", "POST");

                        data.put("name", name);
                        data.put("password", pass);

                        new HTTPPostHandler() {
                            @Override
                            public void onResponseData(String response) {
                                super.onResponseData(response);
                                assert response != null;
                                try {
                                    //Log.d("TESTING", response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    Context context = getApplicationContext();
                                    boolean error = jsonObject.getBoolean("error");
                                    if(error) {
                                        String message = jsonObject.getString("message");
                                        boolean state_err = jsonObject.getBoolean("state");

                                        register.setEnabled(true);
                                        tryToLogin --;

                                        String lt = Integer.toString(tryToLogin);
                                        final CharSequence nof = message + "\n" + "Lần thử: " + lt;

                                        nof_login.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                nof_login.setText(nof);
                                            }
                                        });

                                        if(state_err) {
                                            // Lỗi này ứng với trường hợp người dùng nhập sai tên hoặc password
                                            if(tryToLogin == 0) {
                                                login.setEnabled(false);
                                                // Gửi lệnh vô hiệu hóa tài khoản
                                                // Giả lập việc vô hiệu hóa tài khoản người dùng
                                            }

                                        }
                                        else {
                                            // Lỗi người dùng chưa đăng ký tài khoản đặt khả năng cao là hacker
                                            if(tryToLogin == 0) {
                                                login.setEnabled(false);
                                            }
                                        }
                                    }
                                    else {
                                        String name = jsonObject.getString("name");
                                        String message = jsonObject.getString("message");
                                        boolean special = jsonObject.getBoolean("special");

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                        intent.putExtra("state", "login");
                                        intent.putExtra("name", name);
                                        intent.putExtra("message", message);
                                        intent.putExtra("sp", special);

                                        startActivity(intent);
                                    }
                                }
                                catch (JSONException e) {
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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register.class);

                intent.putExtra("name", name_pri);
                intent.putExtra("password", pass_pri);

                startActivity(intent);
            }
        });
    }
}