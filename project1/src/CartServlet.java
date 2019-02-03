import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartServlet() {
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
		PrintWriter out = response.getWriter();
		
		try
		{
			Connection dbc = dataSource.getConnection();
			Statement statement = dbc.createStatement();
	
			if (request.getParameter("quantity") != null)
			{
				String id = request.getParameter("movie_id");
				String qt = request.getParameter("quantity");
				String query = 
						"SELECT title from movies where id = ?";
				
				PreparedStatement Statement = dbc.prepareStatement(query);
				
				Statement.setString(1, id);
				
				ResultSet rs = Statement.executeQuery();
				while (rs.next())
				{
					user.getCart().add(rs.getString("title"), qt);
				}
				rs.close();
			}
			
			JsonArray jsonArray = new JsonArray();
	
			for (String key: user.getCart().getItems().keySet())
			{
				System.out.println("ok");
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_title", key);
				jsonObject.addProperty("quantity", user.getCart().getItems().get(key));
				jsonArray.add(jsonObject);
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
