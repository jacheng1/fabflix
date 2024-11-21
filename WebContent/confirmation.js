/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleConfirmationResult(resultData) {
    console.log("handleConfirmationResult: populating confirmation table from resultData");

    let confirmationTableBodyElement = jQuery("#confirmation_table_body");
    let subtotalElement = jQuery("#total");

    // iterate through resultData
    let subtotal = 0;
    let s_total = "";

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr>";

        subtotal += resultData[i]["movie_price"] * resultData[i]["movie_quantity"];

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