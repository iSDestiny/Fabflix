import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
	
//	@Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
       
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
		Employee employee = (Employee) session.getAttribute("employee");
		PrintWriter out = response.getWriter();
		
		try
		{
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localdb");
			
            if (ds == null)
                out.println("ds is null.");

            Connection dbc = ds.getConnection();
            if (dbc == null)
                out.println("dbcon is null.");
			
//			Connection dbc = dataSource.getConnection();
	
			if (request.getParameter("quantity") != null)
			{
				String id = request.getParameter("movie_id");
				String qt = request.getParameter("quantity");
				String update = request.getParameter("update");
				String query = 
						"SELECT title from movies where id = ?";
				
				PreparedStatement Statement = dbc.prepareStatement(query);
				
				Statement.setString(1, id);
				
				ResultSet rs = Statement.executeQuery();
				
				while (rs.next())
				{
					if(user != null) 
					{
						if (Integer.parseInt(qt) == 0)
						{
							user.getCart().delete(id, rs.getString("title"));
						}
						else
						{
							if (update.equals("true"))
							{
								user.getCart().update(id, rs.getString("title"), qt);
							}
							else
								user.getCart().add(id, rs.getString("title"), qt);
						}
					}
					else
					{
						if (Integer.parseInt(qt) == 0)
						{
							employee.getCart().delete(id, rs.getString("title"));
						}
						else
						{
							if (update.equals("true"))
							{
								employee.getCart().update(id, rs.getString("title"), qt);
							}
							else
								employee.getCart().add(id, rs.getString("title"), qt);
						}
					}
				}
				rs.close();
			}
			
			JsonObject parentJson = new JsonObject();
			JsonArray jsonArray = new JsonArray();
	
			parentJson.addProperty("movie_list_url", (String) request.getSession().getAttribute("movielistURL"));
			if(user != null)
			{
				for (ArrayList<String> key: user.getCart().getItems().keySet())
				{
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("movie_id", key.get(0));
					jsonObject.addProperty("movie_title", key.get(1));
					jsonObject.addProperty("quantity", user.getCart().getItems().get(key));
					jsonArray.add(jsonObject);
				}
			}
			else
			{
				for (ArrayList<String> key: employee.getCart().getItems().keySet())
				{
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("movie_id", key.get(0));
					jsonObject.addProperty("movie_title", key.get(1));
					jsonObject.addProperty("quantity", employee.getCart().getItems().get(key));
					jsonArray.add(jsonObject);
				}
			}
			
			parentJson.add("cart_entries", jsonArray);
			out.write(parentJson.toString());
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
