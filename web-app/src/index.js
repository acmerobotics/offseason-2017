import React from 'react';
import ReactDOM from 'react-dom';
import Dashboard from './components/Dashboard';
import registerServiceWorker from './util/registerServiceWorker';
import './index.css';

ReactDOM.render(<Dashboard />, document.getElementById('root'));
registerServiceWorker();
