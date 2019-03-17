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



/**
 * Servlet implementation class BrowseServlet
 */
@WebServlet(name = "AddMovie", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		String dob = request.getParameter("dob");
		String name = request.getParameter("star_name");
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String genre = request.getParameter("genre");
		
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


			String query = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?)}";

			PreparedStatement statement = dbcon.prepareStatement(query);
			// id = nm9423080 (current max)
			String starIdQuery = "select max(id) from stars";
			PreparedStatement starIdStatement = dbcon.prepareStatement(starIdQuery);
			ResultSet starIdResult = starIdStatement.executeQuery();
			
			String star_id = "";
			if(starIdResult.next())
			{
				String currentMaxId = starIdResult.getString(1);
				int parsedMax = Integer.parseInt(currentMaxId.substring(2));
				int new_id = parsedMax + 1;
				star_id = "nm" + Integer.toString(new_id);
			}
			
			String movieIdQuery = "select max(id) from movies";
			PreparedStatement movieIdStatement = dbcon.prepareStatement(movieIdQuery);
			ResultSet movieIdResult = movieIdStatement.executeQuery();
			
			String movie_id = "";
			if(movieIdResult.next())
			{
				String currentMaxId = movieIdResult.getString(1);
				int parsedMax = Integer.parseInt(currentMaxId.substring(2));
				int new_id = parsedMax + 1;
				movie_id = "tt" + Integer.toString(new_id);
			}
			
			//System.out.println("id: " + id + ", dob: " + dob);
			statement.setString(1, movie_id);
			statement.setString(2, title);
			statement.setInt(3, Integer.parseInt(year));
			statement.setString(4, director);
			statement.setString(5, genre);
			statement.setString(6, star_id);
			statement.setString(7, name);
			
			if(dob == "")
			{
				statement.setNull(8, Types.INTEGER);
			}
			else {
				statement.setInt(8, Integer.parseInt(dob));
			}
			
			JsonObject jsonObject = new JsonObject();
			
			ResultSet rs = statement.executeQuery();
			if(rs.next())
			{
				String success = String.valueOf(rs.getBoolean("success"));
				if(success.equals("true"))
				{
					jsonObject.addProperty("success", "true");
					jsonObject.addProperty("message", "Successfully added " + title + " into database");
				}
				else
				{
					jsonObject.addProperty("success", "false");
					jsonObject.addProperty("message", "Failed to add " + title + " into database, it already exists");
				}
			}
			
            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
            starIdResult.close();
            movieIdResult.close();
            statement.close();
            dbcon.close();
			
		} catch(Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("success", "false");
			jsonObject.addProperty("message", "Failed to add " + title + " into database. Error Message: " + e.getMessage());
			out.write(jsonObject.toString());
		}
	}
}