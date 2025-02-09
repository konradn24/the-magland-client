package konradn24.tml.inventory.crafting;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.inventory.Inventory;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.states.GameState;

public class CraftingInterface {

	public static final int LAYOUT_LABEL_WIDTH = 300;
	public static final int LAYOUT_LABEL_HEIGHT = 50;
	
	private Inventory inventory;
	private List<Integer> attributes = new ArrayList<>();
	
	private Map<CraftingRecipe, Boolean> recipes = new HashMap<>();
	
	private Map<String, AdvancedLabel> labels;
	private Font font;
	private String layoutID;
	private int x, y, width, height;
	private int rows, columns;
	private int labelsPerColumn;
	
	private Handler handler;
	
	private boolean clickCooldown;

	public CraftingInterface(Inventory inventory, int... attributes) {
		this.inventory = inventory;

		for(int attribute : attributes) this.attributes.add(attribute);
		
		this.labels = new HashMap<>();
		this.font = Presets.FONT_INVENTORY.deriveFont(18f);
		this.clickCooldown = false;
	}
	
	public void useGraphics(String layoutID, Handler handler, int x, int y, int width, int height) {
		this.layoutID = layoutID;
		this.handler = handler;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.rows = Math.max(height / LAYOUT_LABEL_HEIGHT, 1);
		this.columns = Math.max(width / LAYOUT_LABEL_WIDTH, 1);
		this.labelsPerColumn = (int) Math.max(Math.ceil(labels.size() / columns), rows);
		
		handler.getStyle().addLayout(GameState.class, layoutID, x, y, width, height, rows, columns);
		
		Logging.info("Crafting Interface (layoutID: " + layoutID + ") initialized");
	}
	
	public void tick() {
		for(Map.Entry<String, AdvancedLabel> entry : labels.entrySet()) {
			String recipeID = entry.getKey();
			AdvancedLabel label = entry.getValue();
			
			if(handler.getMouseManager().isLeftPressed() && label.isOn()) {
				if(clickCooldown) break;
				
				craft(recipeID);
				clickCooldown = true;
			} else if(!handler.getMouseManager().isLeftPressed()) clickCooldown = false;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Presets.COLOR_BACKGROUND);
		g.fillRect(x, y, width, height);
		
		for(AdvancedLabel label : labels.values()) {
			label.render(g, handler);
		}
	}
	
	public void craft(String recipeID) {
		CraftingRecipe recipe = null;
		
		for(Map.Entry<CraftingRecipe, Boolean> entry : recipes.entrySet()) {
			if(entry.getKey().getId() == recipeID && entry.getValue()) {
				recipe = entry.getKey();
				break;
			}
		}
		
		if(recipe == null) return;
		
		boolean afforded = true;
		for(Map.Entry<Integer, Integer> entry : recipe.getRequired().entrySet()) {
			int requiredItemID = entry.getKey();
			int requiredItemAmount = entry.getValue();
			
			boolean removed = inventory.remove(Item.getItem(requiredItemID), requiredItemAmount);
			afforded = afforded ? removed : false;
		}
		
		boolean success = inventory.add(recipe.getResultItem(), recipe.getResultAmount(), true);
		
		if(!afforded) Logging.warning("Crafted " + recipe.getId() + " although not enough required items!");
		if(success) Logging.info("Crafting " + recipe.getId() + " completed");
		else Logging.warning("Crafting " + recipe.getId() + " failed!");
		
		refresh();
	}
	
