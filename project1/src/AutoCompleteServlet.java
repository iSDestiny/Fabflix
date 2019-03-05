import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/title-suggestions")
public class AutoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		String title = request.getParameter("query");
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			
			JsonArray jsonArray = new JsonArray();
			
			// return the empty json array if query is null or empty
			if (title == null || title.trim().isEmpty()) {
				out.write(jsonArray.toString());
				return;
			}	
			
			// TODO: in project 4, you should do full text search with MySQL to find the matches on movies
			String query1 = "SELECT m.id, title\r\n" + 
					"FROM movies m\r\n" +
					"WHERE MATCH(m.title) AGAINST(? in BOOLEAN MODE) LIMIT 10\r\n";
			
			PreparedStatement statement = dbcon.prepareStatement(query1);
			
			String[] splited = title.split("\\s+");
			if (splited.length == 1) 
				{
					statement.setString(1, splited[0] + "*");
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
			
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("value", rs.getString("title"));
				JsonObject data = new JsonObject();
				data.addProperty("id", rs.getString("id"));
				jsonObject.add("data", data);
				jsonArray.add(jsonObject);
			}
			
			out.write(jsonArray.toString());
			rs.close();
			statement.close();
			dbcon.close();
			return;
			
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
}