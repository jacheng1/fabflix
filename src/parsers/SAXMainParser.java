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

    private String tempVal;
    private boolean validMovie;
    //to maintain context
    private Movie tempMov;
    private int inconsistencies;
    int duplicateMovies = 0;
    private String tempDir;

    public SAXMainParser() {
        movies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        //printData();
        writeMoviesToFile(movies, "src/parsers/movies.txt");
        UpdateDatabase db = new UpdateDatabase();
        //db.insertMovies(movies);
        //System.out.println("Inserted " + movies.size() + " movies");
        //db.insertGenres(movies);
       // db.printInconsistencies();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
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

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<Movie> it = movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public static void writeMoviesToFile(List<Movie> movies, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Movie movie : movies) {
               // if (!movie.getGenre().isEmpty()) {
                if (movie.getTitle() == "Monster's Ball") {
                    System.out.println("found monsters ball");
                }
                    writer.write(movie.getId() + "\t" + movie.getTitle() + "\t" + movie.getYear() + "\t" + movie.getDirector() + "\n");
                //}
            }
            System.out.println("Movies written to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/parsers/genres-in-movies.txt"))) {
            for (Movie movie : movies) {
                for (String genre : movie.getGenre()) {
                    if (!genre.isEmpty()) {
                        writer.write(movie.getId() + "\t" + genre + "\n");
                    }
                }
            }
            System.out.println("Movies written to " + "src/parsers/genres-in-movies.txt");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset

        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMov = new Movie();
            validMovie = true;
            //tempMov.setDirector(tempDir);

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (qName.equalsIgnoreCase("fid") && validMovie) {
                if (tempVal.isEmpty() || tempVal == null) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing title");
                }
                tempMov.setId(tempVal);
            } else if (qName.equalsIgnoreCase("t") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing title"+ tempMov.getId());
                }
                tempMov.setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("year") && validMovie) {

                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing year"+ tempMov.getId());
                }
                tempMov.setYear(Integer.parseInt(tempVal));

            } else if (qName.equalsIgnoreCase("cat") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing Genre"+ tempMov.getId());
                }
                tempMov.setGenre(tempVal);
            } else if (qName.equalsIgnoreCase("dirn") && validMovie) {
                if (tempVal.isEmpty()) {
                    validMovie = false;
                    throw new InvalidParameterException("Missing director" + tempMov.getId());
                }
                tempMov.setDirector(tempVal);
            }
            else if (qName.equalsIgnoreCase("film")) {
                if (validMovie) System.out.println(tempMov.getId() + " is a valid movie");
                if (!validMovie) {
                    System.out.println("this is not a valid movie " + tempMov.getId());
                } else if (!isUnique(tempMov, movies)) {
                    System.out.println("not adding this move: " + tempMov.getId());
                }   else if (!tempMov.getGenre().isEmpty() && !tempMov.getId().isEmpty() && !tempMov.getTitle().isEmpty() && !tempMov.getDirector().isEmpty() && tempMov.getYear() != 0) {
                    movies.add(tempMov);
                }
            }
        }   catch (InvalidParameterException e) {
            System.out.println(e);
        }   catch(Exception e) {
            //
        }

    }
    public  boolean isUnique(Movie m, List<Movie> movies) {
        for (Movie m1 : movies) {
            if (m1.getId().equals(m.getId()) || (m1.getTitle().equals(m.getTitle()) && m1.getDirector().equals(m.getDirector()) && m1.getYear()==(m.getYear()))) {
                System.out.println(m1.getId() + " is a duplicate");
                duplicateMovies++;
                return false;
            }
        }
        return true;
    }
    public int getDuplicateMovies() {
        return duplicateMovies;
    }
    public static void main(String[] args) {
        SAXMainParser spe = new SAXMainParser();
        spe.runExample();
    }

}

