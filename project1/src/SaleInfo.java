import java.util.ArrayList;

public class SaleInfo {
	private String title;
	private ArrayList<Integer> ids;
	
	SaleInfo(String title)
	{
		this.title = title;
		ids = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getIds()
	{
		return ids;
	}
	
	public void addId(int id)
	{
		ids.add(id);
	}
	
	public int getQuantity()
	{
		return ids.size();
	}
	
	public String getTitle()
	{
		return title;
	}
}
