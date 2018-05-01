package io.wifi.p2p;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import static android.os.Looper.getMainLooper;

/**
 * Created by zyusk on 01.05.2018.
 */

public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    public WiFiP2PManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "WiFiP2PManagerModule";
    }

    @ReactMethod
    public void log() {
        System.out.println("Hello world");
        Log.d("Hello","World");
    }

    @ReactMethod
    public void test() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            System.out.println("not null");
            manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
            System.out.println(manager); //null
            channel = manager.initialize(activity, getMainLooper(), null);
            System.out.println(channel);
        }
    }
}
