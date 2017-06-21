const DEFAULT_OPTIONS = {
  colors: [
    '#2979ff',
    '#dd2c00',
    '#4caf50',
    '#7c4dff',
    '#ffa000',
  ],
  lineWidth: 2,
  durationMs: 30000,
  padding: 15,
  fontSize: 14,
};

function niceNum(range, round) {
  const exponent = Math.floor(Math.log10(range));
  const fraction = range / (10 ** exponent);
  let niceFraction;
  if (round) {
    if (fraction < 1.5) {
      niceFraction = 1;
    } else if (fraction < 3) {
      niceFraction = 2;
    } else if (fraction < 7) {
      niceFraction = 5;
    } else {
      niceFraction = 10;
    }
  } else if (fraction <= 1) {
    niceFraction = 1;
  } else if (fraction <= 2) {
    niceFraction = 2;
  } else if (fraction <= 5) {
    niceFraction = 5;
  } else {
    niceFraction = 10;
  }
  return niceFraction * (10 ** exponent);
}

// interesting algorithm (see http://erison.blogspot.nl/2011/07/algorithm-for-optimal-scaling-on-chart.html)
function getAxisScaling(min, max) {
  const maxTicks = 9;
  const range = niceNum(max - min, false);
  const tickSpacing = niceNum(range / (maxTicks - 1), true);
  const niceMin = Math.floor(min / tickSpacing) * tickSpacing;
  const niceMax = (Math.floor(max / tickSpacing) + 1) * tickSpacing;
  return [niceMin, niceMax, tickSpacing];
}

// shamelessly stolen from https://github.com/chartjs/Chart.js/blob/master/src/core/core.ticks.js
function formatTicks(tickValue, ticks) {
  // If we have lots of ticks, don't use the ones
  let delta = ticks.length > 3 ? ticks[2] - ticks[1] : ticks[1] - ticks[0];

  // If we have a number like 2.5 as the delta, figure out how many decimal places we need
  if (Math.abs(delta) > 1) {
    if (tickValue !== Math.floor(tickValue)) {
      // not an integer
      delta = tickValue - Math.floor(tickValue);
    }
  }

  const logDelta = Math.log10(Math.abs(delta));
  let tickString = '';

  if (tickValue !== 0) {
    let numDecimal = -1 * Math.floor(logDelta);
    numDecimal = Math.max(Math.min(numDecimal, 20), 0); // toFixed has a max of 20 decimal places
    tickString = tickValue.toFixed(numDecimal);
  } else {
    tickString = '0'; // never show decimal places for 0
  }

  return tickString;
}

function map(value, fromLow, fromHigh, toLow, toHigh) {
  const frac = (value - fromLow) / (fromHigh - fromLow);
  return toLow + frac * (toHigh - toLow);
}

