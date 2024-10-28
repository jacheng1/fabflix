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

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
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

        String id = request.getParameter("id");
        String customerId = request.getParameter("customer_id");
        String movieId = request.getParameter("movie_id");
        String quantity = request.getParameter("quantity");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO sales (id, customerId, movieId, saleDate, quantity) VALUES (?, ?, ?, CURRENT_DATE, ?)";

            PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
            insertStatement.setString(1, id);
            insertStatement.setString(2, customerId);
            insertStatement.setString(3, movieId);
            insertStatement.setString(4, quantity);

            insertStatement.executeUpdate();
            insertStatement.close();

            String selectQuery =
                    "SELECT sales.id, sales.customerId, sales.movieId, sales.saleDate, sales.quantity, movies.title " +
                            "FROM sales " +
                            "JOIN movies ON sales.movieId = movies.id " +
                            "WHERE sales.id = LAST_INSERT_ID()";

            PreparedStatement selectStatement = conn.prepareStatement(selectQuery);

            ResultSet rs = selectStatement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String sale_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_quantity = rs.getString("quantity");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sale_id", sale_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_quantity", movie_quantity);

                jsonArray.add(jsonObject);
            }
            rs.close();
            selectStatement.close();

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
