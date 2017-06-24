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
  if (newValue === '' || /^-?\d*\.([1-9]*0+)*$/.test(newValue)) {
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
  case 'int':
    return validateInt(option.value, newValue);
  case 'double':
    return validateDouble(option.value, newValue);
  case 'pid': {
    const key = Object.keys(newValue)[0];
    const { valid, value } = validateDouble(option.value[key], newValue[key]);
    if (!option.invalid) {
      option.invalid = [];
    }
    if (valid && option.invalid.indexOf(key) !== -1) {
      option.invalid = option.invalid.filter(el => el !== key);
    } else if (!valid && option.invalid.indexOf(key) === -1) {
      option.invalid.push(key);
    }
    return {
      valid: option.invalid.length === 0,
      value: {
        ...option.value,
        [key]: value,
      },
    };
  }
  default:
    return {
      valid: true,
      value: newValue,
    };
  }
}
