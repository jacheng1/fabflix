package parsers;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/moviedb";
    private static final String USER = "mytestuser";
    private static final String PASSWORD = "My6$Password";
    private int starsAdded = 0;
    private int duplicateStars = 0;
    private int inconsistencies = 0;
    private int duplicateMovies = 0;
    private int genresAdded = 0;
    private int moviesAdded = 0;
    private int genresInMoviesAdded = 0;
    private int starsInMoviesAdded = 0;
    private int moviesNotFound = 0;
    private int starsNotFound = 0;
    private int moviesNoStars = 0;
/*
    public void printInconsistencies() {

        System.out.println("Inserted " + starsAdded);
        System.out.println("Inserted " + genresAdded);
        System.out.println("Inserted " + moviesAdded);
        System.out.println("Inserted " + genresInMoviesAdded);
        System.out.println("Inserted " + starsInMoviesAdded);

        System.out.println(inconsistencies + " movies inconsistent");
        System.out.println(duplicateMovies + " movies duplicate");
        System.out.println(moviesNoStars + " movies has no stars");
        System.out.println(moviesNotFound + " movies not found");
        System.out.println(duplicateStars + " stars duplicate");
        System.out.println(starsNotFound + " stars not found");
    }
*/
   // private ArrayList<String> inconsistencies;
    public void insertStarsInMovies(List<StarinMovie> starmovielist) {
       String insertStarsInMoviesSQL = "INSERT INTO stars_in_movies (starId, movieId) " +
               "SELECT s.id, ? FROM stars s where s.name = ? and not exists (" +
               "SELECT 1 FROM stars_in_movies sim WHERE sim.starId = s.id and sim.movieId = ?)";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(insertStarsInMoviesSQL);

            for (StarinMovie star : starmovielist) {
                try {
                    preparedStatement.setString(1, star.getMovieId());
                    preparedStatement.setString(2, star.getName());
                    preparedStatement.setString(3, star.getMovieId());
                    preparedStatement.executeUpdate();
                    starsInMoviesAdded++;
                }   catch (java.sql.SQLIntegrityConstraintViolationException sq) {
                    System.out.println(sq.getMessage());
                }   catch (SQLException e) {
                    //inconsistencies.add(e);
                    System.out.println(e);
                }
            }
            System.out.println("Inserted " + starsAdded +  " stars.");

        } catch (SQLException e) {
            System.out.println("Error inserting stars: " + e.getMessage());
        }

    }
    public void insertStars(List<Star> starlist) {
        String insertStarSQL = "INSERT INTO stars (id, name, birthyear)" +
                "SELECT CONCAT('nm', (SELECT MAX(SUBSTRING(id, 3)) from stars) + 1), ?, ? FROM stars WHERE NOT EXISTS (SELECT 1 FROM stars WHERE name = ?)";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertStarSQL);

            for (Star star : starlist) {
                try {
                    if (star.getDOB() == 0 || star.getName().isEmpty()) {
                        throw new InvalidParameterException("Missing values");
                    }
                    preparedStatement.setString(1, star.getName());
                    preparedStatement.setInt(2, star.getDOB());
                    preparedStatement.setString(3, star.getName());
                    preparedStatement.executeUpdate();
                    starsAdded++;
                }   catch (java.sql.SQLIntegrityConstraintViolationException sq) {
                    duplicateStars++;
                }   catch (SQLException e) {
                    //inconsistencies.add(e);
                    System.out.println(e);
                }   catch (Exception e) {
                    System.out.println(e);
                }
            }
            System.out.println("Inserted " + starsAdded +  " stars.");

        } catch (SQLException e) {
            System.out.println("Error inserting stars: " + e.getMessage());
        }

    }

    public void insertGenres(List<Movie> movies) {
        String insertGenreSQL = "INSERT INTO genres (name) SELECT ? " +
                "WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertGenreSQL)) {

            for (Movie movie : movies) {
                try {
                    ArrayList<String> genres = movie.getGenre();
                    for (String genre : genres) {
                        preparedStatement.setString(1, genre);
                        preparedStatement.setString(2, genre);
                        preparedStatement.executeUpdate();
                    }
                }   catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    if (e.getMessage().contains("Duplicate entry")) {
                        duplicateMovies++;
                    }   else if (e.getMessage().contains("cannot be null")) {
                        inconsistencies++;
                    }
                    System.out.println(e);
                }
            }
            System.out.println("Movies inserted successfully.");

        } catch (SQLException e) {
            System.out.println("Error inserting movies: " + e.getMessage());
        }

    }
    public void insertMovies(List<Movie> movies) {
        String insertMovieSQL = "INSERT INTO movies (id, title, year, director)" +
        "SELECT ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = ? OR (title = ? and year = ? and director = ?))";


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertMovieSQL)) {

            for (Movie movie : movies) {
                try {
                    if (movie.getId().isEmpty() || movie.getTitle().isEmpty() || movie.getDirector().isEmpty() || movie.getYear() == 0) {
                        throw new InvalidParameterException("Missing values");
                    }
                    preparedStatement.setString(1, movie.getId());
                    preparedStatement.setString(2, movie.getTitle());
                    preparedStatement.setInt(3, movie.getYear());
                    preparedStatement.setString(4, movie.getDirector());
                    preparedStatement.setString(5, movie.getId());
                    preparedStatement.setString(6, movie.getTitle());
                    preparedStatement.setInt(7, movie.getYear());
                    preparedStatement.setString(8, movie.getDirector());
                    preparedStatement.executeUpdate();
                    moviesAdded++;
                }   catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    if (e.getMessage().contains("Duplicate entry")) {
                        duplicateMovies++;
                    }   else if (e.getMessage().contains("cannot be null")) {
                        inconsistencies++;
                    }
                    System.out.println(e);
                }   catch (Exception eq) {
                    inconsistencies++;
                }
            }
            System.out.println("Movies inserted successfully.");

        } catch (SQLException e) {
            System.out.println("Error inserting movies: " + e.getMessage());
        }
    }
}
