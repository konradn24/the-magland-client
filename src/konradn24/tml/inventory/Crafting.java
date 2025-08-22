package konradn24.tml.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.widgets.slots.SlotMenu;
import konradn24.tml.items.Item;
import konradn24.tml.utils.Logging;

public class Crafting {
	
	private Inventory inventory;
	private List<Integer> attributes = new ArrayList<>();
	private SlotMenu<CraftingSlot> slotMenu;
	
	private float width, height;
	
	private Handler handler;
	
	public Crafting(int... attributes) {
		for(int attribute : attributes) this.attributes.add(attribute);
	}
	
	public void useGraphics(Handler handler, float x, float y, float width, float height, int columns) {
		this.handler = handler;
		this.width = width;
		this.height = height;
		
		slotMenu = new SlotMenu<>(CraftingSlot.class, x, y, width / columns, columns, handler);
		slotMenu.getDropdown().setWidth(150);
	}
	
	public void update(float dt) {
		slotMenu.update(dt);
	}
	
	public void renderGUI(long vg) {
		slotMenu.renderGUI(vg);
	}
	
	public void craft(String recipeID, int amount) {
		if(inventory == null) {
			Logging.error("Crafting: " + recipeID + " failed! Interface not attached to any inventory!");
			return;
		}
		
		if(amount <= 0) {
			Logging.warning("Crafting: " + recipeID + " failed! Invalid amount (" + amount + ")!");
			return;
		}
		
		CraftingRecipe recipe = null;
		
		for(CraftingSlot slot : slotMenu.getSlots()) {
			if(slot.getCraftingRecipe() == null) {
				continue;
			}
			
			if(slot.getCraftingRecipe().getId().equals(recipeID)) {
				recipe = slot.getCraftingRecipe();
				break;
			}
		}
		
		if(recipe == null) {
			Logging.error("Crafting: " + recipeID + " failed! No recipe of this ID!");
			return;
		}
		
		int affordableAmount = recipe.getAffordableAmount(inventory, handler);
		
		if(affordableAmount < amount) {
			amount = affordableAmount;
			Logging.warning("Crafting: cannot craft " + amount + " of " + recipeID + "! Not enough items! Reduced amount to " + affordableAmount);
			return;
		}
		
		inventory.remove(recipe.getCostProperties(amount), recipe.isOnlyItemClassEquality());
		inventory.add(recipe.getResultProperties(amount));
		
		Logging.info("Crafting " + recipe.getId() + " completed (amount: " + amount + ")");
	}
	
	public void refresh() {
		if(inventory == null) {
			Logging.error("Crafting: cannot refresh - interface not attached to any inventory!");
			return;
		}
		
		slotMenu.getSlotsLayout().forEach((slot, i) -> {
			slot.setCraftingRecipe(null);
		});
		
		// TODO: Add recipe only when player has at least one necessary item
		int i = 0;
		for(Item item : Item.items) {
			if(item == null) continue;
			
			// Check if has any matching attributes
			if(!item.hasAttributeAny(attributes)) {
				continue;
			}
			
			// Add recipes
			for(CraftingRecipe recipe : item.getRecipes()) {
				slotMenu.getSlot(i).setCraftingRecipe(recipe);
				slotMenu.getSlot(i).setAmount(recipe.getAffordableAmount(inventory, handler));
				i++;
			}
		}
		
		Logging.info("Crafting Interface refreshed - " + i + "/" + slotMenu.getSlotsAmount() + " slots in use");
	}
	
	/** @return Map of crafting recipes with boolean as value indicating if recipe is affordable **/
	public Map<CraftingRecipe, Boolean> getRecipes() {
		Map<CraftingRecipe, Boolean> recipesMap = new HashMap<>();
		
		for(CraftingSlot slot : slotMenu.getSlots()) {
			if(slot.getCraftingRecipe() == null) {
				continue;
			}
			
			recipesMap.put(slot.getCraftingRecipe(), slot.getAmount() > 0);
		}
		
		return recipesMap;
	}
	
	public int getAffordableRecipesAmount() {
		int count = 0;
		for(boolean affordable : getRecipes().values())
			if(affordable) count++;
		
		return count;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public float getX() {
		return slotMenu.getX();
	}

	public void setX(float x) {
		slotMenu.setX(x);
	}

	public float getY() {
		return slotMenu.getY();
	}

	public void setY(float y) {
		slotMenu.setY(y);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public List<Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Integer> attributes) {
		this.attributes = attributes;
	}
}
