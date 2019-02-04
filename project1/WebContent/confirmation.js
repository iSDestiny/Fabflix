function handleResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson);
}


$.post(
        "api/confirmation",
        (resultDataString) => handleResult(resultDataString)
);