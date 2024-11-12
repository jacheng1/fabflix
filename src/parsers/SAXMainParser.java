package parsers;

import java.io.*;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXMainParser extends DefaultHandler {
    List<Movie> movies;
    private HashMap<String,Integer> genreIdMap;

    private String tempVal;
    private boolean validMovie;
    // to maintain context
    private Movie tempMov;
    int duplicateMovies = 0;
    private String tempDir;
    private Integer genreId;

    public SAXMainParser() {
        movies = new ArrayList<Movie>();
    }

    public void runExample() throws IOException, FileNotFoundException, UnsupportedEncodingException {
        parseDocument();
        // printData();
        writeMoviesToFile(movies, "src/parsers/movies.txt");
        UpdateDatabase db = new UpdateDatabase();
        // db.insertMovies(movies);
        System.out.println("Inserted " + movies.size() + " movies");
        writeGenreFile();
        // db.insertGenres(movies);
        writeGenreInMoviesToFile();
        // db.printInconsistencies();
    }

    private void parseDocument() {

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Employees '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public static void writeMoviesToFile(List<Movie> movies, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Movie movie : movies) {
                writer.write(movie.getId() + "\t" + movie.getTitle() + "\t" + movie.getYear() + "\t" + movie.getDirector() + "\n");
            }
            System.out.println("Movies written to " + fileName);

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        } try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/parsers/genres-in-movies.txt"))) {
            for (Movie movie : movies) {
                for (String genre : movie.getGenre()) {
                    writer.write(movie.getId() + "\t" + genre + "\n");
                }
            }
            System.out.println("Movies written to " + "src/parsers/genres-in-movies.txt");

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private void writeGenreFile() throws IOException {
        PrintWriter writer = new PrintWriter("genres.txt", "UTF-8");
        Set<Genre> hash_Set = new HashSet<Genre>();
        PrintWriter badFile = new PrintWriter("genresInconsistencies.md", "UTF-8");

        Iterator<Movie> movieIterator = movies.iterator();
        while (movieIterator.hasNext()) {
            Movie m = movieIterator.next();

            Iterator<Genre> currentGenre = m.getGenreList().iterator();
            while (currentGenre.hasNext()){
                Genre genre = currentGenre.next();
                if (!genre.getInconsistent()) {
                    hash_Set.add(genre);
                }
                else {
                    badFile.printf("- Genre Name: %s\n", genre.getName());
                }
            }
        }

        Iterator<Genre> i = hash_Set.iterator();
        while (i.hasNext()) {
            Genre g = i.next();
            if (!genreIdMap.containsKey(g.getName())){
                ++genreId;
                g.setId(genreId);
                genreIdMap.put(g.getName(), g.getId());

                writer.printf("%s,%s\n", g.getId(), g.getName());
            }
        }

        writer.close();
        badFile.close();
    }

    private void writeGenreInMoviesToFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("genres_in_movies2.txt", "UTF-8");
        PrintWriter inconsistencyFile = new PrintWriter("genreInMoviesInconsistencies.md", "UTF-8");

        Iterator<Movie> iterator = movies.iterator();
        while (iterator.hasNext()){
            Movie currentMovie = iterator.next();

            Iterator<Genre> currentGenre = currentMovie.getGenreList().iterator();
            while (currentGenre.hasNext()) {
                Genre g = currentGenre.next();
                if (g.getId() != null)
                    writer.printf("%d, %s\n", g.getId(), currentMovie.getId());

                else {
                    inconsistencyFile.printf("- Genre %s not found", g.getName());
                }
            }
        }

        writer.close();
        inconsistencyFile.close();
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";

        if (qName.equalsIgnoreCase("dirname")) {
            tempDir = attributes.getValue("dirname");
        }
        if (qName.equalsIgnoreCase("film")) {
            // create a new instance of employee
            tempMov = new Movie();
            tempMov.setDirector(tempDir);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("film")) {
                //add it to the list
                validMovie = true;

                movies.add(tempMov);

            } else if (qName.equalsIgnoreCase("t") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing title");
                }

                tempMov.setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("fid") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;

                    throw new InvalidParameterException("Missing title");
                }

                tempMov.setId(tempVal);
            } else if (qName.equalsIgnoreCase("year") && validMovie) {

                if (tempVal.isEmpty()) {
                    validMovie = false;

                    throw new InvalidParameterException("Missing title");
                }

                tempMov.setYear(Integer.parseInt(tempVal));
            } else if (qName.equalsIgnoreCase("cat") && validMovie) {
                tempMov.setGenre(tempVal);
            } else if (qName.equalsIgnoreCase("dirn") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing title");
                }

                tempMov.setDirector(tempVal);
            }
            else if (qName.equalsIgnoreCase("/film") && !validMovie && isUnique(tempMov, movies)) {
                movies.remove(tempMov);
            }
        }   catch (Exception e) {
            //
        }

    }

    public  boolean isUnique(Movie m, List<Movie> movies) {
        for (Movie m1 : movies) {
            if (m1.getId().equals(m.getId())) {
                duplicateMovies++;

                return false;
            }
        }
        return true;
    }

    public int getDuplicateMovies() {
        return duplicateMovies;
    }

    public static void main(String[] args) throws IOException {
        SAXMainParser spe = new SAXMainParser();
        spe.runExample();
    }
}