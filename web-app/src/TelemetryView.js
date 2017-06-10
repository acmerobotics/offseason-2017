import React, { Component } from 'react';

class TelemetryView extends Component {
  render() {
    const tableRows = this.props.telemetry.map(item => (
      <tr>
        <td>{item[0]}</td>
        <td>{item[1]}</td>
      </tr>
    ));
    return (
      <div className="tile" id="telemetry">
        <div className="heading">
          <h2>Telemetry</h2>
          <div className="iconGroup">
            <div className="small chart icon"></div>
          </div>
        </div>
        <table>
          <tbody>{tableRows}</tbody>
        </table>
      </div>
    );
  }
}

export default TelemetryView;
