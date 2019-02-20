import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.sql.*;

public class MainParser {

    List<Movie> myMovies;
    Document dom;

    public MainParser() {
        //create a list to hold the employee objects
        myMovies = new ArrayList<>();
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
            dom = db.parse("mains243.xml");

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
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        
        try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "Jason", "Jimmy123$");
			
			if (nl != null && nl.getLength() > 0) {
	            for (int i = 0; i < nl.getLength(); i++) {
	            	Movie newfilm = new Movie();
	                Node movie = (Node) nl.item(i);
	                NodeList dir = movie.getChildNodes();
	                for (int j = 0; j < dir.getLength(); j++)
	                {
	                	Node child = (Node) dir.item(j);
	                	if (child.getNodeName() == "director")
	                	{
	                		NodeList director = dir.item(j).getChildNodes();
	                		for (int k = 0; k < director.getLength(); k++)
	                		{
	                			Node child2 = (Node) director.item(k);
	                			if (child2.getNodeName() == "dirname" || child2.getNodeName() == "dirn")
	                				newfilm.setDirector(child2.getTextContent());
	                		}
	                	}
	                	else if(child.getNodeName() == "films")
	                	{
	                		NodeList films = dir.item(j).getChildNodes();
	                		for (int k = 0; k < films.getLength(); k++)
	                		{
	                			Node film = (Node) films.item(k);
	                			NodeList filmattr = film.getChildNodes();
	                			for (int l = 0; l < filmattr.getLength(); l++)
	                			{
	                				Node attributes = (Node) filmattr.item(l);
	                				if (attributes.getNodeName() == "fid")
	                				{
	                					newfilm.setId(attributes.getTextContent());
	                				}
	                				else if(attributes.getNodeName() == "t")
	                				{
	                					if (attributes.getTextContent() == "" || attributes.getTextContent() == null)
	                					{
	                						System.out.println("Title cannot be null");
	                						newfilm.setTitle("No Title Given");
	                					}
	                					newfilm.setTitle(attributes.getTextContent());
	                				}
	                				else if(attributes.getNodeName() == "year")
	                				{
	                					try
	                					{
	                						if (attributes.getNodeName() == "" || attributes.getNodeName() == null)
	                						{
	                							newfilm.setYear(0);
	                						}
	                						else
	                						{
			                					int year = Integer.parseInt(attributes.getTextContent());
			                					newfilm.setYear(year);
	                						}
	                					}
	                					catch(Exception e)
	                					{
	                						System.out.println("Movie year is in wrong format");
	                						newfilm.setYear(0);
	                					}
	                				}
	                				else if(attributes.getNodeName() == "cats")
	                				{
	                					if (attributes.hasChildNodes())
	                					{
		                					NodeList genres = attributes.getChildNodes();
		                					if (genres != null && genres.getLength() > 0)
		                					{
		                						for (int z = 0; z < genres.getLength(); z++)
		                						{
		                							Genre movie_genre = new Genre();
		                							Node genre = (Node) genres.item(z);
		                							movie_genre.setName(genre.getTextContent());
		                							movie_genre.setId(100);
		                							newfilm.addGenre(movie_genre);
		                						}
		                					}
	                					}
	                				}
	                			}
	                		}
	                	}
	                }
	                
	                String max = "select id from movies order by id DESC LIMIT 1";
	                PreparedStatement maxo = connection.prepareStatement(max);
	                ResultSet rs = maxo.executeQuery();
	                String max_id = ""; 
	                
	                while (rs.next())
	                {
	                	max_id = rs.getString("id");
	                }
	                
	                String nums = max_id.substring(2);
	                int maxim = Integer.parseInt(nums);
	                maxim += 1;
	                
	                newfilm.setId("ZZ" + maxim);
	                
	                String insert = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
	                PreparedStatement inserto = connection.prepareStatement(insert);
	                inserto.setString(1, newfilm.getId());
	                inserto.setString(2, newfilm.getTitle());
	                inserto.setInt(3, newfilm.getYear());
	                inserto.setString(4, newfilm.getDirector());
	                inserto.executeUpdate();
	                
	                if (newfilm.getGenre().size() > 0)
	                {
	                	String query = "select name from genres";
                		PreparedStatement que = connection.prepareStatement(query);
                		ResultSet rs2 = que.executeQuery();
                		
                		ArrayList<String> possible = new ArrayList<String>();
                		while (rs2.next())
                		{
                			String gen = rs2.getString("name");
                			possible.add(gen);
                		}
                		
                		
	                	for (int s = 0; s < newfilm.getGenre().size(); s++)
	                	{	
	                		if (!possible.contains(newfilm.getGenre().get(s).getName()))
	                		{
	                			String geop = "INSERT INTO genres (name) VALUES (?)";
	        	                PreparedStatement statement = connection.prepareStatement(geop);
	        	                statement.setString(1, newfilm.getGenre().get(s).getName());
	        	                statement.executeUpdate();
	                		}
	                		
	                		String q = "select id from genres where name = ?";
	                		PreparedStatement statement1 = connection.prepareStatement(q);
	                		statement1.setString(1, newfilm.getGenre().get(s).getName());
	                		ResultSet r5 = statement1.executeQuery();
	                		
	                		while (r5.next())
	                		{
	                			try
	                			{
		                			int genreId = r5.getInt("id");
		                			String insr = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
			                		PreparedStatement update = connection.prepareStatement(insr);
			                		update.setInt(1, genreId);
			                		update.setString(2, newfilm.getId());
			                		update.executeUpdate();
	                			}
	                			catch(Exception e)
	                			{
	                				
	                			}
	                		}
	                		
	                	}
	                }
	                myMovies.add(newfilm);
	            }
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    public List<Movie> getMain()
    {
    	return myMovies;
    }

    public static void main(String[] args) {
        //create an instance
        MainParser dpe = new MainParser();
        
        //call run example
        dpe.runExample();
    }

}
