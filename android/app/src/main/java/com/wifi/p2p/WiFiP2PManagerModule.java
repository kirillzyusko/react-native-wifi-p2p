package com.wifi.p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;

/**
 * Created by kiryl on 30.4.18.
 */

public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public WiFiP2PManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Activity activity = getCurrentActivity();
        if (activity != null) {
            mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(activity.getApplicationContext(), getMainLooper(), null);
            mReceiver = new WiFiDirectBroadcastReceiver();
        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.
                ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {
                Log.d(WiFiDirectActivity.TAG, "No devices found");
                return;
            }
        }
    };

    @Override
    public String getName() {
        return "WiFiP2PManager";
    }

    @ReactMethod
    public void connect(String message) {
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "fail connection: "+ reason);
            }
        });
    }

    /**
     * A BroadcastReceiver that notifies of important wifi p2p events.
     */
    private class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        public WiFiDirectBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // UI update to indicate wifi p2p status.

                Log.d(TAG, "P2P state changed.");
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling connection is notified with a
                // callback on PeerListListener.onPeersAvailable()
                mManager.requestPeers(mChannel, getPeerListener());
                Log.d(TAG, "P2P peers changed.");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "P2P connection changed.");

                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    // we are connected with the other device, request connection
                    // info to find group owner IP
                    Log.d(TAG, "We are connected, yay! "+networkInfo.getState());
                    mManager.requestConnectionInfo(mChannel, getConnectionListener());
                }

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "This device changed.");
            }
        }
    }
}
