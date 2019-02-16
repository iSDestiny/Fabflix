function handleResult(data) {
	var confirmation_content = jQuery('#confirmation_content');
	for (var i = 0; i < data.length; ++i) {
		var rowHTML = '<div class="col-12">';
		rowHTML += '<div class="card mt-3 bg-dark">';
		rowHTML += '<div class="card-body">';
		rowHTML += '<h5 class="card-title">' + data[i]['movie_title'] + '</h5>';
		rowHTML += '<p class="card-text mb-1"><strong>Quantity</strong>: ' + data[i]['quantity'] + '</p>';
		rowHTML += '<p class="card-text mb-1"><strong>Sale IDs</strong>: ';
		data[i]['sale_ids'].forEach(function(id) {
			rowHTML += id + ', ';
		});
		rowHTML = rowHTML.slice(0, rowHTML.lastIndexOf(',')) + '</p>';

		rowHTML += '</div></div></div>';
		confirmation_content.append(rowHTML);
	}
}

jQuery.ajax({
	dataType : 'json', // Setting return data type
	method   : 'GET', // Setting request method
	url      : 'api/confirmation', // Setting request url, which is mapped by StarsServlet in Stars.java
	success  : (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
