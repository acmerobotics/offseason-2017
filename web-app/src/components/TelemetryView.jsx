import React, { Component } from 'react';

class TelemetryView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: [],
    };

    this.handleItemSelect = this.handleItemSelect.bind(this);
  }

  handleItemSelect(evt, key) {
    if (evt.target.checked) {
      this.setState({
        selected: [
          ...this.state.selected,
          key,
        ],
      });
    } else {
      this.setState({
        selected: this.state.selected.filter(v => v !== key),
      });
    }
  }

  render() {
    const tableRows = this.props.telemetry.map(item => (
      <tr key={item[0]}>
        <td>{item[0]}</td>
        <td>{item[1]}</td>
        <td>
          <input
            type="checkbox"
            value={this.state.selected.indexOf(item[0]) !== -1}
            onChange={evt => this.handleItemSelect(evt, item[0])} />
        </td>
      </tr>
    ));
    const style = {
      gridRow: 1,
      gridColumn: 3,
    };
    return (
      <div className="tile" style={style}>
        <div className="heading">
          <h2>Telemetry</h2>
          <div className="iconGroup">
            <div
              className="small chart icon"
              onClick={() => this.props.onGraph(this.state.selected)} />
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
