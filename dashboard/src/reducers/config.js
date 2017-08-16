import { RECEIVE_CONFIG } from '../actions/config';

const initialState = [];

const config = (state = initialState, action) => {
  switch (action.type) {
  case RECEIVE_CONFIG:
    return action.config;
  default:
    return state;
  }
};

export default config;
