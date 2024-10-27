let cart = $("#cart");

document.addEventListener('DOMContentLoaded', function ()
{
    const addToCartButton = document.getElementById('add-to-cart-button');

    function handleAddToCart(cartEvent) {
        console.log("submit cart form");
        /**
         * When users click the submit button, the browser will not direct
         * users to the url defined in HTML form. Instead, it will call this
         * event handler when the event is triggered.
         */
        cartEvent.preventDefault();

        const movieID = document.getElementById()
            let
        movie_info = $('#movie')
        $.ajax("api/single-movie", {
            method: "POST",
            data: cart.serialize(),
            success: resultDataString => {
                alert('Movie added to cart');
            }
        });

        // clear input form

    }
});