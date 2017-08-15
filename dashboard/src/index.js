import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import Dashboard from './components/Dashboard';
import configureStore from './redux/configureStore';
import registerServiceWorker from './util/registerServiceWorker';
import './index.css';

const store = configureStore();

ReactDOM.render(
  <Provider store={store}>
    <Dashboard />
  </Provider>,
  document.getElementById('root'));
registerServiceWorker();
