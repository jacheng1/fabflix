package parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    public List<Movie> parseMovies(String fileName) {
        List<Movie> movies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    String id = parts[0];
                    String title = parts[1];
                    int year = Integer.parseInt(parts[2]);
                    String director = parts[3];
                    ArrayList<String> genres = new ArrayList<>();

                    Movie movie = new Movie(title, year, id, director, genres);
                    if (isValidMovie(movie)) {
                        movies.add(movie);
                    } else {
                        System.out.println("Invalid movie data: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return movies;
    }

    private boolean isValidMovie(Movie movie) {
        if (movie.getId() == null || movie.getId().isEmpty()) return false;
        if (movie.getTitle() == null || movie.getTitle().isEmpty()) return false;
        if (movie.getYear() <= 0) return false;
        if (movie.getDirector() == null || movie.getDirector().isEmpty()) return false;

        return true;
    }
}
