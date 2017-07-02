import React, { Component } from 'react';
import OptionGroup from './OptionGroup';
import Heading from './Heading';
import IconGroup from './IconGroup';
import Icon from './Icon';

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
    return (
      <div>
        <Heading level={2} text="Configuration">
          <IconGroup>
            <Icon icon="refresh" size="small" />
            {
              (this.props.config.every(v => !v.invalid || v.invalid.length === 0)) ?
                <Icon icon="save" size="small" onClick={this.props.onSave} /> : undefined
            }
          </IconGroup>
        </Heading>
        {this.props.config.map((optionGroup, optionGroupIndex) => (
          <OptionGroup
            key={optionGroupIndex}
            name={optionGroup.name}
            options={optionGroup.options}
            onChange={
              (optionIndex, value) => this.props.onChange(optionGroupIndex, optionIndex, value)
            }
            onSave={this.props.onSave} />
        ))}
      </div>
    );
  }
}

export default ConfigView;
