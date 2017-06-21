import React, { Component } from 'react';

const shorten = (str, len) => {
  str = str.toString();
  if (str.length > len) {
    return `${str.slice(0, len - 3)}...`;
  }
  return str;
};

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
      <tr key={item.name}>
        <td>{item.name}</td>
        <td>{shorten(item.value, 15)}</td>
        {
          item.name === 'time'
            ? <td />
            : (
              <td>
                <input
                  type="checkbox"
                  value={this.state.selected.indexOf(item.name) !== -1}
                  onChange={evt => this.handleItemSelect(evt, item.name)} />
              </td>
            )
        }
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
