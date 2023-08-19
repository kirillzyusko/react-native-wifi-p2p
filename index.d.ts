import type { EmitterSubscription } from 'react-native'

export interface Device {
    deviceAddress: string
    deviceName: string
    isGroupOwner: boolean
    primaryDeviceType: string | null
    secondaryDeviceType: string | null
    status: number
}
export interface ConnectionArgs {
    deviceAddress: string
    groupOwnerIntent?: number
}
export interface GroupInfo {
    interface: string
    networkName: string
    passphrase: string
    owner: {
        deviceAddress: string
        deviceName: string
        primaryDeviceType: string | null
        secondaryDeviceType: string | null
        status: number
    }
}

export interface WifiP2pInfo {
    groupOwnerAddress: {
        hostAddress: string
        isLoopbackAddress: boolean
    } | null
    groupFormed: boolean
    isGroupOwner: boolean
}
export const PEERS_UPDATED_ACTION: string
export const CONNECTION_INFO_UPDATED_ACTION: string
export const THIS_DEVICE_CHANGED_ACTION: string
// public methods
export const initialize: () => Promise<boolean>
export const startDiscoveringPeers: () => Promise<string>
export const stopDiscoveringPeers: () => Promise<void>
export const subscribeOnThisDeviceChanged: (callback: (data: GroupInfo) => void) => EmitterSubscription

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
export const unsubscribeFromThisDeviceChanged: (callback: (data: GroupInfo) => void) => void

export const subscribeOnPeersUpdates: (callback: (data: {devices: Device[]}) => void) => EmitterSubscription

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
export const unsubscribeFromPeersUpdates: (callback: (data: {devices: Device[]}) => void) => void

export const subscribeOnConnectionInfoUpdates: (callback: (data: WifiP2pInfo) => void) => EmitterSubscription

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
export const unsubscribeFromConnectionInfoUpdates: (callback: (data: WifiP2pInfo) => void) => void
export const getAvailablePeers: () => Promise<{devices: Device[]}>
export const connect: (deviceAddress: string) => Promise<void>
export const connectWithConfig: (config: ConnectionArgs) => Promise<void>
export const cancelConnect: () => Promise<void>
export const createGroup: () => Promise<void>
export const removeGroup: () => Promise<void>
export const getConnectionInfo: () => Promise<WifiP2pInfo>
export const getGroupInfo: () => Promise<GroupInfo>
export const sendFile: (pathToFile: string) => Promise<{ time: number, file: string }>
export const sendFileTo: (pathToFile: string, address: string) => Promise<{ time: number, file: string }>
export const receiveFile: (folder: string, fileName: string, forceToScanGallery?: boolean) => Promise<string>
export const sendMessage: (message: string) => Promise<{ time: number, message: string }>
export const sendMessageTo: (message: string, address: string) => Promise<{ time: number, message: string }>
export const receiveMessage: (props: { meta: boolean }) => Promise<string>
export const stopReceivingMessage: () => void

// system methods
export const subscribeOnEvent: (event: string, callback: Function) => EmitterSubscription

/**
 * @deprecated since RN 0.65 because of favour to new API.
 * @see https://github.com/kirillzyusko/react-native-wifi-p2p/releases/tag/3.3.0 for migration process.
 */
export const unsubscribeFromEvent: (event: string, callback: Function) => void