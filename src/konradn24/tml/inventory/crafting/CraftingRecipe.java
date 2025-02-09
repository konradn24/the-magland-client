package konradn24.tml.inventory.crafting;

import java.util.HashMap;
import java.util.Map;

import konradn24.tml.inventory.items.Item;

public class CraftingRecipe {
	
	private String id;
	private Map<Integer, Integer> required = new HashMap<>();
	private Map<Integer, Boolean> keep = new HashMap<>();
	private Item resultItem;
	private int resultAmount;
	
	public CraftingRecipe(Item resultItem, String id, Map<Integer, Integer> required, int resultAmount) {
		this.resultItem = resultItem;
		this.id = id;
		this.required = required;
		this.resultAmount = resultAmount;
		
		for(int item : required.keySet()) {
			this.keep.put(item, false);
		}
	}
	
	public CraftingRecipe(Item resultItem, String id, Map<Integer, Integer> required, Map<Integer, Boolean> keep, int resultAmount) {
		this.resultItem = resultItem;
		this.id = id;
		this.required = required;
		this.resultAmount = resultAmount;
		
		for(int item : required.keySet()) {
			if(!keep.containsKey(item)) this.keep.put(item, false);
		}
	}

	public String getId() {
		return id;
	}
	
	public Map<Integer, Integer> getRequired() {
		return required;
	}

	public void setRequired(Map<Integer, Integer> required) {
		this.required = required;
	}

	public Map<Integer, Boolean> getKeep() {
		return keep;
	}

	public void setKeep(Map<Integer, Boolean> keep) {
		this.keep = keep;
	}

	public Item getResultItem() {
		return resultItem;
	}

	public void setResultItem(Item resultItem) {
		this.resultItem = resultItem;
	}

	public int getResultAmount() {
		return resultAmount;
	}

	public void setResultAmount(int resultAmount) {
		this.resultAmount = resultAmount;
	}
}
