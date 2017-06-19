import React from 'react';

const ConfigOption = ({ option, onChange }) => {
  const changeHandler = (e) => {
    if (option.type === 'boolean') {
      onChange(e.target.checked);
    } else if (option.type === 'enum') {
      onChange(e.target.selectedIndex);
    } else {
      onChange(e.target.value);
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
