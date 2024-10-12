// handles movie list table
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie list table from resultData");

    let movieListTableBodyElement = jQuery("#movie_table_body"); // find empty table body by id "movie_table_body"

    // iterate through resultData
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
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
        rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
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

        movieListTableBodyElement.append(rowHTML); // append row created to table body; refreshes page
    }
}

// makes HTTP GET request; upon success, uses callback function handleMovieListResult()
jQuery.ajax({
    dataType: "json", // set return data type
    method: "GET", // set request method
    url: "api/movielist", // set request URL as mapped by MovieListServlet
    success: (resultData) => handleMovieListResult(resultData) // set callback function to handle returned data from MovieListServlet
});