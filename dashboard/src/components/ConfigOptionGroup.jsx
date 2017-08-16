import React from 'react';
import PropTypes from 'prop-types';
import ConfigOption from './ConfigOption';
import Heading from './Heading';

const ConfigOptionGroup = ({ name, options }) => (
  <div>
    <Heading level={3} text={name} />
    <table>
      <tbody>
        {options.map((option, optionIndex) => (
          <ConfigOption
            key={optionIndex}
            optionGroup={name}
            option={option} />
        ))}
      </tbody>
    </table>
  </div>
);

ConfigOptionGroup.propTypes = {
  name: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired
};

export default ConfigOptionGroup;
