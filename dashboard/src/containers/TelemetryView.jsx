import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import Heading from '../components/Heading';

const truncate = (str, len) => {
  str = str.toString();
  if (str.length > len) {
    return `${str.slice(0, len - 3)}...`;
  }
  return str;
};

const TelemetryView = ({ telemetry }) => {
  const tableRows = telemetry.entries.map(item => (
    <tr key={item.name}>
      <td>{item.name}</td>
      <td>{truncate(item.value, 15)}</td>
    </tr>
  ));
  return (
    <div>
      <Heading level={2} text="Telemetry" />
      <table>
        <tbody>{tableRows}</tbody>
      </table>
    </div>
  );
};

TelemetryView.propTypes = {
  telemetry: PropTypes.shape({
    entries: PropTypes.arrayOf(PropTypes.shape({
      name: PropTypes.string.isRequired,
      value: PropTypes.any.isRequired
    })).isRequired
  }).isRequired
};

const mapStateToProps = ({ telemetry }) => ({
  telemetry
});

export default connect(mapStateToProps)(TelemetryView);
