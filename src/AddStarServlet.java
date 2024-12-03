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

@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add_star")
public class AddStarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // define serial UID

    private DataSource dataSource; // create DataSource, registered in web

    // servlet initialization
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb_master"); // fetch DataSource from environment, configure JDBC in application server
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // Validate user input for name
        if (name == null || name.trim().isEmpty()) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Star name is required.");
            out.write(responseJsonObject.toString());

            response.setStatus(400);
            out.close();

            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String idQuery = "SELECT CONCAT(\"nm\",(SELECT MAX(SUBSTRING(id, 3)) FROM stars) + 1) as starID";
            ResultSet rs = conn.prepareStatement(idQuery).executeQuery();
            rs.next();
            String starID = rs.getString("starID");

            String insertQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

            try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
                statement.setString(1, starID);
                statement.setString(2, name);
                if (birthYear != null && !birthYear.trim().isEmpty()) {
                    statement.setInt(3, Integer.parseInt(birthYear));
                } else {
                    statement.setNull(3, java.sql.Types.INTEGER);
                }

                // Execute INSERT statement on moviedb
                int insertRows = statement.executeUpdate();
                if (insertRows > 0) {
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Success! Star ID: " + starID);
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Failed to add star.");
                }

                responseJsonObject.addProperty("starID", rs.getString("starID"));

                rs.close();
                statement.close();

                response.setStatus(200);
            }
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("errorMessage", "Server error: " + e.getMessage());

            response.setStatus(500);

        } finally {
            out.write(responseJsonObject.toString());
            out.close();
        }
    }
}
