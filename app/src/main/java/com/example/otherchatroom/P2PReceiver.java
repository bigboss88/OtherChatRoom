package com.example.otherchatroom;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class P2PReceiver extends BroadcastReceiver {
    private MainActivity activity;
    private WifiP2pManager p2pMan;
    private WifiP2pManager.Channel chan;
    public P2PReceiver(MainActivity activityi,WifiP2pManager p2p,WifiP2pManager.Channel chan){
        activity = activityi;
        p2pMan =p2p;
        this.chan = chan;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //if the available peer list changes request the new list
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            //peer list has changed
            if (p2pMan != null){
                p2pMan.requestPeers(chan, activity.peerListListener);
            }
        }
        //when the connection status changes(new device or a device disconnected) update the list of connected peers
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            //connection state has changed
            WifiP2pGroup group = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            activity.updateConList(group);
            WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            activity.setInfo(info);
            //p2pMan.requestConnectionInfo(chan,activity.connectionInfoListener);
        }

        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
        else {

            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

}