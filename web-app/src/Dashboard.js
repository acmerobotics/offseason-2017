import React, { Component } from 'react';
import FieldView from './FieldView';
import TelemetryView from './TelemetryView';
import ConfigView from './ConfigView';

class Dashboard extends Component {
  constructor(props) {
    super(props);
    this.handleConfigChange = this.handleConfigChange.bind(this);
    this.handleConfigSave = this.handleConfigSave.bind(this);
    this.handleMessage = this.handleMessage.bind(this);
    this.handleConfigRefresh = this.handleConfigRefresh.bind(this);
    this.state = {
      isConnected: false,
      telemetry: [],
      config: []
    };
  }

  handleConfigChange(e, optionIndex) {
    const configCopy = this.state.config.slice();
    const option = configCopy[optionIndex];
    let val = e.value;
    if (option.type === "int") {
      val = parseInt(val, 10);
    } else if (option.type === "double") {
      val = parseFloat(val);
    } else if (option.type === "enum") {
      val = option.values.indexOf(val);
    }
    option.value = val;
    this.setState({
      config: configCopy
    });
  }

  handleConfigRefresh() {
    this.socket.send(JSON.stringify({
      type: "get",
      data: "config"
    }));
  }

  handleConfigSave() {
    this.socket.send(JSON.stringify({
      type: "update",
      data: {
        config: this.state.config
      }
    }));
  }

  render() {
    return (
    <div>
      <header>
        <h1>FTC Dashboard</h1>
        <div className="iconGroup">
          <div className={this.state.isConnected ? "large icon wifi" : "large icon no-wifi"}></div>
        </div>
      </header>
      <div id="grid">
        <FieldView telemetry={this.state.telemetry} />
        <ConfigView config={this.state.config} onChange={this.handleConfigChange} onSave={this.handleConfigSave} onRefresh={this.handleConfigRefresh} />
        <TelemetryView telemetry={this.state.telemetry} />
      </div>
    </div>
    );
  }

  handleMessage(msg) {
    if (msg.type === "pong") {
      const pingTime = Date.now() - this.lastPingTime;
      if (pingTime > 250) {
        console.warn("WARNING! Slow ping time (" + pingTime + "ms)");
      }
    } else if(msg.type === "update") {
      console.log("recv'd an update");
      console.dir(msg);
      this.setState(msg.data);
    } else {
      console.log("recv'd unknown message: ");
      console.log(msg);
    }
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

  connect() {
    this.socket = new WebSocket('ws://192.168.1.8:8000');
    this.socket.onmessage = (evt) => {
      const msg = JSON.parse(evt.data);
      this.handleMessage(msg);
    };
    this.socket.onopen = (evt) => {
      console.log("socket opened");
      this.setState({
        isConnected: true
      });
    };
    this.socket.onclose = (evt) => {
      this.setState({
        isConnected: false
      });
      console.log("socket closed");

      setTimeout(() => {
        console.log("attempting to reconnect");
        this.connect();
      }, 3000);
    };
  }
}

export default Dashboard;
