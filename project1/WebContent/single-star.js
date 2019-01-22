function handleStarResult(resultData) {
	createStarDetails(resultData);
}

function createStarDetails(data) {
    var movieDetails = jQuery("#star_body");
    
    console.log("in function");
	
	for(var i = 0; i < data.length; ++i)
	{
        console.log("in loop");
		var rowHTML =  '<div class="col-12">';
		rowHTML += "<div class=\"card bg-light\">";
		rowHTML += "<div class=\"card-body\">";
		rowHTML += "<h2 class=\"card-title\">" + data[i]["star_name"] + "</h5>";
		rowHTML += '<p class="card-text mb-1"><strong>ID</strong>: ' + data[i]["star_id"] + "</p>"; 
		rowHTML += '<p class="card-text mb-1"><strong>Date of Birth</strong>: ' + 
			data[i]["star_dob"] + "</p>";

		rowHTML += '<p class="card-text mb-1"><strong>Starred in</strong>: ';
		data[i]["stars_in"].forEach(function(star){
			rowHTML += '<a href="single-movie.html?id=' + star["movie_id"] + '">' + star["movie_title"] + "</a>";
			rowHTML += ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(",")) + "</p>";
		
		rowHTML += '<button type="button" class="btn btn-success mt-2">Add to cart</button>'
		
		rowHTML += "</div></div></div>";
		movieDetails.append(rowHTML);
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

var starId= getParameterByName('id');


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});