package io.wifi.p2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by kiryl on 16.7.18.
 */
public class WiFiP2PDeviceMapper {
    public WritableMap mapDevicesInfoToReactEntity(WifiP2pDeviceList deviceList) {
        WritableArray array = mapDeviceListToReactEntityArray(deviceList);

        WritableMap params = Arguments.createMap();
        params.putArray("devices", array);

        return params;
    }

    public WritableArray mapDeviceListToReactEntityArray(WifiP2pDeviceList deviceList) {
        WritableArray array = Arguments.createArray();

        for (WifiP2pDevice device : deviceList.getDeviceList()) {
            WritableMap params = mapDeviceInfoToReactEntity(device);

            array.pushMap(params);
        }

        return array;
    }

    public WritableMap mapDeviceInfoToReactEntity(WifiP2pDevice device) {
        WritableMap params = Arguments.createMap();

        params.putString("deviceName", device.deviceName);
        params.putString("deviceAddress", device.deviceAddress);
        params.putString("primaryDeviceType", device.primaryDeviceType);
        params.putString("secondaryDeviceType", device.secondaryDeviceType);
        params.putInt("status", device.status);

        return params;
    }
}
