public class Star {
	String id;
	String name;
	int birthYear;
	
	public Star() {
		
	};
	
	String getId()
	{
		return id;
	}
	
	String getName()
	{
		return name;
	}
	
	int getbirthYear()
	{
		return birthYear;
	}
	
	void setId(String id)
	{
		this.id = id;
	}
	
	void setName(String name)
	{
		this.name = name;
	}
	
	void setbirthYear(int date)
	{
		this.birthYear = date;
	}
	
	public String toString()
	{
		return "Star id: " + id + "\nStar name: " + name + "\nStar year: " + birthYear;
	}
}
