package parsers;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class LoadFromFile {
    private static final String URL = "jdbc:mysql://localhost:3306/moviedb?allowLoadLocalInfile=true";
    private static final String USER = "mytestuser";
    private static final String PASSWORD = "My6$Password";


    public static void loadMoviedata() {
        String filePath = "src/parsers/movies.txt";

        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_movies LIKE movies;";
        String loadTempTableSQL = "LOAD DATA LOCAL INFILE '" + filePath + "' " +
                "INTO TABLE temp_movies " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' ";
        String insertIntoMainTableSQL = "INSERT INTO movies (id, title, year, director, price) " +
                "SELECT id, title, year, director, r_price " +
                "FROM (" +
                "SELECT id, title, year, director, ROUND(5.00 + (RAND() * (20.00 - 5.00)), 2) AS r_price " +
                "FROM temp_movies ) as temp " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM movies " +
                "    WHERE movies.id = temp.id " +
                "       OR (movies.title = temp.title AND movies.year = temp.year AND movies.director = temp.director) " +
                ");";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(loadTempTableSQL)) {

            statement.execute("SET GLOBAL local_infile = 1");
            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);

            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " rows into the main table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadStarsdata(String filePath) {
        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_stars (name VARCHAR(255), birthYear INT);";
        String loadTempTableSQL = "LOAD DATA LOCAL INFILE '" + filePath + "' " +
                "INTO TABLE temp_stars " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' ";
        String insertIntoMainTableSQL = "INSERT INTO stars (id, name, birthYear) " +
                "SELECT CONCAT('nm', LEFT(UUID(), 8)), name, birthYear " +
                "FROM temp_stars " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM stars " +
                "    WHERE temp_stars.birthYear = 0" +
                "       OR (stars.name = temp_stars.name AND stars.birthYear = temp_stars.birthYear) " +
                ");";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute("SET GLOBAL local_infile = 1");
            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);
            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " rows into the stars table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCast(String filePath) {
        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_stars_in_movies (" +
                "starName VARCHAR(255), " +
                "movieId VARCHAR(255));";
        String loadTempTableSQL = "LOAD DATA LOCAL INFILE '" + filePath + "' " +
                "INTO TABLE temp_stars_in_movies " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' " +
                "(starName, movieId);";
        String insertIntoMainTableSQL = "INSERT INTO stars_in_movies (starId, movieId) " +
                "SELECT stars.id, temp.movieId " +
                "FROM temp_stars_in_movies as temp " +
                "JOIN stars ON stars.name = temp.starName " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM stars_in_movies as sim " +
                "    WHERE sim.starId = stars.id AND sim.movieId = temp.movieId) " +
                "AND EXISTS ( " +
                "    SELECT 1 " +
                "    FROM movies m " +
                "    WHERE m.id = temp.movieId);";


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute("SET GLOBAL local_infile = 1");
            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);
            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " rows into the stars_in_movies table.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
