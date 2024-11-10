$(document).ready(function() {
    // Perform operations upon clicking add_star_form Submit button
    $("#add_star_form").submit(function(event) {
        event.preventDefault();

        // Retrieve name and birthYear from add_star_form input
        var name = $("#name").val();
        var birthYear = $("#birthYear").val();

        $("#add_star_error_message").text("");

        // Send Ajax POST request to AddStarServlet with input data
        $.ajax({
            url: 'api/add_star',
            type: 'POST',
            data: {
                name: name,
                birthYear: birthYear
            },
            dataType: 'json',
            success: (resultData) => {
                const messageElement = $("#add_star_error_message");

                // If successful, show success message and reset add_star_form input fields
                if (resultData.status === "success") {
                    messageElement.css("color", "green");
                    messageElement.text(`Success! Star ID: ${resultData.starID}`);

                    $("#add_star_form")[0].reset();
                } else {
                    // If unsuccessful, show error message
                    messageElement.css("color", "red");
                    messageElement.text(resultData.message);
                }
            },
            error: function(xhr, status, error) {
                // Show error message
                $("#add_star_error_message").text("Error: " + error);
            }
        });
    });
});