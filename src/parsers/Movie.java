package parsers;

import java.util.ArrayList;
import java.util.List;

public class Movie{
    private String title;
    private int year;
    private String id;
    private String director;
    private ArrayList<String> genre;
    List<Genre> genres;

    public Movie(){
        genre = new ArrayList<>();
        genres = new ArrayList<>();
    }

    public Movie(String title, int year, String id, String director, ArrayList<String> genre) {
        this.title = title;
        this.year = year;
        this.id = id;
        this.director = director;
        this.genre = genre;
    }

    public Movie(String title, int year, String id, String director) {
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public List<Genre> getGenreList() {
        return this.genres;
    }

    public void setGenre(String genre) {
        try {
            this.genre.add(genre);
        } catch (Exception e) { System.out.println(e); }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Title: " + title);
        sb.append("Year: " + year);
        sb.append("ID: " + id);
        sb.append("Director: " + director);
        sb.append("Genre: " + genre);

        return sb.toString();
    }
}