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
import java.sql.ResultSet;
import java.sql.PreparedStatement;

// declare WebServlet named SingleMovieServlet, map to URL "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet{
    private static final long serialVersionUID = 2L; // define serial UID

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
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') AS genres, " +
                    "GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS stars, " +
                    "GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids " +
                    "FROM moviedb.movies m " +
                    "JOIN moviedb.ratings r ON m.id = r.movieId " +
                    "LEFT JOIN moviedb.genres_in_movies gm ON m.id = gm.movieId " +
                    "LEFT JOIN moviedb.genres g ON gm.genreId = g.id " +
                    "LEFT JOIN moviedb.stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN moviedb.stars s ON sm.starId = s.id " +
                    "LEFT JOIN ( " +
                    "   SELECT starId, COUNT(*) AS star_movie_count " +
                    "   FROM moviedb.stars_in_movies " +
                    "   GROUP BY starId " +
                    ") AS star_counts ON s.id = star_counts.starId " +
                    "WHERE m.id = ? " +
                    "GROUP BY m.id, m.title, m.year, m.director, r.rating";

            PreparedStatement statement = conn.prepareStatement(query); // declare statement

            statement.setString(1, id); // set parameter shown as "?" in SQL query to id retrieved from URL; 1 indicates first "?" in query

            ResultSet rs = statement.executeQuery(); // execute query

            JsonArray jsonArray = new JsonArray(); // construct new JsonArray object

            // iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genre = rs.getString("genres");
                String movie_star = rs.getString("stars");
                String movie_rating = rs.getString("rating");
                String star_ids = rs.getString("star_ids");

                // create new JsonObject based on data retrieved from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_star", movie_star);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("star_ids", star_ids);

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
