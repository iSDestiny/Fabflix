import React, { Component } from 'react';
import { View, Text, TextInput, Button, StyleSheet } from 'react-native';
import { Navigation } from 'react-native-navigation';
import { attemptAuth, authLoginSucess } from '../../store/actions/index';
import { connect } from 'react-redux';

class AuthScreen extends Component {
	state = {
		email    : {
			value   : '',
			isValid : false
		},
		password : {
			value   : '',
			isValid : false
		}
	};

	handleInput = (key, val) => {
		this.setState((prevState) => {
			return {
				[key] : {
					...prevState[key],
					value : val
				}
			};
		});
	};
	// 192.168.0.38
	sendHttpLoginRequest = (email, password) => {
		fetch('http://fabflix.fun:8080/project1/api/login', {
			method  : 'POST',
			headers : {
				Accept         : 'application/json',
				'Content-Type' : 'application/json'
			},
			body    : JSON.stringify({
				username : email,
				password : password,
				android  : 'true'
			})
		})
			.catch((e) => {
				console.log(e);
				alert('Login Failed!');
				return;
			})
			.then((response) => response.json())
			.then((parsedResponse) => {
				console.log(JSON.stringify(parsedResponse));
				let success =

						parsedResponse['status'] === 'success' ? true :
						false;
				let sessionId = parsedResponse['sessionId'];
				this.props.onLoginSuccess(success, sessionId);
				this.validateInfo();
			});
	};

	loginHandler = () => {
		this.props.onLogin({ email: this.state.email.value, password: this.state.password.value });
		this.sendHttpLoginRequest(this.state.email.value, this.state.password.value);
		// this.request();
	};

	validateInfo = () => {
		if (this.props.loginSucess) {
			Navigation.push(this.props.componentId, {
				component : {
					name      : 'fabflix.MoviesScreen',
					passProps : {
						text : 'pushed screen'
					},
					options   : {
						topBar : {
							title : {
								text : 'Movies'
							}
						}
					}
				}
			});
		} else {
			alert('Login information is incorrect');
		}
	};

	render() {
		return (
			<View>
				<TextInput
					placeholder="Username"
					value={this.state.email.value}
					onChangeText={(val) => {
						this.handleInput('email', val);
					}}
				/>
				<TextInput
					placeholder="Password"
					value={this.state.password.value}
					onChangeText={(val) => {
						this.handleInput('password', val);
					}}
				/>
				<Button title="Login" onPress={this.loginHandler} />
			</View>
		);
	}
}

const mapStateToProps = (state) => {
	return {
		loginEmail    : state.auth.loginData.email,
		loginPassword : state.auth.loginData.password,
		loginSucess   : state.auth.loginData.success,
		sessionId     : state.auth.loginData.sessionId
	};
};

const mapDispatchToProps = (dispatch) => {
	return {
		onLogin        : (data) => dispatch(attemptAuth(data)),
		onLoginSuccess : (status, id) => dispatch(authLoginSucess(status, id))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(AuthScreen);
