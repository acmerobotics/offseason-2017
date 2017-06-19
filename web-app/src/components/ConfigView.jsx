import React, { Component } from 'react';
import ConfigOptionGroup from './ConfigOptionGroup';

class ConfigView extends Component {
  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleRefresh = this.handleRefresh.bind(this);
  }

  handleSave() {
    this.props.onSave();
  }

  handleRefresh() {
    this.props.onRefresh();
  }

  render() {
    const style = {
      gridRow: 1,
      gridColumn: 2,
    };
    return (
      <div className="tile" style={style}>
        <div className="heading">
          <h2>Configuration</h2>
          <div className="iconGroup">
            <div className="small refresh icon" onClick={this.handleRefresh} />
            <div className="small save icon" onClick={this.handleSave} />
          </div>
        </div>
        {this.props.config.map((optionGroup, optionGroupIndex) => (
          <ConfigOptionGroup
            key={optionGroupIndex}
            name={optionGroup.name}
            options={optionGroup.options}
            onChange={
              (optionIndex, value) => this.props.onChange(optionGroupIndex, optionIndex, value)
            } />
        ))}
      </div>
    );
  }
}

export default ConfigView;
