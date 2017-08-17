import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import CheckboxOption from '../components/options/CheckboxOption';
import PIDOption from '../components/options/PIDOption';
import SelectOption from '../components/options/SelectOption';
import TextOption from '../components/options/TextOption';
import { updateOptionValue } from '../actions/config';

export const ConfigOptionType = {
  BOOLEAN: 'boolean',
  INT: 'int',
  DOUBLE: 'double',
  STRING: 'string',
  ENUM: 'enum',
  PID: 'pid'
};

class ConfigOption extends React.Component {
  renderOption() {
    const { option } = this.props;

    switch (option.type) {
    case ConfigOptionType.INT:
    case ConfigOptionType.DOUBLE:
    case ConfigOptionType.STRING:
      return <TextOption value={option.value} onChange={this.props.onChange} />;
    case ConfigOptionType.BOOLEAN:
      return <CheckboxOption value={option.value} onChange={this.props.onChange} />;
    case ConfigOptionType.ENUM:
      return <SelectOption value={option.value} values={option.values} onChange={this.props.onChange} />;
    case ConfigOptionType.PID:
      return <PIDOption value={option.value} onChange={this.props.onChange} />;
    default:
      return <p>Unknown option type: {option.type}</p>;
    }
  }

  render() {
    return (
      <tr>
        <td>{this.props.option.name}</td>
        <td>{this.renderOption()}</td>
      </tr>
    );
  }
}

ConfigOption.propTypes = {
  option: PropTypes.shape({
    type: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    value: PropTypes.any.isRequired,
    values: PropTypes.arrayOf(PropTypes.string)
  }).isRequired,
  optionGroup: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired
};

const mapDispatchToProps = (dispatch, ownProps) => ({
  onChange: (newValue) => {
    dispatch(updateOptionValue(ownProps.optionGroup, ownProps.option, newValue));
  }
});

export default connect(null, mapDispatchToProps)(ConfigOption);
