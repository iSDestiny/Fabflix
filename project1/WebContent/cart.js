var url = new URL(window.location.toString());
const urlParams = new URLSearchParams(window.location.search);
const movie_id = urlParams.get('movie_id');
const quantity = urlParams.get('quantity');
const update = urlParams.get('update');
var update_buttons;
var update_inputs;

var number = 0;
function handleShoppingCart(resultData) {
	createCartCard(resultData);
	update_buttons = document.querySelectorAll(".update_button");
	update_inputs = document.querySelectorAll(".quantity");
	for(var i = 0; i < update_inputs.length; ++i)
	{
		var value = update_inputs[i].value;
		var name = update_inputs[i].name;
		update_buttons[i].addEventListener("click", function(){
			var quantity = update_inputs[parseInt(this.name)].value;
			
			if(quantity === "" || parseInt(quantity) < 0)
			{
				return false;
			}
			
			var id = update_inputs[parseInt(this.name)].name;
			var url_new = new URL(window.location.toString());
			var urlParams_new = new URLSearchParams(url_new.search);
			urlParams_new.set("quantity", quantity);
			urlParams_new.set("movie_id", id);
			urlParams_new.set("update", "true");
			url_new.pathname = "project1/cart.html";
			url_new.search = urlParams_new.toString();
			window.location.href = url_new.toString();
		});
	}
}

function createCartCard(data) {
	var cartCardCollection = jQuery("#cart_card_collection");
	
	for(var i = 0; i < data.length; ++i)
	{
		var rowHTML =  '<div class="col-md-6 col-lg-4 mb-3">';
		rowHTML += "<div class=\"card bg-light\">";
		rowHTML += "<div class=\"card-body\">";
		rowHTML += "<h5 class=\"card-title\">" + data[i]["movie_title"] + "</h5>";
		
		rowHTML += '<div><input type="number" class="quantity" name="' + data[i]["movie_id"] + '"placeholder="Quantity" min="0" step="1"></input></div>';
		
		rowHTML += '<button name="' + i + '" class="update_button btn btn-success mt-2"' + '>Update</button>';
			
		rowHTML += '<p class="card-text mb-1"><strong>Quantity: </strong>' + 
			data[i]["quantity"] + "</p>";
		
		rowHTML += '<div><a href="cart.html?movie_id=' + data[i]["movie_id"] + '&quantity=0&update=false" class="btn btn-danger mt-2"'
		+ '>Remove</a></div>';
		
		rowHTML += "</div></div></div>";
		cartCardCollection.append(rowHTML);
	}
}

function updateQuantity()
{
	var url_new = new URL(window.location.toString());
	var urlParams_new = new URLSearchParams(url_new.search);
	urlParams_new.set("quantity", quantity);
	urlParams_new.set("movie_id", id);
	urlParams_new.set("update", "true");
	url_new.pathname = "project1/cart.html";
	url_new.search = urlParams_new.toString();
	window.location.href = url_new.toString();
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart?movie_id=" + movie_id + "&quantity=" + quantity + "&update=" + update,
    success: (resultData) => handleShoppingCart(resultData) 
});