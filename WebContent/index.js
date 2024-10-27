// derived from Project 1 API example: https://github.com/UCI-Chenli-teaching/cs122b-project1-api-example/blob/main/WebContent/index.js

/**
 * Retrieve parameter from obtained request URL, matched by parameter name
 * @param target
 * @returns {string|null}
 */
function getParameterByName(target) {
    let url = window.location.href; // retrieve request URL

    target = target.replace(/[\[\]]/g, "\\$&"); // encode target parameter name to URL encoding

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"), results = regex.exec(url); // use regular expression to find matched parameter value

    if (!results) {
        return null;
    }

    if (!results[2]) {
        return '';
    }

    return decodeURIComponent(results[2].replace(/\+/g, " ")); // return decoded parameter value
}

// handles movie list table
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie list table from resultData");
    console.log("resultData: ", resultData);

    let movieListTableBodyElement = jQuery("#movie_table_body"); // find empty table body by id "movie_table_body"
    movieListTableBodyElement.empty(); // clear any existing content in case of refresh

    // iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        // concatenate HTML tags with resultData JSON object
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            "</a>" +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>";
        let genreNames = resultData[i]["movie_genre"].split(", ");
        let genreIds = resultData[i]["genre_ids"].split(", ");
        for (let j = 0; j < genreNames.length; j++) {
            rowHTML += '<a href="index.html?genre=' + genreIds[j] + '">' + genreNames[j] + '</a>';

            if (j < genreNames.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</th>";
        rowHTML += "<th>";
        let starNames = resultData[i]["movie_star"].split(", ");
        let starIds = resultData[i]["star_ids"].split(", ");
        for (let j = 0; j < starNames.length; j++) {
            rowHTML += '<a href="single-star.html?id=' + starIds[j] + '">' + starNames[j] + '</a>';

            if (j < starNames.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        movieListTableBodyElement.append(rowHTML);
    }

    updatePaginationControls();
}

function updatePaginationControls() {
    let currentPage = parseInt(getParameterByName("page")) || 1;
    let moviesPerPage = getParameterByName("n") || "10";
    let totalResults = 100;

    let totalPages = Math.ceil(totalResults / moviesPerPage);

    let paginationControls = jQuery("#pagination-controls");
    paginationControls.empty();

    if (currentPage > 1) {
        paginationControls.append('<button class="btn btn-primary prev-button" id="prev-button" type="button">← Prev</button>');
    }

    if (currentPage < totalPages) {
        paginationControls.append('<button class="btn btn-primary next-button" id="next-button" type="button">Next →</button>');
    }

    jQuery("#prev-button").on("click", function() {
        navigateToPage(currentPage - 1);
    });

    jQuery("#next-button").on("click", function() {
        navigateToPage(currentPage + 1);
    });
}

function navigateToPage(newPage) {
    let url = new URL(window.location.href);

    url.searchParams.set("page", newPage);
    window.location.href = url.toString();
}

// get parameter(s) for alphanumeric browse
let prefix = getParameterByName("prefix") || "";

// get parameter(s) for genre browse
let genreId = getParameterByName("genre") || "";

// get parameter(s) for search
let title = getParameterByName("title") || "";
let year = getParameterByName("year") || "";
let director = getParameterByName("director") || "";
let starName = getParameterByName("star") || "";

// get parameter(s) for sort
let moviesPerPage = getParameterByName("n") || "";
let sortBy = getParameterByName("sort") || "";
let page = getParameterByName("page") || "1"; // Default to page 1

let ajaxURL = "api/movielist";
if (prefix) {
    // create ajax URL for alphanumeric browse

    ajaxURL += "?prefix=" + encodeURIComponent(prefix);
}
else if (genreId) {
    // create ajax URL for genre browse

    ajaxURL += "?genre=" + encodeURIComponent(genreId);
}
else if (title || year || director || starName) {
    // create ajax URL for search

    let searchParams = [];

    if (title) {
        searchParams.push("title=" + encodeURIComponent(title));
    }

    if (year) {
        searchParams.push("year=" + encodeURIComponent(year));
    }

    if (director) {
        searchParams.push("director=" + encodeURIComponent(director));
    }

    if (starName) {
        searchParams.push("star=" + encodeURIComponent(starName));
    }

    if (searchParams.length > 0) {
        ajaxURL += "?" + searchParams.join("&");
    }
}
else if (moviesPerPage || sortBy) {
    // create ajax URL for update

    let updateParams = [];

    if (moviesPerPage) {
        updateParams.push("n=" + encodeURIComponent(moviesPerPage));
    }

    if (sortBy) {
        updateParams.push("sort=" + encodeURIComponent(sortBy));
    }

    if (updateParams.length > 0) {
        ajaxURL += "?" + updateParams.join("&");
    }
}

if (page) {
    ajaxURL += (ajaxURL.includes("?") ? "&" : "?") + "page=" + encodeURIComponent(page);
}

console.log("Making Ajax request to: ", ajaxURL);

// makes HTTP GET request; upon success, uses callback function handleMovieListResult()
jQuery.ajax({
    dataType: "json", // set return data type
    method: "GET", // set request method
    url: ajaxURL, // set request URL as mapped by MovieListServlet
    success: (resultData) => handleMovieListResult(resultData) // set callback function to handle returned data from MovieListServlet
});