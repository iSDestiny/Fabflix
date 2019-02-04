var search_url = new URL(window.location.toString());
var search_inputs = document.querySelectorAll("#search_form input");
var search_submit = document.querySelector("#search_submit_button");

$( "#search_form" ).submit(function( event ) {
    event.preventDefault();
    search_function();
});

search_submit.addEventListener("click", function(){
    search_function();
})

function search_function() {
    search_url.search = "?";
    var search_params = new URLSearchParams(search_url.search);
    var title = search_inputs[0].value;
    var year = search_inputs[1].value;
    var director = search_inputs[2].value;
    var star = search_inputs[3].value;
    search_params.set("sort", "titleasc");
    search_params.set("limit", "10");
    search_params.set("page", "1");
    if (title !== "") {
        search_params.set("title", title);
    }
    else {
        search_params.delete("title");
    }
    if (year !== "") {
        search_params.set("year", year);
    }
    else {
        search_params.delete("year");
    }
    if (director !== "") {
        search_params.set("director", director);
    }
    else {
        search_params.delete("director");
    }
    if (star !== "") {
        search_params.set("star", star);
    }
    else {
        search_params.delete("star");
    }
    search_url.pathname = "project1/movielist.html";
    search_url.search = search_params.toString();
    window.location.href = search_url.toString();
}
