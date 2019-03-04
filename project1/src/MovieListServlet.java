import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.Map;

/**
 * Servlet implementation class MovieListServlet
 */
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session = request.getSession();
		StringBuffer URL = request.getRequestURL();
		String urlQuery = request.getQueryString();
		URL.replace(URL.indexOf("api/movies"), URL.length(), "movielist.html?" + urlQuery);
		session.setAttribute("movielistURL", URL.toString());
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		Map<String, String[]> params = request.getParameterMap();
		System.out.println(params.toString());
		
		try {
			Connection dbc = dataSource.getConnection();
			PreparedStatement statement = buildQuery(dbc, params);
			
			
			ResultSet rs = statement.executeQuery();
			
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
	
	private PreparedStatement buildQuery(Connection dbc, Map<String, String[]> params) throws SQLException
	{
		String query = "";
		PreparedStatement statement = null;
		
		if(params.size() == 3)
		{
			query = "SELECT m.id, title, year, director, rating\r\n" + 
					"FROM movies m, ratings r\r\n" +
					"WHERE m.id = r.movieId\r\n" + 
					"ORDER BY SORT1 SORT2\r\n" + 
					"LIMIT LIMITP\r\n" +
					"OFFSET OFFSETP";
			query = processQuery(query, params);
			statement = dbc.prepareStatement(query);
		}
		else if(params.size() <= 4)
		{
			if(params.containsKey("title"))
			{
				String index_query = "show index from movies";
				PreparedStatement index = dbc.prepareStatement(index_query);
				ResultSet ind = index.executeQuery();
				
				ind.next();
				if (ind.next() == false) {
					System.out.println("Go in here");
					String ft = "ALTER TABLE movies ADD FULLTEXT(title)";
					PreparedStatement full = dbc.prepareStatement(ft);
					full.executeUpdate();
				}
				
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND MATCH(m.title) AGAINST(? in BOOLEAN MODE)\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				
				String[] splited = params.get("title")[0].split("\\s+");
				if (splited.length == 1) 
					{
						if (splited[0].length() < 3) {statement.setString(1, splited[0] + "*");}
						else {
						statement.setString(1, params.get("title")[0]);
						}
					}
				else 
				{
					String searchable = "+" + splited[0] + "*";
					for (int i = 1; i < splited.length; i++)
					{
						searchable += "+" + splited[i] + "*";
					}
					statement.setString(1, searchable);
				}
				System.out.println(params.get("title")[0]);
			}
			else if(params.containsKey("letter"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.title LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, params.get("letter")[0]+"%");
			}
			else if(params.containsKey("genre"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, genres g, genres_in_movies gm, ratings r\r\n" + 
						"WHERE m.id = gm.movieId AND g.id = gm.genreId AND g.name = ? AND m.id = r.movieId\r\n" +
						"ORDER BY SORT1 SORT2\r\n" +
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, params.get("genre")[0]);
			}
			else if(params.containsKey("year"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.year = ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, params.get("year")[0]);
			}
			else if(params.containsKey("director"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.director LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("director")[0]+"%");
			}
			else 
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
			}
		}
		
		else if(params.size() == 5)
		{
			if(params.containsKey("title") && params.containsKey("year"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.title LIKE ? AND m.year = ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("title")[0]+"%");
				statement.setString(2, params.get("year")[0]);
			}
			else if(params.containsKey("title") && params.containsKey("director"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.title LIKE ? AND m.director LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("title")[0]+"%");
				statement.setString(1, "%"+params.get("director")[0]+"%");
			}
			else if(params.containsKey("title") && params.containsKey("star"))
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.title LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, "%"+params.get("title")[0]+"%");
			}
			else if(params.containsKey("director") && params.containsKey("year"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.director LIKE ? AND m.year = ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("director")[0]+"%");
				statement.setString(2, params.get("year")[0]);
			}
			else if(params.containsKey("director") && params.containsKey("star"))
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.director LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, "%"+params.get("director")[0]+"%");
			}
			else
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.year = ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, params.get("year")[0]);
			}	
		}
		else if(params.size() == 6)
		{
			if(params.containsKey("title") && params.containsKey("year") && params.containsKey("director"))
			{
				query = "SELECT m.id, title, year, director, rating\r\n" + 
						"FROM movies m, ratings r\r\n" +
						"WHERE m.id = r.movieId AND m.title LIKE ? AND m.year = ? AND m.director LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" +
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("title")[0]+"%");
				statement.setString(3, "%"+params.get("director")[0]+"%");
				statement.setString(2, params.get("year")[0]);
			}
			else if(params.containsKey("title") && params.containsKey("year") && params.containsKey("star"))
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.year = ? AND m.title LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, params.get("year")[0]);
				statement.setString(3, "%"+params.get("title")[0]+"%");
			}	
			else if(params.containsKey("title") && params.containsKey("director") && params.containsKey("star"))
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.director LIKE ? AND m.title LIKE ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, "%"+params.get("director")[0]+"%");
				statement.setString(3, "%"+params.get("title")[0]+"%");
			}
			else
			{
				query = "SELECT m.id, title, year, director, rating, name\r\n" + 
						"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
						"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.director LIKE ? AND m.year = ?\r\n" + 
						"ORDER BY SORT1 SORT2\r\n" + 
						"LIMIT LIMITP\r\n" + 
						"OFFSET OFFSETP";
				query = processQuery(query, params);
				statement = dbc.prepareStatement(query);
				statement.setString(1, "%"+params.get("star")[0]+"%");
				statement.setString(2, "%"+params.get("director")[0]+"%");
				statement.setString(3, params.get("year")[0]);
			}
		}
		else
		{
			query = "SELECT m.id, title, year, director, rating, name\r\n" + 
					"FROM movies m, ratings r, stars_in_movies sm, stars s \r\n" + 
					"WHERE m.id = r.movieId AND m.id = sm.movieId AND s.id = sm.starId AND s.name LIKE ? AND m.director LIKE ? AND m.year = ? AND m.title LIKE ?\r\n" + 
					"ORDER BY SORT1 SORT2\r\n" + 
					"LIMIT LIMITP\r\n" + 
					"OFFSET OFFSETP";
			query = processQuery(query, params);
			statement = dbc.prepareStatement(query);
			statement.setString(1, "%"+params.get("star")[0]+"%");
			statement.setString(2, "%"+params.get("director")[0]+"%");
			statement.setString(3, params.get("year")[0]);	
			statement.setString(4, "%"+params.get("title")[0]+"%");
		}

		System.out.println(query);
		System.out.println(statement.toString());
		return statement;
	}
	
	String processQuery(String query, Map<String, String[]> params)
	{
		String resultQuery = query;
		int offset;
		
		if(params.containsKey("sort"))
		{
			if(params.get("sort")[0].equals("titleasc"))
			{
				resultQuery = query.replaceAll("SORT1", "title");
				resultQuery = resultQuery.replaceAll("SORT2", "ASC");
				System.out.println("titleasc " + resultQuery);
			}
			else if(params.get("sort")[0].equals("titledesc"))
			{
				resultQuery = query.replaceAll("SORT1", "title");
				resultQuery = resultQuery.replaceAll("SORT2", "DESC");
				System.out.println("titledesc " + resultQuery);
			}
			else if(params.get("sort")[0].equals("ratingasc"))
			{
				resultQuery = query.replaceAll("SORT1", "rating");
				resultQuery = resultQuery.replaceAll("SORT2","ASC");
				System.out.println("ratingasc " + resultQuery);
			}
			else if(params.get("sort")[0].equals("ratingdesc"))
			{
				resultQuery = query.replaceAll("SORT1", "rating");
				resultQuery = resultQuery.replaceAll("SORT2", "DESC");
				System.out.println("ratingasc " + resultQuery);
			}
			else if(params.get("sort")[0].equals("yearasc"))
			{
				resultQuery = query.replaceAll("SORT1", "year");
				resultQuery = resultQuery.replaceAll("SORT2", "ASC");
				System.out.println("ratingasc " + resultQuery);
			}
			else if(params.get("sort")[0].equals("yeardesc"))
			{
				resultQuery = query.replaceAll("SORT1", "year");
				resultQuery = resultQuery.replaceAll("SORT2", "DESC");
				System.out.println("ratingasc " + resultQuery);
			}
		}
		else
		{
			resultQuery = query.replaceAll("SORT1", "title");
			resultQuery = resultQuery.replaceAll("SORT2", "ASC");
			System.out.println("default " + resultQuery);
		}
		
		if(params.containsKey("limit") && params.containsKey("page"))
		{
			resultQuery = resultQuery.replaceAll("LIMITP", params.get("limit")[0]);
			offset = (Integer.parseInt(params.get("page")[0]) - 1) * Integer.parseInt(params.get("limit")[0]);
			resultQuery = resultQuery.replaceAll("OFFSETP", Integer.toString(offset));
		}
		else
		{
			resultQuery = resultQuery.replaceAll("LIMITP", "25");
			resultQuery = resultQuery.replaceAll("OFFSETP", "0");
		}
		
		return resultQuery;
	}
}
