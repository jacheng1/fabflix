// derived from Project 2 Login Cart example: https://github.com/UCI-Chenli-teaching/cs122b-project2-login-cart-example/blob/main/WebContent/login.js

let payment_form = $("#payment_form");

/**
 * Handle the data returned by PaymentServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    console.log(resultDataString);

    let resultDataJson = JSON.parse(resultDataString);

    console.log("Handle payment response.");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // if payment successful, redirect user to confirmation.html.
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        // if payment failed, display error message on webpage

        console.log("Show error message.");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("Submit payment form.");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            data: payment_form.serialize(),
            dataType: "text",
            success: handlePaymentResult
        }
    );
}

document.addEventListener("DOMContentLoaded", function() {
    const totalPrice = sessionStorage.getItem("totalPrice");

    if (totalPrice) {
        document.getElementById("totalPriceDisplay").textContent = `$${parseFloat(totalPrice).toFixed(2)}`;
    } else {
        document.getElementById("totalPriceDisplay").textContent = "0.00";
    }
});

// bind the submit action of payment form to a handler function
payment_form.submit(submitPaymentForm);

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