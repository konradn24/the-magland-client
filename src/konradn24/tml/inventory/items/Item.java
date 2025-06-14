/*

-- Item basic class

Implementations:
	- getInfo() to specify item's manual
	- getDisplayedName() to specify item's displayed name
	- isTool() to specify whether item is tool or not (see Tool.java)

*/
package konradn24.tml.inventory.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.inventory.crafting.CraftingRecipe;
import konradn24.tml.inventory.tools.HolyCross;
import konradn24.tml.inventory.tools.Screwdriver;
import konradn24.tml.inventory.tools.WaterBottle;
import konradn24.tml.inventory.tools.axes.AncientAxe;

public abstract class Item {

	//Items declarations
	public static Item[] items = new Item[256];
	
	// Attributes
	public static final int ATTRIB_ALL = 0;
	public static final int ATTRIB_CRAFTABLE = 1;
	public static final int ATTRIB_HAND_CRAFTABLE = 2;
	public static final int ATTRIB_EQUIPPABLE = 3;
	public static final int ATTRIB_EATABLE = 4;
	
	protected BufferedImage texture;
	protected String name;
	
	protected float weight;
	protected boolean infoTab;

	protected List<Integer> attributes = new ArrayList<>();
	protected List<CraftingRecipe> recipes = new ArrayList<>();
	
	protected static Handler handler;
	
	//Buttons
	private Button equipButton, infoButton, eatButton;
	
	public Item() {
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		this.name = new String(c);
		
		texture = Assets.getTexture(name);
		
		equipButton = new Button(new BufferedImage[] {Assets.equipBtn, Assets.equipBtn}, 0, 0, 20, 20, handler);
		infoButton = new Button(new BufferedImage[] {Assets.infoBtn, Assets.infoBtn}, 0, 0, 20, 20, handler);
		eatButton = new Button(new BufferedImage[] {Assets.eatBtn, Assets.eatBtn}, 0, 0, 20, 20, handler);
	
		setAttributes(ATTRIB_ALL);
	}
	
	public static void init(Handler handler) {
		Item.handler = handler;
		
		registerItem(Stick.class);
		registerItem(Stone.class);
		registerItem(Thread.class);
		registerItem(Cord.class);
		registerItem(Rope.class);
		registerItem(AncientAxe.class);
		
		registerItem(Screwdriver.class, 100);
		registerItem(WaterBottle.class, 101);
		registerItem(HolyCross.class, 102);
		
		Logging.info(declaredItemsAmount() + " items initialized");
	}
	
	public static int declaredItemsAmount() {
		int amount = 0;
		for(Item item : items)
			if(item != null)
				amount++;
		
		return amount;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public String getInfo() {
		return "In development...";
	}
	
	public String getDisplayedName() {
		String displayedName = "";
		
		int lastIndex = 0;
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			
			if(i == name.length() - 1) {
				displayedName += (lastIndex == 0 ? "" : " ") + name.substring(lastIndex, i + 1);
				return displayedName.substring(0, 1).toUpperCase() + displayedName.substring(1);
			}
			
			if(Character.isUpperCase(c)) {
				displayedName += (lastIndex == 0 ? "" : " ") + name.substring(lastIndex, i);
				lastIndex = i;
			}
		}
		
		return displayedName.substring(0, 1).toUpperCase() + displayedName.substring(1);
	}
	
	public boolean isTool() {
		return false;
	}
	
	private static void registerItem(Class<? extends Item> itemClass) {
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null) {
				try {
					items[i] = itemClass.getDeclaredConstructor().newInstance();
					return;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					Logging.error("Register item " + itemClass.getName() + " failed!");
					e.printStackTrace();
				}
			}
		}
		
		Logging.error("Register item " + itemClass.getName() + " failed - registry is full!");
	}
	
	private static void registerItem(Class<? extends Item> itemClass, int id) {
		if(id < 0 || id >= items.length) {
			Logging.error("Register item " + itemClass.getName() + " failed - provided ID is out of range!");
			return;
		}
		
		if(items[id] != null) {
			Logging.error("Register item " + itemClass.getName() + " failed - provided ID is already used!");
			return;
		}
		
		try {
			items[id] = itemClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logging.error("Register item " + itemClass.getName() + " failed!");
			e.printStackTrace();
		}
	}
	
	//GETTERS AND SETTERS
	
	public static Item getItem(int id) {
		if(id < 0 || id >= items.length)
			return null;
		
		return items[id];
	}
	
	public static Item getItem(String id) {
		for(Item item : items) {
			if(item == null) continue;
			if(item.name.matches(id)) return item;
		}
		
		return null;
	}
	
	public static Item getItem(Class<? extends Item> itemClass) {
		for(Item item : items) {
			if(item == null) continue;
			if(item.getClass().equals(itemClass)) return item;
		}
		
		return null;
	}
	
	public BufferedImage getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Button getEquipButton() {
		return equipButton;
	}

	public void setEquipButton(Button equipButton) {
		this.equipButton = equipButton;
	}

	public Button getInfoButton() {
		return infoButton;
	}

	public void setInfoButton(Button infoButton) {
		this.infoButton = infoButton;
	}

	public Button getEatButton() {
		return eatButton;
	}

	public void setEatButton(Button eatButton) {
		this.eatButton = eatButton;
	}

	public String getName() {
		return name;
	}
	
	public boolean isInfoTab() {
		return infoTab;
	}

	public void setInfoTab(boolean infoTab) {
		this.infoTab = infoTab;
	}
	
	public List<Integer> getAttributes(){
		return attributes;
	}
	
	public boolean hasAttribute(int attribute) {
		if(attributes.contains(attribute))
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean hasAttributeAny(int... attributes) {
		for(int attribute : this.attributes) {
			if(Arrays.asList(attributes).contains(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasAttributeAny(List<Integer> attributes) {
		for(int attribute : this.attributes) {
			if(attributes.contains(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected void setAttributes(int... attributes) {
		for(int attribute : attributes)
			this.attributes.add(attribute);
	}

	public List<CraftingRecipe> getRecipes() {
		return recipes;
	}
	
	public CraftingRecipe getRecipe(int recipeID) {
		return recipes.get(recipeID);
	}
	
	public CraftingRecipe getRecipe(String recipeID) {
		return (CraftingRecipe) recipes.stream().filter(recipe -> recipe.getId() == recipeID)
				.toArray()[0];
	}
	
	public void setRecipes(CraftingRecipe... recipes) {
		this.recipes.addAll(Arrays.asList(recipes));
	}
	
	public void addRecipe(CraftingRecipe recipe) {
		recipes.add(recipe);
	}
	
	public void removeRecipe(String id) {
		recipes.removeIf(recipe -> (recipe.getId() == id));
	}
}
