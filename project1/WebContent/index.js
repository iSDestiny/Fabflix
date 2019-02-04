/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);
    console.log(resultDataJson["id"]);
    console.log(resultDataJson["cart"]);

    // show the session information 
//     $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
//     $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
}

$.ajax({
    type: "POST",
    url: "api/index",
    success: (resultDataString) => handleSessionData(resultDataString)
});