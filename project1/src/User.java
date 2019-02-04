
public class User {
	private final String username;
	private final int id;
	private Cart c;
	
	public User(int id, String username, Cart c) {
		this.username = username;
		this.id = id;
		this.c = c;
	}
	
	public String getUsername() 
	{
		return this.username;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public Cart getCart()
	{
		return c;
	}
	
}
