import React, { Component } from 'react';

class FieldView extends Component {
  render() {
    return (
      <div className="tile" id="field">
        <h2>Field</h2>
        <canvas ref="canvas" width="500" height="500" data-test={this.props.telemetry}></canvas>
      </div>
    );
  }

  componentDidMount() {
    this.renderCanvas();
  }

  componentDidUpdate() {
    this.renderCanvas();
  }

  renderCanvas() {
    const ctx = this.refs.canvas.getContext("2d");
    ctx.fillStyle = "rgb(200, 200, 200)";
    ctx.fillRect(0, 0, 500, 500);
    ctx.strokeStyle = "rgb(100, 100, 100)";
    for (let i = 0; i <= 6; i++) {
      const val = (500 * i) / 6;
      ctx.moveTo(0, val);
      ctx.lineTo(500, val);
      ctx.moveTo(val, 0);
      ctx.lineTo(val, 500);
    }
    ctx.stroke();
    ctx.beginPath();
    const telemetry = this.props.telemetry;
    let x = NaN, y = NaN;
    for (let i = 0; i < telemetry.length; i++) {
      if (telemetry[i][0] === "x") {
        x = telemetry[i][1];
      } else if (telemetry[i][0] === "y") {
        y = telemetry[i][1];
      }
    }
    x = 250 * (x + 1);
    y = 250 * (y + 1);
    ctx.fillStyle = "rgb(255, 0, 0)";
    ctx.arc(x, y, 10, 0, 2 * Math.PI, false);
    ctx.fill();
  }
}

export default FieldView;
