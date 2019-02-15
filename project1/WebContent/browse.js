var url = new URL(window.location.toString());
var search_params = new URLSearchParams(url.search);

function handleBrowseResult(resultData) {
	var browseContent = jQuery('#browse_content');
	for (var i = 0; i < resultData.length; ++i) {
		var entry =
			'<div class="col-6 col-md-4 col-lg-3 col-xl-2 mb-2 d-flex justify-content-center"><button type="button" class="btn btn-danger">' +
			resultData[i] +
			'</button></div>';
		browseContent.append(entry);
	}

	var genreButtons = document.querySelectorAll('#browse_content button');
	var letterButtons = document.querySelectorAll('#letter_content button');

	addLetterEvents(letterButtons);
	addGenreEvents(genreButtons);
}

jQuery.ajax({
	dataType : 'json', // Setting return data type
	method   : 'GET', // Setting request method
	url      : 'api/browse', // Setting request url, which is mapped by StarsServlet in Stars.java
	success  : (resultData) => handleBrowseResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

var titleSearchParams = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
var letterContent = jQuery('#letter_content');

for (var i = 0; i < titleSearchParams.length; ++i) {
	var entry = '<button type="button" class="btn btn-danger">' + titleSearchParams[i] + '</button>';
	letterContent.append(entry);
}

function addLetterEvents(letterButtons) {
	for (var i = 0; i < letterButtons.length; ++i) {
		letterButtons[i].addEventListener('click', function() {
			var letter = this.textContent;
			search_params.set('letter', letter);
			search_params.set('page', '1');
			search_params.set('limit', '10');
			search_params.set('sort', 'titleasc');
			url.search = search_params.toString();
			url.pathname = 'project1/movielist.html';
			window.location.href = url.toString();
		});
	}
}

function addGenreEvents(genreButtons) {
	for (var i = 0; i < genreButtons.length; ++i) {
		console.log(genreButtons[i].textContent);
		genreButtons[i].addEventListener('click', function() {
			var genre = this.textContent;
			search_params.set('genre', genre);
			search_params.set('page', '1');
			search_params.set('limit', '10');
			search_params.set('sort', 'titleasc');
			url.search = search_params.toString();
			url.pathname = 'project1/movielist.html';
			window.location.href = url.toString();
		});
	}
}
