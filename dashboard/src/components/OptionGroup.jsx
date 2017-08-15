import React from 'react';
import PropTypes from 'prop-types';
import Option from './Option';
import Heading from './Heading';

const OptionGroup = ({ name, options, onChange, onSave }) => (
  <div>
    <Heading level={3} text={name} />
    <table>
      <tbody>
        {options.map((option, optionIndex) => (
          <Option
            key={optionIndex}
            option={option}
            onChange={value => onChange(optionIndex, value)}
            onSave={onSave} />
        ))}
      </tbody>
    </table>
  </div>
);

OptionGroup.propTypes = {
  name: PropTypes.string.isRequired,
  options: PropTypes.arrayOf(Option.propTypes.option),
  onChange: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired
};

export default OptionGroup;
