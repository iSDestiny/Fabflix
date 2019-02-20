
public class Genre {
	int id;
	String name;
	
	public Genre() {};
	
	int getId()
	{
		return id;
	}
	
	String getName()
	{
		return name;
	}
	
	void setId(int id)
	{
		this.id = id;
	}
	
	void setName(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return "Genre id: " + id + "\nGenre name: " + name;
	}

}
