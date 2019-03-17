var inputs = document.querySelectorAll('#add_movie_form input');

$('#add_movie_form').submit(function(event) {
	event.preventDefault();
	add_movie_function();
});

function add_movie_function() {
	var search_url = new URL(window.location.toString());
	search_url.search = '?';
	var title = inputs[0].value;
	var year = inputs[1].value;
	var director = inputs[2].value;
	var genre = inputs[3].value;
	var star_name = inputs[4].value;
	var dob = inputs[5].value;

	var params =
		'star_name=' +
		star_name +
		'&dob=' +
		dob +
		'&title=' +
		title +
		'&year=' +
		year +
		'&director=' +
		director +
		'&genre=' +
		genre;

	$.getJSON('../api/addmovie?' + params, (resultData) => {
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
