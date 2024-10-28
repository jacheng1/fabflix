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

// bind the submit action of payment form to a handler function
payment_form.submit(submitPaymentForm);