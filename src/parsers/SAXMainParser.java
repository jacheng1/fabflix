package parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXMainParser extends DefaultHandler {

    List<Movie> movies;

    private String tempVal;

    //to maintain context
    private Movie tempMov;

    public SAXMainParser() {
        movies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        printData();
        writeMoviesToFile(movies, "movies");

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
                for (String genre : movie.getGenre()) {
                    writer.write(movie.getId() + "\t" + genre + "\n");
                }
            }
            System.out.println("Movies written to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMov = new Movie();

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            movies.add(tempMov);

        } else if (qName.equalsIgnoreCase("t")) {
            tempMov.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMov.setId(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMov.setYear(Integer.parseInt(tempVal));
            }   catch (Exception e) {
                System.out.println(e);
            }
        }
        else if (qName.equalsIgnoreCase("cat")) {
            tempMov.setGenre(tempVal);
        }
        else if (qName.equalsIgnoreCase("dirn")) {
            tempMov.setDirector(tempVal);
        }

    }

    public static void main(String[] args) {
        SAXMainParser spe = new SAXMainParser();
        spe.runExample();
    }

}
