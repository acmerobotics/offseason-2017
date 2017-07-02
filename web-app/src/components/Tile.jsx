import React from 'react';

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

export default Tile;
