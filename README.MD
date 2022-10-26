# react-native-wifi-p2p

`react-native-wifi-p2p` is a library to provide WiFi Direct (Peer-To-Peer, P2P) service to react-native.

**_Important_**: currently only **Android** support realized.
If you want to provide iOS support, then you should to note to [react-native-multipeer](https://github.com/lwansbrough/react-native-multipeer) repository.

**This library will not work on emulator. If you want to use this library, you should test working it on real devices.**

Test project with all API cases you can find [here](https://github.com/kirillzyusko/react-native-wifi-p2p-example).

## React Native Compatibility
To use this library you need to ensure you match up with the correct version of React Native you are using.

p.s. React Native introduced AndroidX support in 0.60, which is a **breaking change** for most libraries (incl. this one) using native Android functionality.

| `react-native-wifi-p2p` version | Required React Native Version                                                              |
| ----------------------------------------- | --------------------------------------------------------------------------------- |
| `1.x.x`                                   | `>= 0.60`                                                                         |
| `0.x.x`                                   | `<= 0.59`                                                                         |

# Contents
* [Installation](#installation)
  * [npm install](#install-library-from-npm)
  * [grand permissions](#allow-grant-permission-to-wifi-module)
  * [link library](#link-library)
* [Overview API](#overview)
  * [API methods](#api)
  * [Consts usage](#constants)
  * [Caveats](#caveats)
* [Example of usage](#example-of-usage)

## Installation

### Install library from `npm`

```sh
npm install react-native-wifi-p2p --save
# or with yarn
# yarn add react-native-wifi-p2p
```

### Allow grant permission to WiFi module

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
+   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
+   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>                                          
    ...
```

### Link library
> **Note**: You should skip it if you use react-native >0.60 
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
    
  - Add import `import io.wifi.p2p.WiFiP2PManagerPackage;` to the imports at the top of the file.
  - Add `new WiFiP2PManagerPackage()` to the list returned by the `getPackages()` method. Add a comma to the previous item if there's already something there.

# Overview

## API

### Initialization
* [initialize()](#initialize)
* [startDiscoveringPeers()](#startdiscoveringpeers)
* [stopDiscoveringPeers()](#stopdiscoveringpeers)

### Subscribers & Actions annunciators
* [subscribeOnPeersUpdates(callback)](#subscribeonpeersupdatescallback)
* [subscribeOnConnectionInfoUpdates(callback)](#subscribeonconnectioninfoupdatescallback)
* [subscribeOnThisDeviceChanged(callback)](#subscribeonthisdevicechangedcallback)

### Interaction with other devices
* [getAvailablePeers()](#getavailablepeers)
* [connect(deviceAddress)](#connectdeviceaddress)
* [connectWithConfig(connectionArgs)](#connectwithconfigconnectionargs)
* [cancelConnect()](#cancelconnect)
* [createGroup()](#creategroup)
* [removeGroup()](#removegroup)
* [getGroupInfo()](#getgroupinfo)
* [getConnectionInfo()](#getconnectioninfo)
* [sendFile(pathToFile)](#sendfilepathtofile)
* [sendMessage(message)](#sendmessagemessage)
* [receiveFile(folder, fileName, forceToScanGallery)](#receivefilefolder-filename-forcetoscangallery)
* [receiveMessage()](#receivemessage)

### CONSTANTS
* [WifiP2pDevice statuses](#wifip2pdevice-statuses)
* [Events of library](#events-of-library)

## API

## Initialization

### initialize()

This method must calling before any using of others method, because here happened start initialization of `react-native-wifi-p2p` module.
Recommend to initialize it on root files, but if your application has specific logic, then you can call it before directly using.
Notice, that recommend called only once.

```javascript
initialize()
    .then((isInitializedSuccessfully) => console.log('isInitializedSuccessfully: ', isInitializedSuccessfully))
    .catch((err) => console.log('initialization was failed. Err: ', err));
```

### startDiscoveringPeers()

`startDiscoveringPeers()` starting emit action to discover available devices. Note, that this function doesn't return any information about available devices/peers. It return information about successfully start.

```javascript
startDiscoveringPeers()
    .then(() => console.log('Starting of discovering was successful'))
    .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details: ${err}`));
```

### stopDiscoveringPeers()

`stopDiscoveringPeers()` prevent emit action to discover available devices. Note, that this function doesn't return any information about available devices/peers. It return information about successfully stopping of discovering.

```javascript
stopDiscoveringPeers()
    .then(() => console.log('Stop discovering was successful'))
    .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details: ${err}`));
```
## Subscribers & Actions annunciators

### subscribeOnPeersUpdates(callback)

`subscribeOnPeersUpdates(callback)` allow to subscribe on events, that will notify about availability of nearby devices.

```javascript
const subscription = subscribeOnPeersUpdates(({ devices }) => {
    console.log(`New devices available: ${devices}`);
});

// in order to remove a subscription (on unmount for example)
subscription.remove();
```

### subscribeOnConnectionInfoUpdates(callback)

```javascript
const subscription = subscribeOnConnectionInfoUpdates((event) => {
    console.log('Connection Info Updates: ', event);
});

// in order to remove a subscription (on unmount for example)
subscription.remove();
```

### subscribeOnThisDeviceChanged(callback)

```javascript
const subscription = subscribeOnThisDeviceChanged((event) => {
    console.log('This device changed: ', event);
});

// in order to remove a subscription (on unmount for example)
subscription.remove();
```

## Interaction with other devices
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


### connectWithConfig(connectionArgs)

Connect to network. The method takes two arguments `deviceAddress` and an optional 
`groupOwnerIntent`. `groupOwnerIntent` specifies to the OS the intentsity with which the device initiaiting the connection wants to be the group Owner.(i.e the server). It takes a value between `0` and `15`, 0 and 15 being the least and most likely group owners respectively. i.e device specifying `groupOwnerIntent 15` is more likely to be a group owner.
Link to andrdoid documentation [https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pConfig]

```javascript
connectWithConfig({ deviceAddress: '22:47:da:9d:58:83', groupOwnerIntent: 15 })
    .then(() => console.log('Successfully connected as group Owner.'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

### cancelConnect()

Cancel connection from network. Should use when you want to reconnect or turn off wifi.

```javascript
cancelConnect()
    .then(() => console.log('Connection successfully canceled'))
    .catch(err => console.error('Something gone wrong. Details: ', err));
```

If you want to [disconnect](https://stackoverflow.com/questions/18679481/wifi-direct-end-connection-to-peer-on-android/18792707#18792707), then you can use the following chain of functions:

```javascript
getGroupInfo()
    .then(() => removeGroup())
    .then(() => console.log('Succesfully disconnected!'))
    .catch(err => console.error('Something gone wrong. Details: ', err))
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

```javascript
createGroup()
      .then(() => {
        setTimeout(() => {
          getGroupPassphraseInfo().then(passphrase => console.log(passphrase));
        }, 3000);
      })
      .catch(err => console.error("Something gone wrong. Details: ", err));
```

### getGroupInfo()

You can call it after `createGroup()` method is executed. Info from this method also is available in `subscribeOnThisDeviceChanged`. This method was created basically for supporting API <= 17. See [issue](https://github.com/kirillzyusko/react-native-wifi-p2p/issues/21) thread. 

### getConnectionInfo()

This method in the main is used in `sendFile()` flow. It's needed for saving `WifiP2pInfo` internally of this library. Also this method returns actual connection information. Fot its usage see description of `sendFile` method.

### sendFile(pathToFile)
You should call this method if you want to copy file from client side to server using wi-fi p2p feature. Before sending you should execute next steps:

1. Create group on server side
2. Connect to server-group from client side
3. After establishing connection you should call `getConnectionInfo()` on client side
4. Call `receiveFile(directory, fileName)` on server side (`receiveFile` return `Promise<string>` - path to saved file)
5. Call `sendFile(pathToFile)` on client side, after resolving `getConnectionInfo()`

```javascript
import { PermissionsAndroid } from 'react-native';

PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to read',
                      'message': 'READ_EXTERNAL_STORAGE'
                  }
              )
          .then(granted => {
              if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  console.log("You can use read operation")
              } else {
                  console.log("Read operation permission denied")
              }
          })
          .then(() => {
              return PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to write',
                      'message': 'WRITE_EXTERNAL_STORAGE'
                  }
              )
          })
          .then(() => {
              // `/storage/emulated/0/Music/Bullet For My Valentine:Letting You Go.mp3` - example of `pathToFIle`
              return sendFile('path/to/file')
                  .then((metaInfo) => console.log('File sent successfully:', metaInfo))
                  .catch(err => console.log('Error while file sending', err));
          })
          .catch(err => console.log(err));
```

### sendMessage(message)
You should call this method if you want to send from client side to server (group) using wi-fi p2p feature. Before sending you should execute next steps:
1. Create group on server side (side, which will receive message)
2. Connect to server-group from client side
3. After establishing connection you should call `getConnectionInfo()` on client side (also recommend do it on group-server side)
4. Call `receiveMessage()` on server side (`receiveMessage` return `Promise<string>` - message from client)
5. Call `sendMessage(message)` on client side, after resolving `getConnectionInfo()`

_Note_: you cannot send character encoding for string and by default this library uses `UTF-8`.

### receiveFile(folder, fileName, forceToScanGallery)

If you expect, that someone may send you a file - you can call this method in order to receive it.

If you want to save file with the same name as it's on client device, then before sending file you can `sendMessage` about its `fileName`.

`forceToScanGallery` is an optional parameter, which indicate whether should we scan and detect new files or not in order to show them in Gallery app. By default it's `false`.

_Note:_ if you expect file to be received you should request permissions for writing to the storage:

```javascript
PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
          {
              'title': 'Access to read',
              'message': 'READ_EXTERNAL_STORAGE'
          }
      )
          .then(granted => {
              if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  console.log("You can use the storage")
              } else {
                  console.log("Storage permission denied")
              }
          })
          .then(() => {
              return PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to write',
                      'message': 'WRITE_EXTERNAL_STORAGE'
                  }
              )
          })
          .then(() => {
              return receiveFile('/storage/emulated/0/Music/', 'BFMV:Letting You Go.mp3')
                  .then(() => console.log('File received successfully'))
                  .catch(err => console.log('Error while file receiving', err))
          })
          .catch(err => console.log(err));
```

### receiveMessage()

If you expect, that someone may send you a message - you can call this method in order to receive it:
```javascript
receiveMessage()
    .then(message => console.log(`Received message: ${message}`))
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
} from 'react-native-wifi-p2p/device-info-statuses';
```

### Events of library

```javascript
import {
    PEERS_UPDATED_ACTION,
    CONNECTION_INFO_UPDATED_ACTION,
    THIS_DEVICE_CHANGED_ACTION
 } from 'react-native-wifi-p2p';
import {subscribeOnEvent} from 'react-native-wifi-p2p';

// example of usage
subscribeOnEvent(PEERS_UPDATED_ACTION, (event) => {
    console.log(event);
});
```

## Caveats

On Android >= 6.0 you should ask permissions before usage of this library (since this grants are required):

> **Note:** you should ask this grants before `startDiscoveringPeers`

> **Note:** on Android 10 you will probably need to ask `ACCESS_FINE_LOCATION` instead of `ACCESS_COARSE_LOCATION`.

```javascript
import { PermissionsAndroid } from 'react-native';
...
PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
                  {
                      'title': 'Access to wi-fi P2P mode',
                      'message': 'ACCESS_COARSE_LOCATION'
                  }
              )
          .then(granted => {
              if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  console.log("You can use the p2p mode")
              } else {
                  console.log("Permission denied: p2p mode will not work")
              }
          })
