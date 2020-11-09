package com.example.chinh_thuc;

import android.app.Application;

public class VariableConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String host = "http://192.168.1.5";
    public static int port = 90;
    public static String url_socket = "/android";
    public static String token_socket = "";
    public static String token_login = "";
    public static String url_login = "/login";
    public static String url_qr = "/qr";
    public static String url_register = "/create";
    public static String alphabet = "abcdefghijklmnbvbjhkkkhgnkert09358opqrstuavhgf12110vwxyzAB035fCDEFGHIJKLMNOPQRSTfsdjUVWXYZ0123456789dagfy";
    public static int[] arr = {10, 6, 7, 27, 11, 8, 2, 31, 29, 15, 32, 16, 17, 33, 19, 25, 39, 40, 62, 73};
}
