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
        String insertRatingSQL = "INSERT INTO ratings (movieId, rating, numVotes) " +
                "SELECT id, r_rating, 0 " +
                "FROM ( " +
                "    SELECT id, ROUND(RAND() * 10, 1) AS r_rating" +
                "    FROM temp_movies " +
                ") AS temp " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM ratings " +
                "    WHERE ratings.movieId = temp.id " +
                ");";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(loadTempTableSQL)) {

            statement.execute("SET GLOBAL local_infile = 1");
            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);



            int rowsInserted = statement.executeUpdate(insertIntoMainTableSQL);
            System.out.println("Inserted " + rowsInserted + " movies");
            statement.execute(insertRatingSQL);

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
            System.out.println("Inserted " + rowsInserted + " stars.");

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
            System.out.println("Inserted " + rowsInserted + " stars_in_movies.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadGenresInMovies(String filePath) {
        String createTempTableSQL = "CREATE TEMPORARY TABLE temp_genres_in_movies (" +
                "movieId VARCHAR(255), " +
                "genreName VARCHAR(255));";
        String loadTempTableSQL = "LOAD DATA LOCAL INFILE '" + filePath + "' " +
                "INTO TABLE temp_genres_in_movies " +
                "FIELDS TERMINATED BY '\\t' " +
                "LINES TERMINATED BY '\\n' " +
                "(movieId, genreName);";

        String insertGenresSQL = "INSERT INTO genres (name) " +
                "SELECT DISTINCT LOWER(temp.genreName) " +
                "FROM temp_genres_in_movies temp " +
                "WHERE NOT EXISTS (" +
                "SELECT 1 FROM genres g WHERE LOWER(g.name) = LOWER(temp.genreName));";

        String insertGenresInMoviesSQL = "INSERT INTO genres_in_movies (genreId, movieId) " +
                "SELECT DISTINCT g.id, temp.movieId " +
                "FROM temp_genres_in_movies temp " +
                "JOIN genres g ON LOWER(temp.genreName) = LOWER(g.name) " +
                "JOIN movies m ON temp.movieId = m.id " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM genres_in_movies gim " +
                "    WHERE gim.genreId = g.id AND gim.movieId = temp.movieId);";

        String cleanUpGenresInMoviesSQL = "DELETE FROM genres_in_movies " +
                "WHERE movieId IN ( " +
                "    SELECT id FROM movies " +
                "    WHERE id NOT IN (SELECT movieId FROM stars_in_movies) " +
                ");";
        String cleanUpRatingsSQL = "DELETE FROM ratings " +
                "WHERE movieId IN ( " +
                "    SELECT id FROM movies " +
                "    WHERE id NOT IN (SELECT movieId FROM stars_in_movies) " +
                ");";

        String cleanUpMoviesSQL = "DELETE FROM movies " +
                "WHERE id NOT IN (SELECT movieId FROM stars_in_movies);";


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.execute("SET GLOBAL local_infile = 1");
            statement.execute(createTempTableSQL);
            statement.execute(loadTempTableSQL);
            int gInserted = statement.executeUpdate(insertGenresSQL);
            System.out.println("Inserted " + gInserted + " genres");
            int gimInserted = statement.executeUpdate(insertGenresInMoviesSQL);
            System.out.println("Inserted " + gimInserted + " genres_in_movies ");
            statement.execute(cleanUpGenresInMoviesSQL);
            statement.execute(cleanUpRatingsSQL);
            statement.execute(cleanUpMoviesSQL);
            statement.execute("DROP TEMPORARY TABLE IF EXISTS temp_genres_in_movies");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}