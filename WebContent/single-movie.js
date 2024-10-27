// derived from Project 1 API example: https://github.com/UCI-Chenli-teaching/cs122b-project1-api-example/blob/main/WebContent/single-star.js

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

    let starNames = resultData[0]["movie_star"].split(", ");
    let starIds = resultData[0]["star_ids"].split(", ");

    let starsHTML = "";

    for (let i = 0; i < starNames.length; i++) {
        starsHTML += '<a href="single-star.html?id=' + starIds[i] + '">' + starNames[i] + '</a>';

        if (i < starNames.length - 1) {
            starsHTML += ", ";
        }
    }

    // append HTML to movieInfoElement
    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Genre(s): " + resultData[0]["movie_genre"] + "</p>" +
        "<p>Star(s): " + starsHTML + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>");
}

let movieId = getParameterByName("id");


// makes HTTP GET request; upon success, uses callback function handleSingleMovieResult()
jQuery.ajax({
    dataType: "json", // set return data type to JSON
    method: "GET", // set request method to GET
    url: "api/single-movie?id=" + movieId, // set request URL as mapped by SingleMovieServlet
    success: (resultData) => handleSingleMovieResult(resultData) // set callback function to handle returned data from SingleMovieServlet
});


document.addEventListener('DOMContentLoaded', function ()
{
    const addToCartButton = document.getElementById('add-to-cart-button');

    function handleAddToCart(cartEvent) {
        console.log("submit cart form");
        /**
         * When users click the submit button, the browser will not direct
         * users to the url defined in HTML form. Instead, it will call this
         * event handler when the event is triggered.
         */
        cartEvent.preventDefault();

        let
            movie_info = $('#movie')
        $.ajax("api/single-movie", {
            method: "POST",
            data: {movie_id : movieId},
            success: function(data)  {
                alert('Movie added to cart');
            },
            error: function(error) {
                console.error('ERROR: ', error);
            }
        });

        // clear input form

    }
    addToCartButton.addEventListener('click', handleAddToCart)
});