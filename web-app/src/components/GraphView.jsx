import React, { Component } from 'react';
import Heading from './Heading';
import Graph from '../graph';

function shallowArrEquals(arr1, arr2) {
  return arr1.length === arr2.length && arr1.every(val => arr2.indexOf(val) !== -1);
}

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
    this.lastKeys = [];
    this.graph = new Graph(this.canvas);
    this.renderGraph();
    document.addEventListener('keydown', this.handleDocumentKeydown);
  }

  componentDidUpdate() {
    if (!shallowArrEquals(this.lastKeys, this.props.keys)) {
      this.graph.clear();
      this.lastKeys = this.props.keys;
    }
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
      this.graph.render(0, 0,
        Math.min(this.canvas.parentElement.clientWidth - 50, 1000),
        Math.min(this.canvas.parentElement.clientHeight - 50, 1000));
      requestAnimationFrame(this.renderGraph);
    }
  }

  render() {
    return (
      <div>
        <Heading level={2} text="Graph" />
        <canvas ref={(c) => { this.canvas = c; }} width="1000" height="1000" />
      </div>
    );
  }
}

export default GraphView;
