import React from 'react';
import ReactDOM from 'react-dom';
import Dashboard from './components/Dashboard';
import registerServiceWorker from './registerServiceWorker';
import './index.css';

// eslint-disable-next-line
ReactDOM.render(<Dashboard />, document.getElementById('root'));
registerServiceWorker();
