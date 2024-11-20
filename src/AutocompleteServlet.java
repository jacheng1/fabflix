import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            Connection conn = dataSource.getConnection();
            HttpSession session = request.getSession();

            String title = request.getParameter("query") != null ? request.getParameter("query") : "";

            String query = "SELECT id, title FROM movies WHERE match(title) against (? IN BOOLEAN " +
                    "MODE) OR ed(?, lower(title)) <= ? LIMIT 10";
            PreparedStatement statement = conn.prepareStatement(query);

            String filterString = "";
            if (title.length() > 0) {
                String [] filters = title.split(" ");
                for (String word : filters) {
                    filterString += "+" + word + "* ";
                }
            }
            statement.setString(1, filterString);
            statement.setString(2, title.toLowerCase());

            if (title.length() < 4) {
                statement.setInt(3, 1);
            }
            else if (title.length() < 6) {
                statement.setInt(3, 2);
            }
            else {
                statement.setInt(3, 3);
            }

            JsonArray jsonArray = new JsonArray();

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");

                jsonArray.add(generateJsonObject(movie_id, movie_title));
            }

            out.write(jsonArray.toString());

            session.setAttribute("queryResult", jsonArray.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
            conn.close();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());

            out.write(jsonObject.toString());
            response.setStatus(500);

        } finally {
            out.close();
        }
    }

    /*
     * Generate the JSON Object in this format:
     * {
     *   "value": "<value>",
     *   "data": { "id": <value> }
     * }
     */
    private static JsonObject generateJsonObject(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", id);
        jsonObject.add("data", additionalDataJsonObject);

        return jsonObject;
    }
}