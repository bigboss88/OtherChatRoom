package com.example.otherchatroom;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager p2p;
    private WifiP2pManager.Channel chan;
    private P2PReceiver rec;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private List<WifiP2pDevice> connectedPeers =  new ArrayList<WifiP2pDevice>();
    private String[] names;
    private TextView avPeers;
    private EditText peerNum;
    private Button connectButton;
    private EditText userName;
    private EditText message;
    private Button send;
    private TextView log;
    private Button dbg_Con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting up UI elements and what not
        avPeers = (TextView) findViewById(R.id.lbl_AvPeers);
        peerNum = (EditText) findViewById(R.id.txt_PeerNum);
        connectButton = (Button) findViewById(R.id.btn_Connect);
        log = (TextView) findViewById((R.id.lbl_MsgLog));
        dbg_Con = (Button) findViewById(R.id.btn_SeeCon);
        dbg_Con.setOnClickListener(this);
        connectButton.setOnClickListener(this);

        //Adding actions to the intent filter so the P2PReceiver knows what to do
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //change in list of available peers
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        //state of Wi-Fi P2P connectivity has changed

        //this device has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //get the p2p manager
        p2p = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //set uo the channel
        chan = p2p.initialize(this,getMainLooper(),null);
        //initialize our custom receiver
        rec = new P2PReceiver(this,p2p,chan);

        //start peer discovery
        p2p.discoverPeers(chan, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int butid = v.getId();
        //Button to connect to other peers
        if(butid == connectButton.getId()){
            int index = Integer.parseInt(peerNum.getText().toString());
            connect(index);
        }

        //Button to show connected peers
        else if(butid == dbg_Con.getId()){
            String str = "";
            // for each connected peer add it to a string and send it to a Toast
            for (WifiP2pDevice device: connectedPeers){
                Log.d("Connected Device",device.deviceName);
                str += device.deviceName;
                str+='\n';
            }
            Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
        }
    }

    //setting up our peerListListener, basically it just gets all the discovered peers and displays them
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            if (!peersList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peersList.getDeviceList());
                int i=0;
                names =  new String[peersList.getDeviceList().size()];

                for (WifiP2pDevice device : peersList.getDeviceList()){
                    names[i] = device.deviceName;
                    i++;
                }
            }
            // if no peers. Not sure if it works correctly
            if (peers.size() ==0){
                Toast.makeText(getApplicationContext(),"Something Wrong",Toast.LENGTH_SHORT).show();
            }
            else{
                String n ="";
                for(int i = 0;i< names.length;i++){
                    n+=""+i+": "+ names[i];
                    n+='\n';
                }
                avPeers.setText(n);
            }
        }


    };

    //just listens for a connection and lets the user know
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            Toast.makeText(MainActivity.this,"A connection came",Toast.LENGTH_SHORT).show();
        }
    };

    public void onResume(){
        super.onResume();

        registerReceiver(rec,intentFilter);
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(rec);
    }

    public void connect(int index) {
        //if the index isn't valid yell at the user
        if(index < 0 || index > peers.size()){
            Toast.makeText(MainActivity.this,"Invalid entry",Toast.LENGTH_SHORT).show();
            return;
        }
        //setting up the connection
        final WifiP2pDevice device = peers.get(index);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        //start the connection
        p2p.connect(chan, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"Connection Worked",Toast.LENGTH_SHORT).show();
                //connectedPeers.add(device);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this,"Connection failed retry",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //we need an up to date list of connected peers
    public void updateConList(WifiP2pGroup group){
        Collection<WifiP2pDevice> con_ls = group.getClientList(); // get the list
        if(group != null){
            // if the current device is the group owner
            if(group.isGroupOwner()){

                connectedPeers.clear();
                connectedPeers.addAll(con_ls);
            }
            //if it's a client you need to actually add the group owner since it won't be in the client list
            else{
                connectedPeers.clear();
                connectedPeers.add(group.getOwner());
                connectedPeers.addAll(con_ls);
            }
        }
        Log.d("Added Peer Size",""+con_ls.size());
        if(group.getOwner() != null && group != null){
            Log.d(" group owner", group.getOwner().deviceName);
        }
        Toast.makeText(MainActivity.this,"Updated Peer List",Toast.LENGTH_SHORT).show();
    }

}

