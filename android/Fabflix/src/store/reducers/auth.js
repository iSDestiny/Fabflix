import { AUTH_SUCCESS, ATTEMPT_AUTH } from '../actions/actionTypes';

const initialState = {
	loginData : {
		email     : null,
		password  : null,
		success   : false,
		sessionId : null
	}
};

const reducer = (state = initialState, action) => {
	switch (action.type) {
		case ATTEMPT_AUTH:
			return {
				...state,
				loginData : {
					...state.loginData,
					...action.loginData
				}
			};
		case AUTH_SUCCESS:
			return {
				...state,
				loginData : {
					...state.loginData,
					success   : action.success,
					sessionId : action.sessionId
				}
			};
		default:
			return state;
	}
};

export default reducer;
