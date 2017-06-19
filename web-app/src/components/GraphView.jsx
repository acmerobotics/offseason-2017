import React, { Component } from 'react';
import Graph from './Graph';

class GraphView extends Component {
  componentDidMount() {
    this.graph = new Graph(this.canvas, {
      color: [
        'rgb(48, 129, 253)',
        '#e53935',
        '#1de9b6',
      ],
    });
    this.renderGraph();
  }

  componentDidUpdate() {
    if (this.props.keys.length > 0) {
      const data = [];
      this.props.telemetry.forEach((entry) => {
        if (this.props.keys.indexOf(entry[0]) !== -1) {
          data.push(parseFloat(entry[1]));
        }
      });
      this.graph.addData(parseInt(this.props.telemetry[0][1], 10), data);
    }
  }

  renderGraph() {
    this.graph.renderGraph(0, 0,
      Math.min(this.canvas.parentElement.clientWidth - 50, 1000),
      Math.min(this.canvas.parentElement.clientHeight - 50, 1000));
    requestAnimationFrame(this.renderGraph.bind(this));
  }

  render() {
    const style = {
      gridRow: 1,
      gridColumn: 1,
      overflow: 'hidden',
    };
    return (
      <div className="tile" style={style}>
        <h2>Graph</h2>
        <canvas ref={(c) => { this.canvas = c; }} width="1000" height="1000" />
      </div>
    );
  }
}

export default GraphView;
