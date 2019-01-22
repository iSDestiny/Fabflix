import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.sql.Statement;

/**
 * Servlet implementation class MovieListServlet
 */
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbc = dataSource.getConnection();
			Statement statement = dbc.createStatement();
			
			String query = 
					"SELECT id, title, year, director, rating\r\n" + 
					"FROM movies m, ratings r\r\n" + 
					"WHERE m.id = r.movieId \r\n" + 
					"ORDER BY r.rating DESC\r\n" + 
					"LIMIT 20";
			
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			// Movie Entry Row
			while(rs.next())
			{
				String movie_id = rs.getString("id");
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_rating = rs.getString("rating");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movie_id);
				jsonObject.addProperty("movie_title", movie_title);
				jsonObject.addProperty("movie_year", movie_year);
				jsonObject.addProperty("movie_director", movie_director);
				jsonObject.addProperty("movie_rating", movie_rating);
				
				
				// Genre List Query
				String genresQuery = 
						"SELECT name\r\n" + 
						"FROM movies m, genres g, genres_in_movies gm\r\n" + 
						"WHERE m.id = gm.movieId AND g.id = gm.genreId AND m.id = ?";
				
				PreparedStatement genresStatement = dbc.prepareStatement(genresQuery);

				genresStatement.setString(1, movie_id);
				ResultSet genresResult = genresStatement.executeQuery();
				
				JsonArray genresJsonArr = new JsonArray();
				
				while(genresResult.next())
				{
					//genres.add(genresResult.getString("name"));
					genresJsonArr.add(genresResult.getString("name"));
				}
				
				jsonObject.add("movie_genres", genresJsonArr);
				
				// Stars List Query
				String starsQuery = 
						"SELECT s.id, s.name\r\n" + 
						"FROM movies m, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = sm.movieId AND s.id = sm.starId AND m.id = ?";
				
				PreparedStatement starsStatement = dbc.prepareStatement(starsQuery);
				
				starsStatement.setString(1, movie_id);
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
            dbc.close();
			
		} catch(Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
		}
	}

}
