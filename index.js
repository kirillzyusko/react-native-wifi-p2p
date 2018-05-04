import { NativeModules } from 'react-native';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

const initialize = () => new Promise.resolve();

const getAvailablePeers = () => new Promise((resolve, reject) => {
    WiFiP2PManager.getAvailablePeersList(peersList => {
        const peers = JSON.parse(peersList);
        resolve(peers);
    })
});

const connect = (deviceAddress) => new Promise((resolve, reject) => {
    WiFiP2PManager.connect(data => {
        resolve(data);
    })
});

export {
    getAvailablePeers,
    initialize,
    connect
};
