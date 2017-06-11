import React, { Component } from 'react';

class ConfigView extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleSave = this.handleSave.bind(this);
    this.handleRefresh = this.handleRefresh.bind(this);
  }

  handleChange(e, optionIndex) {
    this.props.onChange({
      value: (e.target.type === "checkbox") ? e.target.checked : e.target.value
    }, optionIndex);
  }

  handleSave() {
    this.props.onSave();
  }

  handleRefresh() {
    this.props.onRefresh();
  }

  render() {
    const optionList = this.props.config.map((option, optionIndex) => {
      let input;
      if (option.type === "text" || option.type === "int" || option.type === "double") {
        input = <input type="text" value={(option.value === option.value) ? option.value : ""} onChange={evt => this.handleChange(evt, optionIndex)} />;
      } else if (option.type === "boolean") {
        input = <input type="checkbox" value={option.value} onChange={evt => this.handleChange(evt, optionIndex)} />;
      } else if (option.type === "enum") {
        input = (
          <select value={option.values[option.value]} onChange={evt => this.handleChange(evt, optionIndex)}>
            {
              option.values.map((v) => (<option value={v}>{v}</option>))
            }
          </select>
        )
      } else {
        input = <span>Unknown input type '{option.type}'</span>;
      }
      return (
        <tr>
          <td>{option.key}</td>
          <td>{input}</td>
        </tr>
      );
    });
    return (
      <div className="tile" id="config">
        <div className="heading">
          <h2>Configuration</h2>
          <div className="iconGroup">
            <div className="small refresh icon" onClick={this.handleRefresh}></div>
            <div className="small save icon" onClick={this.handleSave}></div>
          </div>
        </div>
        <table>
          <tbody>{optionList}</tbody>
        </table>
      </div>
    );
  }
}

export default ConfigView;
