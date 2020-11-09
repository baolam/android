package com.example.chinh_thuc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRCodeScan extends AppCompatActivity {
    private SurfaceView camera;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private String decode;
    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_scan);

        camera = findViewById(R.id.camera);

        Context context = getApplicationContext();
        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        SurfaceViewer();
        ActivitySet();

    }

    private void ActivitySet() {
        Intent intent = getIntent();
        String setCount = intent.getStringExtra("RST_COUNTER");

        if(setCount != null) {
            if (setCount.equals("true")) {
                counter = 0;
            }
        }
    }

    private void SurfaceViewer() {
        camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Context context = getApplicationContext();
                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                try {
                    cameraSource.start(holder);
                }
                catch (IOException e) {
                    e.printStackTrace();
                };
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if(qrcode.size() != 0) {
                    decode = qrcode.valueAt(0).displayValue;

                    if(counter == 0) {
                        Intent intent = new Intent(QRCodeScan.this, MainActivity.class);
                        intent.putExtra("qrCode", decode);
                        intent.putExtra("state", "scanner");
                        startActivity(intent);
                    }

                    // Stop sending data more than 1
                    counter ++;
                }
            }
        });
    }
}