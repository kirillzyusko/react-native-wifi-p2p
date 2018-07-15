# react-native-wifi-p2p

``react-native-wifi-p2p`` is a build to provide WiFi Direct (Peer-To-Peer, P2P) service to react-native.

**_Important_**: currently only **Android** support realized.
If you want to provide iOS support, then you should to note to [react-native-multipeer](https://github.com/lwansbrough/react-native-multipeer) repository.

## Installation

- Install library from `npm`

```sh
npm install kirikzyusko1996/react-native-wifi-p2p --save
```

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

```diff
+ include ':react-native-wifi-p2p'
+ project(':react-native-wifi-p2p').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-wifi-p2p/android')
```

- Insert the following lines inside the dependencies block in `android/app/build.gradle`:

```diff
+ compile project(':react-native-wifi-p2p')
```

Open up `android/app/src/main/java/[...]/MainApplication.java`

* Add import `import io.wifi.p2p.WiFiP2PManagerPackage;` to the imports at the top of the file.
* Add `new WiFiP2PManagerPackage()` to the list returned by the `getPackages()` method. Add a comma to the previous item if there's already something there.

## API
* [initialize()](https://github.com/kirikzyusko1996/react-native-wifi-p2p#initialize)
* [startDiscoveringPeers()](https://github.com/kirikzyusko1996/react-native-wifi-p2p#startdiscoveringpeers-promise)
* [subscribeOnPeersUpdates(callback)](https://github.com/kirikzyusko1996/react-native-wifi-p2p#subscribeonpeersupdatescallback)
* [unsubscribeOnPeersUpdates(callback)](https://github.com/kirikzyusko1996/react-native-wifi-p2p#unsubscribeonpeersupdatescallback)
* [connect(deviceAddress)](https://github.com/kirikzyusko1996/react-native-wifi-p2p#connectdeviceaddress)
* [createGroup]() `future`
* [sendData]() `future`
* [sendMessage]() `future`

### initialize()

This method must calling before any using of others method, because here happened start initialization of `react-native-wifi-p2p` module.
Recommend to initialize it on root files, but if your application has specific logic, then you can call it before directly using.
Notice, that recommend called only once.

### startDiscoveringPeers(): Promise<string>

`startDiscoveringPeers()` starting emit action to discover available devices. Note, that this function doesn't return any information about available devices/peers. It return information about successfully start.

```javascript
startDiscoveringPeers()
    .then(status => console.log('Starting of discovering was successful'))
    .cactch(status => console.error('Something is gone wrong. Are you sure, that your WiFi enabled?'))
})
```

### subscribeOnPeersUpdates(callback)

`subscribeOnPeersUpdates(callback)` allow to subscribe on events, that will notify about availability of nearby devices.

```javascript
subscribeOnPeersUpdates(({ devices }) => {
    console.log(`New devices available: ${devices}`);
})
```

### unsubscribeOnPeersUpdates(callback)
```javascript
unsubscribeOnPeersUpdates((event) => console.log(event))
```

### connect(deviceAddress)

Connect to network. The devices found from the `subscribeOnPeersUpdates` method. Should use `deviceAddress` field from necessary device from array. This field represent a `MAC address` that is used to connect.

```javascript
connect('22:47:da:9d:58:83')
    .then(status => console.log('Successfully connected'))
    .catch(err => console.log('Something gone wrong: ', err))
```