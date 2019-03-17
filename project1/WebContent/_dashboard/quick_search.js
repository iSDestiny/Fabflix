var cache_results = []

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	
	// TODO: if you wa0nt to check past query results first, you can do it here
	var len = cache_results.length;
	for (var i = 0; i < len; i++)
	{
		if (cache_results[i].search.localeCompare(query) == 0)
			{
				console.log("Using cache")
				handleLookupAjaxSuccess(cache_results[i].arr, query, doneCallback)
				return;
			}
	}
	
	jQuery.ajax({
		"method": "GET",
		"url": "../api/title-suggestions?query=" + escape(query),
		"success": function(data) {
			console.log("Using ajax")
			handleLookupAjaxSuccess(data, query, doneCallback) 
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	//var jsonData = JSON.parse(data);
	var jsonData = jQuery.parseJSON(JSON.stringify(data));
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	cache_results.push({
		search: query,
		arr: data
	});
	doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	var new_url = new URL(window.location.toString())
	new_url.search = '?';
	var search_params = new URLSearchParams(new_url.search);
	new_params.set('id', suggestion["data"]["id"]);
	new_url.pathname = 'project1/single-movie.html';
	new_url.search = new_params.toString();
//	window.location.replace("single-movie.html?id=" + suggestion["data"]["id"])
	window.location.href = new_url.toString();
}

$("#autocomplete").autocomplete({
    lookup: function (query, doneCallback) {
    		if (query.length > 2){
    		handleLookup(query, doneCallback)}
    },
    onSelect: function(suggestion) {
    	handleSelectSuggestion(suggestion)
    },
    deferRequestBy: 300,
    
    // TODO: add other parameters, such as minimum characters
});

function handleNormalSearch(query) {
	quick_search();
}

var search_url = new URL(window.location.toString());
var search_form = document.querySelector('#quick_search');
var quick_search_submit = document.querySelector('#quick_search_submit');

quick_search_submit.addEventListener('click', function() {
	quick_search();
});

search_form.submit = quick_search;

document.querySelector('#quick_search input').addEventListener('keypress', function(event) {
	if (event.keyCode == 13) {
		event.preventDefault();
		quick_search();
	}
});

function quick_search() {
	search_url.search = '?';
	var search_params = new URLSearchParams(search_url.search);
	var search_value = search_form['search_query'].value;
	search_params.set('sort', 'titleasc');
	search_params.set('limit', '10');
	search_params.set('page', '1');
	if (search_value !== '') {
		search_params.set('title', search_value);
	} else {
		search_params.delete('title');
	}
	search_url.pathname = 'project1/movielist.html';
	search_url.search = search_params.toString();
	window.location.href = search_url.toString();
}
