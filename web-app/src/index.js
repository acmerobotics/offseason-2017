import React from 'react';
import ReactDOM from 'react-dom';
import Dashboard from './Dashboard';
import registerServiceWorker from './registerServiceWorker';
import './index.css';

ReactDOM.render(<Dashboard />, document.getElementById('root'));
registerServiceWorker();
