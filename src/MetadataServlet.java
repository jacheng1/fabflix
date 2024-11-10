import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@WebServlet(name = "MetadataServlet", urlPatterns = "/_dashboard/api/metadata")
public class MetadataServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tableResultSet = metaData.getTables("moviedb", null, "%", new String[] { "TABLE" });

            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");

                JsonArray columnJsonArray = new JsonArray();
                ResultSet columnResultSet = metaData.getColumns("moviedb", null, tableName, "%");

                while (columnResultSet.next()) {
                    JsonObject columnJsonObject = new JsonObject();
                    columnJsonObject.addProperty("columnName", columnResultSet.getString("COLUMN_NAME"));
                    columnJsonObject.addProperty("type", columnResultSet.getString("TYPE_NAME"));
                    columnJsonObject.addProperty("size", columnResultSet.getString("COLUMN_SIZE"));

                    columnJsonArray.add(columnJsonObject);
                }
                columnResultSet.close();

                jsonResponse.add(tableName, columnJsonArray);
            }
            tableResultSet.close();

            response.setStatus(200);
            out.write(jsonResponse.toString());

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