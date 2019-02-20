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
import java.sql.*;

public class ActorParser {

    List<Star> Stars;
    Document dom;

    public ActorParser() {
        //create a list to hold the employee objects
        Stars = new ArrayList<>();
    }

    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        //printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("actors63.xml");

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
        NodeList nl = docEle.getElementsByTagName("actor");
        
        try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "@Rocking1");
			
			if (nl != null && nl.getLength() > 0) {
	            for (int i = 0; i < nl.getLength(); i++) {
	            	Star star = new Star();
	                Node actor = (Node) nl.item(i);
	                NodeList attributes = actor.getChildNodes();
	                for (int j = 0; j < attributes.getLength(); j++)
	                {
	                	Node child = (Node) attributes.item(j);
	                	if (child.getNodeName() == "stagename")
	                	{
	                		star.setName(child.getTextContent());
	                	}
	                	else if(child.getNodeName() == "dob")
	                	{	
	                		String year = child.getTextContent();
	                		
	                		if (year == "")
	                		{
	                			star.setbirthYear(0);
	                		}
	                		else
	                		{
	                			try
	                			{
			                		int yea = Integer.parseInt(year);
			                		star.setbirthYear(yea);
	                			}
	                			catch(Exception e)
	                			{
	                				System.out.println("Birthdate is in wrong format");
	                				star.setbirthYear(0);
	                			}
	                		}
	                	}
	                }
	                String max = "select id from stars order by id DESC LIMIT 1";
	                PreparedStatement maxo = connection.prepareStatement(max);
	                ResultSet rs = maxo.executeQuery();
	                String max_id = ""; 
	                
	                while (rs.next())
	                {
	                	max_id = rs.getString("id");
	                }
	                System.out.println("Id is: " + max_id);
	                String nums = max_id.substring(2);
	                int maxim = Integer.parseInt(nums);
	                maxim += 1;
	                
	                star.setId("nm" + maxim);
	                
	                String insert = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
	                PreparedStatement inserto = connection.prepareStatement(insert);
	                inserto.setString(1, star.getId());
	                inserto.setString(2, star.getName());
	                inserto.setInt(3, star.getbirthYear());
	                inserto.executeUpdate();
	                Stars.add(star);
	            }
	        }
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    }


    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Stars '" + Stars.size() + "'.");

        Iterator<Star> it = Stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
            System.out.println("--------------------");
        }
    }

    public static void main(String[] args) {
        //create an instance
        ActorParser dpe = new ActorParser();

        //call run example
        dpe.runExample();
    }

}
