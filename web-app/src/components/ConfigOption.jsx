import React from 'react';

const ConfigOption = ({ option, onChange }) => {
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
  let input;
  switch (option.type) {
  case 'string':
  case 'int':
  case 'double':
    input = (
      <input
        type="text"
        value={option.value}
        onChange={changeHandler} />
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
          onChange={keyChangeHandler('p')} />&nbsp;&nbsp;
        i: <input
          type="text"
          size="8"
          value={option.value.i}
          onChange={keyChangeHandler('i')} />&nbsp;&nbsp;
        d: <input
          type="text"
          size="8"
          value={option.value.d}
          onChange={keyChangeHandler('d')} />&nbsp;&nbsp;
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

export default ConfigOption;
