const DEFAULT_OPTIONS = {
  padding: 15,
  gridLineColor: 'rgb(120, 120, 120)',
};

function scale(value, fromStart, fromEnd, toStart, toEnd) {
  return toStart + ((toEnd - toStart) * (value - fromStart) / (fromEnd - fromStart));
}

export default class Field {
  constructor(canvas, options) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.options = DEFAULT_OPTIONS;
    Object.assign(this.options, options || {});
    this.overlay = [];
  }

  render(x, y, width, height) {
    this.canvas.width = this.canvas.width;
    const smallerDim = width < height ? width : height;
    const fieldSize = smallerDim - 2 * this.options.padding;
    this.renderField(
      x + (width - fieldSize) / 2,
      y + (height - fieldSize) / 2,
      fieldSize, fieldSize);
  }

  renderField(x, y, width, height) {
    this.renderGridLines(x, y, width, height, 7, 7);
    this.renderOverlay(x, y, width, height);
  }

  renderGridLines(x, y, width, height, numTicksX, numTicksY) {
    this.ctx.strokeStyle = this.options.gridLineColor;
    this.ctx.lineWidth = 1;

    const horSpacing = width / (numTicksX - 1);
    const vertSpacing = height / (numTicksY - 1);

    for (let i = 0; i < numTicksX; i += 1) {
      const lineX = x + horSpacing * i + 0.5;
      this.ctx.beginPath();
      this.ctx.moveTo(lineX, y);
      this.ctx.lineTo(lineX, y + height);
      this.ctx.stroke();
    }

    for (let i = 0; i < numTicksY; i += 1) {
      const lineY = y + vertSpacing * i + 0.5;
      this.ctx.beginPath();
      this.ctx.moveTo(x, lineY);
      this.ctx.lineTo(x + width, lineY);
      this.ctx.stroke();
    }
  }

  renderOverlay(x, y, width, height) {
    this.overlay.forEach((element) => {
      switch (element.type) {
      case 'fill':
        this.ctx.fillStyle = element.color;
        break;
      case 'stroke':
        this.ctx.strokeStyle = element.color;
        break;
      case 'strokeWidth':
        this.ctx.lineWidth = element.width;
        break;
      case 'circle':
        this.ctx.beginPath();
        this.ctx.arc(
          scale(element.x, 0, 1, x, width + x),
          scale(element.y, 0, 1, y, height + y),
          element.radius, 0, 2 * Math.PI);
        if (element.stroke) {
          this.ctx.stroke();
        } else {
          this.ctx.fill();
        }
        break;
      case 'polygon': {
        this.ctx.beginPath();
        const { xPoints, yPoints, stroke } = element;
        this.ctx.moveTo(scale(xPoints[0], 0, 1, x, width + x),
          scale(yPoints[0], 0, 1, y, height + y));
        for (let i = 1; i < xPoints.length; i += 1) {
          this.ctx.lineTo(scale(xPoints[i], 0, 1, x, width + x),
            scale(yPoints[i], 0, 1, y, height + y));
        }
        this.ctx.closePath();
        if (stroke) {
          this.ctx.stroke();
        } else {
          this.ctx.fill();
        }
        break;
      }
      case 'polyline': {
        this.ctx.beginPath();
        const { xPoints, yPoints } = element;
        this.ctx.moveTo(scale(xPoints[0], 0, 1, x, width + x),
          scale(yPoints[0], 0, 1, y, height + y));
        for (let i = 1; i < xPoints.length; i += 1) {
          this.ctx.lineTo(scale(xPoints[i], 0, 1, x, width + x),
            scale(yPoints[i], 0, 1, y, height + y));
        }
        this.ctx.stroke();
        break;
      }
      default:
        console.log(`unknown type: ${element.type}`);
        console.log(element);
      }
    });
  }

  setOverlay(overlay) {
    this.overlay = overlay;
  }
}
