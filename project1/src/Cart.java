import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart {
	Map<ArrayList<String>, Integer> items = new HashMap<ArrayList<String>, Integer>();

	public Cart() {};
	
	public void add(String id, String title, String qt)
	{
		ArrayList<String> item = new ArrayList<String>();
		item.add(id);
		item.add(title);
		
		if (items.containsKey(item))
		{
			int quantity = Integer.parseInt(qt);
			items.put(item, items.get(item) + quantity);
		}
		else
		{
			int quantity = Integer.parseInt(qt);
			items.put(item, quantity);
		}
	}
	
	public void delete(String id, String title)
	{
		ArrayList<String> item = new ArrayList<String>();
		item.add(id);
		item.add(title);
		
		items.remove(item);
	}
	
	public void update(String id, String title, String qt)
	{
		ArrayList<String> item = new ArrayList<String>();
		item.add(id);
		item.add(title);
		
		int quantity = Integer.parseInt(qt);
		
		items.replace(item, quantity);
	}
	
	public String toString()
	{
		return items.toString();
	}
	
	public Map<ArrayList<String>, Integer> getItems()
	{
		return items;
	}
}
