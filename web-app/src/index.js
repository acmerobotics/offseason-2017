import React from 'react';
import ReactDOM from 'react-dom';
import Dashboard from './components/Dashboard';
import { config, status, telemetry } from './reducers';
import registerServiceWorker from './registerServiceWorker';
import './index.css';

ReactDOM.render(<Dashboard />, document.getElementById('root'));
registerServiceWorker();
