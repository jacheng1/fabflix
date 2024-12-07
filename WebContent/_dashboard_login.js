let dashboard_login_form = $("#dashboard_login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleDashboardLoginResult(resultDataString) {
    console.log(resultDataString);

    let resultDataJson = JSON.parse(resultDataString);

    console.log("Handle login response.");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // if login successful, redirect user to main.html.
    if (resultDataJson["status"] === "success") {
        window.location.replace("_dashboard/index.html");
    } else {
        // if login failed, display error message on webpage

        console.log("Show error message.");
        console.log(resultDataJson["message"]);
        $("#dashboard_login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitDashboardLoginForm(formSubmitEvent) {
    console.log("Submit dashboard login form.");

    formSubmitEvent.preventDefault();

    // // retrieve reCAPTCHA response token
    // let recaptchaResponse = grecaptcha.getResponse();
    // if (!recaptchaResponse) {
    //     // if reCAPTCHA is not completed, show an error message
    //     $("#dashboard_login_error_message").text("Please complete the reCAPTCHA.");
    //
    //     return;
    // }
    //
    // // construct form data
    // let formData = dashboard_login_form.serialize() + "&g-recaptcha-response=" + recaptchaResponse;

    $.ajax(
        "api/dashboard_login", {
            method: "POST",
            data: dashboard_login_form.serialize(),
            dataType: "text",
            success: handleDashboardLoginResult
        }
    );
}

// bind the submit action of login form to a handler function
dashboard_login_form.submit(submitDashboardLoginForm);