import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;  

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class AddStarServlet
 */

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-star")
public class AddStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddStarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		System.out.println("in add-star servlet");
		String new_dob = request.getParameter("dob");
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
		
		String name = request.getParameter("star_name");
		
		PrintWriter out = response.getWriter();
		
		try 
		{
			java.util.Date date = simpleDate.parse(new_dob);
			java.sql.Date dob = new java.sql.Date(date.getTime()); 
			
			Connection dbcon = dataSource.getConnection();

			String query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

			PreparedStatement statement = dbcon.prepareStatement(query);
			// id = nm9423080 (current max)
			String idQuery = "select max(id) from stars";
			PreparedStatement idStatement = dbcon.prepareStatement(idQuery);
			ResultSet idResult = idStatement.executeQuery();
			
			String id = "";
			if(idResult.next())
			{
				String currentMaxId = idResult.getString("id");
				int parsedMax = Integer.parseInt(currentMaxId.substring(2));
				int new_id = parsedMax + 1;
				id = "nm" + Integer.toString(new_id);
			}
			
			System.out.println("id: " + id + ", dob: " + dob);
			
			statement.setString(1, id);
			statement.setString(2, name);
			statement.setDate(3, dob);
			statement.executeUpdate();
			
			JsonObject jsonObject = new JsonObject();
			
			jsonObject.addProperty("success", "true");
			jsonObject.addProperty("message", "Successfully added " + name + "into star table");

			
            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
            idResult.close();
            statement.close();
            dbcon.close();
			
		} catch(Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("success", "false");
			jsonObject.addProperty("message", "Failed to add " + name + "into star table. Error Message: " + e.getMessage());
			out.write(jsonObject.toString());
		}
	}



}
