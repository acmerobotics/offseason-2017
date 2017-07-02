import React from 'react';

const Heading = ({ children, text, level }) => (
  <div className="heading">
    {React.createElement(`h${level}`, null, text)}
    {children}
  </div>
);

export default Heading;
