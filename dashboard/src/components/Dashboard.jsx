import React, { Component } from 'react';
import { connect as reduxConnect } from 'react-redux';
import PropTypes from 'prop-types';
import Heading from './Heading';
import Header from './Header';
import IconGroup from './IconGroup';
import Icon from './Icon';
import TelemetryView from './TelemetryView';
import ConfigView from './ConfigView';
import GraphView from './GraphView';
import FieldView from './FieldView';
import Tile from './Tile';
import TileGrid from './TileGrid';
import validateOptionInput from '../util/validator';
import { connect, disconnect } from '../actions/socket';

class Dashboard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isConnected: false,
      pingTime: 0,
      telemetry: {
        entries: [],
      },
      config: [],
      fieldOverlay: {
        ops: [],
      },
    };

    this.handleConfigChange = this.handleConfigChange.bind(this);
    this.handleConfigSave = this.handleConfigSave.bind(this);
    this.handleMessage = this.handleMessage.bind(this);
    this.handleConfigRefresh = this.handleConfigRefresh.bind(this);
  }

  componentDidMount() {
    this.props.dispatch(connect('192.168.1.7', 8000));
  }

  componentWillUnmount() {
    this.props.dispatch(disconnect());
  }

  handleConfigChange(optionGroupIndex, optionIndex, newValue) {
    const configCopy = this.state.config.slice();
    const optionGroup = configCopy[optionGroupIndex];
    const option = optionGroup.options[optionIndex];
    const { valid, value } = validateOptionInput(option, newValue);
    console.log(`[onChange]
      ${option.name} (${option.type}):\t
      ${JSON.stringify(option.value)} => ${JSON.stringify(value)}`);
    option.value = value;
    const name = option.name;
    if (!optionGroup.invalid) {
      optionGroup.invalid = [];
    }
    if (valid && optionGroup.invalid.indexOf(name) !== -1) {
      optionGroup.invalid = optionGroup.invalid.filter(el => el !== name);
    } else if (!valid && optionGroup.invalid.indexOf(name) === -1) {
      optionGroup.invalid.push(name);
    }
    this.setState({
      config: configCopy,
    });
  }

  handleConfigRefresh() {
    this.socket.send(JSON.stringify({
      type: 'get',
      data: 'config',
    }));
  }

  handleConfigSave() {
    if (this.state.config.every(v => !v.invalid || v.invalid.length === 0)) {
      this.socket.send(JSON.stringify({
        type: 'update',
        data: {
          config: this.state.config,
        },
      }));
    }
  }

  handleMessage(msg) {
    if (msg.type === 'pong') {
      const pingTime = Date.now() - this.lastPingTime;
      this.setState({ pingTime });
    } else if (msg.type === 'update') {
      this.setState(msg.data);
    } else {
      console.log('recv\'d unknown message: ');
      console.log(msg);
    }
  }

  render() {
    return (
      <div>
        <Header>
          <Heading text="FTC Dashboard" level={1}>
            <IconGroup>
              {
                this.props.isConnected ?
                  <p>{this.props.pingTime}ms&nbsp;&nbsp;&nbsp;&nbsp;</p>
                  : null
              }
              <Icon icon={this.props.isConnected ? 'wifi' : 'no-wifi'} size="large" />
            </IconGroup>
          </Heading>
        </Header>
        <TileGrid>
          <Tile row="1 / span 2" col={1} hidden>
            <FieldView
              overlay={this.state.fieldOverlay} />
            {/* <GraphView
              telemetry={this.state.telemetry} /> */}
          </Tile>
          <Tile row={1} col={2}>
            <ConfigView
              config={this.state.config}
              onChange={this.handleConfigChange}
              onSave={this.handleConfigSave}
              onRefresh={this.handleConfigRefresh} />
          </Tile>
          <Tile row={2} col={2}>
            <TelemetryView
              telemetry={this.state.telemetry} />
          </Tile>
        </TileGrid>
      </div>
    );
  }
}

Dashboard.propTypes = {
  isConnected: PropTypes.bool.isRequired,
  pingTime: PropTypes.number.isRequired
};

const mapStateToProps = ({ socket }) => ({
  isConnected: socket.isConnected,
  pingTime: socket.pingTime
});

export default reduxConnect(mapStateToProps)(Dashboard);
