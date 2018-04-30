package com.wifi.p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;

/**
 * Created by kiryl on 30.4.18.
 */

public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    public WiFiP2PManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "MyModule";
    }

    @ReactMethod
    public void connect(String message) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(activity.getApplicationContext(), getMainLooper(), null);
            mReceiver = new WiFiDirectBroadcastReceiver();
        }
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
