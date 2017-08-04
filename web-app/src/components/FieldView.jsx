import React from 'react';
import Field from '../field';
import Heading from './Heading';

class FieldView extends React.Component {
  constructor(props) {
    super(props);

    this.renderField = this.renderField.bind(this);
  }

  componentDidMount() {
    this.field = new Field(this.canvas);
    this.renderField();
  }

  componentWillUpdate() {
    this.field.setOverlay(this.props.overlay);
  }

  renderField() {
    if (this.canvas) {
      this.field.render(0, 0,
        Math.min(this.canvas.parentElement.parentElement.clientWidth - 32, 1000),
        Math.min(this.canvas.parentElement.parentElement.clientHeight - 50, 1000));
      requestAnimationFrame(this.renderField);
    }
  }

  render() {
    return (
      <div>
        <Heading level={2} text="Field" />
        <canvas ref={(c) => { this.canvas = c; }} width="1000" height="1000" />
      </div>
    );
  }
}

export default FieldView;
