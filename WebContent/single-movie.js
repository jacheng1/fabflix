/**
 * Retrieve parameter from obtained request URL, matched by parameter name
 * @param target
 * @returns {string|null}
 */
function getParameterByName(target) {
    let url = window.location.href; // retrieve request URL

    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"), results = regex.exec(url); // use regular expression to find matched parameter value

    if (!results) {
        return null;
    }

    if (!results[2]) {
        return '';
    }

    return decodeURIComponent(results[2].replace(/\+/g, " ")); // return decoded parameter value
}

/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleSingleMovieResult(resultData) {
    console.log("handleSingleMovieResult: populating single movie info from resultData");

    let movieInfoElement = jQuery("#movie_info"); // find empty h3 body movie_info by id, populate it

    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Genre(s): " + resultData[0]["movie_genre"] + "</p>" +
        "<p>Star(s): " + resultData[0]["movie_star"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>");
}

let movieId = getParameterByName("id");

// makes HTTP GET request; upon success, uses callback function singleMovieResult()
jQuery.ajax({
    dataType: "json", // set return data type
    method: "GET", // set request method
    url: "api/single-movie?id=" + movieId, // set request URL as mapped by SingleMovieServlet
    success: (resultData) => handleSingleMovieResult(resultData) // set callback function to handle returned data from SingleMovieServlet
});