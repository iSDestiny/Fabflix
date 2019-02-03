import java.util.HashMap;
import java.util.Map;

public class Cart {
	Map<String, Integer> items = new HashMap<String, Integer>();

	public Cart() {};
	
	public void add(String item, String qt)
	{
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
	
	public void delete(String item)
	{
		items.remove(item);
	}
	
	public void update(String item, int quantity)
	{
		items.put(item, quantity);
	}
	
	public String toString()
	{
		return items.toString();
	}
	
	public Map<String, Integer> getItems()
	{
		return items;
	}
}
