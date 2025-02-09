package konradn24.tml.inventory.items;

import java.awt.Graphics;
import java.util.Map;

import konradn24.tml.inventory.crafting.CraftingRecipe;

public class Stick extends Item {

	public Stick() {
		super();
		
		setAttributes(Item.ATTRIB_CRAFTABLE, Item.ATTRIB_HAND_CRAFTABLE);
		setRecipes(new CraftingRecipe(this, "log_to_sticks", Map.of(1, 1), 6),
				new CraftingRecipe(this, "log_to_sticks_2", Map.of(1, 1), 5),
				new CraftingRecipe(this, "log_to_sticks_3", Map.of(1, 1), 4),
				new CraftingRecipe(this, "log_to_sticks_4", Map.of(1, 1), 3),
				new CraftingRecipe(this, "log_to_sticks_5", Map.of(1, 1), 2),
				new CraftingRecipe(this, "log_to_sticks_6", Map.of(1, 2), 3),
				new CraftingRecipe(this, "log_to_sticks_7", Map.of(1, 3), 4),
				new CraftingRecipe(this, "log_to_sticks_8", Map.of(1, 4), 5));
		
		weight = 0.5f;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {

	}

	public String getInfo() {
		return "Sticks can be found on ground. Use them to make necessary items and tools or as fuel for fire.";
	}
}
