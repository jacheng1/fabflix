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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/dashboard_login")
public class DashboardLoginServlet extends HttpServlet {
    private DataSource dataSource;

    // servlet initialization
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb"); // fetch DataSource from environment, configure JDBC in application server
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        JsonObject responseJsonObject = new JsonObject();

        try {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            try (Connection conn = dataSource.getConnection()) {
                String query = "SELECT email, password FROM employees WHERE email = ?"; // define SQL query
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, email);

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    // Email exists

                    // retrieve encrypted password from moviedb
                    String dbPassword = rs.getString("password");

                    if (new StrongPasswordEncryptor().checkPassword(password, dbPassword)) {
                        // Successful login

                        // Set this user on this session
                        request.getSession().setAttribute("employee", email);

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "Login successful.");
                    } else {
                        // Incorrect password

                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "Incorrect password.");
                    }
                } else {
                    // Email not found

                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "User " + email + " does not exist.");
                }
                rs.close();
                statement.close();
            }
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "An error occurred: " + e.getMessage());

            request.getServletContext().log("Error:", e);
            response.setStatus(500); // set response status to 500 (Internal Server Error)
        } finally {
            out.write(responseJsonObject.toString());
            out.close();
        }

        System.out.println("Response sent to client: " + responseJsonObject.toString());
    }
}