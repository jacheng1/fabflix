

/**
 * Handle the data returned by IndexServlet
 * @param resultData jsonObject, consists of session info
 */

function handleShoppingCartResult(resultData) {

    console.log("handle session response");
    console.log(resultData);


    // show the session information 

    // show cart information
    console.log(resultData["previousItems"]);
    handleCartArray(resultData);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    //let jsonArray = JSON.parse(resultArray);
    console.log(resultArray);
    let item_list = $("#previousItems");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point

        res += "<li>" + resultArray[i]["movie_title"] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleChangeQuantity(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/checkout", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}
jQuery.ajax({
    dataType: "json", // set return data type to JSON
    method: "GET", // set request method to GET
    url: "api/checkout", // set request URL as mapped by SingleMovieServlet
    success: (resultData) => handleShoppingCartResult(resultData),
    error: function(error) {
        console.log("ERROR:  " + error);
    }
    // set callback function to handle returned data from SingleMovieServlet
});

// Bind the submit action of the form to a event handler function

