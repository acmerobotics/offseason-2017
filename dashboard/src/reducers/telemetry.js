import { RECEIVE_TELEMETRY } from '../actions/telemetry';

const initialState = {
  entries: []
};

const telemetry = (state = initialState, action) => {
  switch (action.type) {
  case RECEIVE_TELEMETRY:
    return action.telemetry;
  default:
    return state;
  }
};

export default telemetry;