```

If you are using Android Oreo (8.0) or higher, then you also need to request `FINE_LOCATION` permission, and location [should be enabled](https://stackoverflow.com/questions/50475371/android-wifi-direct-p2p-not-able-to-find-peers-on-android-8/53149375#53149375).

### Example of usage

```javascript
import React, { PureComponent } from 'react';
import {
  StyleSheet,
  View,
  Button
} from 'react-native';
import {
  initialize,
  startDiscoveringPeers,
  stopDiscoveringPeers,
  subscribeOnConnectionInfoUpdates,
  subscribeOnThisDeviceChanged,
  subscribeOnPeersUpdates,
  connect,
  cancelConnect,
  createGroup,
  removeGroup,
  getAvailablePeers,
  sendFile,
  receiveFile,
  getConnectionInfo,
  getGroupInfo,
  receiveMessage,
  sendMessage,
} from 'react-native-wifi-p2p';
import { PermissionsAndroid } from 'react-native';

type Props = {};
export default class App extends PureComponent<Props> {
  peersUpdatesSubscription;
  connectionInfoUpdatesSubscription;
  thisDeviceChangedSubscription;

  state = {
    devices: []
  };

  async componentDidMount() {
      try {
          await initialize();
          // since it's required in Android >= 6.0
          const granted = await PermissionsAndroid.request(
              PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION,
              {
                  'title': 'Access to wi-fi P2P mode',
                  'message': 'ACCESS_COARSE_LOCATION'
              }
          );

          console.log(granted === PermissionsAndroid.RESULTS.GRANTED ? "You can use the p2p mode" : "Permission denied: p2p mode will not work");

          this.peersUpdatesSubscription = subscribeOnPeersUpdates(this.handleNewPeers);
          this.connectionInfoUpdatesSubscription = subscribeOnConnectionInfoUpdates(this.handleNewInfo);
          this.thisDeviceChangedSubscription = subscribeOnThisDeviceChanged(this.handleThisDeviceChanged);

          const status = await startDiscoveringPeers();
          console.log('startDiscoveringPeers status: ', status);
      } catch (e) {
          console.error(e);
      }
  }

