package parsers;import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LoadFromFile {
    private static final String URL = "jdbc:mysql://localhost:3306/moviedb";
    private static final String USER = "mytestuser";
    private static final String PASSWORD = "My6$Password";

    public static void loadMoviedata() {
        String filePath = "/src/parsers/movies.txt";

        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_movies LIKE movies;";
        String loadTempTableSQL = "LOAD DATA INFILE '" + filePath + "' " +
                "INTO TABLE temp_movies " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(id, title, year, director);";
        String insertIntoMainTableSQL = "INSERT INTO movies (id, title, year, director) " +
                "SELECT id, title, year, director " +
                "FROM temp_movies " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM movies " +
                "    WHERE movies.id = temp_movies.id " +
                "       OR (movies.title = temp_movies.title AND movies.year = temp_movies.year AND movies.director = temp_movies.director) " +
                ");";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);

            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " rows into the main table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadStarsdata(String filePath) {
        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_stars LIKE stars;";
        String loadTempTableSQL = "LOAD DATA INFILE '" + filePath + "' " +
                "INTO TABLE temp_stars " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(id, name, birthYear);";
        String insertIntoMainTableSQL = "INSERT INTO stars (id, name, birthYear) " +
                "SELECT id, name, birthYear " +
                "FROM temp_stars " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM stars " +
                "    WHERE stars.id = temp_stars.id " +
                "       OR (stars.name = temp_stars.name AND stars.birthYear = temp_stars.birthYear) " +
                ");";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);
            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " rows into the stars table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
