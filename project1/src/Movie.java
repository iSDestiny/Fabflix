import java.util.ArrayList;

public class Movie {
	String id;
	String title = "No Title";
	int year = 0;
	String director;
	ArrayList<Genre> genrelist = new ArrayList<Genre>();
	ArrayList<Star> starlist = new ArrayList<Star>();
	
	public Movie() {};
	
	String getId()
	{
		return id;
	}
	
	String getTitle()
	{
		return title;
	}
	
	int getYear()
	{
		return year;
	}
	
	String getDirector()
	{
		return director;
	}
	
	void setId(String id)
	{
		this.id = id;
	}
	
	void setTitle(String title)
	{
		this.title = title;
	}
	
	void setYear(int year)
	{
		this.year = year;
	}
	
	void setDirector(String director)
	{
		this.director = director;
	}
	
	void addGenre(Genre genre)
	{
		genrelist.add(genre);
	}
	
	void addStar(Star star)
	{
		starlist.add(star);
	}
	
	ArrayList<Star> getStar()
	{
		return starlist;
	}
	
	ArrayList<Genre> getGenre()
	{
		return genrelist;
	}
	
	public String toString()
	{
		String genre = "";
		if (genrelist != null)
		{
			for (int i = 0; i < genrelist.size(); i++)
			{
				genre += genrelist.get(i).toString() + "\n";
			}
		}
		
		String star = "";
		if (starlist != null)
		{
			for (int j = 0; j < starlist.size(); j++)
			{
				star += starlist.get(j).toString() + "\n";
			}
		}
		
		return "Movie id: " + id + "\nTitle: " + title + "\nYear: " + year + "\nDirector: " + director + "\nGenres: " + genre + "\nStars: " + star;
	}
}
