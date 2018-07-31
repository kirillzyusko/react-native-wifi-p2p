import { DeviceEventEmitter, NativeModules } from 'react-native';
import { getError } from './reason-code';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

// ACTIONS
const PEERS_UPDATED_ACTION = 'PEERS_UPDATED';
const CONNECTION_INFO_UPDATED_ACTION = 'CONNECTION_INFO_UPDATED';

// CONSTS
const MODULE_NAME = 'WIFI_P2P';

const initialize = () => WiFiP2PManager.init();

const startDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.discoverPeers((reasonCode) => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
});

const subscribeOnEvent = (event, callback) => {
    DeviceEventEmitter.addListener(`${MODULE_NAME}:${event}`, callback);
};

const unsubscribeFromEvent = (event, callback) => {
    DeviceEventEmitter.removeListener(`${MODULE_NAME}:${event}`, callback);
};

const subscribeOnPeersUpdates = (callback) => subscribeOnEvent(PEERS_UPDATED_ACTION, callback);

const unsubscribeFromPeersUpdates = (callback) => unsubscribeFromEvent(PEERS_UPDATED_ACTION, callback);

const subscribeOnConnectionInfoUpdates = (callback) => subscribeOnEvent(CONNECTION_INFO_UPDATED_ACTION, callback);

const unsubscribeFromConnectionInfoUpdates = (callback) => unsubscribeFromEvent(CONNECTION_INFO_UPDATED_ACTION, callback);

const connect = (deviceAddress) => new Promise((resolve, reject) => {
    WiFiP2PManager.connect(deviceAddress, status => {
        status === undefined ? resolve() : reject(getError(status));
    })
});

const disconnect = () => new Promise((resolve, reject) => {
    WiFiP2PManager.disconnect(status => {
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

const isSuccessfulInitialize = () => WiFiP2PManager.isSuccessfulInitialize();

const stopDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.stopPeerDiscovery(reasonCode => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
});

const sendFile = (pathToFile) => new Promise((resolve, reject) => {
    WiFiP2PManager.sendFile(pathToFile, (reasonCode) => {
        console.log(reasonCode);
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    });
});

const receiveFile = () => new Promise((resolve, reject) => {
    WiFiP2PManager.receiveFile((reasonCode) => {
        console.log(reasonCode);
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    });
});

//////////////////////////////////////////////////////////////////

const isWiFiEnabled = () => true;

const setWiFiState = (isEnabled) => {};

export {
    // public methods
    initialize,
    isSuccessfulInitialize,
    startDiscoveringPeers,
    stopDiscoveringPeers,
    subscribeOnPeersUpdates,
    unsubscribeFromPeersUpdates,
    subscribeOnConnectionInfoUpdates,
    unsubscribeFromConnectionInfoUpdates,
    getAvailablePeers,
    connect,
    disconnect,
    createGroup,
    removeGroup,

    // experimental
    sendFile,
    receiveFile,

    // system methods
    subscribeOnEvent,
    unsubscribeFromEvent,

    // const
    PEERS_UPDATED_ACTION,
    CONNECTION_INFO_UPDATED_ACTION,

    // future realization
    // isWiFiEnabled,
    // setWiFiState,
    // sendFile,
    // receiveFile
};
