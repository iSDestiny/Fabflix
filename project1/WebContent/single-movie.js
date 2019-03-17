function handleMovieResult(resultData) {
	createMovieDetails(resultData);
}

function createMovieDetails(data) {
	var movieDetails = jQuery('#movie_body');
	var backbtn = document.querySelector('#backbtn');
	for (var i = 0; i < data.length; ++i) {
		var rowHTML = '<div class="col-12">';
		rowHTML += '<div class="card mt-3 bg-dark">';
		rowHTML += '<div class="card-body">';
		rowHTML += '<h2 class="card-title">' + data[i]['movie_title'] + ' (' + data[i]['movie_year'] + ')' + '</h5>';
		rowHTML += '<p class="card-text mb-1"><strong>ID</strong>: ' + data[i]['movie_id'] + '</p>';
		rowHTML += '<p class="card-text mb-1"><strong>Director</strong>: ' + data[i]['movie_director'] + '</p>';

		rowHTML +=
			'<p class="card-text mb-1"><strong>Rating</strong>: ' +
			'<i class="far fa-star"></i> ' +
			data[i]['movie_rating'] +
			'</p>';

		rowHTML += '<p class="card-text mb-1"><strong>Genre(s)</strong>: ';
		data[i]['movie_genres'].forEach(function(genre) {
			rowHTML += genre + ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(',')) + '</p>';

		rowHTML += '<p class="card-text mb-1"><strong>Stars</strong>: ';
		data[i]['movie_stars'].forEach(function(star) {
			rowHTML += '<a href="single-star.html?id=' + star['star_id'] + '">' + star['star_name'] + '</a>';
			rowHTML += ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(',')) + '</p>';

		rowHTML +=
			'<a href="cart.html?movie_id=' +
			data[i]['movie_id'] +
			'&quantity=1&update=false" class="btn btn-danger mt-2"' +
			'>Add to cart</a>';

		rowHTML += '</div></div></div>';
		movieDetails.append(rowHTML);
	}

	backbtn.addEventListener('click', function() {
		var movieListURL = data[0]['movie_list_url'];
		if (movieListURL == null) {
			window.location.href =
				window.location.protocol +
				'//' +
				window.location.host +
				'/' +
				window.location.pathname +
				'?limit=10&sort=ratingdesc&page=1';
		} else {
			var url_new = new URL(window.location.toString());
			url_new.search = movieListURL.substring(movieListURL.indexOf("?"));
			url_new.pathname = "project1/" + movieListURL.substring(0, movieListURL.indexOf("?"));
			window.location.href = url_new.toString();
		}
	});
}

function getParameterByName(target) {
	// Get request URL
	let url = window.location.href;
	// Encode target parameter name to url encoding
	target = target.replace(/[\[\]]/g, '\\$&');

	// Use regular expression to find matched parameter value
	let regex = new RegExp('[?&]' + target + '(=([^&#]*)|&|#|$)'),
		results = regex.exec(url);
	if (!results) return null;
	if (!results[2]) return '';

	// Return the decoded parameter value
	return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

var movieId = getParameterByName('id');

jQuery.ajax({
	dataType : 'json', // Setting return data type
	method   : 'GET', // Setting request method
	url      : 'api/single-movie?id=' + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
	success  : (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
