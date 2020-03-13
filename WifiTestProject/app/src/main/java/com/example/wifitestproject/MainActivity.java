package com.example.wifitestproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiP2pManager p2pManager ;
    private WifiP2pManager.Channel channel ;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private boolean isPermissionGranted=false;
    private final int WIFI_PERMISSION_GRANTED=1;
    private List<WifiP2pDevice> nearByWifiDevices = new ArrayList<>();
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private ListView peerList;

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            if(!peers.getDeviceList().equals(nearByWifiDevices)){
                nearByWifiDevices.clear();
                nearByWifiDevices.addAll(peers.getDeviceList());
                deviceNames = new String[nearByWifiDevices.size()];
                devices = new WifiP2pDevice[nearByWifiDevices.size()];
                int i=0;
                for(WifiP2pDevice device: peers.getDeviceList()){
                    deviceNames[i]=device.deviceName;
                    devices[i]=device;
                    i++;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
                peerList.setAdapter(arrayAdapter);
            }
            if(nearByWifiDevices.size()==0)
                Toast.makeText(MainActivity.this, "No devices found!!!", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        peerList = findViewById(R.id.list_view);
        checkThisAppPermission();
        if(isPermissionGranted) {
            performActionAfterPermissionGranted();
            findNearByWifiHotspot();
        }
    }

    private void performActionAfterPermissionGranted() {
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pManager.initialize(this, getMainLooper(), null);
        receiver = new WifiBroadcastReceiver(p2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void checkThisAppPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted=false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                    WIFI_PERMISSION_GRANTED);
        }else{
            isPermissionGranted=true;
        }
    }

    private void findNearByWifiHotspot(){
        p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Finding nearby devices!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Error finding devices!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WIFI_PERMISSION_GRANTED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted=true;
                    performActionAfterPermissionGranted();
                } else {
                    isPermissionGranted=false;
                    Toast.makeText(this, "Permission needed", Toast.LENGTH_SHORT).show();
                    checkThisAppPermission();
                }
                return;
            }
        }
    }
}
