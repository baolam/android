package com.example.chinh_thuc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class CarformActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_ho;
    private EditText edt_ten;
    private TextView tv_form;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carform);

        edt_ho = findViewById(R.id.edt_ho);
        edt_ten = findViewById(R.id.edt_ten);
        tv_form = findViewById(R.id.tv_form);

        Button btn_send_form = findViewById(R.id.btn_send_form);

        btn_send_form.setOnClickListener(this);
    }

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_form) {
            String ho = edt_ho.getText()
                            .toString();
            String ten = edt_ten.getText()
                            .toString();
            if (checkValid(ho, ten)) {
                JSONObject config_srv = new JSONObject();
                JSONObject information = new JSONObject();

                try {
                    config_srv.put("url", MainActivity.server + "/user/form");
                    config_srv.put("method", "POST");

                    information.put("ho", ho);
                    information.put("ten", ten);

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
                                         Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                // Initalize socket --> handle
                                // Wait
                                try {
                                    mSocket = IO.socket(MainActivity.server + "/io/user");
                                    mSocket.on("notification", onGetNotification);

                                    mSocket.connect();

                                    if (mSocket.connected()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Kết nối nhận dữ liệu thành công", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                                    }
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
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
                tv_form.setText("Bạn điền thông tin sai");
            }
        }
    }

    private boolean checkValid(String ho, String ten) {
        return !ho.equals("") && !ten.equals("");
    }

    private final Emitter.Listener onGetNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String notify = data.getString("warm");
                        boolean ok = data.getBoolean("ok");

                        if (ok) {
                            Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_LONG)
                                    .show();
                            mSocket.disconnect();
                            return;
                        }

                        Toast.makeText(getApplicationContext(), notify, Toast.LENGTH_LONG)
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}