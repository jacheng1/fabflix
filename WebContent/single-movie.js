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
    }
    addToCartButton.addEventListener('click', handleAddToCart)
});

const handleLookup = (query, doneCallback) => {
    // Only trigger autocomplete for >=3 characters
    if (query.length < 3) {
        return;
    }

    console.log("Autocomplete initiated.");

    const cachedData = localStorage.getItem(query);
    if (cachedData) {
        console.log("Using cached results.");

        const parsedData = JSON.parse(cachedData);

        // Ensure cached data is correctly formatted
        const formattedSuggestions = parsedData.map((item) => ({
            value: String(item.value || item), // Enforce string value
            data: item.data || null
        }));

        handleLookupAjaxSuccess(formattedSuggestions, doneCallback);

        return;
    }

    console.log("Sending Ajax request to server...");

    jQuery.ajax({
        method: "GET",
        url: `api/autocomplete?full-text=${encodeURIComponent(query)}`,
        success: (data) => {
            localStorage.setItem(query, JSON.stringify(data));
            handleLookupAjaxSuccess(data, doneCallback);
        },
        error: (error) => {
            console.error("Autocomplete error:", error);
        },
    });
};

const handleLookupAjaxSuccess = (data, doneCallback) => {
    console.log("Raw autocomplete suggestion(s):", data);

    const suggestions = data.map((item) => ({
        value: String(item.value || item),
        data: item.data || null
    }));

    console.log("Formatted autocomplete suggestion(s):", suggestions);

    doneCallback({ suggestions });
};

const handleSelectSuggestion = (suggestion) => {
    window.location.href = `single-movie.html?id=${suggestion.data.id}`;
};

const handleSearch = (event) => {
    const query = $("#autocomplete").val();
    if (query.trim() === "") {
        alert("Please enter some text for searching.");
        event.preventDefault();
        return;
    }

    console.log("Full-text search query:", query);
};

$(document).ready(() => {
    $("#autocomplete").autocomplete({
        lookup: (query, doneCallback) => handleLookup(query, doneCallback),
        onSelect: (suggestion) => handleSelectSuggestion(suggestion),
        deferRequestBy: 300
    });

    $("#search-form").on("submit", handleSearch);
});