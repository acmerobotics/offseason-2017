import { RECEIVE_CONFIG, UPDATE_OPTION_VALUE } from '../actions/config';
import validateOptionValue from '../util/validator';

const initialState = [];

const config = (state = initialState, action) => {
  switch (action.type) {
  case RECEIVE_CONFIG:
    return action.config;
  case UPDATE_OPTION_VALUE: {
    const { option, newValue } = action;

    const validatedValue = validateOptionValue(option, newValue).value;

    return state.map(optionGroup => {
      if (optionGroup.name === action.optionGroup) {
        return {
          ...optionGroup,
          options: optionGroup.options.map(o => {
            if (o.name === option.name) {
              return {
                ...o,
                value: validatedValue
              };
            } else {
              return o;
            }
          })
        };
      } else {
        return optionGroup;
      }
    });
  }
  default:
    return state;
  }
};

export default config;
