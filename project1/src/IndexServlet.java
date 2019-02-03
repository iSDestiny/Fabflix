import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below, 
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * handles POST requests to store session information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        Long lastAccessTime = session.getLastAccessedTime();
        User user = (User) session.getAttribute("user");
  
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        responseJsonObject.addProperty("user", user.getUsername());
        responseJsonObject.addProperty("id", user.getId());
        responseJsonObject.addProperty("cart", user.getCart().toString());
        
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }
}
