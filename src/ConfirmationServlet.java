import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb_master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        String customerId = (String) session.getAttribute("customerId");

        System.out.println("before requesting params");


        PrintWriter out = response.getWriter();
        System.out.println("before trying connection");
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("successfully got connection");

            String insertQuery = "INSERT INTO sales (customerId, movieId, saleDate, quantity) VALUES (?, ?, CURRENT_DATE, ?)";

            Map<String, Integer> movieQuantity = new HashMap<>();
            JsonArray jsonArray = new JsonArray();

            for (String item : previousItems) {
                if (movieQuantity.containsKey(item)) {
                    movieQuantity.put(item, movieQuantity.get(item) + 1);
                }   else {
                    movieQuantity.put(item, 1);
                }
            }

            ArrayList<String> moviesAddedToDB = new ArrayList<>();

            for (String previousItem : previousItems) {
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);

                if (!moviesAddedToDB.contains(previousItem)) {
                    moviesAddedToDB.add(previousItem);

                    insertStatement.setString(1, customerId);
                    insertStatement.setString(2, previousItem);
                    insertStatement.setString(3, String.valueOf(movieQuantity.get(previousItem)));

                    System.out.println("successfully got insert statement");
                    System.out.println(insertStatement.toString());
                    insertStatement.executeUpdate();
                    insertStatement.close();

                    System.out.println("successfully executed insert statement");
                    String selectQuery =
                            "SELECT sales.id, sales.customerId, sales.movieId, sales.saleDate, sales.quantity, movies.title, movies.price " +
                                    "FROM sales " +
                                    "JOIN movies ON sales.movieId = movies.id " +
                                    "WHERE sales.id = LAST_INSERT_ID()";

                    PreparedStatement selectStatement = conn.prepareStatement(selectQuery);

                    ResultSet rs = selectStatement.executeQuery();
                    System.out.println("successfully executed select statement");

                    while (rs.next()) {
                        String sale_id = rs.getString("id");
                        String movie_title = rs.getString("title");
                        String movie_quantity = rs.getString("quantity");
                        String movie_price = rs.getString("price");

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("sale_id", sale_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_quantity", movie_quantity);
                        jsonObject.addProperty("movie_price", movie_price);

                        jsonArray.add(jsonObject);
                    }
                    rs.close();
                    selectStatement.close();
                }
            }
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            out.write(jsonArray.toString());
            session.removeAttribute("previousItems");
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
