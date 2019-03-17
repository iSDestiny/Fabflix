import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;



/**
 * Servlet implementation class BrowseServlet
 */
@WebServlet(name = "AddStar", urlPatterns = "/api/addstar")
public class AddStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		String dob = request.getParameter("dob");
		String name = request.getParameter("star_name");
		
		PrintWriter out = response.getWriter();
		
		try 
		{
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/masterdb");
			
            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
			
//			Connection dbcon = dataSource.getConnection();
			
			String query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

			PreparedStatement statement = dbcon.prepareStatement(query);
			// id = nm9423080 (current max)
			String idQuery = "select max(id) from stars";
			PreparedStatement idStatement = dbcon.prepareStatement(idQuery);
			ResultSet idResult = idStatement.executeQuery();
			
			String id = "";
			if(idResult.next())
			{
				String currentMaxId = idResult.getString(1);
				int parsedMax = Integer.parseInt(currentMaxId.substring(2));
				int new_id = parsedMax + 1;
				id = "nm" + Integer.toString(new_id);
			}
			
			System.out.println("id: " + id + ", dob: " + dob);
			
			statement.setString(1, id);
			statement.setString(2, name);
			
			if(dob == "")
			{
				statement.setNull(3, Types.INTEGER);
			}
			else {
				statement.setInt(3, Integer.parseInt(dob));
			}
			
			statement.executeUpdate();
			
			JsonObject jsonObject = new JsonObject();
			
			jsonObject.addProperty("success", "true");
			jsonObject.addProperty("message", "Successfully added " + name + " into star table");

			
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
			jsonObject.addProperty("message", "Failed to add " + name + " into star table. Error Message: " + e.getMessage());
			out.write(jsonObject.toString());
		}
	}
}