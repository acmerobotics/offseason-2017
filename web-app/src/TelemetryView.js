import React, { Component } from 'react';

class TelemetryView extends Component {
  render() {
    const tableRows = this.props.telemetry.map(item => (
      <tr>
        <td>{item[0]}</td>
        <td>{item[1]}</td>
      </tr>
    ));
    const style = {
      gridRow: 1,
      gridColumn: 3
    };
    return (
      <div className="tile" style={style}>
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
