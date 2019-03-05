import { ATTEMPT_AUTH, AUTH_SUCCESS } from './actionTypes';

export const attemptAuth = (data) => {
	return {
		type      : ATTEMPT_AUTH,
		loginData : data
	};
};

export const authLoginSucess = (status, sessionId) => {
	return {
		type      : AUTH_SUCCESS,
		success   : status,
		sessionId : sessionId
	};
};

export const authLogin = (data) => {
	return (dispatch) => {
		dispatch(loginStart());
		fetch('https://fabflix.fun:8443/project1/api/login', {
			method  : 'POST',
			body    : JSON.stringify({
				username : data.email,
				password : data.password,
				android  : 'true'
			}),
			headers : {
				'Content-Type' : 'application/json'
			}
		})
			.catch((e) => {
				console.log(e);
				alert('Login Failed!');
			})
			.then((response) => response.json())
			.then((parsedResponse) => {
				console.log(parsedResponse);
				alert(parsedResponse);
			});
	};
};
