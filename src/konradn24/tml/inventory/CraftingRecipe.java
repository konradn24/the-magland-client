package konradn24.tml.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import konradn24.tml.Handler;

public class CraftingRecipe {
	
	private String id;
	private Map<InventoryProperty, Boolean> required;
	private List<InventoryProperty> result;
	
	private boolean onlyItemClassEquality;
	
	public CraftingRecipe(String id, Map<InventoryProperty, Boolean> required, boolean onlyItemClassEquality, List<InventoryProperty> result) {
		this.id = id;
		this.required = required;
		this.onlyItemClassEquality = onlyItemClassEquality;
		this.result = result;
	}
	
	public int getAffordableAmount(Inventory inventory, Handler handler) {
		if(inventory == null) {
			return 0;
		}
		
		int amount = Integer.MAX_VALUE;
		
		if(handler.getGameRules().infiniteResources) {
			return amount;
		}
		
		for(Map.Entry<InventoryProperty, Boolean> required : getRequired().entrySet()) {
			InventoryProperty property = required.getKey();
			boolean keep = required.getValue();
			int factor = (int) Math.floor((onlyItemClassEquality ? inventory.getItemAmount(property.item) : inventory.getItemAmount(property)) / property.amount);
			
			if(keep && factor > 0) {
				continue;
			}
			
			if(factor < amount) {
				amount = factor;
			}
		}
		
		return amount;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<InventoryProperty, Boolean> getRequired() {
		return required;
	}
	
	public List<InventoryProperty> getRequiredProperties() {
		return getRequiredProperties(1);
	}
	
	public List<InventoryProperty> getRequiredProperties(int amount) {
		List<InventoryProperty> requiredProperties = new ArrayList<>(required.keySet());
		
		requiredProperties.forEach(property -> {
			if(required.get(property)) {
				return;
			}
			
			property.amount *= amount;
		});
		
		return requiredProperties;
	}

	public List<InventoryProperty> getCostProperties() {
		return getCostProperties(1);
	}
	
	public List<InventoryProperty> getCostProperties(int amount) {
		List<InventoryProperty> requiredProperties = required.entrySet().stream()
			.filter(entry -> Boolean.FALSE.equals(entry.getValue()))
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());
		
		requiredProperties.forEach(property -> property.amount *= amount);
		
		return requiredProperties;
	}
	
	public void setRequired(Map<InventoryProperty, Boolean> required) {
		this.required = required;
	}

	public boolean isOnlyItemClassEquality() {
		return onlyItemClassEquality;
	}

	public void setOnlyItemClassEquality(boolean onlyItemClassEquality) {
		this.onlyItemClassEquality = onlyItemClassEquality;
	}

	public List<InventoryProperty> getResultProperties() {
		return result;
	}
	
	public List<InventoryProperty> getResultProperties(int amount) {
		List<InventoryProperty> resultProperties = new ArrayList<>(result);
		resultProperties.forEach(property -> property.amount *= amount);
		
		return resultProperties;
	}

	public void setResult(List<InventoryProperty> result) {
		this.result = result;
	}
}
