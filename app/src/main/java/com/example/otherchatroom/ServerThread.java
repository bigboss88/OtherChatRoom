package com.example.otherchatroom;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread {

    private MainActivity main;
    public ServerThread(MainActivity act){
        this.main = act;
    }
    @Override
    public void run(){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(49152);
            Log.d("Socket Sever","Created Server Socket");
            while(true){
                Socket client = serverSocket.accept();
                Log.d("Socket Client","Connection Accepted");
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                final String out = dataInputStream.readUTF();
                Log.d("Socket Server","Got Message: "+out);
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        main.updateLog(out);
                    }
                });

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

}
