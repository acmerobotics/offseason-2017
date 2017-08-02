import React from 'react';

class SelectView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: this.props.selected || [],
    };
  }

  handleChange(evt, val) {
    if (evt.target.checked) {
      this.setState({
        selected: [...this.state.selected, val],
      }, () => this.props.onChange(this.state.selected));
    } else {
      this.setState({
        selected: this.state.selected.filter(el => val !== el),
      }, () => this.props.onChange(this.state.selected));
    }
  }

  render() {
    return (
      <table>
        <tbody>
          {
            this.props.arr
              .filter(val => !this.props.exclude || this.props.exclude.indexOf(val) === -1)
              .map(val => (
                <tr key={val}>
                  <td>
                    <input
                      type="checkbox"
                      onChange={evt => this.handleChange(evt, val)}
                      checked={this.state.selected.indexOf(val) !== -1} />
                  </td>
                  <td>{val}</td>
                </tr>
              ))
          }
        </tbody>
      </table>
    );
  }
}

export default SelectView;
