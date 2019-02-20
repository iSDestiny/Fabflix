import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
		
        String gRecaptchaResponse = request.getParameter("captcha");
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

		JsonObject responseJsonObject = new JsonObject();
		
        try {
			RecaptchaVerifyUtils.verify(gRecaptchaResponse);
			responseJsonObject.addProperty("recaptcha", "success");
        } catch (Exception e) {
			responseJsonObject.addProperty("recaptcha", "failure");
        }

        try
        {
	        Connection dbcon = dataSource.getConnection();
	
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
			jsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(jsonObject.toString());

			response.setStatus(500);
		}
    }
}
