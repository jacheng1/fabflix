$(document).ready(function() {
    // Perform operations upon clicking add_movie_form Submit button
    $("#add_movie_form").submit(function(event) {
        event.preventDefault();

        // Retrieve input values from add_movie_form
        var title = $("#title").val();
        var year = $("#year").val();
        var director = $("#director").val();
        var name = $("#name").val();
        var genreName = $("#genreName").val();

        $("#add_movie_error_message").text("");

        // Send Ajax POST request to AddMovieServlet with input data
        $.ajax({
            url: 'api/add_movie',
            type: 'POST',
            data: {
                title: title,
                year: year,
                director: director,
                name: name,
                genreName: genreName
            },
            dataType: 'json',
            success: (resultData) => {
                console.log(resultData);
                const messageElement = $("#add_movie_error_message");

                // If successful, show success message and reset add_movie_form input fields
                if (resultData.status === "success") {
                    messageElement.css("color", "green");
                    messageElement.text(resultData.message);

                    $("#add_movie_form")[0].reset();
                } else {
                    // If unsuccessful, show error message
                    messageElement.css("color", "red");
                    messageElement.text(resultData.message);
                }
            },
            error: function(xhr, status, error) {
                // Show error message
                $("#add_movie_error_message").text("Error: " + error);
            }
        });
    });
});