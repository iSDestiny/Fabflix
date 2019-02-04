import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int id = user.getId();
		PrintWriter out = response.getWriter();
		Long lastAccessTime = session.getLastAccessedTime();
		Date saleDate = new Date(lastAccessTime);
		
		try
		{
			Connection dbc = dataSource.getConnection();
			Statement statement = dbc.createStatement();
			
			JsonArray jsonArray = new JsonArray();
	
			for (ArrayList<String> key: user.getCart().getItems().keySet())
			{
				for (int i = 0; i < user.getCart().getItems().get(key); i++)
				{
					statement.executeUpdate("insert into sales (customerId, movieId, saleDate)"
							+ " values ( " + id + ", " + "'" + key.get(0) + "', " + "'" + saleDate + "')");
				}
				JsonObject jsonObject = new JsonObject();
				
				String query = 
						"SELECT id from sales where customerId=" + id + " and movieId=" + "'" + key.get(0) + "' and saleDate=" + "'" + saleDate + "'";
				ResultSet rs = statement.executeQuery(query);
				
				while (rs.next())
				{
					jsonObject.addProperty("sale_id", rs.getInt("id"));
					jsonObject.addProperty("movie_title", key.get(1));
					jsonObject.addProperty("quantity", user.getCart().getItems().get(key));
					jsonArray.add(jsonObject);
				}
			}
	
			out.write(jsonArray.toString());
	        statement.close();
	        dbc.close();
	    } 
		catch(Exception e) 
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
		}
	}

}
