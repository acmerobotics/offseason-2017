import React from 'react';
import Graph from './Graph';

class GraphCanvas extends React.Component {
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
        this.props.telemetry.entries
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
    if (!this.state.paused && this.canvas) {
      this.graph.render(0, 0,
        Math.min(this.canvas.parentElement.parentElement.clientWidth - 32, 1000),
        Math.min(this.canvas.parentElement.parentElement.clientHeight - 50, 1000));
      requestAnimationFrame(this.renderGraph);
    }
  }

  render() {
    return <canvas ref={(c) => { this.canvas = c; }} width="1000" height="1000" />;
  }
}

export default GraphCanvas;
