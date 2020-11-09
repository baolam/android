package com.example.chinh_thuc;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CounterTime extends Application {
    private int length = Toast.LENGTH_SHORT;
    private boolean knowStateInternet = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void ConfigTime() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(checkInternet(), 0, 5, TimeUnit.SECONDS);
    }

    private Runnable checkInternet() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("TESTING", "CheckInternet");

                Context context = getApplicationContext();
                boolean state = ConnectionReceiver.isConnected();
                if(state) {
                    if(! knowStateInternet) {
                        CharSequence nof = "Internet connected";
                        Toast toast = Toast.makeText(context, nof, length);
                        toast.show();
                        knowStateInternet = true;
                    }
                }
                else {
                    if(knowStateInternet) {
                        CharSequence nof = "Check internet connected";
                        Toast toast = Toast.makeText(context, nof, length);
                        toast.show();
                        knowStateInternet = false;
                    }
                }
            }
        };

        return  runnable;
    }

    public boolean stateInternet() {
        return knowStateInternet;
    }
}
