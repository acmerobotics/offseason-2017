import React from 'react';
import PropTypes from 'prop-types';

const CheckboxOption = ({ value, onChange }) => (
  <input type="checkbox" value={value} onChange={evt => onChange(evt.target.checked)} />
);

CheckboxOption.propTypes = {
  value: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired
};

export default CheckboxOption;
