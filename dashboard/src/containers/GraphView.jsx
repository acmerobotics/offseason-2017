import React, { Component } from 'react';
import { connect } from 'react-redux';
import TelemetryView from './TelemetryView';
import Heading from '../components/Heading';
import MultipleCheckbox from '../components/MultipleCheckbox';
import GraphCanvas from './GraphCanvas';
import IconGroup from '../components/IconGroup';
import Icon from '../components/Icon';

class GraphView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      graphing: false,
      keys: [],
    };

    this.handleClick = this.handleClick.bind(this);
    this.handleDocumentKeydown = this.handleDocumentKeydown.bind(this);
  }

  componentDidMount() {
    document.addEventListener('keydown', this.handleDocumentKeydown);
  }

  componentWillUnmount() {
    document.removeEventListener('keydown', this.handleDocumentKeydown);
  }

  handleDocumentKeydown(evt) {
    if (!this.state.graphing && evt.code === 'Enter') {
      this.setState({
        graphing: true,
      });
    } else if (this.state.graphing && evt.code === 'Escape') {
      this.setState({
        graphing: false,
      });
    }
  }

  handleClick() {
    this.setState({
      graphing: !this.state.graphing,
    });
  }

  render() {
    return (
      <div>
        <Heading level={2} text="Graph">
          <IconGroup>
            <Icon
              icon={this.state.graphing ? 'close' : 'chart'}
              size="small"
              onClick={this.handleClick} />
          </IconGroup>
        </Heading>
        {
          this.state.graphing ?
            <GraphCanvas keys={this.state.keys} telemetry={this.props.telemetry} />
            :
            (
              <MultipleCheckbox
                arr={this.props.telemetry.entries.map(entry => entry.name)}
                exclude={['time']}
                onChange={selected => this.setState({ keys: selected })}
                selected={this.state.keys} />
            )
        }
      </div>
    );
  }
}

GraphView.propTypes = {
  telemetry: TelemetryView.propTypes.telemetry
};

const mapStateToProps = ({ telemetry }) => ({
  telemetry
});

export default connect(mapStateToProps)(GraphView);