  componentWillUnmount() {
    this.peersUpdatesSubscription?.remove();
    this.connectionInfoUpdatesSubscription?.remove();
    this.thisDeviceChangedSubscription?.remove();
  }

  handleNewInfo = (info) => {
    console.log('OnConnectionInfoUpdated', info);
  };

  handleNewPeers = ({ devices }) => {
    console.log('OnPeersUpdated', devices);
    this.setState({ devices: devices });
  };

  handleThisDeviceChanged = (groupInfo) => {
      console.log('THIS_DEVICE_CHANGED_ACTION', groupInfo);
  };

  connectToFirstDevice = () => {
      console.log('Connect to: ', this.state.devices[0]);
      connect(this.state.devices[0].deviceAddress)
          .then(() => console.log('Successfully connected'))
          .catch(err => console.error('Something gone wrong. Details: ', err));
  };

  onCancelConnect = () => {
      cancelConnect()
          .then(() => console.log('cancelConnect', 'Connection successfully canceled'))
          .catch(err => console.error('cancelConnect', 'Something gone wrong. Details: ', err));
  };

  onCreateGroup = () => {
      createGroup()
          .then(() => console.log('Group created successfully!'))
          .catch(err => console.error('Something gone wrong. Details: ', err));
  };

  onRemoveGroup = () => {
      removeGroup()
          .then(() => console.log('Currently you don\'t belong to group!'))
          .catch(err => console.error('Something gone wrong. Details: ', err));
  };

