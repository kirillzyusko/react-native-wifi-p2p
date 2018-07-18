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

const addEventListener = (event, callback) => {
    DeviceEventEmitter.addListener(`${MODULE_NAME}:${event}`, callback);
};

const removeEventListener = (event, callback) => {
    DeviceEventEmitter.removeListener(`${MODULE_NAME}:${event}`, callback);
};

const subscribeOnPeersUpdates = (callback) => addEventListener(PEERS_UPDATED_ACTION, callback);

const unsubscribeOnPeersUpdates = (callback) => removeEventListener(PEERS_UPDATED_ACTION, callback);

const subscribeOnConnectionInfoUpdates = (callback) => addEventListener(CONNECTION_INFO_UPDATED_ACTION, callback);

const unsubscribeOnConnectionInfoUpdates = (callback) => removeEventListener(CONNECTION_INFO_UPDATED_ACTION, callback);

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

const getAvailablePeers = () => new Promise(resolve => {
    WiFiP2PManager.getAvailablePeersList(peersList => {
        resolve(peersList);
    })
});

const isSuccessfulInitialize = () => new Promise(resolve => {
    WiFiP2PManager.isSuccessfulInitialize(status => {
        resolve(status);
    });
});

const stopDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.stopPeerDiscovery(reasonCode => {
        reasonCode === undefined ? resolve() : reject(getError(reasonCode));
    })
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
    unsubscribeOnPeersUpdates,
    subscribeOnConnectionInfoUpdates,
    unsubscribeOnConnectionInfoUpdates,
    getAvailablePeers,
    connect,
    disconnect,
    createGroup,
    removeGroup,

    // system methods
    addEventListener,
    removeEventListener,

    // const
    PEERS_UPDATED_ACTION,
    CONNECTION_INFO_UPDATED_ACTION,

    // future realization
    isWiFiEnabled,
    setWiFiState,
};
