package io.wifi.p2p;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.databinding.ObservableArrayList;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

/**
 * Created by zyusk on 01.05.2018.
 */
public class WiFiP2PManagerModule extends ReactContextBaseJavaModule implements WifiP2pManager.ConnectionInfoListener {
    private WifiP2pInfo wifiP2pInfo;
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

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.wifiP2pInfo = info;
    }

    @ReactMethod
    public void getConnectionInfo(final Promise promise) {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInformation) {
                // todo: replace to mapper
                System.out.println(wifiP2pInformation);

                wifiP2pInfo = wifiP2pInformation;
                WritableMap groupOwnerAddress = Arguments.createMap();
                groupOwnerAddress.putString("hostAddress", wifiP2pInformation.groupOwnerAddress.getHostAddress());
                //groupOwnerAddress.putString("canonicalHostName", info.groupOwnerAddress.getCanonicalHostName());
                //groupOwnerAddress.putString("hostName", info.groupOwnerAddress.getHostName());
                groupOwnerAddress.putBoolean("isLoopbackAddress", wifiP2pInformation.groupOwnerAddress.isLoopbackAddress());

                WritableMap params = Arguments.createMap();
                params.putMap("groupOwnerAddress", groupOwnerAddress);
                params.putBoolean("groupFormed", wifiP2pInformation.groupFormed);
                params.putBoolean("isGroupOwner", wifiP2pInformation.isGroupOwner);

                promise.resolve(params);
            }
        });
    }

    @ReactMethod
    public void init() {
        if (manager != null) { // prevent reinitialization
            return;
        }

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
    public void isSuccessfulInitialize(Promise promise) {
        Boolean isSuccessfulInitialize = manager != null && channel != null;
        promise.resolve(isSuccessfulInitialize);
    }

    @ReactMethod
    public void createGroup(final Callback callback) {
        manager.createGroup(channel,  new WifiP2pManager.ActionListener()  {
            public void onSuccess() {
                callback.invoke();
                //Group creation successful
            }

            public void onFailure(int reason) {
                callback.invoke(Integer.valueOf(reason));
                //Group creation failed
            }
        });
    }

    @ReactMethod
    public void removeGroup(final Callback callback) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke();
            }

            @Override
            public void onFailure(int reason) {
                callback.invoke(Integer.valueOf(reason));
            }
        });
    }

    @ReactMethod
    public void getAvailablePeersList(final Promise promise) {
        manager.requestPeers(channel, new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList deviceList) {
                WritableMap params = mapper.mapDevicesInfoToReactEntity(deviceList);
                promise.resolve(params);
            }
        });
    }

    @ReactMethod
    public void discoverPeers(final Callback callback) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke();
            }

            @Override
            public void onFailure(int reasonCode) {
                callback.invoke(Integer.valueOf(reasonCode));
            }
        });
    }

    @ReactMethod
    public void stopPeerDiscovery(final Callback callback) {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke();
            }

            @Override
            public void onFailure(int reasonCode) {
                callback.invoke(Integer.valueOf(reasonCode));
            }
        });
    }

    @ReactMethod
    public void disconnect(final Callback callback) {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke();
            }

            @Override
            public void onFailure(int reasonCode) {
                callback.invoke(Integer.valueOf(reasonCode));
            }
        });
    }

    @ReactMethod
    public void connect(String deviceAddress, final Callback callback) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                callback.invoke();
                // WiFiP2PBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reasonCode) {
                callback.invoke(Integer.valueOf(reasonCode));
            }
        });
    }

    @ReactMethod
    public void sendFile(String filePath, Callback callback) {
        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = Uri.fromFile(new File(filePath));
        System.out.println("Sending: " + uri);
        System.out.println("Intent----------- " + uri);
        Intent serviceIntent = new Intent(getCurrentActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                wifiP2pInfo.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getCurrentActivity().startService(serviceIntent);

        callback.invoke("soon will be");
    }

    @ReactMethod
    public void receiveFile(Callback callback) {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (info.groupFormed && info.isGroupOwner) {
                    new FileServerAsyncTask(getCurrentActivity())
                            .execute();
                } else if (info.groupFormed) {
                    // The other device acts as the client. In this case, we enable the
                    // get file button.
                }
                // hide the connect button
            }
        });

        callback.invoke("soon will be");
    }
}
