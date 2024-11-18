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

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String prefix = request.getParameter("prefix");

        String genre = request.getParameter("genre");

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("star");

        String fullText = request.getParameter("full-text");

        String moviesPerPage = request.getParameter("n");
        String sortBy = request.getParameter("sort");
        int moviesPerPageParsed = (moviesPerPage != null) ? Integer.parseInt(moviesPerPage) : 10;
        int page = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;
        int offset = (page - 1) * moviesPerPageParsed;

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
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
                            ") AS star_counts ON s.id = star_counts.starId"
            );

            List<String> conditions = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if ("*".equals(prefix)) {
                conditions.add("m.title REGEXP '^[^a-zA-Z0-9]'");
            }
            else if (prefix != null && prefix.length() == 1 && (Character.isLetter(prefix.charAt(0)) || Character.isDigit(prefix.charAt(0)))) {
                conditions.add("m.title LIKE ?");
                params.add(prefix + "%");
            }

            if (genre != null && !genre.isEmpty()) {
                conditions.add("g.id = ?");
                params.add(genre);
            }

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

            if (fullText != null && !fullText.isEmpty()) {
                String[] tokens = fullText.split("\\s+");
                StringBuilder searchQuery = new StringBuilder();
                for (String token : tokens) {
                    System.out.println(token);
                    if (!token.isEmpty()) {
                        searchQuery.append("+").append(token).append("* ");
                    }
                }

                conditions.add("MATCH (m.title) AGAINST (? IN BOOLEAN MODE)");
                System.out.println(searchQuery.toString());
                System.out.println(conditions.toString());
                params.add(searchQuery.toString().trim());
            }

            if (!conditions.isEmpty()) {
                queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            queryBuilder.append(" GROUP BY m.id, m.title, m.year, m.director, r.rating");

            if (sortBy != null && !sortBy.isEmpty()) {
                switch (sortBy) {
                    case "0":
                        queryBuilder.append(" ORDER BY m.title ASC, r.rating DESC");
                        break;
                    case "1":
                        queryBuilder.append(" ORDER BY m.title ASC, r.rating ASC");
                        break;
                    case "2":
                        queryBuilder.append(" ORDER BY m.title DESC, r.rating DESC");
                        break;
                    case "3":
                        queryBuilder.append(" ORDER BY m.title DESC, r.rating ASC");
                        break;
                    case "4":
                        queryBuilder.append(" ORDER BY r.rating DESC, m.title ASC");
                        break;
                    case "5":
                        queryBuilder.append(" ORDER BY r.rating DESC, m.title DESC");
                        break;
                    case "6":
                        queryBuilder.append(" ORDER BY r.rating ASC, m.title ASC");
                        break;
                    case "7":
                        queryBuilder.append(" ORDER BY r.rating ASC, m.title DESC");
                        break;
                }
            }

            if (moviesPerPage != null && !moviesPerPage.isEmpty()) {
                queryBuilder.append(" LIMIT ?");
                params.add(String.valueOf(moviesPerPageParsed));

                queryBuilder.append(" OFFSET ?");
                params.add(String.valueOf(offset));
            }

            queryBuilder.append(";");
            System.out.println(queryBuilder.toString());
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, params.get(i));

            }

            if (moviesPerPage != null && !moviesPerPage.isEmpty()) {
                statement.setInt(params.size() - 1, moviesPerPageParsed);
                statement.setInt(params.size(), offset);
            }

            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", rs.getString("movie_id"));
                jsonObject.addProperty("movie_title", rs.getString("title"));
                jsonObject.addProperty("movie_year", rs.getString("year"));
                jsonObject.addProperty("movie_director", rs.getString("director"));
                jsonObject.addProperty("movie_genre", rs.getString("genres"));

                jsonObject.addProperty("movie_star", rs.getString("stars"));
                jsonObject.addProperty("movie_rating", rs.getString("rating"));
                jsonObject.addProperty("star_ids", rs.getString("star_ids"));
                jsonObject.addProperty("genre_ids", rs.getString("genre_ids"));

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");

            out.write(jsonArray.toString());

            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
