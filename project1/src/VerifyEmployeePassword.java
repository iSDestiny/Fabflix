import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class VerifyEmployeePassword {

	/*
	 * After you update the passwords in customers table,
	 *   you can use this program as an example to verify the password.
	 *   
	 * Verify the password is simple:
	 * success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	 * 
	 * Note that you need to use the same StrongPasswordEncryptor when encrypting the passwords
	 * 
	 */
//	public static void main(String[] args) throws Exception {
//
//		System.out.println(verifyCredentials("a@email.com", "a2"));
//		System.out.println(verifyCredentials("a@email.com", "a3"));
//
//	}

	public static boolean verifyCredentials(String email, String password) throws Exception {
		
//        String loginUser = "Jason";
//        String loginPasswd = "Jimmy123$";
//        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
//
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		
        Context initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");

        // Look up our data source
        DataSource ds = (DataSource) envCtx.lookup("jdbc/localdb");

        Connection connection = ds.getConnection();

		
		Statement statement = connection.createStatement();

		String query = String.format("SELECT * from employees where email='%s'", email);

		ResultSet rs = statement.executeQuery(query);

		boolean success = false;
		if (rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
		}

		rs.close();
		statement.close();
		connection.close();
		
		System.out.println("verify " + email + " - " + password);

		return success;
	}

}
