import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CastParser {

    List<Movie> myMovies;
    Document dom;

    public CastParser() {
        //create a list to hold the employee objects
        myMovies = new ArrayList<>();
    }

    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("casts124.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("dirfilms");
        
       // System.out.println(nl.getLength());
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
            	Movie newfilm = new Movie();
                Node movie = (Node) nl.item(i);
                NodeList dir = movie.getChildNodes();
                for (int j = 0; j < dir.getLength(); j++)
                {
                	Node child = (Node) dir.item(j);
                	if (child.getNodeName() == "is")
                	{
                		newfilm.setDirector(child.getTextContent());
                	}
                	else if(child.getNodeName() == "filmc")
                	{
                		NodeList films = dir.item(j).getChildNodes();
                		for (int k = 0; k < films.getLength(); k++)
                		{
                			Node film = (Node) films.item(k);
                			NodeList filmattr = film.getChildNodes();
                			for (int l = 0; l < filmattr.getLength(); l++)
                			{
                				Node attributes = (Node) filmattr.item(l);
                				if (attributes.getNodeName() == "f")
                				{
                					newfilm.setId(attributes.getTextContent());
                				}
                				else if(attributes.getNodeName() == "t")
                				{
                					newfilm.setTitle(attributes.getTextContent());
                				}
                				else if(attributes.getNodeName() == "a")
                				{
                					Star star = new Star();
                					star.setName(attributes.getTextContent());
                					newfilm.addStar(star);
                				}
                			}
                		}
                	}
                }
                myMovies.add(newfilm);
            }
        }
    }


    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
            System.out.println("--------------------");
        }
    }

    public static void main(String[] args) {
        //create an instance
        CastParser dpe = new CastParser();

        //call run example
        dpe.runExample();
    }

}

