
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXActorParser extends DefaultHandler {

    List<Star> stars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public SAXActorParser() {
        stars = new ArrayList<Star>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a fStary
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("actors63.xml", this);

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

        System.out.println("No of Employees '" + stars.size() + "'.");

        Iterator<Star> it = stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempStar = new Star();

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("stagename")) {
            //add it to the list
            stars.add(tempStar);
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempStar.setDOB(Integer.parseInt(tempVal));
            }   catch (Exception e) {
                System.out.println(e);
            }
        }


    }

    public static void main(String[] args) {
        SAXActorParser spe = new SAXActorParser();
        spe.runExample();
    }

}
