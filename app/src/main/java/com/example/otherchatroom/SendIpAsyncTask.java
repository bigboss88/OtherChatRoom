package com.example.otherchatroom;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendIpAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context con;
    private WifiP2pInfo info;
    private String ip;
    public SendIpAsyncTask(Context context, WifiP2pInfo info,String ip){
        this.con = context;this.info = info;this.ip=ip;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        Socket socket = new Socket();
        int port = 49152;
        int SOCKET_TIMEOUT = 5000;

        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(info.groupOwnerAddress.getHostAddress(), port), SOCKET_TIMEOUT);
            Log.d("Socket IP", "Client has connected");
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("!"+ip);
            dataOutputStream.close();

        }
        catch (IOException e){
            Log.d("Socket IP Error","Error when creating socket");
        }
        catch (SecurityException e){
            Log.d("Socket IP Error","Error with socket permissions");
        }
        catch (IllegalArgumentException e){
            Log.d("Socket IP Error","Port number not valid");
        }
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        Log.d("Socket IP Error", "Can't close");
                    }
                }
            }
        }

        return null;
    }
}
