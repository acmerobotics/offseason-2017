import React, { Component } from 'react';
import Graph from './Graph';

class GraphView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      graphing: false
    };
  }

  componentDidMount() {
    this.graph = new Graph(this.refs.canvas, {
      color: "rgb(48, 129, 253)"
    });
    this.renderGraph();
  }

  componentDidUpdate() {
    if (this.props.telemetry.length >= 2) {
      this.graph.addData(parseInt(this.props.telemetry[0][1], 10), parseFloat(this.props.telemetry[1][1]));
    }
  }

  renderGraph() {
    this.graph.renderGraph(0, 0,
      Math.min(this.refs.canvas.parentElement.clientWidth - 50, 1000),
      Math.min(this.refs.canvas.parentElement.clientHeight - 100, 1000));
    requestAnimationFrame(this.renderGraph.bind(this));
  }

  render() {
    const style = {
      gridRow: 1,
      gridColumn: 1,
      overflow: "hidden"
    };
    return (
      <div className="tile" style={style}>
        <h2>Graph</h2>
        <select>
          {this.props.telemetry.map(v => (<option>{v[0]}</option>))}
        </select>
        <canvas ref="canvas" width="1000" height="1000"></canvas>
      </div>
    );
  }
}

export default GraphView;
