import React from 'react';
import ConfigOption from './ConfigOption';

const ConfigOptionGroup = ({ name, options, onChange }) => (
  <div>
    <h3>{name}</h3>
    <table>
      <tbody>
        {options.map((option, optionIndex) => (
          <ConfigOption
            key={optionIndex}
            option={option}
            onChange={value => onChange(optionIndex, value)} />
        ))}
      </tbody>
    </table>
  </div>
);

export default ConfigOptionGroup;
