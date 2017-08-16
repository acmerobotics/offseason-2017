import { sendMessage, MessageType } from './socket';

export const RECEIVE_CONFIG = 'RECEIVE_CONFIG';

export const receiveConfig = (config) => ({
  type: RECEIVE_CONFIG,
  config
});

export const getConfig = () => (
  (dispatch) => {
    dispatch(sendMessage({
      type: MessageType.GET,
      data: 'config'
    }));
  }
);