  onStopInvestigation = () => {
      stopDiscoveringPeers()
          .then(() => console.log('Stopping of discovering was successful'))
          .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details`, err));
  };

  onStartInvestigate = () => {
      startDiscoveringPeers()
          .then(status => console.log('startDiscoveringPeers', `Status of discovering peers: ${status}`))
          .catch(err => console.error(`Something is gone wrong. Maybe your WiFi is disabled? Error details: ${err}`));
  };

  onGetAvailableDevices = () => {
      getAvailablePeers()
          .then(peers => console.log(peers));
  };

  onSendFile = () => {
      //const url = '/storage/sdcard0/Music/Rammstein:Amerika.mp3';
      const url = '/storage/emulated/0/Music/Bullet For My Valentine:Letting You Go.mp3';
      PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to read',
                      'message': 'READ_EXTERNAL_STORAGE'
                  }
              )
          .then(granted => {
              if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  console.log("You can use the storage")
              } else {
                  console.log("Storage permission denied")
              }
          })
          .then(() => {
              return PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to write',
                      'message': 'WRITE_EXTERNAL_STORAGE'
                  }
              )
          })
          .then(() => {
              return sendFile(url)
                  .then((metaInfo) => console.log('File sent successfully', metaInfo))
                  .catch(err => console.log('Error while file sending', err));
          })
          .catch(err => console.log(err));
  };

  onReceiveFile = () => {
      PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
          {
              'title': 'Access to read',
              'message': 'READ_EXTERNAL_STORAGE'
          }
      )
          .then(granted => {
              if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  console.log("You can use the storage")
              } else {
                  console.log("Storage permission denied")
              }
          })
          .then(() => {
              return PermissionsAndroid.request(
                  PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
                  {
                      'title': 'Access to write',
                      'message': 'WRITE_EXTERNAL_STORAGE'
                  }
              )
          })
          .then(() => {
              return receiveFile('/storage/emulated/0/Music/', 'BFMV:Letting You Go.mp3')
                  .then(() => console.log('File received successfully'))
                  .catch(err => console.log('Error while file receiving', err))
          })
          .catch(err => console.log(err));
  };

  onSendMessage = () => {
      sendMessage("Hello world!")
        .then((metaInfo) => console.log('Message sent successfully', metaInfo))
        .catch(err => console.log('Error while message sending', err));
  };

  onReceiveMessage = () => {
      receiveMessage()
          .then((msg) => console.log('Message received successfully', msg))
          .catch(err => console.log('Error while message receiving', err))
  };

  onGetConnectionInfo = () => {
    getConnectionInfo()
        .then(info => console.log('getConnectionInfo', info));
  };

  onGetGroupInfo = () => {
      getGroupInfo()
        .then(info => console.log('getGroupInfo', info));
  };

  render() {
    return (
      <View style={styles.container}>
        <Button
          title="Connect"
          onPress={this.connectToFirstDevice}
        />
        <Button
          title="Cancel connect"
          onPress={this.onCancelConnect}
        />
        <Button
          title="Create group"
          onPress={this.onCreateGroup}
        />
        <Button
          title="Remove group"
          onPress={this.onRemoveGroup}
        />
        <Button
          title="Investigate"
          onPress={this.onStartInvestigate}
        />
        <Button
          title="Prevent Investigation"
          onPress={this.onStopInvestigation}
        />
        <Button
          title="Get Available Devices"
          onPress={this.onGetAvailableDevices}
        />
        <Button
          title="Get connection Info"
          onPress={this.onGetConnectionInfo}
        />
        <Button
          title="Get group info"
          onPress={this.onGetGroupInfo}
        />
        <Button
          title="Send file"
          onPress={this.onSendFile}
        />
        <Button
          title="Receive file"
          onPress={this.onReceiveFile}
        />
        <Button
          title="Send message"
          onPress={this.onSendMessage}
        />
        <Button
          title="Receive message"
          onPress={this.onReceiveMessage}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

```
