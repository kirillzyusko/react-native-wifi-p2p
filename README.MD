# react-native-wifi-p2p

`react-native-wifi-p2p` is a library to provide WiFi Direct (Peer-To-Peer, P2P) service to react-native.

**_Important_**: currently only **Android** support realized.
If you want to provide iOS support, then you should to note to [react-native-multipeer](https://github.com/lwansbrough/react-native-multipeer) repository.

# Contents
* [Installation](#installation)
  * [npm install]()
  * [grand permissions]()
  * [link library](#link-library)
    * [Manually](#manually)
    * [Automatically](#automatically)
* [Overview API](#overview)
  * [API methods](#api)
  * [Consts usage](#constants)
* [Example of usage](#example-of-usage)

## Installation

- Install library from `npm`

```sh
npm install react-native-wifi-p2p --save
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

## Link library
  - #### Manually
    * Append the following lines to `android/settings.gradle`

    ```
    include ':react-native-wifi-p2p'
    project(':react-native-wifi-p2p').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-wifi-p2p/android')
    ```

    * Insert the following lines inside the dependencies block in `android/app/build.gradle`:

    ```
    compile project(':react-native-wifi-p2p')
    ```

    * Open up `android/app/src/main/java/[...]/MainApplication.java`
    
        1. Add import `import io.wifi.p2p.WiFiP2PManagerPackage;` to the imports at the top of the file.
        2. Add `new WiFiP2PManagerPackage()` to the list returned by the `getPackages()` method. Add a comma to the previous item if there's already something there.
        
  - #### Automatically
  ```bash
    react-native link react-native-wifi-p2p
  ```  
## Overview
### API
* [initialize()](#initialize)
* [isSuccessfulInitialize()](#issuccessfulinitialize)
* [startDiscoveringPeers()](#startdiscoveringpeers-promise)
* [subscribeOnPeersUpdates(callback)](#subscribeonpeersupdatescallback)
* [unsubscribeOnPeersUpdates(callback)](#unsubscribeonpeersupdatescallback)
* [subscribeOnConnectionInfoUpdates(callback)](#subscribeonconnectioninfoupdatescallback)
* [unsubscribeOnConnectionInfoUpdates(callback)](#unsubscribeonconnectioninfoupdatescallback),
* [getAvailablePeers()](#getavailablepeers)
* [connect(deviceAddress)](#connectdeviceaddress)
* [disconnect()](#disconnect)
* [createGroup()](#creategroup)
* [removeGroup()](#removegroup)
* [sendData(deviceAddress)]() `future`
* [sendMessage(deviceAddress)]() `future`

### CONSTANTS
* [WifiP2pDevice statuses](#wifip2pdevice-statuses)
* [Events of library](#events-of-library)

## API
### initialize()

This method must calling before any using of others method, because here happened start initialization of `react-native-wifi-p2p` module.
Recommend to initialize it on root files, but if your application has specific logic, then you can call it before directly using.
Notice, that recommend called only once.

```javascript
initialize();
```

### isSuccessfulInitialize()
Return boolean status `true/false` of initialization method.

```javascript
if(isSuccessfulInitialize()) {
    console.log('Initialization was successfully');
} else {
    console.log('Unlucky attempt of initialization. Try again!');
}
``` 

### startDiscoveringPeers(): Promise<string>

`startDiscoveringPeers()` starting emit action to discover available devices. Note, that this function doesn't return any information about available devices/peers. It return information about successfully start.

```javascript
startDiscoveringPeers()
    .then(() => console.log('Starting of discovering was successful'))
    .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details: ${err}`));
```

### subscribeOnPeersUpdates(callback)

`subscribeOnPeersUpdates(callback)` allow to subscribe on events, that will notify about availability of nearby devices.

```javascript
subscribeOnPeersUpdates(({ devices }) => {
    console.log(`New devices available: ${devices}`);
});
```

### unsubscribeOnPeersUpdates(callback)
```javascript
unsubscribeOnPeersUpdates((event) => console.log(event));
```

### subscribeOnConnectionInfoUpdates(callback)

```javascript
subscribeOnConnectionInfoUpdates((event) => {
    console.log('Connection Info Updates: ', event);
});
```

### unsubscribeOnConnectionInfoUpdates(callback)

```javascript
unsubscribeOnConnectionInfoUpdates((event) => {
    console.log('Unsubscribe from ConnectionInfoUpdates action: ', event);
});
```

### getAvailablePeers()
If you don't want to always get notification from event emitter, when peers list has changed, you can use this method. This method will return info about nearby devices, that is available for searching, at the time of the call this method.
**Important:** before using this method you also need to call `startDiscoveringPeers`. In other case you always will get empty array.
```javascript
getAvailablePeers()
    .then(({ devices }) => console.log(devices))
```

### connect(deviceAddress)

Connect to network. The devices found from the `subscribeOnPeersUpdates` method. Should use `deviceAddress` field from necessary device from array. This field represent a `MAC address` that is used to connect.

```javascript
connect('22:47:da:9d:58:83')
    .then(() => console.log('Successfully connected'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

### disconnect()

Disconnect from network. Should use when you want to reconnect or turn off wifi.

```javascript
disconnect()
    .then(() => console.log('Successfully disconnected'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

### createGroup()

P2P **STAR** is a peer-to-peer strategy that supports a **1-to-N**, or **star-shaped**, connection topology. In other words, this enables connecting devices within radio range (~100m) in a star shape, where each device can, at any given time, play the role of either a hub (where it can accept incoming connections from N other devices), or a spoke (where it can initiate an outgoing connection to a single hub), but not both. So achieve this goal you can use `createGroup` method, so create your own group.

```javascript
createGroup()
    .then(() => console.log('Group created successfully!'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

### removeGroup()
Before exit of application you need to call this method, if earlier you created group via `createGroup()` method.

```javascript
removeGroup()
    .then(() => console.log('Currently you don\'t belong to group!'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

## CONSTANTS

### WifiP2pDevice statuses

```javascript
import {
    CONNECTED,
    INVITED,
    FAILED,
    AVAILABLE,
    UNAVAILABLE
} from 'react-native-android-p2p-wifi/device-info-statuses';
```

### Events of library

```javascript
import {
    PEERS_UPDATED_ACTION,
    CONNECTION_INFO_UPDATED_ACTION,
 } from 'react-native-android-p2p-wifi';
```

### Example of usage

```javascript
import React, { Component } from 'react';
import {
    initialize,
    startDiscoveringPeers
} from 'react-native-android-p2p-wifi';

class ExampleUsgeP2PModule extends Component {
    componentDidMount() {
        initialize();
        // check on isSuccessfulInitialize() if need
        startDiscoveringPeers()
            .then(() => console.log('Starting of discovering was successful'))
            .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details: ${err}`));
        // subscribe to event of peers updates
        // or get current peers by getAvailablePeers() method
    }
    
    render() {
        return (
            <View>
                <Text></Text>
            </View>
        )
    }
}
```
