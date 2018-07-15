# react-native-wifi-p2p

``react-native-wifi-p2p`` is a build to provide WiFi Direct (Peer-To-Peer, P2P) service to react-native.

**_important_**: currently only android support realized

## Installation

- Install library from `npm`

``npm install kirikzyusko1996/react-native-wifi-p2p --save``

- Allow grant permission to WiFi module

```diff
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="io.wifi.p2p"
    android:versionCode="1"
    android:versionName="1.0">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
+   <uses-permission
+               android:required="true"
+               android:name="android.permission.ACCESS_COARSE_LOCATION"/>
+   <uses-permission
+           android:required="true"
+           android:name="android.permission.ACCESS_WIFI_STATE"/>
+   <uses-permission
+           android:required="true"
+           android:name="android.permission.CHANGE_WIFI_STATE"/>                                              

    ...
```

- Append the following lines to `android/settings.gradle`

```
include ':react-native-wifi-p2p'
project(':react-native-wifi-p2p').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-wifi-p2p/android')
```

- Insert the following lines inside the dependencies block in `android/app/build.gradle`:

``compile project(':react-native-wifi-p2p')``

Open up `android/app/src/main/java/[...]/MainApplication.java`

* Add import `import io.wifi.p2p.WiFiP2PManagerPackage;` to the imports at the top of the file.
* Add `new WiFiP2PManagerPackage()` to the list returned by the `getPackages()` method. Add a comma to the previous item if there's already something there.

## API
* [initialize]()
* [getAvailablePeers]()
* [connect]()
* [isWiFiEnabled]()
* [setWiFiState]()
