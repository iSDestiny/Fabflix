function handleShoppingCart(resultData) {
	createCartCard(resultData);
}

function createCartCard(data) {
	var cartCardCollection = jQuery("#cart_card_collection");
	
	for(var i = 0; i < data.length; ++i)
	{
		var rowHTML =  '<div class="col-md-6 col-lg-4 mb-3">';
		rowHTML += "<div class=\"card bg-light\">";
		rowHTML += "<div class=\"card-body\">";
		rowHTML += "<h5 class=\"card-title\">" + data[i]["movie_title"] + "</h5>";

		rowHTML += '<p class="card-text mb-1"><strong>Quantity</strong>: ' + 
			data[i]["quantity"] + "</p>";
		
		rowHTML += "</div></div></div>";
		cartCardCollection.append(rowHTML);
	}
}

const urlParams = new URLSearchParams(window.location.search);
const movie_id = urlParams.get('movie_id');
const quantity = urlParams.get('quantity');

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart?movie_id=" + movie_id + "&quantity=" + quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleShoppingCart(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});