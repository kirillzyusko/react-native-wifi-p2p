package io.wifi.p2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

/** Created by kiryl on 16.7.18. */
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
    params.putBoolean("isGroupOwner", device.isGroupOwner());
    params.putInt("status", device.status);

    return params;
  }

  public WritableMap mapWiFiP2PInfoToReactEntity(WifiP2pInfo wifiP2pInformation) {
    WritableMap params = Arguments.createMap();

    if (wifiP2pInformation.groupOwnerAddress != null) {
      WritableMap groupOwnerAddress = Arguments.createMap();
      groupOwnerAddress.putString(
          "hostAddress", wifiP2pInformation.groupOwnerAddress.getHostAddress());
      groupOwnerAddress.putBoolean(
          "isLoopbackAddress", wifiP2pInformation.groupOwnerAddress.isLoopbackAddress());

      params.putMap("groupOwnerAddress", groupOwnerAddress);
    } else {
      params.putNull("groupOwnerAddress");
    }

    params.putBoolean("groupFormed", wifiP2pInformation.groupFormed);
    params.putBoolean("isGroupOwner", wifiP2pInformation.isGroupOwner);

    return params;
  }

  public WritableMap mapWiFiP2PGroupInfoToReactEntity(WifiP2pGroup group) {
    WritableMap params = Arguments.createMap();

    params.putString("interface", group.getInterface());
    params.putString("networkName", group.getNetworkName());
    params.putString("passphrase", group.getPassphrase());

    WifiP2pDevice groupOwner = group.getOwner();

    if (group.getOwner() != null) {
      WritableMap owner = Arguments.createMap();

      owner.putString("deviceAddress", groupOwner.deviceAddress);
      owner.putString("deviceName", groupOwner.deviceName);
      owner.putInt("status", groupOwner.status);
      owner.putString("primaryDeviceType", groupOwner.primaryDeviceType);
      owner.putString("secondaryDeviceType", groupOwner.secondaryDeviceType);

      params.putMap("owner", owner);
    } else {
      params.putNull("owner");
    }

    return params;
  }

  public WritableMap mapSendFileBundleToReactEntity(Bundle bundle) {
    WritableMap params = Arguments.createMap();

    params.putDouble("time", bundle.getLong("time"));
    params.putString("file", bundle.getString("file"));

    return params;
  }

  public WritableMap mapSendMessageBundleToReactEntity(Bundle bundle) {
    WritableMap params = Arguments.createMap();

    params.putDouble("time", bundle.getLong("time"));
    params.putString("message", bundle.getString("message"));

    return params;
  }
}
