package parsers;

import java.util.ArrayList;

public class StarinMovie {
    private String name;
    private String id;
    private String movieId;
    private String movieTitle;

    public StarinMovie() {
    }

    public StarinMovie(String name, String movieId, String movieTitle) {
        this.name = name;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() { return movieTitle; }

    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star in Movie Details - ");
        sb.append("Name: " + getName());
        sb.append(", ");
        sb.append("Movie:" + getMovieTitle());
        sb.append(", ");
        sb.append("Movie Id:" + getMovieId());
        sb.append(", ");

        return sb.toString();
    }
}
