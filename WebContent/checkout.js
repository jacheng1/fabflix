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

let totalPrice = 0; // declare global variable for total price of all movies in shopping cart

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    //let jsonArray = JSON.parse(resultArray);
    console.log(resultArray);

    let cartListTableBodyElement = jQuery("#previousItems");

    // change it to html list
    let moviesDisplayed = [];

    for (let i = 0; i < resultArray.length; i++) {
        let rowTotalPrice = 0;

        totalPrice += resultArray[i]['price'];

        if (!moviesDisplayed.includes(resultArray[i]['movie_id'])) {
            moviesDisplayed.push(resultArray[i]['movie_id']);

            rowTotalPrice = resultArray[i]['price'] * resultArray[i]['quantity'];

            let res = "";

            res += "<tr class='item-row' data-movie-id='" + resultArray[i]['movie_id'] + "'>";
            res +=
                "<th class='titleofMovie'>" +
                '<a href="single-movie.html?id=' + resultArray[i]['movie_id'] + '">'
                + resultArray[i]["movie_title"] +
                "</a>" +
                "</th>";
            res += "<th class='movie-quantity'>" +
                "   <button type='button' class='btn btn-outline-primary add' style='margin-right: 10px;'>+</button>";
            res += "<span class='quantity-of-movies' style='margin: 0 10px;'>" + resultArray[i]['quantity'] +
                "</span>";
            res += "<button type='button' class='btn btn-outline-primary subtract' style='margin-left: 10px;'>-</button>" +
                "</th>";
            res += "<th>" +
                "<button type='button' class='btn btn-outline-primary remove-from-cart'>Delete</button>" +
                "</th>";
            res += "<th class='movie-price'>$" + resultArray[i]['price'] + "</th>";
            res += "<th class='row-movie-price'>$" + rowTotalPrice.toFixed(2) + "</th>";
            res += "</tr>";

            cartListTableBodyElement.append(res);
        }
    }

    updateSubtotal(totalPrice);
    // clear the old array and show the new array in the frontend

}

// Update the subtotal display
function updateSubtotal(totalPrice) {
    jQuery("#subtotal").html(`<div class='checkout-summary'><tr><th>Subtotal: </th><th class='price'>$${totalPrice.toFixed(2)}</th></tr></div>`);
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
        console.log("ERROR: " + error);
    }
    // set callback function to handle returned data from SingleMovieServlet
});

// Bind the submit action of the form to a event handler function
document.addEventListener('DOMContentLoaded', () => {
    console.log("Listening for event...");

    function handleChangeQuantity(button, cartEvent) {
        console.log("handleChangeQuantity()");
        /**
         * When button is pressed then change in quantity will update the session's previousItems attribute
         * users to the url defined in HTML form. Instead, it will call this
         * event handler when the event is triggered.
         */
        const row = button.closest('tr');
        const movieId = row.getAttribute('data-movie-id');

        let quantityE = row.querySelector('.quantity-of-movies');
        let quantity = parseInt(quantityE.textContent);

        let moviePriceE = row.querySelector('.movie-price');
        let moviePrice = parseFloat(moviePriceE.textContent.replace('$', ''));

        let rowMoviePriceE = row.querySelector('.row-movie-price');
        let rowMoviePrice = parseFloat(rowMoviePriceE.textContent.replace('$', ''));

        if (cartEvent ==='add') {
            quantity += 1;
            rowMoviePrice += moviePrice;
            totalPrice += moviePrice;
        }
        else if (cartEvent ==='subtract') {
            if (quantity > 0) {
                quantity -= 1;
                rowMoviePrice -= moviePrice;
                totalPrice -= moviePrice;
            }
            else {
                alert("Error: Movie quantity cannot be less than 0 - please click the Delete button.");
            }
        }
        else if (cartEvent ==='remove-from-cart') {
            rowMoviePrice = 0;
            totalPrice -= moviePrice * quantity;

            row.remove();

            quantity = 0;
        }

        quantityE.textContent = quantity;
        rowMoviePriceE.textContent = `$${rowMoviePrice.toFixed(2)}`;
        updateSubtotal(totalPrice);

        $.ajax("api/checkout", {
            method: "POST",
            data: {movie_id : movieId, cartEvent : cartEvent},
            success: function(response) {
                console.log('SUCCESS: Quantity updated successfully.');

                alert("Quantity updated successfully!");
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