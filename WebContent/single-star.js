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

/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleSingleStarResult(resultData) {
    console.log("handleSingleStarResult: populating star info from resultData");

    console.log("resultData: ", resultData);

    let starInfoElement = jQuery("#star_info"); // find empty h3 body star_info by id, populate it

    // append HTML to starInfoElement
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Year of Birth: " + (resultData[0]["star_birthYear"] === null ? "N/A" : resultData[0]["star_birthYear"]) + "</p>");

    console.log("handleSingleStarResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body"); // find empty table body movie_table_body by id, populate it

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
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

let starId = getParameterByName('id');

// makes HTTP GET request; upon success, uses callback function handleSingleStarResult()
jQuery.ajax({
    dataType: "json",  // set return data type to JSON
    method: "GET", // set request method to GET
    url: "api/single-star?id=" + starId, // set request URL as mapped by SingleMovieServlet
    success: (resultData) => handleSingleStarResult(resultData) // set callback function to handle returned data from SingleStarServlet
});