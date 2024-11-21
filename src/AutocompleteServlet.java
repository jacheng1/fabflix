import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
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
        response.setCharacterEncoding("UTF-8");

        String title = request.getParameter("full-text");
        if (title == null || title.isEmpty()) {
            response.getWriter().write("[]");

            return;
        }

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT id, title FROM movies " +
                    "WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) LIMIT 10";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                String filterString = buildFilterString(title);
                statement.setString(1, filterString);

                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    String movieId = rs.getString("id");
                    String movieTitle = rs.getString("title");
                    jsonArray.add(generateJsonObject(movieId, movieTitle));
                }
                rs.close();
                statement.close();

                out.write(jsonArray.toString());
                response.setStatus(200);
            }
        } catch (Exception e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "An unexpected error occurred.");
            response.getWriter().write(errorJson.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    private String buildFilterString(String title) {
        StringBuilder filterString = new StringBuilder();
        for (String word : title.split(" ")) {
            filterString.append("+").append(word).append("* ");
        }
        return filterString.toString().trim();
    }

//    For future fuzzy search:
//    private int determineEditDistance(String title) {
//        if (title.length() < 4) {
//            return 1;
//        }
//        if (title.length() < 6) {
//            return 2;
//        }
//
//        return 3;
//    }

    private JsonObject generateJsonObject(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalData = new JsonObject();
        additionalData.addProperty("id", id);
        jsonObject.add("data", additionalData);

        return jsonObject;
    }
}