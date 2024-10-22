// derived from Project 2 Login Cart example: https://github.com/UCI-Chenli-teaching/cs122b-project2-login-cart-example/blob/main/src/LoginServlet.java

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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("application/json");

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT email, password FROM customers WHERE email = ?"; // define SQL query

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // email found

                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    // password found

                    // set this user on this session
                    request.getSession().setAttribute("user", new User(email));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else {
                    // password mismatch

                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password.");

                    request.getServletContext().log("Login failed: Incorrect password.");
                }
            }
            else {
                // email mismatch

                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "User " + email + " does not exist.");

                request.getServletContext().log("Login failed: Email not found.");
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
    }
}
