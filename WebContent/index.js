/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie list table from resultData");

    let movieListTableBodyElement = jQuery("#movie_table_body"); // find empty table body by id "movie_table_body"

    // iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        // concatenate HTML tags with resultData JSON object
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="movie-list.html?id=>' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            "</a>" +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_star"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        movieListTableBodyElement.append(rowHTML); // append row created to table body; refreshes page
    }
}

/**
 * Runs after .js is loaded.
 * Makes HTTP GET request, registers on success callback function handleMovieListResult
 */
jQuery.ajax({
    dataType: "json", // set return data type
    method: "GET", // set request method
    url: "api/movielist", // set request URL as mapped by MovieListServlet
    success: (resultData) => handleMovieListResult(resultData) // set callback function to handle returned data from MovieListServlet
});