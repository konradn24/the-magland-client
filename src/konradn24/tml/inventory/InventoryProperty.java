package konradn24.tml.inventory;

import java.util.ArrayList;
import java.util.List;

import konradn24.tml.items.Item;

public class InventoryProperty {

	public Item item;
	public int amount;
	
	public InventoryProperty(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public List<InventoryProperty> shatter() {
		List<InventoryProperty> list = new ArrayList<>();
		
		for(int i = 0; i < amount; i++) {
			list.add(copy(1));
		}
		
		return list;
	}
	
	public InventoryProperty copy(int amount) {
		InventoryProperty copy = new InventoryProperty(item, amount);
		
		return copy;
	}
	
	/** Checks if item and it's params is equal to another item and params **/
	public boolean isEqual(InventoryProperty property) {
		return item == property.item;
	}
	
	public float getWeight() {
		return item.getWeight() * amount;
	}
}
