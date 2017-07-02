import React from 'react';

const Icon = ({ icon, size, onClick }) => (
  <div className={`icon ${icon} ${size}`} onClick={onClick} />
);

export default Icon;
