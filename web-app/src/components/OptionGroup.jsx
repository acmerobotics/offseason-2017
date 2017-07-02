import React from 'react';
import Option from './Option';
import Heading from './Heading';

const OptionGroup = ({ name, options, onChange }) => (
  <div>
    <Heading level={3} text={name} />
    <table>
      <tbody>
        {options.map((option, optionIndex) => (
          <Option
            key={optionIndex}
            option={option}
            onChange={value => onChange(optionIndex, value)} />
        ))}
      </tbody>
    </table>
  </div>
);

export default OptionGroup;
