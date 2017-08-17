import { ConfigOptionType } from '../containers/ConfigOption';

function validateInt(oldValue, newValue) {
  if (/^-?$/.test(newValue)) {
    return {
      valid: false,
      value: newValue,
    };
  } else if (/^-?\d+$/.test(newValue)) {
    return {
      valid: true,
      value: parseInt(newValue, 10),
    };
  }
  return {
    valid: typeof oldValue !== 'string',
    value: oldValue,
  };
}

function validateDouble(oldValue, newValue) {
  if (newValue === '' || /^-0?$/.test(newValue) || /^-?\d*\.([1-9]*0+)*$/.test(newValue)) {
    return {
      valid: false,
      value: newValue,
    };
  } else if (/^-?\d*(\.\d+)?$/.test(newValue)) {
    return {
      valid: true,
      value: parseFloat(newValue),
    };
  }
  return {
    valid: typeof oldValue !== 'string',
    value: oldValue,
  };
}

export default function validateOptionInput(option, newValue) {
  switch (option.type) {
  case ConfigOptionType.INT:
    return validateInt(option.value, newValue);
  case ConfigOptionType.DOUBLE:
    return validateDouble(option.value, newValue);
  case ConfigOptionType.PID: {
    const p = validateDouble(option.value.p, newValue.p);
    const i = validateDouble(option.value.i, newValue.i);
    const d = validateDouble(option.value.d, newValue.d);

    return {
      valid: p.valid && i.valid && d.valid,
      value: {
        p: p.value,
        i: i.value,
        d: d.value
      }
    };
  }
  default:
    return {
      valid: true,
      value: newValue,
    };
  }
}
