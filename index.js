import { DeviceEventEmitter, NativeModules } from 'react-native';
import { getError } from './reason-code';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

// ACTIONS
const PEERS_UPDATED_ACTION = 'PEERS_UPDATED';
const CONNECTION_INFO_UPDATED_ACTION = 'CONNECTION_INFO_UPDATED';
const THIS_DEVICE_CHANGED_ACTION = 'THIS_DEVICE_CHANGED_ACTION';

// CONSTS
const MODULE_NAME = 'WIFI_P2P';

const initialize = () => WiFiP2PManager.init();

const startDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.discoverPeers((reasonCode) => {
        reasonCode === undefined ? resolve('success') : reject(getError(reasonCode));
    })
});

const subscribeOnEvent = (event, callback) => {
    return DeviceEventEmitter.addListener(`${MODULE_NAME}:${event}`, callback);
};

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
const unsubscribeFromEvent = (event, callback) => {
    DeviceEventEmitter.removeListener(`${MODULE_NAME}:${event}`, callback);
};

const subscribeOnThisDeviceChanged = (callback) => subscribeOnEvent(THIS_DEVICE_CHANGED_ACTION, callback);

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
const unsubscribeFromThisDeviceChanged = (callback) => unsubscribeFromEvent(THIS_DEVICE_CHANGED_ACTION, callback);

const subscribeOnPeersUpdates = (callback) => subscribeOnEvent(PEERS_UPDATED_ACTION, callback);

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
const unsubscribeFromPeersUpdates = (callback) => unsubscribeFromEvent(PEERS_UPDATED_ACTION, callback);

const subscribeOnConnectionInfoUpdates = (callback) => subscribeOnEvent(CONNECTION_INFO_UPDATED_ACTION, callback);

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
const unsubscribeFromConnectionInfoUpdates = (callback) => unsubscribeFromEvent(CONNECTION_INFO_UPDATED_ACTION, callback);

const connect = (deviceAddress) => connectWithConfig({ deviceAddress });

const connectWithConfig = (args) => new Promise((resolve, reject) => {
    WiFiP2PManager.connectWithConfig(args, status => {
        status === undefined ? resolve() : reject(getError(status));
    })
});

const cancelConnect = () => new Promise((resolve, reject) => {
    WiFiP2PManager.cancelConnect(status => {
        status === undefined ? resolve() : reject(getError(status));
    })
});

const createGroup = () => new Promise((resolve, reject) => {
    WiFiP2PManager.createGroup(reasonCode => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
});

const removeGroup = () => new Promise((resolve, reject) => {
    WiFiP2PManager.removeGroup(reasonCode => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
});

const getAvailablePeers = () => WiFiP2PManager.getAvailablePeersList();

const stopDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.stopPeerDiscovery(reasonCode => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
});

const sendFile = (pathToFile) => WiFiP2PManager.sendFile(pathToFile);

const sendFileTo = (pathToFile, address) => WiFiP2PManager.sendFileTo(pathToFile, address);

const receiveFile = (folder, fileName, forceToScanGallery = false) => new Promise((resolve, reject) => {
    WiFiP2PManager.receiveFile(folder, fileName, forceToScanGallery, (pathToFile) => {
        resolve(pathToFile);
    });
});

const sendMessage = (message) => WiFiP2PManager.sendMessage(message);

const sendMessageTo = (message, address) => WiFiP2PManager.sendMessageTo(message, address);

const receiveMessage = (props) => new Promise((resolve, reject) => {
    WiFiP2PManager.receiveMessage(props, (message) => {
        resolve(message);
    });
});

const stopReceivingMessage = () => WiFiP2PManager.stopReceivingMessage()

const getConnectionInfo = () => WiFiP2PManager.getConnectionInfo();

const getGroupInfo = () => WiFiP2PManager.getGroupInfo();

export {
    // public methods
    initialize,
    startDiscoveringPeers,
    stopDiscoveringPeers,
    subscribeOnThisDeviceChanged,
    unsubscribeFromThisDeviceChanged,
    subscribeOnPeersUpdates,
    unsubscribeFromPeersUpdates,
    subscribeOnConnectionInfoUpdates,
    unsubscribeFromConnectionInfoUpdates,
    getAvailablePeers,
    connect,
    connectWithConfig,
    cancelConnect,
    createGroup,
    removeGroup,
    getConnectionInfo,
    getGroupInfo,
    sendFile,
    sendFileTo,
    receiveFile,
    sendMessage,
    sendMessageTo,
    receiveMessage,
    stopReceivingMessage,

    // system methods
    subscribeOnEvent,
    unsubscribeFromEvent,

    // const
    PEERS_UPDATED_ACTION,
    CONNECTION_INFO_UPDATED_ACTION,
    THIS_DEVICE_CHANGED_ACTION,
};
