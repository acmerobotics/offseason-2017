import React, { Component } from 'react';
import TelemetryView from './TelemetryView';
import ConfigView from './ConfigView';
import GraphView from './GraphView';

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

  handleConfigChange(optionGroupIndex, optionIndex, value) {
    const configCopy = this.state.config.slice();
    const option = configCopy[optionGroupIndex].options[optionIndex];
    let val = value;
    if (option.type === 'int') {
      if (val !== '') {
        val = parseInt(val, 10);
      }
    } else if (option.type === 'double') {
      if (val !== '' && val !== '.' && !/\d\.$/.test(val)) {
        val = parseFloat(val);
      }
    }
    if (isNaN(val)) {
      val = option.value;
    }
    console.log(`[onChange] ${option.name} (${option.type}):\t${option.value} => ${val}`);
    option.value = val;
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
    const host = '192.168.1.10';
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
        <header>
          <h1>FTC Dashboard</h1>
          <div className="iconGroup">
            <div
              className={this.state.isConnected ? 'large icon wifi' : 'large icon no-wifi'} />
          </div>
        </header>
        <div id="grid">
          <GraphView
            telemetry={this.state.telemetry}
            keys={this.state.graphKeys} />
          <ConfigView
            config={this.state.config}
            onChange={this.handleConfigChange}
            onSave={this.handleConfigSave}
            onRefresh={this.handleConfigRefresh} />
          <TelemetryView
            telemetry={this.state.telemetry}
            onGraph={this.handleGraph} />
        </div>
      </div>
    );
  }
}

export default Dashboard;