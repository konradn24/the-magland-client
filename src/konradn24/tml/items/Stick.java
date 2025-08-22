package konradn24.tml.items;

import java.util.List;
import java.util.Map;

import konradn24.tml.inventory.CraftingRecipe;
import konradn24.tml.inventory.InventoryProperty;

public class Stick extends Item {

	public Stick() {
		super();
		
		setAttributes(Item.ATTRIB_CRAFTABLE, Item.ATTRIB_HAND_CRAFTABLE);
		
		weight = 0.5f;
	}

	@Override
	public void init() {
		CraftingRecipe recipe1 = new CraftingRecipe(
				"stoneToSticks", 
				Map.of(
					new InventoryProperty(Item.getItem(1), 2), false
				), true,
				List.of(new InventoryProperty(this, 4))
		);
		
		setRecipes(recipe1);
	}
	
	@Override
	public void update(float dt) {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void renderGUI(long vg) {
		
	}

	public String getInfo() {
		return "Sticks can be found on ground. Use them to make necessary items and tools or as fuel for fire.";
	}
}
