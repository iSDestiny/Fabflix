import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
//    @Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String id = request.getParameter("id");
        String expiration = request.getParameter("expiration");
        
        try
        {
        	// Start connection pooling
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localdb");
			

            Connection dbcon = ds.getConnection();

        	
//	        Connection dbcon = dataSource.getConnection();
	
			String query = "SELECT * from creditcards where id = ?";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			
			ResultSet rs = statement.executeQuery();
			JsonObject responseJsonObject = new JsonObject();
			
			if (rs.next()) {
				if (firstName.equals(rs.getString("firstName")) && lastName.equals(rs.getString("lastName")) 
						&& expiration.equals(rs.getString("expiration"))) {
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("message", "success");
				}
				else 
				{
					responseJsonObject.addProperty("message", "Customer information is invalid");
				}
			}
			else
			{
				responseJsonObject.addProperty("message", "Customer information is invalid");
			}

			response.getWriter().write(responseJsonObject.toString());
	        
	        response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
        }
        catch (Exception e) 
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(jsonObject.toString());

			response.setStatus(500);
		}
    }
}
