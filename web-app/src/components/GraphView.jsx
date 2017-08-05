import React, { Component } from 'react';
import Heading from './Heading';
import SelectView from './SelectView';
import CanvasGraphView from './CanvasGraphView';
import IconGroup from './IconGroup';
import Icon from './Icon';

class GraphView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      graphing: false,
      keys: [],
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    this.setState({
      graphing: !this.state.graphing,
    });
  }

  render() {
    return (
      <div>
        <Heading level={2} text="Graph">
          {
            this.state.keys.length > 0 ?
              (
                <IconGroup>
                  <Icon
                    icon={this.state.graphing ? 'close' : 'chart'}
                    size="small"
                    onClick={this.handleClick} />
                </IconGroup>
              ) : undefined
          }
        </Heading>
        {
          this.state.graphing ?
            (<CanvasGraphView keys={this.state.keys} telemetry={this.props.telemetry} />)
            :
            (
              <SelectView
                arr={this.props.telemetry.entries.map(el => el.name)}
                exclude={['time']}
                onChange={selected => this.setState({ keys: selected })} />
            )
        }
      </div>
    );
  }
}

export default GraphView;
