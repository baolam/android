package com.example.chinh_thuc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRCodeScan extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {
    private SurfaceView camera;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_scan);

    }

    @Override
    public void onScanned(Barcode barcode) {
        String data = barcode.displayValue;
        if (data.equals("lamdethuondahihi")) {
            Intent intent = new Intent(QRCodeScan.this, CarformActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }
}