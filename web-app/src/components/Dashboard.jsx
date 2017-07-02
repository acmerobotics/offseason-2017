import React, { Component } from 'react';
import Heading from './Heading';
import Header from './Header';
import IconGroup from './IconGroup';
import Icon from './Icon';
import TelemetryView from './TelemetryView';
import ConfigView from './ConfigView';
import GraphView from './GraphView';
import Tile from './Tile';
import TileGrid from './TileGrid';
import validateOptionInput from '../validator';

class Dashboard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isConnected: false,
      graphKeys: [],
      telemetry: [],
      config: [],
    };

    this.handleConfigChange = this.handleConfigChange.bind(this);
    this.handleConfigSave = this.handleConfigSave.bind(this);
    this.handleMessage = this.handleMessage.bind(this);
    this.handleConfigRefresh = this.handleConfigRefresh.bind(this);
    this.handleGraph = this.handleGraph.bind(this);
  }

  componentDidMount() {
    this.connect();
    this.pingId = setInterval(() => {
      if (this.state.isConnected) {
        this.lastPingTime = Date.now();
        this.socket.send('{"type":"ping"}');
      }
    }, 1000);
  }

  componentWillUnmount() {
    clearInterval(this.pingId);
    this.socket.close();
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
    this.socket.send(JSON.stringify({
      type: 'update',
      data: {
        config: this.state.config,
      },
    }));
  }

  handleGraph(keys) {
    this.setState({
      graphKeys: keys,
    });
  }

  handleMessage(msg) {
    if (msg.type === 'pong') {
      const pingTime = Date.now() - this.lastPingTime;
      if (pingTime > 250) {
        console.warn(`WARNING! Slow ping time (${pingTime}ms)`);
      }
    } else if (msg.type === 'update') {
      this.setState(msg.data);
    } else {
      console.log('recv\'d unknown message: ');
      console.log(msg);
    }
  }

  connect() {
    // const host = process.env.NODE_ENV === 'development' ? '192.168.1.10' : '192.168.49.1';
    const host = '192.168.1.12';
    const port = 8000;
    this.socket = new WebSocket(`ws://${host}:${port}`);
    this.socket.onmessage = (evt) => {
      const msg = JSON.parse(evt.data);
      this.handleMessage(msg);
    };
    this.socket.onopen = () => {
      console.log('socket opened');
      this.setState({
        isConnected: true,
      });
    };
    this.socket.onclose = () => {
      console.log('socket closed');
      this.setState({
        isConnected: false,
      });

      setTimeout(() => {
        this.connect();
      }, 3000);
    };
  }

  render() {
    return (
      <div>
        <Header>
          <Heading text="FTC Dashboard" level={1}>
            <IconGroup>
              <Icon icon={this.state.isConnected ? 'wifi' : 'no-wifi'} size="large" />
            </IconGroup>
          </Heading>
        </Header>
        <TileGrid>
          <Tile row="1 / span 2" col={1} hidden>
            <GraphView
              telemetry={this.state.telemetry}
              keys={this.state.graphKeys} />
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
              telemetry={this.state.telemetry}
              onGraph={this.handleGraph} />
          </Tile>
        </TileGrid>
      </div>
    );
  }
}

export default Dashboard;
