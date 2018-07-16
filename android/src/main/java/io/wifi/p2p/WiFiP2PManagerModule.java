package io.wifi.p2p;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
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
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

/**
 * Created by zyusk on 01.05.2018.
 */
public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ReactApplicationContext reactContext;
    private final IntentFilter intentFilter = new IntentFilter();
    private WiFiP2PDeviceMapper mapper = new WiFiP2PDeviceMapper();

    public WiFiP2PManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
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
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Activity activity = getCurrentActivity();
        if (activity != null) {
            manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
            channel = manager.initialize(activity, getMainLooper(), null);

            WiFiP2PBroadcastReceiver receiver = new WiFiP2PBroadcastReceiver(manager, channel, reactContext);
            activity.registerReceiver(receiver, intentFilter);
        }
    }

    @ReactMethod
    public boolean isSuccesfullInitialize() {
        return manager != null && channel != null;
    }

    @ReactMethod
    public void createGroup(final Callback callback) {
        manager.createGroup(channel,  new WifiP2pManager.ActionListener()  {
            public void onSuccess() {
                System.out.println("WiFi Group creation successful");
                callback.invoke(true);
                //Group creation successful
            }

            public void onFailure(int reason) {
                System.out.println("WiFi Group creation failed");
                callback.invoke(reason);
                //Group creation failed
            }
        });
    }

    @ReactMethod
    public void removeGroup(final Callback callback) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke(true);
            }

            @Override
            public void onFailure(int reason) {
                callback.invoke(reason);
            }
        });
    }

    @ReactMethod
    public void getAvailablePeersList(final Callback callback) {
        manager.requestPeers(channel, new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList deviceList) {
                WritableMap params = mapper.mapDevicesInfoToReactEntity(deviceList);
                System.out.println(params);
                callback.invoke(params);
            }
        });
    }

    @ReactMethod
    public void discoverPeers(final Callback callback) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke(true);
            }

            @Override
            public void onFailure(int reasonCode) {
                callback.invoke(false);
            }
        });
    }

    @ReactMethod
    public void disconnect(final Callback callback) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke(true);
            }

            @Override
            public void onFailure(int reason) {
                callback.invoke(false);
            }
        });
    }

    @ReactMethod
    public void connect(final String deviceAddress, final Callback callback) {
        // Picking the first device found on the network.
        WifiP2pDevice device = new WifiP2pDevice();

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        System.out.println("config: " + config + "| device: " + device);
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke(deviceAddress);
                System.out.println("Connect is successfully");
                // WiFiP2PBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                callback.invoke(null);
                System.out.println("Connect is failure");
            }
        });
    }
}
