package com.example.otherchatroom;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class MessageAsyncTask extends AsyncTask<String,Void,String> {
    private Context con;
    private List<WifiP2pDevice> peers;
    private WifiP2pInfo info;
    public MessageAsyncTask(Context context, List<WifiP2pDevice> conPeers, WifiP2pInfo info) {
        con = context;
        peers = conPeers;
        this.info = info;
    }

    @Override
    protected String doInBackground(String... params) {
        for (WifiP2pDevice device : peers) {
            String host = device.deviceAddress;
            Socket socket = new Socket();
            int port = 49152;
            int SOCKET_TIMEOUT = 5000;

            try {
                socket.bind(null);
                socket.connect(new InetSocketAddress(info.groupOwnerAddress.getHostAddress(), port), SOCKET_TIMEOUT);
                Log.d("owner host Address",info.groupOwnerAddress.getHostAddress());
                Log.d("Socket", "Client has connected");
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(params[0]);
                dataOutputStream.close();

            }
            catch (IOException e){
                Log.d("Socket Message Error","Error when creating socket");
            }
            catch (SecurityException e){
                Log.d("Socket Messgae Error","Error with socket permissions");
            }
            catch (IllegalArgumentException e){
                Log.d("Socket Error","Port number not valid");
            }
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            Log.d("Socket Error", "Can't close");
                        }
                    }
                }
            }


        }
        return null;
    }
}
