package io.wifi.p2p;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by zyusk on 01.05.2018.
 */

public class WiFiP2PManagerModule extends ReactContextBaseJavaModule {
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
}
