// derived from Project 1 API example: https://github.com/UCI-Chenli-teaching/cs122b-project1-api-example/blob/main/src/SingleStarServlet.java

import com.google.gson.JsonArray;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// declare WebServlet named SingleStarServlet, map to URL "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L; // define serial UID

    private DataSource dataSource; // create DataSource, registered in web

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // set type to "application/json" - server returns JSON object

        String id = request.getParameter("id"); // retrieve parameter id from URL request
        request.getServletContext().log("Retrieving parameter id: " + id);

        PrintWriter out = response.getWriter(); // output stream to STDOUT

        // try connection from dataSource, catch potential error(s), & close connection after usage
        try (Connection conn = dataSource.getConnection()) {
            // define SQL query
            String query = "SELECT * FROM stars AS s " +
                    "JOIN stars_in_movies AS sim ON s.id = sim.starId " +
                    "JOIN movies AS m ON sim.movieId = m.id " +
                    "WHERE s.id = ? " +
                    "ORDER BY m.year DESC, m.title ASC";

            PreparedStatement statement = conn.prepareStatement(query); // declare statement

            statement.setString(1, id); // set parameter shown as "?" in SQL query to id retrieved from URL; 1 indicates first "?" in query

            ResultSet rs = statement.executeQuery(); // execute query

            JsonArray jsonArray = new JsonArray(); // construct new JsonArray object

            // iterate through each row of rs
            while (rs.next()) {
                String star_id = rs.getString("starId");
                String star_name = rs.getString("name");
                String star_birthYear = rs.getString("birthYear");

                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");

                // create new JsonObject based on data retrieved from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_birthYear", star_birthYear);

                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);

                jsonArray.add(jsonObject); // add created jsonObject to jsonArray
            }
            rs.close(); // close rs
            statement.close(); // close statement

            out.write(jsonArray.toString()); // write JSON string to output

            response.setStatus(200); // set response status to 200 (OK)
        } catch (Exception e) {
            // write to output stream as JSON object
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);

            response.setStatus(500); // set response status to 500 (Internal Server Error)
        } finally {
            out.close(); // close output stream
        }
    }
}