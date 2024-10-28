import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/checkout")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 5L; // define serial UID

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
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        JsonObject responseJsonObject = new JsonObject();
        System.out.println("Here in doGet in ShoppingCartServlet");

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        PrintWriter out = response.getWriter();
        System.out.println(1);
        try (Connection conn = dataSource.getConnection()) {
            // define SQL query

            String query = "SELECT m.id, m.title, m.price " +
                    "FROM moviedb.movies m " +
                    "WHERE m.id = ? " +
                    "GROUP BY m.id, m.title, m.price";

            JsonArray jsonArray = new JsonArray();
            Map<String, Integer> movieQuantity = new HashMap<>();

            for (String item : previousItems) {
                if (movieQuantity.containsKey(item)) {
                    movieQuantity.put(item, movieQuantity.get(item) + 1);
                }   else {
                    movieQuantity.put(item, 1);
                }
            }
            for (String previousItem : previousItems) {
                System.out.println("im jere" + previousItem);

                PreparedStatement statement = conn.prepareStatement(query); // declare statement
                statement.setString(1, previousItem);

                ResultSet rs = statement.executeQuery();

                while(rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    // double price = rs.getString("price");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("price", rs.getDouble("price"));
                    jsonObject.addProperty("quantity", movieQuantity.get(movie_id));
                    jsonArray.add(jsonObject);
                }
                rs.close(); // close rs
                statement.close();
            }

             // set parameter shown as "?" in SQL query to id retrieved from URL; 1 indicates first "?" in query
            System.out.println(jsonArray.toString());
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

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieID= request.getParameter("movie_id");
        String cartEvent = request.getParameter("cartEvent");

        System.out.println("are you null?" + movieID + " " + cartEvent);

        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> previousItems = (ArrayList<String>)session.getAttribute("previousItems");

        if (previousItems == null) {
            previousItems = new ArrayList<String>();
            previousItems.add(movieID);

            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                switch (cartEvent) {
                    case "add":
                        previousItems.add(movieID);

                        break;
                    case "subtract":
                        previousItems.remove(movieID);

                        break;
                    case "remove-from-cart":
                        previousItems.removeIf(item -> item.equals(movieID));

                        break;
                }
            }
        }

        JsonObject responseJsonObject = new JsonObject();
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);

        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}
