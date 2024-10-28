import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;

    // servlet initialization
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb"); // fetch DataSource from environment, configure JDBC in application server
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String creditCardNumber = request.getParameter("credit_card_number");
        String expirationDate = request.getParameter("expiration_date");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT id, firstName, lastName, expiration FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?"; // define SQL query

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, creditCardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expirationDate);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // credit card found

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else {
                // credit card mismatch

                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Credit card information not found.");

                request.getServletContext().log("Payment failed: Incorrect credit card details.");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("errorMessage", e.getMessage());

            request.getServletContext().log("Error:", e);
            response.setStatus(500); // set response status to 500 (Internal Server Error)
        }

        response.getWriter().write(responseJsonObject.toString());
        System.out.println("Response sent to client: " + responseJsonObject.toString());
    }
}

