import { DeviceEventEmitter, NativeModules } from 'react-native';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

// ACTIONS
const PEERS_UPDATED_ACTION = 'PEERS_UPDATED';
const CONNECTION_INFO_UPDATED_ACTION = 'CONNECTION_INFO_UPDATED';

// CONSTS
const MODULE_NAME = 'WIFI_P2P';

const initialize = () => WiFiP2PManager.init();

const startDiscoveringPeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.discoverPeers((isSuccess) => {
        resolve(isSuccess);
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

const connect = (deviceAddress) => new Promise((resolve, reject) => {
    WiFiP2PManager.connect(deviceAddress, data => {
        resolve(data);
    })
});

const disconnect = () => new Promise((resolve, reject) => {
    WiFiP2PManager.disconnect(status => {
        resolve(status);
    })
});

const createGroup = () => new Promise((resolve, reject) => {
    WiFiP2PManager.createGroup(reasonCode => {
        resolve(reasonCode);
    })
});

const removeGroup = () => new Promise((resolve, reject) => {
    WiFiP2PManager.removeGroup(reasonCode => {
        resolve(reasonCode);
    })
});

const getAvailablePeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.getAvailablePeersList(peersList => {
        resolve(peersList);
    })
});

//////////////////////////////////////////////////////////////////

const isWiFiEnabled = () => true;

const setWiFiState = (isEnabled) => {};

export {
    // public methods
    initialize,
    startDiscoveringPeers,
    subscribeOnPeersUpdates,
    unsubscribeOnPeersUpdates,
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
