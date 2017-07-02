import React from 'react';
import Heading from './Heading';

const truncate = (str, len) => {
  str = str.toString();
  if (str.length > len) {
    return `${str.slice(0, len - 3)}...`;
  }
  return str;
};

const TelemetryView = ({ telemetry }) => {
  const tableRows = telemetry.map(item => (
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

export default TelemetryView;
