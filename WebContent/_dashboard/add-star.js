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
            success: function(response) {
                // If successful, show alert with success message and reset add_star_form input fields
                if (response.status === "success") {
                    alert(response.message);

                    $("#add_star_form")[0].reset();
                } else {
                    // If unsuccessful, show alert with error message
                    $("#add_star_error_message").text(response.message);
                }
            },
            error: function(xhr, status, error) {
                // Show error message
                $("#add_star_error_message").text("Error: " + error);
            }
        });
    });
});