var search_url = new URL(window.location.toString());
var search_form = document.querySelector("#quick_search")
var quick_search_submit = document.querySelector("#quick_search_submit")

quick_search_submit.addEventListener("click", function(){
    quick_search();
})

search_form.submit = quick_search;

document.querySelector("#quick_search input").addEventListener('keypress', function(event) {
    if (event.keyCode == 13) {
        event.preventDefault();
        quick_search();
    }
});

function quick_search() {
    search_url.search = "?";
    var search_params = new URLSearchParams(search_url.search);
    var search_value = search_form["search_query"].value;
    search_params.set("sort", "titleasc");
    search_params.set("limit", "10");
    search_params.set("page", "1");
    if (search_value !== "") {
        search_params.set("title", search_value);
    }
    else {
        search_params.delete("title");
    }
    search_url.pathname = "project1/movielist.html";
    search_url.search = search_params.toString();
    window.location.href = search_url.toString();
}
