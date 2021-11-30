package com.example.chinh_thuc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String server = "http://192.168.1.10:3000";
    private ImageView imgQR;
    private EditText edt_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgQR = findViewById(R.id.QR_image);
        edt_server = findViewById(R.id.edt_server);

        Button btn_login = findViewById(R.id.LoginBtn);
        Button btn_scanqr = findViewById(R.id.ScanQr);

        btn_login.setOnClickListener(this);
        btn_scanqr.setOnClickListener(this);

        try {
            ShowImageQR(800, 500);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        edt_server.setText(server);
        edt_server.setEnabled(false);

        Intent intent = getIntent();
        if (intent != null) {
            // Handle here
        }
    }

    private void ShowImageQR(int width, int height) throws WriterException {
        QRCodeWriter qr = new QRCodeWriter();
        BitMatrix bitMatrix = qr.encode("Nguyễn Đức Bảo Lâm là người lập trình", BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        imgQR.setImageBitmap(bitmap);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        String tmp_server = edt_server.getText().toString();
        if (tmp_server.equals("")) {
            server = tmp_server;
            Toast.makeText(getApplicationContext(), "Bạn cần phải điền thông tin ở dưới", Toast.LENGTH_LONG).show();
            return;
        }
        switch (v.getId()) {
            case R.id.ScanQr: {
                Intent intent = new Intent(MainActivity.this, QRCodeScan.class);
                startActivity(intent);
                break;
            }
            case R.id.LoginBtn: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}