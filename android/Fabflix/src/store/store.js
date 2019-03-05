import { createStore, combineReducers } from 'redux';
import reducers from './reducers/root';
import authReducer from './reducers/auth';

const rootReducer = combineReducers({
	movies : reducers,
	auth   : authReducer
});

const configureStore = () => {
	return createStore(rootReducer);
};

export default configureStore;
