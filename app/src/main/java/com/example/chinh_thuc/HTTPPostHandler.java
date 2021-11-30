package com.example.chinh_thuc;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.Arrays;

public class HTTPPostHandler extends AsyncTask<JSONObject, String, String> {
    private String utf8 = "utf-8";
    private String value1_state2 = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        JSONObject jsonObject = jsonObjects[0];
        JSONObject data = jsonObjects[1];
        String result = null;
        try {
            String url = jsonObject.getString("url");
            String method = jsonObject.getString("method");

            Log.d("TESTING", url);

            HttpURLConnection httpURLConnection = ConfigRequest(url, method);
            SendRequest(httpURLConnection, data);

            result = response(httpURLConnection);


        } catch (JSONException | IOException e) {
            Log.d("TESTING", e.toString());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onResponseData(s);
    }

    private HttpURLConnection ConfigRequest(String mUrl, String method) throws IOException {
        URL url = new URL(mUrl);
        Log.d("TESTING", mUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        if(method.equals("POST")) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            String key1 = "Content-Type";
            String value1 ="application/json;charset=utf-8";
            String key2 = "Accept";
            String value2 = "application/json";

            httpURLConnection.setRequestProperty(key1, value1);
            httpURLConnection.setRequestProperty(key2, value2);
            httpURLConnection.setRequestMethod(method);

        }

        return httpURLConnection;
    }

    private void SendRequest(HttpURLConnection httpURLConnection, JSONObject data) throws IOException {
        OutputStreamWriter dataOutputStream = new OutputStreamWriter(httpURLConnection.getOutputStream());
        String send = data.toString();
        Log.d("TESTING", send);
        byte[] input = send.getBytes(utf8);

        dataOutputStream.write(send);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private String response(HttpURLConnection httpURLConnection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream(), utf8)
        );
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line.trim());
        }
        return stringBuilder.toString();
    }

    public void onResponseData(String response) {

    }
}
