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

function handleConfirmationResult(resultData) {
    console.log("handleConfirmationResult: populating confirmation table from resultData");

    let confirmationTableBodyElement = jQuery("#confirmation_table_body");

    // iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr>";

        rowHTML += "<th>" + resultData[i]["sale_id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_quantity"] + "</th>";
        rowHTML += "</tr>"

        confirmationTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation",
    success: (resultData) => handleConfirmationResult(resultData)
});