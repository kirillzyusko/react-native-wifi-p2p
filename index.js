import { NativeModules } from 'react-native';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

const initialize = () => WiFiP2PManager.init();

const getAvailablePeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.getAvailablePeersList(peersList => {
        const peers = JSON.parse(peersList);
        resolve(peers);
    })
});

const connect = (deviceAddress) => new Promise((resolve, reject) => {
    WiFiP2PManager.connect(deviceAddress, data => {
        resolve(data);
    })
});

const isWiFiEnabled = () => true;

const setWiFiState = (isEnabled) => {};

const addEventListener = (event, callback) => {

};

export {
    getAvailablePeers,
    initialize,
    isWiFiEnabled,
    setWiFiState,
    connect
};
