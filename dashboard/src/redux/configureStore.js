import { applyMiddleware, createStore } from 'redux';
import logger from 'redux-logger';
import thunk from 'redux-thunk';
import reducer from './reducer';

const configureStore = () => (
  createStore(
    reducer,
    applyMiddleware(thunk, logger)
  )
);

export default configureStore;
