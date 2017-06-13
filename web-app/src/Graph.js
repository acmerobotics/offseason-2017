const DEFAULT_OPTIONS = {
  color: "rgb(255, 0, 0)",
  lineWidth: 2,
  durationMs: 25000,
  padding: 20,
  fontSize: 14
};

// interesting algorithm (see http://erison.blogspot.nl/2011/07/algorithm-for-optimal-scaling-on-chart.html)
function getAxisScaling(min, max) {
  const maxTicks = 9;
  const range = niceNum(max - min, false);
  const tickSpacing = niceNum(range / (maxTicks - 1), true);
  const niceMin = (Math.floor(min / tickSpacing) - 1) * tickSpacing;
  const niceMax = (Math.floor(max / tickSpacing) + 1) * tickSpacing;
  return [niceMin, niceMax, tickSpacing];
}

function niceNum(range, round) {
  const exponent = Math.floor(Math.log10(range));
  const fraction = range / Math.pow(10, exponent);
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
  } else {
    if (fraction <= 1) {
      niceFraction = 1;
    } else if (fraction <= 2) {
      niceFraction = 2;
    } else if (fraction <= 5) {
      niceFraction = 5;
    } else {
      niceFraction = 10;
    }
  }
  return niceFraction * Math.pow(10, exponent);
}

// shamelessly stolen from https://github.com/chartjs/Chart.js/blob/master/src/core/core.ticks.js
function formatTicks(tickValue, ticks) {
	// If we have lots of ticks, don't use the ones
	var delta = ticks.length > 3 ? ticks[2] - ticks[1] : ticks[1] - ticks[0];

	// If we have a number like 2.5 as the delta, figure out how many decimal places we need
	if (Math.abs(delta) > 1) {
		if (tickValue !== Math.floor(tickValue)) {
			// not an integer
			delta = tickValue - Math.floor(tickValue);
		}
	}

	var logDelta = Math.log10(Math.abs(delta));
	var tickString = '';

	if (tickValue !== 0) {
		var numDecimal = -1 * Math.floor(logDelta);
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
    this.ctx = canvas.getContext("2d");
    this.options = options || {};
    Object.keys(DEFAULT_OPTIONS).forEach(val => {
      if (typeof this.options[val] === "undefined") {
        this.options[val] = DEFAULT_OPTIONS[val];
      }
    });
    this.data = [[], []];
    this.lastDataTime = 0;
    this.lastSimTime = 0;
  }

  renderGraph(x, y, width, height) {
    x = x || 0;
    y = y || 0;
    width = width || this.canvas.width;
    height = height || this.canvas.height;

    this.ctx.fillStyle = "white";
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    this._renderAxesAndLines(x, y, width, height);
  }

  addData(time, data) {
    const now = Date.now();
    if (this.lastDataTime === 0) {
      this.lastSimTime = now + 250;
    } else {
      this.lastSimTime += (time - this.lastDataTime);
    }
    this.lastDataTime = time;
    this.data[0].push(this.lastSimTime);
    this.data[1].push(data);
  }

  _renderAxesAndLines(x, y, width, height) {
    // remove old points
    const now = Date.now();
    while ((now - this.data[0][0]) > (this.options.durationMs + 250)) {
      this.data[0].shift();
      this.data[1].shift();
    }

    // get y-axis scaling
    const yVals = this.data[1];
    const min = Math.min.apply(Math, yVals);
    const max = Math.max.apply(Math, yVals);
    const scaling = getAxisScaling(min, max);

    // configure text options
    this.ctx.textAlign = "right";
    this.ctx.font = this.options.fontSize + "px sans-serif";
    this.ctx.textBaseline = "middle";
    this.ctx.strokeStyle = "rgb(120, 120, 120)";
    this.ctx.fillStyle = "rgb(50, 50, 50)";
    this.ctx.lineJoins = "round";
    this.ctx.lineWidth = 1;

    // get tick array
    const ticks = [];
    for (let i = scaling[0]; i <= scaling[1]; i += scaling[2]) {
      ticks.push(i);
    }

    // generate strings
    let maxWidth = 0;
    const tickStrings = [];
    for (let i = 0; i < ticks.length; i++) {
      const s = formatTicks(ticks[i], ticks);
      tickStrings.push(s);
      const width = this.ctx.measureText(s).width;
      if (width > maxWidth) {
        maxWidth = width;
      }
    }

    // draw axis labels and horizontal gridlines
    const graphHeight = height - 2 * this.options.padding;
    const vertSpacing = graphHeight / (ticks.length - 1);
    x += this.options.padding + maxWidth;
    y += this.options.padding;
    for (let i = 0; i < tickStrings.length; i++) {
      this.ctx.fillText(tickStrings[i], x, y + (ticks.length - i - 1) * vertSpacing);
      this.ctx.beginPath();
      this.ctx.moveTo(x + this.options.padding, Math.floor(y + (ticks.length - i - 1) * vertSpacing) + 0.5);
      this.ctx.lineTo(width - this.options.padding, Math.floor(y + (ticks.length - i - 1) * vertSpacing) + 0.5);
      this.ctx.stroke();
    }

    x += this.options.padding;

    // draw vertical gridlines
    const graphWidth = width - x - this.options.padding;
    const horSpacing = graphWidth / 4;
    for (let i = 0; i < 5; i++) {
      this.ctx.beginPath();
      this.ctx.moveTo(Math.floor(x + i * horSpacing) + 0.5, y);
      this.ctx.lineTo(Math.floor(x + i * horSpacing) + 0.5, height - this.options.padding);
      this.ctx.stroke();
    }

    // draw data lines
    const time = this.data[0];
    for (let i = 1; i < this.data.length; i++) {
      const d = this.data[i];
      this.ctx.beginPath();
      this.ctx.strokeStyle = this.options.color;
      this.ctx.lineWidth = this.options.lineWidth;
      this.ctx.moveTo(x + (time[0] - now + this.options.durationMs) * graphWidth / this.options.durationMs,
                      map(d[0], scaling[0], scaling[1], this.options.padding + graphHeight, this.options.padding));
      for (let j = 1; j < d.length; j++) {
        this.ctx.lineTo(x + (time[j] - now + this.options.durationMs) * graphWidth / this.options.durationMs,
                        map(d[j], scaling[0], scaling[1], this.options.padding + graphHeight, this.options.padding));
      }
      this.ctx.stroke();
    }

    // cover up overflow
    this.ctx.fillStyle = "white";
    this.ctx.fillRect(x - this.options.padding, y, this.options.padding, graphHeight);
    this.ctx.fillRect(x + graphWidth + 1, y, this.options.padding, graphHeight);
  }
}
