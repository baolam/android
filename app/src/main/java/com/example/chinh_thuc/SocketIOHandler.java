package com.example.chinh_thuc;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIOHandler {
    private Socket mSocket;

    public void ConfigSocket() {
        try {
            mSocket = IO.socket(VariableConfig.host + VariableConfig.url_socket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket socket() {
        return mSocket;
    }

}
