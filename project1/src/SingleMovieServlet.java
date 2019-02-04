import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");

		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();

		try 
		{
			Connection dbcon = dataSource.getConnection();

			String query = "SELECT id,title,year,director,rating \r\n" +
							"FROM movies m, ratings r\r\n" +
							"WHERE m.id = r.movieId and m.id = ?";

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);

			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) 
			{
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movie_rating = rs.getString("rating");

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_rating", movie_rating);
				jsonObject.addProperty("movie_list_url", (String) request.getSession().getAttribute("movielistURL"));
				
				// Genre List Query
				String genresQuery = 
						"SELECT name\r\n" + 
						"FROM movies m, genres g, genres_in_movies gm\r\n" + 
						"WHERE m.id = gm.movieId AND g.id = gm.genreId AND m.id = ?";
				
				PreparedStatement genresStatement = dbcon.prepareStatement(genresQuery);

				genresStatement.setString(1, movieId);
				ResultSet genresResult = genresStatement.executeQuery();
				
				JsonArray genresJsonArr = new JsonArray();
				
				while(genresResult.next())
				{
					genresJsonArr.add(genresResult.getString("name"));
				}
				
				jsonObject.add("movie_genres", genresJsonArr);
				
				// Stars List Query
				String starsQuery = 
						"SELECT s.id, s.name\r\n" + 
						"FROM movies m, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = sm.movieId AND s.id = sm.starId AND m.id = ?";
				
				PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery);
				
				starsStatement.setString(1, movieId);
				ResultSet starsResult = starsStatement.executeQuery();
				
				JsonArray starsJsonArr = new JsonArray();
				
				while(starsResult.next())
				{
					JsonObject starJsonObject = new JsonObject();
					starJsonObject.addProperty("star_id", starsResult.getString("id"));
					starJsonObject.addProperty("star_name", starsResult.getString("name"));
					starsJsonArr.add(starJsonObject);
				}
				
				jsonObject.add("movie_stars", starsJsonArr);
				
				genresResult.close();
				genresStatement.close();
				starsResult.close();
				starsStatement.close();
				
				jsonArray.add(jsonObject);
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
            rs.close();
            statement.close();
            dbcon.close();
			
		} catch(Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
		}
	}

}
