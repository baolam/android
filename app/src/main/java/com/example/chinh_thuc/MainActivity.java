package com.example.chinh_thuc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageView imgQR;
    Button LoginBtn, ScanQr;
    EditText generateQR;
    //CounterTime counterTime;

    private String ID;
    private int length = Toast.LENGTH_SHORT;
    private String name_user = null;

    private int counter = 0;
    private boolean login = false;
    private boolean user_special = false;
    private boolean page = false;
    private boolean knowStateInternet = false;
    private boolean oneMore = true;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(VariableConfig.host + ":" + VariableConfig.port + VariableConfig.url_socket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //counterTime.ConfigTime();

        LoginBtn = findViewById(R.id.LoginBtn);
        ScanQr = findViewById(R.id.ScanQr);
        imgQR = findViewById(R.id.QR_image);
        generateQR = findViewById(R.id.generateQR);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(checkInternet(), 0, 2, TimeUnit.SECONDS);
        //scheduledExecutorService.scheduleAtFixedRate(ConnectSocket(), 0, 2, TimeUnit.SECONDS);

        //mSocket.connect();
        mSocket.on("id", on_SocketID);
        mSocket.on("stateCar", on_state_Car);

        //mSocket.connect();

        // Layout mac dinh khi chua login
        ScanQr.setEnabled(false);
        generateQR.setEnabled(false);

        CharSequence textScan = "...";
        ScanQr.setText(textScan);
        //-------------------------------

        try {
            ShowImageQR("Truong trung hoc co so & trung hoc pho thong Dong Da", 400, 400);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! login) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Context context = getApplicationContext();
                    CharSequence nof = "Logout complete";
                    Toast toast = Toast.makeText(context, nof, length);
                    toast.show();
                    name_user = null;
                    login = false;
                }
            }
        });

        ScanQr.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if(! user_special) {
                    final Context context = getApplicationContext();
                    String new_QR = generateQR.getText().toString();
                    if (new_QR.length() <= 5) {
                        // Handler error
                        CharSequence nof = "Không hợp lệ";
                        Toast toast = Toast.makeText(context, nof, length);
                        toast.show();
                    }
                    else {
                        if(name_user == null || name_user.length() == 0) {
                            // Handler error
                            CharSequence nof = "You must be logined";
                            Toast toast = Toast.makeText(context, nof, length);
                            toast.show();

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            JSONObject data = new JSONObject();
                            JSONObject host_url = new JSONObject();

                            try {
                                host_url.put("url", VariableConfig.host + ":" + VariableConfig.port + VariableConfig.url_qr);
                                host_url.put("method", "POST");

                                data.put("QR", new_QR);
                                data.put("name", name_user);
                                data.put("socketid", ID);

                                new HTTPPostHandler() {
                                    @Override
                                    public void onResponseData(String response) {
                                        super.onResponseData(response);
                                        try {
                                            JSONObject data = new JSONObject(response);
                                            boolean error = data.getBoolean("error");

                                            if(error) {
                                                CharSequence nof = name_user + " chưa tồn tại";
                                                Toast toast = Toast.makeText(context, nof, length);
                                                toast.show();
                                            }
                                            else {
                                                String text = data.getString("text");
                                                ShowImageQR(text, 500, 500);
                                            }
                                        } catch (JSONException | WriterException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.execute(host_url, data);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    Intent intent = new Intent(MainActivity.this, QRCodeScan.class);
                    startActivity(intent);
                }
            }
        });

        ActivityData();
    }

    private void ActivityData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();

                String where = intent.getStringExtra("state");
                if(where != null) {
                    Context context = getApplicationContext();
                    if (where.equals("scanner")) {
                        String image = intent.getStringExtra("qrCode");
                        ScanQr.setEnabled(true);
                        CharSequence txt = "Quét mã QR";
                        ScanQr.setText(txt);
                        user_special = true;
                        Toast toast = Toast.makeText(context, image, length);
                        toast.show();

                        // Image is decode
                        JSONObject data = new JSONObject();

                        try {
                            data.put("token", "token()");
                            data.put("decode", image);

                            //mSocket.connect();
                            mSocket.emit("result_decode", data);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(where.equals("register")) {
                        String name = intent.getStringExtra("name");
                        String message = intent.getStringExtra("message");
                        boolean state = intent.getBooleanExtra("QR", false);

                        name_user = name;

                        Toast toast = Toast.makeText(context, message, length);
                        toast.show();

                        if(! state) {
                            Toast toast1 = Toast.makeText(context, (CharSequence) "Your qr...", length);
                            toast1.show();
                        }
                    }

                    if(where.equals("login")) {
                        String name = intent.getStringExtra("name");
                        String message = intent.getStringExtra("message");
                        boolean sp = intent.getBooleanExtra("sp", false);
                        //-------------------------
                        mSocket.connect();
                        // Mở chức năng nút nhấn QR
                        ScanQr.setEnabled(true);
                        //-------------------------
                        user_special = sp;
                        if(sp) {
                            CharSequence text = "Quét mã QR";
                             ScanQr.setText(text);
                        }
                        else {
                            generateQR.setEnabled(true);
                            CharSequence text = "Sinh mã QR mới";
                            ScanQr.setText(text);
                        }

                        name_user = name;
                        Toast toast = Toast.makeText(context, message, length);
                        toast.show();
                        //threadSocket();
                        mSocket.emit("id", name_user);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        mSocket.connect();
//                        mSocket.emit("id", name_user);
                    }
                }
            }
        });
    }

    private void ShowImageQR(String text, int width, int height) throws WriterException {
        QRCodeWriter qr = new QRCodeWriter();
        BitMatrix bitMatrix = qr.encode(text, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        imgQR.setImageBitmap(bitmap);
    }


    private Runnable checkInternet() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean state = ConnectionReceiver.isConnected();

                if(state) {
                    //mSocket.connect();
                    if(! knowStateInternet) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getApplicationContext();
                                CharSequence nof = "Internet connected";
                                Toast toast = Toast.makeText(context, nof, length);
                                toast.show();
                            }
                        }).start();

                        knowStateInternet = true;
                    }
                }
                else {
                    if(knowStateInternet) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getApplicationContext();
                                CharSequence nof = "Check internet connected";
                                Toast toast = Toast.makeText(context, nof, length);
                                toast.show();
                            }
                        }).start();

                        knowStateInternet = false;
                    }
                }
            }
        };

        return  runnable;
    }

