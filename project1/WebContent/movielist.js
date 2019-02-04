function handleMovieListResult(resultData) {
	createMovieCard(resultData);
}

function createMovieCard(data) {
	var movieCardCollection = jQuery("#movie_card_collection");
	
	for(var i = 0; i < Math.min(20, data.length); ++i)
	{
		var rowHTML =  '<div class="col-md-6 col-lg-4 mb-3">';
		rowHTML += "<div class=\"card bg-light\">";
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
		
		rowHTML += '<a href="cart.html?movie_id=' + data[i]["movie_id"] + '&quantity=1&update=false" class="btn btn-success mt-2"'
			+ '>Add to cart</a>';
			
//			'<button type="submit" class="btn btn-success mt-2"' +  ' formaction= "localhost:8080/project1/cart.html?movie_id=' 
//		+ data[i]["movie_id"] + '&quantity=1"' + ">Add to cart</button>";
		
		rowHTML += "</div></div></div>";
		movieCardCollection.append(rowHTML);
	}
	
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});




