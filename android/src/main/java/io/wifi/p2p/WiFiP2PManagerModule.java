package io.wifi.p2p;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.databinding.ObservableArrayList;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

/**
 * Created by zyusk on 01.05.2018.
 */

public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ObservableArrayList<WifiP2pDevice> observablePeers = new ObservableArrayList<>();

    public WiFiP2PManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "WiFiP2PManagerModule";
    }

    @ReactMethod
    public void getInfoAboutCurrentState() {
        System.out.println(manager); // null
        System.out.println(channel);
    }

    @ReactMethod
    public void init() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
            channel = manager.initialize(activity, getMainLooper(), null);
        }
    }

    @ReactMethod
    public boolean isSuccesfullInitialize() {
        return manager != null && channel != null;
    }

    @ReactMethod
    public void getAvailablePeersList(Callback listener) {
        System.out.println(manager); // null
        System.out.println(channel);
        CallbackPeerListener callbackPeerListener = new CallbackPeerListener(listener);
        observablePeers.addOnListChangedCallback(callbackPeerListener);
        System.out.println("Try to request peer list");
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("SUCCESS DISCOVER PEERS");
            }

            @Override
            public void onFailure(int reasonCode) {
                System.out.println("FAILED DISCOVER PEERS " + reasonCode);
            }
        });

        manager.requestPeers(channel, peerListListener);
    }

    @ReactMethod
    public void connect(final String deviceAddress, final Callback listener) {
        // Picking the first device found on the network.
        WifiP2pDevice device = new WifiP2pDevice();

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        System.out.println("config: " + config + "| device: " + device);
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                listener.invoke(deviceAddress);
                System.out.println("Connect is successfully");
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                listener.invoke(null);
                System.out.println("Connect is failure");
            }
        });
    }

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            System.out.println("onPeersAvailable " + peerList.getDeviceList().size() + " " + peerList.describeContents());
            List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());
            if (!refreshedPeers.equals(observablePeers)) {
                System.out.println("List was changed");
                observablePeers.clear();
                observablePeers.addAll(refreshedPeers);
            }

            if (observablePeers.size() == 0) {
                Log.d("PeerListListener", "No devices found");
                return;
            }
        }
    };
}
