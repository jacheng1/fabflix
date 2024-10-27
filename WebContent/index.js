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

function buildAjaxURL() {
    let genreId = getParameterByName("genre") || "";
    let prefix = getParameterByName("prefix") || "";
    let title = getParameterByName("title") || "";
    let year = getParameterByName("year") || "";
    let director = getParameterByName("director") || "";
    let starName = getParameterByName("star") || "";
    let moviesPerPage = getParameterByName("n") || "10"; // default to 10 if not set
    let sortBy = getParameterByName("sort") || "";
    let page = getParameterByName("page") || "1"; // default to 1 if not set

    let ajaxURL = "api/movielist?";

    // append all non-empty filters to Ajax URL
    if (genreId) {
        ajaxURL += `genre=${encodeURIComponent(genreId)}&`;
    }
    if (prefix) {
        ajaxURL += `prefix=${encodeURIComponent(prefix)}&`;
    }
    if (title) {
        ajaxURL += `title=${encodeURIComponent(title)}&`;
    }
    if (year) {
        ajaxURL += `year=${encodeURIComponent(year)}&`;
    }
    if (director) {
        ajaxURL += `director=${encodeURIComponent(director)}&`;
    }
    if (starName) {
        ajaxURL += `star=${encodeURIComponent(starName)}&`;
    }
    ajaxURL += `n=${encodeURIComponent(moviesPerPage)}&sort=${encodeURIComponent(sortBy)}&page=${encodeURIComponent(page)}`;

    return ajaxURL;
}

// execute the AJAX request with all current filters when the page loads
function fetchMovies() {
    let ajaxURL = buildAjaxURL();
    console.log("Making Ajax request to: ", ajaxURL);

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: ajaxURL,
        success: (resultData) => handleMovieListResult(resultData)
    });
}

document.addEventListener("DOMContentLoaded", function () {
    jQuery(".update-button").on("click", function (event) {
        event.preventDefault();

        // retrieve selected values from movies per page and sort by dropdowns
        let moviesPerPage = jQuery("#n").val();
        let sortBy = jQuery("#sort").val();

        let url = new URL(window.location.href); // update the URL parameters based on selections

        if (moviesPerPage) {
            url.searchParams.set("n", moviesPerPage); // set if selected
        } else {
            url.searchParams.delete("n"); // remove if not selected
        }

        if (sortBy) {
            url.searchParams.set("sort", sortBy); // set if selected
        } else {
            url.searchParams.delete("sort"); // remove if not selected
        }

        url.searchParams.set("page", "1"); // reset to first page

        window.history.replaceState(null, "", url.toString()); // update the browser URL without reloading the page

        fetchMovies();
    });
});

fetchMovies();