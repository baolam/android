package com.example.chinh_thuc;

import android.app.Application;

public class WiFiApp extends Application {
    public static WiFiApp wiFiApp;

    @Override
    public void onCreate() {
        super.onCreate();
        wiFiApp = this;
    }

    public static synchronized WiFiApp getInstance() {
        return wiFiApp;
    }
}
