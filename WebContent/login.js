// derived from Project 2 Login Cart example: https://github.com/UCI-Chenli-teaching/cs122b-project2-login-cart-example/blob/main/WebContent/login.js

let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    console.log(resultDataString);

    let resultDataJson = JSON.parse(resultDataString);

    console.log("Handle login response.");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // if login successful, redirect user to main.html.
    if (resultDataJson["status"] === "success") {
        window.location.replace("main.html");
    } else {
        // if login failed, display error message on webpage

        console.log("Show error message.");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("Submit login form.");

    formSubmitEvent.preventDefault();

    // // retrieve reCAPTCHA response token
    // let recaptchaResponse = grecaptcha.getResponse();
    // if (!recaptchaResponse) {
    //     // if reCAPTCHA is not completed, show an error message
    //     $("#login_error_message").text("Please complete the reCAPTCHA.");
    //
    //     return;
    // }
    //
    // // construct form data
    // let formData = login_form.serialize() + "&g-recaptcha-response=" + recaptchaResponse;

    $.ajax(
      "api/login", {
          method: "POST",
          data: login_form.serialize(),
          dataType: "text",
          success: handleLoginResult
        }
    );
}

// bind the submit action of login form to a handler function
login_form.submit(submitLoginForm);