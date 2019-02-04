var sort = document.querySelector("#sort");
var limit = document.querySelector("#limit")
var prev = document.querySelector("#prev");
var next = document.querySelector("#next");
var current_page_element = document.querySelector("#current_page")
var url = new URL(window.location.toString());
var search_params = new URLSearchParams(url.search);
var current_page = 0;

function handleMovieListResult(resultData) {
	createMovieCard(resultData);
	current_page = parseInt(search_params.get("page"));
	var movieCardCollection = jQuery("#movie_card_collection");
	if(current_page === 1)
	{
		prev.disabled = true;
		prev.classList.add("disabled");
	}
	
	if(resultData.length <= search_params.get("limit"))
	{
		next.disabled = true;
		next.classList.add("disabled");
	}

	if(resultData.length === 0)
	{
		movieCardCollection.append('<h3 class="text-center d-flex justify-content-center mx-auto mt-2 mb-4">No Results Found</h3>');
	}
	current_page_element.textContent = current_page.toString();
}

function createMovieCard(data) {
	var movieCardCollection = jQuery("#movie_card_collection");
	
	for(var i = 0; i < data.length; ++i)
	{
		var rowHTML =  '<div class="col mb-3 d-flex align-items-stretch">';
		rowHTML += "<div class=\"card h-100 bg-dark\">";
		rowHTML += "<div class=\"card-body\">";
		rowHTML += "<h5 class=\"card-title\">" + '<strong><a href="single-movie.html?id=' + 
			data[i]["movie_id"] + '">' + data[i]["movie_title"] + "</a></strong>" +
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
		
		rowHTML += '</div></div></div><div class="w-100"></div>';
		movieCardCollection.append(rowHTML);
	}
	
}

function setInitialLimitOption()
{
	var limitValue = search_params.get("limit");
	if(limitValue === "5")
	{
		limit.selectedIndex = "0";
	}
	else if(limitValue === "10")
	{
		limit.selectedIndex = "1";
	}
	else if(limitValue === "25")
	{
		limit.selectedIndex = "2";
	}
	else if(limitValue === "50")
	{
		limit.selectedIndex = "3";
	}
	else if(limitValue === "75")
	{
		limit.selectedIndex = "4";
	}
	else 
	{
		limit.selectedIndex = "5";
	}
}

function setInitialSortOption()
{
	var sortValue = search_params.get("sort");
	if(sortValue === "titleasc")
	{
		sort.selectedIndex = "0";
	}
	else if(sortValue === "titledesc")
	{
		sort.selectedIndex = "1";
	}
	else if(sortValue === "yearasc")
	{
		sort.selectedIndex = "2";
	}
	else if(sortValue === "yeardesc")
	{
		sort.selectedIndex = "3";
	}
	else if(sortValue === "ratingasc")
	{
		sort.selectedIndex = "4";
	}
	else 
	{
		sort.selectedIndex = "5";
	}
}

setInitialLimitOption();
setInitialSortOption();

sort.addEventListener("change", function(){	
	var option = parseInt(this.options[this.selectedIndex].value);
	if(option === 0)
	{
		search_params.set("sort", "titleasc");
	}
	else if(option === 1)
	{

		search_params.set("sort", "titledesc");
	}
	else if(option === 2)
	{
		search_params.set("sort", "yearasc");
	}
	else if(option === 3)
	{
		search_params.set("sort", "yeardesc");
	}
	else if(option === 4)
	{
		search_params.set("sort", "ratingasc");
	}
	else
	{
		search_params.set("sort", "ratingdesc")
	}
	url.search = search_params.toString();
	window.location.href = url.toString();
})

limit.addEventListener("change", function(){
	var option = this.options[this.selectedIndex].value;
	search_params.set("limit", option);
	url.search = search_params.toString();
	window.location.href = url.toString();
})

prev.addEventListener("click", function(){
	search_params.set("page", (current_page-1).toString());
	url.search = search_params.toString();
	window.location.href = url.toString();
})

next.addEventListener("click", function(){
	search_params.set("page", (current_page+1).toString());
	url.search = search_params.toString();
	window.location.href = url.toString();
})

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies" + window.location.search, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});