	public void refresh() {
		recipes.clear();
		
		for(Item item : Item.items) {
			if(item == null) continue;
			
			// Check if has any matching attributes
			if(item.hasAttributeAny(attributes)) {
				// Check if recipe is affordable
				for(CraftingRecipe recipe : item.getRecipes()) {
					// Check if mode infiniteResources is enabled
					if(handler != null && handler.getGameRules().infiniteResources) {
						recipes.put(recipe, true);
						continue;
					}
					
					boolean available = true;
					
					for(Map.Entry<Integer, Integer> required : recipe.getRequired().entrySet()) {
						int requiredItemID = required.getKey();
						int requiredItemAmount = required.getValue();
						
						if(!inventory.getItemsAndTools().containsKey(Item.getItem(requiredItemID))) {
							available = false;
							break;
						}

						if(inventory.getItemsAndTools().get(Item.getItem(requiredItemID)) < requiredItemAmount) {
							available = false;
							break;
						}
					}
					
					recipes.put(recipe, available);
				}
			}
		}
		
		refreshGraphics();
		
		Logging.info("Crafting Interface (layoutID: " + layoutID + ") refreshed - " + recipes.size() + " recipes, " + getAffordableRecipesAmount() + " affordable");
	}
	
	public void refreshGraphics() {
		for(Map.Entry<CraftingRecipe, Boolean> recipeEntry : recipes.entrySet()) {
			CraftingRecipe recipe = recipeEntry.getKey();
			boolean available = recipeEntry.getValue();
			
			if(labels.containsKey(recipe.getId())) {
				labels.get(recipe.getId()).setFont(font);
				labels.get(recipe.getId()).setColor(available ? Color.GREEN : Color.RED);
				labels.get(recipe.getId()).setCursor(available ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR);
				
				String str = "";
				
				for(Map.Entry<Integer, Integer> requiredItem : recipe.getRequired().entrySet()) {
					int requiredItemID = requiredItem.getKey();
					int requiredItemAmount = requiredItem.getValue();
					
					str += requiredItemAmount + " {" + Item.getItem(requiredItemID).getName() + "} + ";
				}
				
				str = str.substring(0, str.length() - 2).concat("{arrow} " + recipe.getResultAmount() + " {" + recipe.getResultItem().getName() + "}");
				labels.get(recipe.getId()).setContent(str);
			} else {
				AdvancedLabel label = new AdvancedLabel("");
				label.setFont(font);
				label.setIconSizeScale(1.25f);
				label.setColor(available ? Color.GREEN : Color.RED);
				label.setCursor(available ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR);
				
				String str = "";
				
				for(Map.Entry<Integer, Integer> requiredItem : recipe.getRequired().entrySet()) {
					int requiredItemID = requiredItem.getKey();
					int requiredItemAmount = requiredItem.getValue();
					
					str += requiredItemAmount + " {" + Item.getItem(requiredItemID).getName() + "} + ";
				}
				
				str = str.substring(0, str.length() - 2).concat("{arrow} " + recipe.getResultAmount() + " {" + recipe.getResultItem().getName() + "}");
				label.setContent(str);
				
				labels.put(recipe.getId(), label);
			}
		}
		
		int i = 0;
		for(Iterator<Map.Entry<String, AdvancedLabel>> iEntry = labels.entrySet().iterator(); iEntry.hasNext();) {
			boolean foundRecipe = false;
			
			Map.Entry<String, AdvancedLabel> entry = iEntry.next();
			String recipeID = entry.getKey();
			AdvancedLabel label = entry.getValue();
			
			for(CraftingRecipe recipe : recipes.keySet()) {
				if(recipe.getId() == recipeID) {
					foundRecipe = true;
					break;
				}
			}
			
			if(!foundRecipe) {
				iEntry.remove();
				break;
			}
			
			label.setPositionCenterX(true, layoutID, i / rows);
			label.setPositionCenterY(true, layoutID, i % rows);
			
			i++;
		}
	}
	
	public Map<CraftingRecipe, Boolean> getRecipes() {
		return recipes;
	}
	
	public int getAffordableRecipesAmount() {
		int count = 0;
		for(boolean affordable : recipes.values())
			if(affordable) count++;
		
		return count;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Integer> attributes) {
		this.attributes = attributes;
	}

	public Map<String, AdvancedLabel> getLabels() {
		return labels;
	}

	public String getLayoutID() {
		return layoutID;
	}

	public int getLabelsPerColumn() {
		return labelsPerColumn;
	}
}
