// STATUSES
const ERROR = 0;
const ERROR_MESSAGE = 'Operation failed due to an internal error.';
const P2P_UNSUPPORTED = 1;
const P2P_UNSUPPORTED_MESSAGE = 'Operation failed because p2p is unsupported on the device.';
const BUSY = 2;
const BUSY_ERROR = 'Operation failed because the framework is busy and unable to service the request.';

const getError = (reasonCode) => {
    switch (reasonCode) {
        case ERROR: return {
            code: ERROR,
            message: ERROR_MESSAGE
        };
        case P2P_UNSUPPORTED: return {
            code: P2P_UNSUPPORTED,
            message: P2P_UNSUPPORTED_MESSAGE
        };
        case BUSY: return {
            code: BUSY,
            message: BUSY_ERROR
        };
        default: return {
            code: reasonCode,
            message: 'Unknown error.'
        }
    }
};

export { getError }
