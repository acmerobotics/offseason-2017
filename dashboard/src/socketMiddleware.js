import {
  CONNECT,
  DISCONNECT,
  SEND_MESSAGE,
  connect,
  receiveConnectionStatus,
  receivePingTime,
  sendMessage,
  MessageType
} from './actions/socket';
import { receiveTelemetry } from './actions/telemetry';
import { receiveConfig } from './actions/config';
import { receiveFieldOverlay } from './actions/fieldOverlay';

let socket, pingSentTime;

export const ping = () => (
  (dispatch, getState) => {
    const { isConnected } = getState().socket;

    if (!isConnected) {
      return;
    }

    pingSentTime = Date.now();

    dispatch(sendMessage({
      type: MessageType.PING
    }));

    setTimeout(() => {
      dispatch(ping());
    }, 1000);
  }
);

const socketMiddleware = store => next => action => {
  switch (action.type) {
  case CONNECT:
    socket = new WebSocket(`ws://${action.host}:${action.port}`);

    socket.onmessage = (evt) => {
      const msg = JSON.parse(evt.data);
      switch (msg.type) {
      case MessageType.PONG: {
        const pingTime = Date.now() - pingSentTime;
        store.dispatch(receivePingTime(pingTime));
        break;
      }
      case MessageType.UPDATE: {
        const { data } = msg;

        if (data.telemetry) {
          store.dispatch(receiveTelemetry(data.telemetry));
        }

        if (data.config) {
          store.dispatch(receiveConfig(data.config));
        }

        if (data.fieldOverlay) {
          store.dispatch(receiveFieldOverlay(data.fieldOverlay));
        }

        break;
      }
      default:
        console.log(`unknown message of type '${msg.type}' received`);
        console.log(msg);
        break;
      }
    };

    socket.onopen = () => {
      store.dispatch(receiveConnectionStatus(true));

      store.dispatch(ping());
    };

    socket.onclose = () => {
      store.dispatch(receiveConnectionStatus(false));

      setTimeout(() => store.dispatch(connect(action.host, action.port)), 3000);
    };

    break;
  case DISCONNECT:
    socket.close();
    break;
  case SEND_MESSAGE: {
    const { isConnected } = store.getState().socket;

    if (isConnected) {
      socket.send(JSON.stringify(action.message));
    }

    break;
  }
  default:
    next(action);

    break;
  }
};

export default socketMiddleware;
