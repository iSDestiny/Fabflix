var inputs = document.querySelectorAll('#add_star_form input');

$('#add_star_form').submit(function(event) {
	event.preventDefault();
	add_star_function();
});

function add_star_function() {
	var search_url = new URL(window.location.toString());
	search_url.search = '?';
	var star_name = inputs[0].value;
	var dob = inputs[1].value;
	var params = 'star_name=' + star_name + '&dob=' + dob;
	$.getJSON('../api/addstar?' + params, (resultData) => {
		var success_message_div = document.querySelector('#success_message');
		var message_element = document.createElement('p');
		if (resultData['success'] === 'true') {
			message_element.innerText = 'Success! ' + resultData['message'];
		} else {
			message_element.innerText = 'Failure! ' + resultData['message'];
		}
		success_message_div.innerHTML = '';
		success_message_div.appendChild(message_element);
		success_message_div.classList.add('border', 'border-light', 'mb-2');
	});
}
