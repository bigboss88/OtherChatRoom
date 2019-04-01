package com.example.otherchatroom;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;


public class ServerThread extends Thread {

    private MainActivity main;
    private HashSet<String> ips;
    private boolean isGroupOwner;
    public ServerThread(MainActivity act,boolean go){
        this.main = act;
        this.isGroupOwner = go;
        ips = new HashSet<String>();
    }
    @Override
    public void run(){
        ServerSocket serverSocket = null;
        Log.d("Server Group owner", "run: "+isGroupOwner);
        try{
            serverSocket = new ServerSocket(49152);
            Log.d("Socket Sever","Created Server Socket");
            while(true){
                Log.d("Server Socket","Waiting for connection");
                Socket client = serverSocket.accept();
                Log.d("Socket Client","Connection Accepted");
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                final String out = dataInputStream.readUTF();

                if(out.charAt(0) == '!' && isGroupOwner){
                    updateIps(client.getInetAddress().getHostAddress());
                    Log.d("Server","Got ip: "+client.getInetAddress().getHostAddress());
                }
                else if (out.charAt(0) != '!'){
                    Log.d("Socket Server", "Got Message: " + out);
                    main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            main.updateLog(out);
                        }
                    });
                    client.close();
                    if (isGroupOwner) {
                        for (String ip : ips) {
                            Socket sock = new Socket();
                            sock.bind(null);
                            Log.d("Ip to send to",ip);
                            sock.connect(new InetSocketAddress(ip, 49152),5000);
                            Log.d("Connetec to IP",ip);
                            DataOutputStream dataOutputStream = new DataOutputStream(sock.getOutputStream());
                            dataOutputStream.writeUTF(out);
                            Log.d("Sent data to",ip);
                            dataOutputStream.close();
                        }
                    }

                }

            }
        }

        catch (IOException e){
            Log.d("Socket Error","Error when creating socket");
        }
        catch (SecurityException e){
            Log.d("Socket Error","Error with socket permissions");
        }
        catch (IllegalArgumentException e){
            Log.d("Socket Error","Port number not valid");
        }
    }

    public void updateIps(String addr){
        Log.d("Socket Server", "updateIps: "+addr);
        ips.add(addr);

    }

}
