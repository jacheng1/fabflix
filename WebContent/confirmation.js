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
    let subtotalElement = jQuery("#total");
    // iterate through resultData
    let subtotal = 0;
    let s_total = "";
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr>";
        subtotal += resultData[i]["movie_price"]*resultData[i]["movie_quantity"];
        rowHTML += "<td>" + resultData[i]["sale_id"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_quantity"] + "</td>";
        rowHTML += "<td>$" + resultData[i]["movie_price"] + "</td>";
        rowHTML += "<td>$" + (resultData[i]["movie_price"]*resultData[i]["movie_quantity"]).toFixed(2) + "</td>";
        rowHTML += "</tr>"

        confirmationTableBodyElement.append(rowHTML);
    }
    s_total += "<div class='subtotal-amount'><tr><th>Subtotal: $</th><td class='price'>";
    s_total += subtotal.toFixed(2);
    s_total += "</td></tr></div>";

    subtotalElement.append(s_total);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation",
    success: (resultData) => handleConfirmationResult(resultData)
});