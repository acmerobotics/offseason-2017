import { applyMiddleware, createStore } from 'redux';
import logger from 'redux-logger';
import thunk from 'redux-thunk';
import socketMiddleware from './socketMiddleware';
import reducer from './reducers';

const configureStore = () => (
  createStore(
    reducer,
    applyMiddleware(thunk, socketMiddleware, logger)
  )
);

export default configureStore;
