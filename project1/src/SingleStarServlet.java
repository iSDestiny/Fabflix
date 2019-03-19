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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");

		String id = request.getParameter("id");

		PrintWriter out = response.getWriter();

		try 
		{
			
			// Start connection pooling
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localdb");
			
            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
			
//			Connection dbcon = dataSource.getConnection();

			String query = "SELECT * from stars where id = ?";

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);

			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();

			while (rs.next()) 
			{
				String starId = rs.getString("id");
				String starName = rs.getString("name");
				String starDob = rs.getString("birthYear");

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("star_id", starId);
				jsonObject.addProperty("star_name", starName);
				jsonObject.addProperty("star_dob", starDob);
				jsonObject.addProperty("movie_list_url", (String) request.getSession().getAttribute("movielistURL"));
				
				String moviesQuery = "SELECT m.id, m.title\r\n" +
									 "FROM movies m, stars s, stars_in_movies sm\r\n" +
									 "WHERE s.id = sm.starId and sm.movieId = m.id and s.id = ?";
				
				PreparedStatement moviestatement = dbcon.prepareStatement(moviesQuery);
				moviestatement.setString(1, id);
				
				ResultSet moviesrs = moviestatement.executeQuery();
				JsonArray moviesArray = new JsonArray();
				
				while (moviesrs.next())
				{
					JsonObject movieJsonObject = new JsonObject();
					movieJsonObject.addProperty("movie_id", moviesrs.getString("id"));
					movieJsonObject.addProperty("movie_title", moviesrs.getString("title"));
					moviesArray.add(movieJsonObject);
				}
				
				jsonObject.add("stars_in", moviesArray);
				jsonArray.add(jsonObject);
			}
			
            out.write(jsonArray.toString());
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} 
		catch (Exception e) 
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();

	}

}
