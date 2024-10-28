

/**
 * Handle the data returned by IndexServlet
 * @param resultData jsonObject, consists of session info
 */

function handleShoppingCartResult(resultData) {

    console.log("handle session response");
    console.log(resultData);


    // show the session information 

    // show cart information
    handleCartArray(resultData);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    //let jsonArray = JSON.parse(resultArray);
    console.log("HERE HERE HERE");
    console.log(resultArray);
    let cartListTableBodyElement = jQuery("#previousItems");
    cartListTableBodyElement.empty();
    // change it to html list
    let moviesDisplayed = [];

    let totalPrice = 0;
    for (let i = 0; i < resultArray.length; i++) {
        totalPrice += resultArray[i]['price'];
        if (!moviesDisplayed.includes(resultArray[i]['movie_id'])) {
            moviesDisplayed.push(resultArray[i]['movie_id']);
            let res = "";
            res += "<tr class='item-row' data-movie-id='" + resultArray[i]['movie_id'] + "'>";
            res +=
                "<td class='titleofMovie'>" +
                '<a href="single-movie.html?id=' + resultArray[i]['movie_id'] + '">'
                + resultArray[i]["movie_title"] +
                "</a>" +
                "</td>";
            res += "<td class='movie-price'>" + resultArray[i]["price"] + "</td>";
            res += "<td class='movie-quantity'>" +
                "   <button class='add'>+</button>";
            res += "<span class='quantity-of-movies'>" + resultArray[i]['quantity'] +
                "</span>";
            res += "<button class='subtract'>-</button>";
            res += "<button class='remove-from-cart'>Remove</button>" +
                "</td>";
            res += "</tr>";
            //cartListTableBodyElement.html("");
            cartListTableBodyElement.append(res);
        }

    }
    let subtotalElement = jQuery("#subtotal");
    subtotalElement.append("<div class='checkout-sumary'> <tr><td>Subtotal:</td><td class='price'>$" + totalPrice + "</td><td style='color: white;'></td></tr>");
    // clear the old array and show the new array in the frontend

}

/**
 * Submit form content with POST method
 * @param button
 * @param cartEvent
 */
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
document.addEventListener('DOMContentLoaded', () => {
    console.log("listening for event");
    function handleChangeQuantity(button, cartEvent) {
        console.log("handle Change quantity");
        /**
         * When button is pressed then change in quantity will update the session's previousItems attribute
         * users to the url defined in HTyML form. Instead, it will call this
         * event handler when the event is triggered.
         */
        const row = button.closest('tr');
        const movieId = row.getAttribute('data-movie-id');

        let quantityE = row.querySelector('.quantity-of-movies');

        let quantity = parseInt(quantityE.textContent);
        if (cartEvent ==='add') {
            console.log('adding movie');
            quantity += 1;
        }   else if (cartEvent ==='subtract') {
            if (quantity > 0) quantity -= 1;
        }   else if (cartEvent ==='remove-from-cart') {
            row.remove();
            quantity = 0;
        }
        quantityE.textContent = quantity;

        $.ajax("api/checkout", {
            method: "POST",
            data: {movie_id : movieId, cartEvent : cartEvent},
            success: function(response) {
                console.log('updated quantity');
                alert("Updated quantity!");
            },
            error: function(error) {
                console.log("ERROR: " + error);
            }
        });

        // clear input form
    }

    document.querySelector('#previousItems').addEventListener('click', (event) => {
        if (event.target.classList.contains('add')) {
            handleChangeQuantity(event.target, 'add');
        } else if (event.target.classList.contains('subtract')) {
            handleChangeQuantity(event.target, 'subtract');
        } else if (event.target.classList.contains('remove-from-cart')) {
            handleChangeQuantity(event.target, 'remove-from-cart');
        }
    });
});




