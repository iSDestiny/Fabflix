public class Employee {
	private final String username;
	private Cart c;
	
	public Employee(String username, Cart c) {
		this.username = username;
		this.c = c;
	}
	
	public String getUsername() 
	{
		return this.username;
	}
	
	public Cart getCart()
	{
		return c;
	}
	
}
