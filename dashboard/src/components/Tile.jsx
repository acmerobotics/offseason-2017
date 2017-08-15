import React from 'react';
import PropTypes from 'prop-types';

const Tile = ({ children, hidden, row, column }) => (
  <div
    className="tile"
    style={{
      overflow: hidden ? 'hidden' : 'auto',
      gridRow: row,
      gridColumn: column,
    }}>
    { children }
  </div>
);

Tile.propTypes = {
  children: PropTypes.node.isRequired,
  hidden: PropTypes.bool,
  row: PropTypes.oneOfType([
    PropTypes.number,
    PropTypes.string
  ]).isRequired,
  column: PropTypes.oneOfType([
    PropTypes.number,
    PropTypes.string
  ]).isRequired
};

export default Tile;
