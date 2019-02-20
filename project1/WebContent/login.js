/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
	let resultDataJson = resultDataString;

	console.log('handle login response');
	console.log(resultDataJson);
	console.log(resultDataJson['status']);

	let recaptchaStatus = true;

	if (resultDataJson['recaptcha'] === 'failure') {
		console.log('recaptcha failed');

		let recaptchaParent = document.querySelector('#recaptcha');
		let recaptchaErrorMessage = document.querySelector('#recaptcha_error_message');

		recaptchaParent.classList.add('recaptcha-failure');

		recaptchaErrorMessage.textContent = 'Please verify that you are not a robot';
		recaptchaErrorMessage.classList.add('recaptcha-error-text');

		recaptchaStatus = false;
	}

	// If login succeeds, it will redirect the user to index.html
	if (resultDataJson['status'] === 'success' && recaptchaStatus) {
		if(resultDataJson['type'] == 'employee') {
			alert("Employee")
			window.location.replace('_dashboard.html/dashboard_home.html');
		} else {
			window.location.replace('index.html');
		}
	} else if (resultDataJson['status'] !== 'success') {
		// If login fails, the web page will display
		// error messages on <div> with id "login_error_message"
		console.log('show error message');
		console.log(resultDataJson['message']);
		$('#login_error_message').text(resultDataJson['message']);
	}
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
	console.log('submit login form');
	/**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
	formSubmitEvent.preventDefault();

	let username = document.querySelector('#username').value;
	let password = document.querySelector('#password').value;
	let captcha = grecaptcha.getResponse();
	let data = { username: username, password: password, captcha: captcha };
	console.log('Captcha: ' + captcha);
	console.log('DATA IS: username: ' + data.username + ', password: ' + data.password + ', captcha: ' + data.captcha);
	$.post('api/login', data, (resultDataString) => handleLoginResult(resultDataString), 'json');
}

// Bind the submit action of the form to a handler function
$('#login_form').submit((event) => submitLoginForm(event));
