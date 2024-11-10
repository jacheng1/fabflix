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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add_movie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // define serial UID

    private DataSource dataSource; // create DataSource, registered in web

    // servlet initialization
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb"); // fetch DataSource from environment, configure JDBC in application server
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieTitle = request.getParameter("title");
        String movieYear = request.getParameter("year");
        String movieDirector = request.getParameter("director");
        String starName = request.getParameter("name");
        String genreName = request.getParameter("genreName");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // Validate user input for required fields
        if ((movieTitle == null || movieTitle.trim().isEmpty()) || (movieYear == null || movieYear.trim().isEmpty()) || (movieDirector == null || movieDirector.trim().isEmpty()) || (starName == null || starName.trim().isEmpty()) || (genreName == null || genreName.trim().isEmpty())) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Required field(s) is empty.");
            out.write(responseJsonObject.toString());

            response.setStatus(400);
            out.close();

            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String procedureQuery = "{call add_movie(?, ?, ?, ?, ?)}";

            try (CallableStatement statement = conn.prepareCall(procedureQuery)) {
                statement.setString(1, movieTitle);
                statement.setInt(2, Integer.parseInt(movieYear));
                statement.setString(3, movieDirector);
                statement.setString(4, starName);
                statement.setString(5, genreName);

                boolean hasResults = statement.execute();
                while (hasResults) {
                    ResultSet rs = statement.getResultSet();
                    while (rs.next()) {
                        String message = rs.getString("message");

                        if (message.contains("Error")) {
                            responseJsonObject.addProperty("status", "fail");
                            responseJsonObject.addProperty("message", message);

                            out.write(responseJsonObject.toString());

                            rs.close();
                            out.close();

                            return;
                        } else {
                            responseJsonObject.addProperty("status", "success");
                            responseJsonObject.addProperty("message", message);
                        }
                    }

                    hasResults = statement.getMoreResults();

                    rs.close();
                }
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