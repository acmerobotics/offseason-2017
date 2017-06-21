import React, { Component } from 'react';
import Graph from './Graph';

class GraphView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      paused: false,
    };

    this.handleDocumentKeydown = this.handleDocumentKeydown.bind(this);
    this.renderGraph = this.renderGraph.bind(this);
  }

  componentDidMount() {
    this.graph = new Graph(this.canvas);
    this.renderGraph();
    document.addEventListener('keydown', this.handleDocumentKeydown);
  }

  componentDidUpdate() {
    if (this.props.keys.length > 0) {
      this.graph.addData(
        this.props.telemetry
          .filter(entry => (this.props.keys.indexOf(entry.name) !== -1 || entry.name === 'time')));
    }
  }

  componentWillUnmount() {
    document.removeEventListener('keydown', this.handleDocumentKeydown);
  }

  handleDocumentKeydown(evt) {
    console.log(evt);
    if (evt.code === 'Space') {
      this.setState({
        paused: !this.state.paused,
      }, () => {
        this.renderGraph();
      });
    }
  }

  renderGraph() {
    if (!this.state.paused) {
      this.graph.renderGraph(0, 0,
        Math.min(this.canvas.parentElement.clientWidth - 50, 1000),
        Math.min(this.canvas.parentElement.clientHeight - 50, 1000));
      requestAnimationFrame(this.renderGraph);
    }
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
