import React from 'react';
import PropTypes from 'prop-types';

const TextOption = ({ value, onChange }) => (
  <input
    type="text"
    value={value}
    onChange={evt => onChange(evt.target.value)} />
);

TextOption.propTypes = {
  value: PropTypes.oneOfType([
    PropTypes.number,
    PropTypes.string
  ]).isRequired,
  onChange: PropTypes.func.isRequired
};

export default TextOption;
