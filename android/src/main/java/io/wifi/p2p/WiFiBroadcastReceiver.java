package io.wifi.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by zyusk on 15.07.2018.
 */
public class WiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ReactApplicationContext reactContext;

    public WiFiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ReactApplicationContext reactContext) {
        super();

        this.manager = manager;
        this.channel = channel;
        this.reactContext = reactContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            manager.requestPeers(channel, peerListListener);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, connectionListener);
            }
        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            WritableArray array = Arguments.createArray();

            for (WifiP2pDevice device : peerList.getDeviceList()) {
                WritableMap params = Arguments.createMap();

                params.putString("deviceName", device.deviceName);
                params.putString("deviceAddress", device.deviceAddress);
                params.putString("primaryDeviceType", device.primaryDeviceType);
                params.putString("secondaryDeviceType", device.secondaryDeviceType);
                params.putInt("status", device.status);

                array.pushMap(params);
            }

            WritableMap params = Arguments.createMap();
            params.putArray("devices", array);
            sendEvent(reactContext, "WIFI_P2P:PEERS_UPDATED", params);
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            WritableMap params = Arguments.createMap();
            params.putString("address", groupOwnerAddress);
            sendEvent(reactContext, "WIFI_P2P:CONNECTION_INFO_UPDATED", params);
        }
    };

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }
}