//    private String token() {
//        String kq = "";
//        StringBuilder l1 = new StringBuilder();
//        for(int i = 0; i < 128; i ++) {
//            l1.append(new Random(VariableConfig.alphabet.length()));
//        }
//        String p = l1.toString();
//        for(int i = 0; i < VariableConfig.arr.length; i ++) {
//            char old = p.charAt(VariableConfig.arr[i]);
//            char chr = VariableConfig.alphabet.charAt(VariableConfig.arr[i]);
//            kq = p.replace(old, chr);
//        }
//
//        Log.d("TESTING", kq);
//
//        return kq;
//    }

    private Emitter.Listener on_SocketID = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject dt = (JSONObject) args[0];
                    try {
                        ID = dt.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener on_state_Car = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        boolean state = data.getBoolean("state");
                        Context context = getApplicationContext();

                        String message = data.getString("message");
                        int pos = data.getInt("pos");
                        if(state) {
                            String newMessage = message + "\n" + "Vị trí xe được gửi là: " + pos;

                            Toast toast = Toast.makeText(context, newMessage, length);
                            toast.show();

                        }
                        else {
                            // Nhận lại xe
                            Toast toast = Toast.makeText(context, message, length);
                            toast.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

//    private Thread HandlerSocket() {
//        Thread thread = new Thread(new Runnable() {
//            @WorkerThread
//            @Override
//            public void run() {
//                if(! page) {
//                    mSocket.connect();
//                }
//                else {
//                    if(login && oneMore) {
//                        mSocket.emit("id", name_user);
//                        oneMore = false;
//                    }
//                }
//            }
//        });
//        return thread;
//    }
//    private Runnable ConnectSocket() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("TESTING", "RUN IN CONNECTSOCCKET RUNNABLE");
//                if(login && oneMore) {
//                      //mSocket.connect();
//                      //mSocket.emit("id", name_user);
////                    mSocket.on("id", on_SocketID);
////                    mSocket.on("stateCar", on_state_Car);
////
////                    mSocket.connect();
////
//                      oneMore = false;
//                }
//            }
//        };
//
//        return runnable;
//    }

//    private void threadSocket() {
//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(login && oneMore) {
//                    //Thread.sleep(100);
//                    mSocket.connect();
//                    mSocket.emit("id", name_user);
//                    oneMore = false;
//                }
//            }
//        });
//
//        thread.start();
//    }
}