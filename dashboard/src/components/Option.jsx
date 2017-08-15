import React from 'react';
import PropTypes from 'prop-types';

const Option = ({ option, onChange, onSave }) => {
  const getValue = (e) => {
    switch (option.type) {
    case 'boolean':
      return e.target.checked;
    case 'enum':
      return e.target.selectedIndex;
    default:
      return e.target.value;
    }
  };
  const changeHandler = e => onChange(getValue(e));
  const keyChangeHandler = key => (
    e => onChange({
      [key]: getValue(e),
    })
  );
  const keyPressHandler = (evt) => {
    if (evt.key === 'Enter') {
      onSave();
      evt.preventDefault();
      evt.stopPropagation();
    }
  };
  let input;
  switch (option.type) {
  case 'string':
  case 'int':
  case 'double':
    input = (
      <input
        type="text"
        value={option.value}
        onChange={changeHandler}
        onKeyPress={keyPressHandler} />
    );
    break;
  case 'boolean':
    input = <input type="checkbox" value={option.value} onChange={changeHandler} />;
    break;
  case 'enum':
    input = (
      <select
        value={option.values[option.value]}
        onChange={changeHandler}>
        {option.values.map(v => (<option key={v} value={v}>{v}</option>))}
      </select>
    );
    break;
  case 'pid':
    input = (
      <span>
        p: <input
          type="text"
          size="8"
          value={option.value.p}
          onChange={keyChangeHandler('p')}
          onKeyPress={keyPressHandler} />&nbsp;&nbsp;
        i: <input
          type="text"
          size="8"
          value={option.value.i}
          onChange={keyChangeHandler('i')}
          onKeyPress={keyPressHandler} />&nbsp;&nbsp;
        d: <input
          type="text"
          size="8"
          value={option.value.d}
          onChange={keyChangeHandler('d')}
          onKeyPress={keyPressHandler} />&nbsp;&nbsp;
      </span>
    );
    break;
  default:
    input = <p>Unknown input</p>;
    break;
  }
  return (
    <tr>
      <td>{option.name}</td>
      <td>{input}</td>
    </tr>
  );
};

Option.propTypes = {
  option: PropTypes.shape({
    name: PropTypes.string.isRequired,
    value: PropTypes.any.isRequired
  }).isRequired,
  onChange: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired
};

export default Option;
