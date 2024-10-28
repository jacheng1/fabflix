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

function handleMovieListResult(resultData) {
    let movieListTableBodyElement = jQuery("#movie_table_body");
    movieListTableBodyElement.empty();
    console.log("Iterating through movielist result");
    // iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr class='item-row' data-movie-title=" + resultData[i]['movie_title'] + " data-movie-id='"+ resultData[i]['movie_id'] +"'>";

        rowHTML += "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '" class="movie-link">'
            + resultData[i]["movie_title"] +
            "</a>" +
            "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>";
        let genreNames = resultData[i]["movie_genre"].split(", ");
        let genreIds = resultData[i]["genre_ids"].split(", ");
        for (let j = 0; j < genreNames.length; j++) {
            rowHTML += '<a href="index.html?genre=' + genreIds[j] + '">' + genreNames[j] + '</a>';
            if (j < genreNames.length - 1) rowHTML += ", ";
        }
        rowHTML += "</td><td>";
        let starNames = resultData[i]["movie_star"].split(", ");
        let starIds = resultData[i]["star_ids"].split(", ");
        for (let j = 0; j < starNames.length; j++) {
            rowHTML += '<a href="single-star.html?id=' + starIds[j] + '" class="star-link">' + starNames[j] + '</a>';
            if (j < starNames.length - 1) rowHTML += ", ";
        }
        rowHTML += "</td><td>"+resultData[i]['movie_rating']+"</td>";
        rowHTML += "<td><button class='add-to-cart-button'>Add To Cart</button></td></tr>";

        movieListTableBodyElement.append(rowHTML);
    }

    jQuery(document).on("click", ".movie-link, .star-link", savePageState); // bind click event for saving movie list page state after all rows are added

    updatePaginationControls(); // update pagination state with each movie list table load
}

function updatePaginationControls() {
    let currentPage = parseInt(getParameterByName("page")) || 1;
    let moviesPerPage = getParameterByName("n") || "10";
    let totalResults = 100;

    let totalPages = Math.ceil(totalResults / moviesPerPage);

    let paginationControls = jQuery("#pagination-controls");
    paginationControls.empty();

    // if there are previous page(s), display prev-button
    if (currentPage > 1) {
        paginationControls.append('<button class="btn btn-primary prev-button" id="prev-button" type="button">← Prev</button>');
    }
    else {
        // else, display prev-button as non-clickable

        paginationControls.append('<button class="btn btn-primary prev-button" id="prev-button" type="button" disabled>← Prev</button>');
    }

    paginationControls.append('<div class="page-number-box" style="display: inline-block; margin: 0 10px; padding: 5px 10px; border-color: #424955 !important; color: #424955 !important; background-color: #181b20 !important;">' +
        'Page ' + currentPage + ' of ' + totalPages + '</div>'); // append page number box between prev-button and next-button

    // if there are leftover page(s), display next-button
    if (currentPage < totalPages) {
        paginationControls.append('<button class="btn btn-primary next-button" id="next-button" type="button">Next →</button>');
    }
    else {
        // else, display next-button as non-clickable

        paginationControls.append('<button class="btn btn-primary next-button" id="next-button" type="button" disabled>Next →</button>');
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

    sessionStorage.setItem("currentPage", newPage);
    savePageState();

    window.location.href = url.toString();
}

function buildAjaxURL(moviesPerPage) {
    // retrieve all possible parameters for Ajax URL
    let genreId = getParameterByName("genre") || "";
    let prefix = getParameterByName("prefix") || "";
    let title = getParameterByName("title") || "";
    let year = getParameterByName("year") || "";
    let director = getParameterByName("director") || "";
    let starName = getParameterByName("star") || "";
    let sortBy = getParameterByName("sort") || "";
    let page = getParameterByName("page") || "1";

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

document.getElementById("result-button").addEventListener("click", function () {
    savePageState();

    let basePath = window.location.origin;
    if (basePath.includes('localhost')) {
        window.location.href = basePath + '/cs122b_project1_war/index.html';
    } else {
        window.location.href = basePath + '/cs122b-project1/index.html';
    }
});

function savePageState() {
    let pageState = {
        genre: getParameterByName("genre") || "",
        prefix: getParameterByName("prefix") || "",
        title: getParameterByName("title") || "",
        year: getParameterByName("year") || "",
        director: getParameterByName("director") || "",
        starName: getParameterByName("star") || "",
        n: getParameterByName("n") || "10",
        sort: getParameterByName("sort") || "",
        page: getParameterByName("page") || "1"
    };

    console.log("savePageState(): Saving page state:", pageState);

    sessionStorage.setItem("movieListPageState", JSON.stringify(pageState));
}

function restorePageState() {
    let savedState = sessionStorage.getItem("movieListPageState");

    if (savedState) {
        let pageState = JSON.parse(savedState);

        // only restore movies-per-page/sort-by settings and URL if there are no existing parameters
        if (!window.location.search) {
            // set URL parameters without reloading if it's the first load
            let url = new URL(window.location.href);

            Object.keys(pageState).forEach(key => {
                if (pageState[key]) {
                    url.searchParams.set(key, pageState[key]);
                } else {
                    url.searchParams.delete(key);
                }
            });

            history.replaceState(null, "", url.toString());
        }

        console.log("restorePageState(): Restoring page state:", pageState);
        fetchMovies();
    } else {
        fetchMovies();
    }
}

// call restorePageState() and fetchMovies() on page load
jQuery(document).ready(() => {
    restorePageState();
    fetchMovies();
});

// execute the AJAX request with all current filters when the page loads
function fetchMovies() {
    let moviesPerPage = getParameterByName("n") || "10";

    let ajaxURL = buildAjaxURL(moviesPerPage);
    console.log("Making Ajax request to: ", ajaxURL);

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: ajaxURL,
        success: (resultData) => handleMovieListResult(resultData),
        error: function(error) {
            console.log(error);
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    jQuery(".update-button").on("click", function (event) {
        event.preventDefault();

        let moviesPerPage = jQuery("#n").val();
        let sortBy = jQuery("#sort").val();

        let url = new URL(window.location.href); // update URL parameters based on selections

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

        window.history.replaceState(null, "", url.toString()); // update browser URL without reloading the page

        savePageState();
        fetchMovies();
    });





});

document.addEventListener('DOMContentLoaded', () => {
    console.log("listening for event");
    function handleAddToCart(button) {
        console.log("handle Change quantity");
        /**
         * When button is pressed then change in quantity will update the session's previousItems attribute
         * users to the url defined in HTyML form. Instead, it will call this
         * event handler when the event is triggered.
         */
        const row = button.closest('tr');
        const movieId = row.getAttribute('data-movie-id');
        const movieTitle = row.getAttribute('data-movie-title');

        $.ajax("api/single-movie", {
            method: "POST",
            data: {movie_id : movieId},
            success: function(response) {
                console.log('updated quantity');
                alert("Added "+ movieTitle + " to cart!");
            },
            error: function(error) {
                console.log("ERROR: " + error);
            }
        });

        // clear input form
    }

    document.querySelector('#movie_table_body').addEventListener('click', (event) => {
        if (event.target.classList.contains('add-to-cart-button')) {
            handleAddToCart(event.target);
        } else {
            console.log("button clicked but not add to cart");
        }
    });
});




function handleAddToCart (button) {

    console.log("add movie to cart");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    button.preventDefault();
    const row = button.closest('tr');
    const movieId = row.getAttribute('data-movie-id');

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

document.querySelector('#movie_table_body').addEventListener('click',(event) => {
    if (event.target.classList.contains('add-to-cart')) handleAddToCart(event.target);
});