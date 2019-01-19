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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
     * @see HttpServlet#HttpServlet()
     */
//    public MovieListServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

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
			
//			System.out.println("The results of the query");
//			ResultSetMetaData metadata = rs.getMetaData();
//			System.out.println("There are " + metadata.getColumnCount() + " columns");
//			
//			int movieNum = 1;
//			while(rs.next())
//			{
//				String movie_id = rs.getString("id");
//				String movie_title = rs.getString("title");
//				String movie_year = rs.getString("year");
//				String movie_director = rs.getString("director");
//				String movie_rating = rs.getString("rating");
//				
//				System.out.println(movieNum + " " + movie_id + " " + movie_title + " " + movie_year + " " +
//						movie_director + " " + movie_rating);
//				++movieNum;
//			}
			
			JsonArray jsonArray = new JsonArray();
			
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
