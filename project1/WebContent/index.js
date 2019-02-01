function handleMovieListResult(resultData) {
	createMovieCard(resultData);
}

function createMovieCard(data) {
	var movieCardCollection = jQuery("#movie_card_collection");
	
	for(var i = 0; i < Math.min(20, data.length); ++i)
	{
		var rowHTML =  '<div class="col mb-3 d-flex align-items-stretch">';
		rowHTML += "<div class=\"card h-100 bg-light\">";
		rowHTML += "<div class=\"card-body\">";
		rowHTML += "<h5 class=\"card-title\">" + '<a href="single-movie.html?id=' + 
			data[i]["movie_id"] + '">' + data[i]["movie_title"] + "</a>" +
			" (" + data[i]["movie_year"] + ")" + "</h5>";
		rowHTML += '<p class="card-text mb-1"><strong>Director</strong>: ' + 
			data[i]["movie_director"] + "</p>";

		rowHTML += '<p class="card-text mb-1"><strong>Rating</strong>: ' + 
			'<i class="far fa-star"></i> ' + data[i]["movie_rating"] + "</p>";

		rowHTML += '<p class="card-text mb-1"><strong>Genre(s)</strong>: '
		data[i]["movie_genres"].forEach(function(genre){
			rowHTML += genre + ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(",")) + "</p>";
		
		rowHTML += '<p class="card-text mb-1"><strong>Stars</strong>: ';
		data[i]["movie_stars"].forEach(function(star){
			rowHTML += '<a href="single-star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a>";
			rowHTML += ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(",")) + "</p>";
		
		rowHTML += '<button type="button" class="btn btn-success mt-2">Add to cart</button>'
		
		rowHTML += '</div></div></div><div class="w-100"></div>';
		movieCardCollection.append(rowHTML);
	}
	
}

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

// var limit = getParameterByName("limit");
// var page = getParameterByName("page");
// var sort = getParameterByName("sort");
// var title = getParameterByName("title");

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies" + window.location.search, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});




