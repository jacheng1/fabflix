// derived from Project 2 Login Cart example: https://github.com/UCI-Chenli-teaching/cs122b-project2-login-cart-example/blob/main/WebContent/login.js

let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("Handle login response.");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // if login successful, redirect user to index.html.
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
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

    $.ajax(
      "api/login", {
          method: "POST",
          data: login_form.serialize(),
          success: handleLoginResult
        }
    );
}

// bind the submit action of login form to a handler function
login_form.submit(submitLoginForm);