import { sendMessage, MessageType } from './socket';

export const RECEIVE_CONFIG = 'RECEIVE_CONFIG';
export const UPDATE_OPTION_VALUE = 'UPDATE_OPTION_VALUE';

export const receiveConfig = (config) => ({
  type: RECEIVE_CONFIG,
  config
});

export const updateOptionValue = (optionGroup, option, newValue) => ({
  type: UPDATE_OPTION_VALUE,
  optionGroup,
  option,
  newValue
});

export const getConfig = () => (
  (dispatch) => {
    dispatch(sendMessage({
      type: MessageType.GET,
      data: 'config'
    }));
  }
);

export const syncConfig = () => (
  (dispatch, getState) => {
    dispatch(sendMessage({
      type: MessageType.UPDATE,
      data: {
        config: getState().config
      }
    }));
  }
);
