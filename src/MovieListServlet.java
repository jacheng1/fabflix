// derived from Project 1 API example: https://github.com/UCI-Chenli-teaching/cs122b-project1-api-example/blob/main/src/StarsServlet.java

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
import java.util.ArrayList;
import java.util.List;

// declare WebServlet named MovieListServlet, map to URL "/api/movielist"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // set type to "application/json" - server returns JSON object

        // get parameter(s) for alphanumeric browse
        String prefix = request.getParameter("prefix");
        if (prefix == null || prefix.isEmpty()) {
            prefix = "";
        }

        // get parameter(s) for genre browse
        String genre = request.getParameter("genre");

        // get parameter(s) for search
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("star");

        // get parameter(s) for sort
        String moviesPerPage = request.getParameter("n");
        String sortBy = request.getParameter("sort");

        PrintWriter out = response.getWriter(); // output stream to STDOUT

        // try connection from dataSource, catch potential error(s), & close connection after usage
        try (Connection conn = dataSource.getConnection()) {
            String query; // define SQL query

            PreparedStatement statement;

            List<String> conditions = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (prefix.equals("*")) {
                // set query for movies starting with non-alphanumeric characters
                query = "SELECT m.id AS movie_id, m.title, m.year, m.director, r.rating, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ', ', 3) AS genres, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars, " +
                        "GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids, " +
                        "GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ', ') AS genre_ids " +
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
                        "WHERE m.title REGEXP '^[^a-zA-Z0-9]' " +
                        "GROUP BY m.id, m.title, m.year, m.director, r.rating";
            } else if ((prefix.length() == 1 && Character.isLetter(prefix.charAt(0))) || (prefix.length() == 1 && Character.isDigit(prefix.charAt(0)))) {
                // set query for movies starting with alphanumeric characters
                query = "SELECT m.id AS movie_id, m.title, m.year, m.director, r.rating, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ', ', 3) AS genres, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars, " +
                        "GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids, " +
                        "GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ', ') AS genre_ids " +
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
                        "WHERE m.title LIKE ? COLLATE utf8mb4_general_ci " +
                        "GROUP BY m.id, m.title, m.year, m.director, r.rating";
            } else {
                // set query for movies for non-title features
                query = "SELECT m.id AS movie_id, m.title, m.year, m.director, r.rating, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ', ', 3) AS genres, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars, " +
                        "GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids, " +
                        "GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ', ') AS genre_ids " +
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
                        ") AS star_counts ON s.id = star_counts.starId";

                if (genre != null && !genre.isEmpty()) {
                    // add onto query for genre browse

                    query += " WHERE g.id = ?";
                    query += " GROUP BY m.id, m.title, m.year, m.director, r.rating";
                }
                else if ((title != null && !title.isEmpty()) || (year != null && !year.isEmpty()) || (director != null && !director.isEmpty()) || (starName != null && !starName.isEmpty())) {
                    // add onto query for search

                    StringBuilder queryBuilder = new StringBuilder(
                            "SELECT m.id AS movie_id, m.title, m.year, m.director, r.rating, " +
                                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ', ', 3) AS genres, " +
                                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ', ', 3) AS stars, " +
                                    "GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids, " +
                                    "GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ', ') AS genre_ids " +
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
                                    ") AS star_counts ON s.id = star_counts.starId "
                    );

                    // check each filter and add the relevant condition and parameter
                    if (title != null && !title.isEmpty()) {
                        conditions.add("m.title LIKE ?");
                        params.add("%" + title + "%");
                    }
                    if (year != null && !year.isEmpty()) {
                        conditions.add("m.year = ?");
                        params.add(year);
                    }
                    if (director != null && !director.isEmpty()) {
                        conditions.add("m.director LIKE ?");
                        params.add("%" + director + "%");
                    }
                    if (starName != null && !starName.isEmpty()) {
                        conditions.add("s.name LIKE ?");
                        params.add("%" + starName + "%");
                    }

                    if (!conditions.isEmpty()) {
                        queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
                    }

                    queryBuilder.append(" GROUP BY m.id, m.title, m.year, m.director, r.rating");
                    query = queryBuilder.toString();
                }
                else {
                    query += " GROUP BY m.id, m.title, m.year, m.director, r.rating";
                }
            }

            if ((moviesPerPage != null && !moviesPerPage.isEmpty()) || (sortBy != null && !sortBy.isEmpty())) {
                // set string for update

                if (moviesPerPage != null && !moviesPerPage.isEmpty()) {
                    switch (moviesPerPage) {
                        case "10":
                            query += " LIMIT 10";
                            break;
                        case "25":
                            query += " LIMIT 25";
                            break;
                        case "50":
                            query += " LIMIT 50";
                            break;
                        case "100":
                            query += " LIMIT 100";
                            break;
                    }
                }
                if (sortBy != null && !sortBy.isEmpty()) {
                    switch (sortBy) {
                        case "0":
                            query += " ORDER BY m.title ASC, r.rating DESC";
                            break;
                        case "1":
                            query += " ORDER BY m.title ASC, r.rating ASC";
                            break;
                        case "2":
                            query += " ORDER BY m.title DESC, r.rating DESC";
                            break;
                        case "3":
                            query += " ORDER BY m.title DESC, r.rating ASC";
                            break;
                        case "4":
                            query += " ORDER BY r.rating DESC, m.title ASC";
                            break;
                        case "5":
                            query += " ORDER BY r.rating DESC, m.title DESC";
                            break;
                        case "6":
                            query += " ORDER BY r.rating ASC, m.title ASC";
                            break;
                        case "7":
                            query += " ORDER BY r.rating ASC, m.title DESC";
                            break;
                    }
                }
            }

            query += ";"; // close completed query

            statement = conn.prepareStatement(query);

            if ((prefix.length() == 1 && Character.isLetter(prefix.charAt(0))) || (prefix.length() == 1 && Character.isDigit(prefix.charAt(0)))) {
                // set string for alphanumeric browsing

                statement.setString(1, prefix + "%");
            }
            else if (genre != null && !genre.isEmpty()) {
                // set string for genre browsing

                statement.setString(1, genre);
            }
            else if ((title != null && !title.isEmpty()) || (year != null && !year.isEmpty()) || (director != null && !director.isEmpty()) || (starName != null && !starName.isEmpty())) {
                // set string for searching

                for (int i = 0; i < params.size(); i++) {
                    statement.setString(i + 1, params.get(i));
                }
            }
            else if ((moviesPerPage != null && !moviesPerPage.isEmpty()) || (sortBy != null && !sortBy.isEmpty())) {
                // set string for update

                for (int i =0; i < params.size(); i++) {
                    statement.setString(i + 1, params.get(i));
                }
            }

            ResultSet rs = statement.executeQuery(); // execute query

            JsonArray jsonArray = new JsonArray(); // construct new JsonArray object

            // iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genre = rs.getString("genres");
                String movie_star = rs.getString("stars");
                String movie_rating = rs.getString("rating");
                String star_ids = rs.getString("star_ids");
                String genre_ids = rs.getString("genre_ids");

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
                jsonObject.addProperty("genre_ids", genre_ids);

                jsonArray.add(jsonObject); // add created jsonObject to jsonArray
            }
            rs.close(); // close rs
            statement.close(); // close declared statement

            request.getServletContext().log("getting " + jsonArray.size() + " results"); // log to localhost log

            out.write(jsonArray.toString()); // write JSON string to output

            response.setStatus(200); // set response status to 200 (OK)
        } catch (Exception e) {
            // write to output stream as JSON object
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500); // set response status to 500 (Internal Server Error)
        } finally {
            out.close(); // close output stream
        }
    }
}
