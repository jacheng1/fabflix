package parsers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXCastParser extends DefaultHandler {

    List<StarinMovie> cast;

    private String tempVal;
    private boolean validStarInMovie;
    //to maintain context
    private StarinMovie tempStar;

    public SAXCastParser() {
        cast = new ArrayList<StarinMovie>();
    }

    public void runExample() {
        parseDocument();
        //printData();
        writeStarsInMoviesToFile(cast, "src/parsers/cast.txt");
        UpdateDatabase db = new UpdateDatabase();
        //db.insertStarsInMovies(cast);
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

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

        System.out.println("No of Cast members '" + cast.size() + "'.");

        Iterator<StarinMovie> it = cast.iterator();
        while (it.hasNext()) {
            try {
                System.out.println(it.next().toString());
            }   catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static void writeStarsInMoviesToFile(List<StarinMovie> starsinmovies, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (StarinMovie starinmovie : starsinmovies) {
                writer.write(starinmovie.getName() + "\t" + starinmovie.getMovieId() +"\n");
            }
            System.out.println("Actors written to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of employee
            tempStar = new StarinMovie();

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                if (qName.equalsIgnoreCase("f")) {
                    //add it to the list
                    cast.add(tempStar);
                    validStarInMovie = true;
                    if (tempVal.isEmpty()) {
                        validStarInMovie = false;
                        throw new InvalidParameterException("Missing film id");
                    }
                    tempStar.setMovieId(tempVal);
                } else if (qName.equalsIgnoreCase("a")) {
                    if (tempVal.isEmpty()) {
                        validStarInMovie = false;
                        throw new InvalidParameterException("Missing actor name");
                    }
                    tempStar.setName(tempVal);
                } else if (qName.equalsIgnoreCase("t")) {
                    tempStar.setMovieTitle(tempVal);
                }
            }   catch (Exception e) {
                //
            }

    }

    public static void main(String[] args) {
        SAXCastParser spe = new SAXCastParser();
        spe.runExample();
    }

}
