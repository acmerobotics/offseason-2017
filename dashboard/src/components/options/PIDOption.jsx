import React from 'react';
import PropTypes from 'prop-types';

const PIDOption = ({ value, onChange }) => (
  <span>
    p: <input
      type="text"
      size="8"
      value={value.p}
      onChange={evt => onChange({
        ...value,
        p: evt.target.value
      })} />&nbsp;&nbsp;
    i: <input
      type="text"
      size="8"
      value={value.i}
      onChange={evt => onChange({
        ...value,
        i: evt.target.value
      })} />&nbsp;&nbsp;
    d: <input
      type="text"
      size="8"
      value={value.d}
      onChange={evt => onChange({
        ...value,
        d: evt.target.value
      })} />&nbsp;&nbsp;
  </span>
);

PIDOption.propTypes = {
  value: PropTypes.shape({
    p: PropTypes.oneOfType([
      PropTypes.number,
      PropTypes.string
    ]).isRequired,
    i: PropTypes.oneOfType([
      PropTypes.number,
      PropTypes.string
    ]).isRequired,
    d: PropTypes.oneOfType([
      PropTypes.number,
      PropTypes.string
    ]).isRequired
  }).isRequired,
  onChange: PropTypes.func.isRequired
};

export default PIDOption;
