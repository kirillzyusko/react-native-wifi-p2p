package io.wifi.p2p;

import static android.os.Looper.getMainLooper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import java.io.File;

/** Created by zyusk on 01.05.2018. */
public class WiFiP2PManagerModule extends ReactContextBaseJavaModule
    implements WifiP2pManager.ConnectionInfoListener {
  private WifiP2pInfo wifiP2pInfo;
  private WifiP2pManager manager;
  private WifiP2pManager.Channel channel;
  private ReactApplicationContext reactContext;
  private static final String TAG = "RNWiFiP2P";
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
    manager.requestConnectionInfo(
        channel,
        new WifiP2pManager.ConnectionInfoListener() {
          @Override
          public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInformation) {
            Log.i(TAG, wifiP2pInformation.toString());

            wifiP2pInfo = wifiP2pInformation;

            promise.resolve(mapper.mapWiFiP2PInfoToReactEntity(wifiP2pInformation));
          }
        });
  }

  @ReactMethod
  public void getGroupInfo(final Promise promise) {
    manager.requestGroupInfo(
        channel,
        new WifiP2pManager.GroupInfoListener() {
          @Override
          public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
              promise.resolve(mapper.mapWiFiP2PGroupInfoToReactEntity(group));
            } else {
              promise.resolve(null);
            }
          }
        });
  }

  @ReactMethod
  public void init(Promise promise) {
    if (manager != null) { // prevent reinitialization
      return;
    }

    IntentFilter intentFilter = new IntentFilter();

    // Indicates a change in the Wi-Fi Direct status.
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

    // Indicates a change in the list of available peers.
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

    // Indicates the state of Wi-Fi Direct connectivity has changed.
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

    // Indicates this device's details have changed.
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    Activity activity = getCurrentActivity();
    if (activity != null) {
      try {
        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, getMainLooper(), null);

        WiFiP2PBroadcastReceiver receiver =
            new WiFiP2PBroadcastReceiver(manager, channel, reactContext);
        activity.registerReceiver(receiver, intentFilter);

        promise.resolve(manager != null && channel != null);
      } catch (NullPointerException e) {
        promise.reject("0x1", "can not get WIFI_P2P_SERVICE");
      }
    }

    promise.reject(
        "0x0", this.getName() + " module can not be initialized, since main activity is `null`");
  }

  @ReactMethod
  public void createGroup(final Callback callback) {
    manager.createGroup(
        channel,
        new WifiP2pManager.ActionListener() {
          public void onSuccess() {
            callback.invoke(); // Group creation successful
          }

          public void onFailure(int reason) {
            callback.invoke(Integer.valueOf(reason)); // Group creation failed
          }
        });
  }

  @ReactMethod
  public void removeGroup(final Callback callback) {
    manager.removeGroup(
        channel,
        new WifiP2pManager.ActionListener() {
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
    manager.requestPeers(
        channel,
        new PeerListListener() {
          @Override
          public void onPeersAvailable(WifiP2pDeviceList deviceList) {
            WritableMap params = mapper.mapDevicesInfoToReactEntity(deviceList);
            promise.resolve(params);
          }
        });
  }

  @ReactMethod
  public void discoverPeers(final Callback callback) {
    manager.discoverPeers(
        channel,
        new WifiP2pManager.ActionListener() {
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
    manager.stopPeerDiscovery(
        channel,
        new WifiP2pManager.ActionListener() {
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
  public void cancelConnect(final Callback callback) {
    manager.cancelConnect(
        channel,
        new WifiP2pManager.ActionListener() {
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
  public void connectWithConfig(ReadableMap readableMap, final Callback callback) {
    Bundle bundle = Arguments.toBundle(readableMap);
    WifiP2pConfig config = new WifiP2pConfig();

    String deviceAddress = bundle.getString("deviceAddress");
    config.deviceAddress = deviceAddress;
    config.wps.setup = WpsInfo.PBC;

    if (bundle.containsKey("groupOwnerIntent")) {
      config.groupOwnerIntent = (int) bundle.getDouble("groupOwnerIntent");
    }
    ;

    manager.connect(
        channel,
        config,
        new WifiP2pManager.ActionListener() {
          @Override
          public void onSuccess() {
            callback.invoke(); // WiFiP2PBroadcastReceiver notifies us. Ignore for now.
          }

          @Override
          public void onFailure(int reasonCode) {
            callback.invoke(Integer.valueOf(reasonCode));
          }
        });
  }
  ;

  @ReactMethod
  public void sendFile(String filePath, final Promise promise) {
    return sendFileTo(filePath, wifiP2pInfo.groupOwnerAddress.getHostAddress(), promise);
  }

  @ReactMethod
  public void sendFileTo(final String filePath, final String address, final Promise promise) {
    // User has picked a file. Transfer it to group owner i.e peer using FileTransferService
    Uri uri = Uri.fromFile(new File(filePath));
    Log.i(TAG, "Sending: " + uri);
    Log.i(TAG, "Intent----------- " + uri);
    Intent serviceIntent = new Intent(getCurrentActivity(), FileTransferService.class);
    serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
    serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, address);
    serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, 8988);
    serviceIntent.putExtra(
        FileTransferService.REQUEST_RECEIVER_EXTRA,
        new ResultReceiver(null) {
          @Override
          protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) { // successful transfer
              promise.resolve(mapper.mapSendFileBundleToReactEntity(resultData));
            } else { // error
              promise.reject(String.valueOf(resultCode), resultData.getString("error"));
            }
          }
        });
    getCurrentActivity().startService(serviceIntent);
  }

  @ReactMethod
  public void receiveFile(
      String folder, String fileName, final Boolean forceToScanGallery, final Callback callback) {
    final String destination = folder + fileName;
    manager.requestConnectionInfo(
        channel,
        new WifiP2pManager.ConnectionInfoListener() {
          @Override
          public void onConnectionInfoAvailable(WifiP2pInfo info) {
            if (info.groupFormed) {
              new FileServerAsyncTask(
                      getCurrentActivity(),
                      callback,
                      destination,
                      new CustomDefinedCallback() {
                        @Override
                        public void invoke(Object object) {
                          if (forceToScanGallery) { // fixes:
                            // https://github.com/kirillzyusko/react-native-wifi-p2p/issues/31
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                              final Intent scanIntent =
                                  new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                              final File file = new File(destination);
                              final Uri contentUri = Uri.fromFile(file);
                              scanIntent.setData(contentUri);
                              reactContext.sendBroadcast(scanIntent);
                            } else {
                              final Intent intent =
                                  new Intent(
                                      Intent.ACTION_MEDIA_MOUNTED,
                                      Uri.parse(
                                          "file://" + Environment.getExternalStorageDirectory()));
                              reactContext.sendBroadcast(intent);
                            }
                          }
                        }
                      })
                  .execute();
            } else {
              Log.i(TAG, "You must be in a group to receive a file");
            }
          }
        });
  }

  @ReactMethod
  public void sendMessage(String message, final Promise promise) {
    this.sendMessageTo(message, wifiP2pInfo.groupOwnerAddress.getHostAddress(), promise);
  }

  @ReactMethod
  public void sendMessageTo(final String message, final String address, final Promise promise) {
    Log.i(TAG, "Sending message: " + message);
    Intent serviceIntent = new Intent(getCurrentActivity(), MessageTransferService.class);
    serviceIntent.setAction(MessageTransferService.ACTION_SEND_MESSAGE);
    serviceIntent.putExtra(MessageTransferService.EXTRAS_DATA, message);
    serviceIntent.putExtra(MessageTransferService.EXTRAS_ADDRESS, address);
    serviceIntent.putExtra(MessageTransferService.EXTRAS_PORT, 8988);
    serviceIntent.putExtra(
        MessageTransferService.REQUEST_RECEIVER_EXTRA,
        new ResultReceiver(null) {
          @Override
          protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) { // successful transfer
              promise.resolve(mapper.mapSendMessageBundleToReactEntity(resultData));
            } else { // error
              promise.reject(String.valueOf(resultCode), resultData.getString("error"));
            }
          }
        });
    getCurrentActivity().startService(serviceIntent);
  }

  @ReactMethod
  public void receiveMessage(final Callback callback) {
    manager.requestConnectionInfo(
        channel,
        new WifiP2pManager.ConnectionInfoListener() {
          @Override
          public void onConnectionInfoAvailable(WifiP2pInfo info) {
            if (info.groupFormed) {
              new MessageServerAsyncTask(callback).execute();
            } else {
              Log.i(TAG, "You must be in a group to receive messages");
            }
          }
        });
  }
}