export default class Graph {
  constructor(canvas, options) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.options = DEFAULT_OPTIONS;
    Object.assign(this.options, options || {});
    this.time = [];
    this.datasets = [];
    this.lastDataTime = 0;
    this.lastSimTime = 0;
  }

  addData(data) {
    if (data.length !== (this.datasets.length + 1)) {
      this.lastSimTime = Date.now() + 250;
      this.time = [this.lastSimTime];
      this.datasets = [];
      let color = 0;
      for (let i = 0; i < data.length; i += 1) {
        if (data[i].name === 'time') {
          this.lastDataTime = data[i].value;
        } else {
          this.datasets.push({
            name: data[i].name,
            data: [data[i].value],
            color: this.options.colors[color % data.length],
          });
          color += 1;
        }
      }
    } else {
      for (let i = 0; i < data.length; i += 1) {
        if (data[i].name === 'time') {
          this.lastSimTime += (data[i].value - this.lastDataTime);
          this.time.push(this.lastSimTime);
          this.lastDataTime = data[i].value;
        } else {
          for (let j = 0; j < this.datasets.length; j += 1) {
            if (data[i].name === this.datasets[j].name) {
              this.datasets[j].data.push(data[i].value);
            }
          }
        }
      }
    }
  }

  renderGraph(x = 0, y = 0, width = this.canvas.width, height = this.canvas.height) {
    const o = this.options;
    this.ctx.fillStyle = 'white';
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    this.ctx.font = `${o.fontSize}px sans-serif`;
    this.ctx.textBaseline = 'middle';
    this.ctx.textAlign = 'left';
    this.ctx.lineWidth = o.lineWidth;
    const keyHeight = this.renderKey(x, y, width);
    this.renderAxesAndLines(x, y + keyHeight, width, height - keyHeight);
  }

  renderKey(x, y, width) {
    const o = this.options;
    const keyLineWidth = 16;
    const numSets = this.datasets.length;
    const height = numSets * o.fontSize + (numSets - 1) * 4;
    for (let i = 0; i < numSets; i += 1) {
      const lineY = y + i * (o.fontSize + 4) + o.fontSize / 2;
      const name = this.datasets[i].name;
      const color = this.datasets[i].color;
      const lineWidth = this.ctx.measureText(name).width + keyLineWidth + 4;
      const lineX = x + (width - lineWidth) / 2;

      this.ctx.strokeStyle = color;
      this.ctx.beginPath();
      this.ctx.moveTo(lineX, lineY);
      this.ctx.lineTo(lineX + keyLineWidth, lineY);
      this.ctx.stroke();

      this.ctx.fillStyle = 'black';
      this.ctx.fillText(name, lineX + keyLineWidth + 4, lineY);
    }
    return height;
  }

  renderAxesAndLines(x, y, width, height) {
    if (this.datasets.length === 0 || this.datasets[0].data.length === 0) return;
    const o = this.options;
    // remove old points
    const now = Date.now();
    while ((now - this.time[0]) > (o.durationMs + 250)) {
      this.time.shift();
      for (let i = 0; i < this.datasets.length; i += 1) {
        this.datasets[i].data.shift();
      }
    }

    // get y-axis scaling
    let min = Number.MAX_VALUE;
    let max = Number.MIN_VALUE;
    for (let i = 0; i < this.datasets.length; i += 1) {
      for (let j = 0; j < this.datasets[i].data.length; j += 1) {
        const val = this.datasets[i].data[j];
        if (val > max) {
          max = val;
        }
        if (val < min) {
          min = val;
        }
      }
    }
    const scaling = getAxisScaling(min, max);

    // configure text options
    this.ctx.textAlign = 'right';
    this.ctx.strokeStyle = 'rgb(120, 120, 120)';
    this.ctx.fillStyle = 'rgb(50, 50, 50)';
    this.ctx.lineJoins = 'round';
    this.ctx.lineWidth = 1;

    // get tick array
    const ticks = [];
    for (let i = scaling[0]; i <= scaling[1]; i += scaling[2]) {
      ticks.push(i);
    }

    // generate strings
    let maxTextWidth = 0;
    const tickStrings = [];
    for (let i = 0; i < ticks.length; i += 1) {
      const s = formatTicks(ticks[i], ticks);
      tickStrings.push(s);
      const textWidth = this.ctx.measureText(s).width;
      if (textWidth > maxTextWidth) {
        maxTextWidth = textWidth;
      }
    }

    // draw axis labels and horizontal gridlines
    const graphHeight = height - (2 * o.padding);
    const vertSpacing = graphHeight / (ticks.length - 1);
    x += o.padding + maxTextWidth;
    y += o.padding;
    for (let i = 0; i < tickStrings.length; i += 1) {
      this.ctx.fillText(tickStrings[i], x, y + (ticks.length - i - 1) * vertSpacing);
      this.ctx.beginPath();
      this.ctx.moveTo(
        x + o.padding,
        Math.floor(y + (ticks.length - i - 1) * vertSpacing) + 0.5);
      this.ctx.lineTo(
        width - o.padding,
        Math.floor(y + (ticks.length - i - 1) * vertSpacing) + 0.5);
      this.ctx.stroke();
    }

    x += o.padding;

    // draw vertical gridlines
    const graphWidth = width - x - o.padding;
    const horSpacing = graphWidth / 4;
    for (let i = 0; i < 5; i += 1) {
      this.ctx.beginPath();
      this.ctx.moveTo(Math.floor(x + i * horSpacing) + 0.5, y);
      this.ctx.lineTo(Math.floor(x + i * horSpacing) + 0.5, y + height - 2 * o.padding);
      this.ctx.stroke();
    }

    // draw data lines
    for (let i = 0; i < this.datasets.length; i += 1) {
      const d = this.datasets[i];
      this.ctx.beginPath();
      this.ctx.strokeStyle = d.color;
      this.ctx.lineWidth = o.lineWidth;
      this.ctx.moveTo(x + (this.time[0] - now + o.durationMs) * graphWidth / o.durationMs,
        y + map(d.data[0], scaling[0], scaling[1], graphHeight, 0));
      for (let j = 1; j < d.data.length; j += 1) {
        this.ctx.lineTo(x + (this.time[j] - now + o.durationMs) * graphWidth / o.durationMs,
          y + map(d.data[j], scaling[0], scaling[1], graphHeight, 0));
      }
      this.ctx.stroke();
    }

    // cover up horizontal overflow
    this.ctx.fillStyle = 'white';
    this.ctx.fillRect(x - o.padding, y, o.padding, graphHeight);
    this.ctx.fillRect(x + graphWidth + 1, y, o.padding, graphHeight);
  }
}
