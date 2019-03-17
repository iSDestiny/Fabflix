import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Map;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
//    @Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	

    	
    	String username = request.getParameter("username");
        String password = request.getParameter("password");
		String android = request.getParameter("android");
		String gRecaptchaResponse = request.getParameter("captcha");
    	
		if(username == null)
		{
	    	StringBuilder buffer = new StringBuilder();
	    	BufferedReader reader = request.getReader();
	    	String line;
	    	while ((line = reader.readLine()) != null) {
	    	    buffer.append(line);
	    	}
	    	String data = buffer.toString();
	    	System.out.println("bufferdata" + data);
	    	JSONObject jObj = new JSONObject(data);
	    	username = jObj.getString("username");
	    	password = jObj.getString("password");
	    	android = jObj.getString("android");
		}
        
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
		
		JsonObject responseJsonObject = new JsonObject();
		
		if(android != null && !android.equals("true")) {
	        try {
				RecaptchaVerifyUtils.verify(gRecaptchaResponse);
				responseJsonObject.addProperty("recaptcha", "success");
	        } catch (Exception e) {
				responseJsonObject.addProperty("recaptcha", "failure");
	        }
		}else
		{
			System.out.println("IN ANDROID FRONTEND");
		}
	
        try
        {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localdb");

            Connection dbcon = ds.getConnection();

        	
//	        Connection dbcon = dataSource.getConnection();
	
			String query = "SELECT id,email,password from customers where email = ?";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, username);
			
			String queryEmployee = "SELECT email, password from employees where email = ?";
			PreparedStatement statementEmployee = dbcon.prepareStatement(queryEmployee);
			statementEmployee.setString(1, username);
			
			ResultSet rs = statement.executeQuery();
			ResultSet rsEmployee = statementEmployee.executeQuery();
			// verifyCredentials("a@email.com", "a2")
			if (rs.next()) {
				if (VerifyPassword.verifyCredentials(username, password)) {
					// Login succeeds
					// Set this user into current session
					request.getSession().invalidate();
					Cart cart = new Cart();
					String sessionId = ((HttpServletRequest) request).getSession().getId();
					Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
					request.getSession().setAttribute("user", new User(rs.getInt("id"), username, cart));
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("sessionId", sessionId);
					responseJsonObject.addProperty("lastAccessTime", lastAccessTime);
					responseJsonObject.addProperty("message", "success");
					responseJsonObject.addProperty("type", "user");
				}
				else 
				{
					responseJsonObject.addProperty("message", "Incorrect password");
					responseJsonObject.addProperty("status", "failure");
				}
			}
			else if(rsEmployee.next())
			{
				if(VerifyEmployeePassword.verifyCredentials(username, password))
				{
					request.getSession().invalidate();
					Cart cart = new Cart();
					String sessionId = ((HttpServletRequest) request).getSession().getId();
					Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
					request.getSession().setAttribute("employee", new Employee(username, cart));
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("sessionId", sessionId);
					responseJsonObject.addProperty("lastAccessTime", lastAccessTime);
					responseJsonObject.addProperty("message", "success");
					responseJsonObject.addProperty("type", "employee");
				}
				else
				{
					responseJsonObject.addProperty("message", "Incorrect password");
					responseJsonObject.addProperty("status", "failure");
				}
			}
			else
			{
				responseJsonObject.addProperty("message", "Email " + username + " doesn't exist");
				responseJsonObject.addProperty("status", "failure");
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
			jsonObject.addProperty("status", "failure");
			jsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(jsonObject.toString());

			response.setStatus(500);
		}
    }
}
