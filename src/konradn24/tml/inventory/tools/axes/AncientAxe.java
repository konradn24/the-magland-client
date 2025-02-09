package konradn24.tml.inventory.tools.axes;

import java.awt.Graphics;
import java.util.Map;

import konradn24.tml.inventory.crafting.CraftingRecipe;
import konradn24.tml.inventory.tools.Tool;

public class AncientAxe extends Tool {

	public AncientAxe() {
		super();
		
		setAttributes(ACTION_CHOP);
		setAttributes(ATTRIB_CRAFTABLE, ATTRIB_HAND_CRAFTABLE, ATTRIB_EQUIPPABLE);
		setRecipes(new CraftingRecipe(this, "ancientAxe1", Map.of(0, 2, 1, 2, 2, 6), 1));
		
		damage = 20;
		weight = 2f;
	}

	@Override
	public void onUse() {

	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {

	}

	public String getInfo() {
		return "With axe you are able to cut down trees, collect wood and establish a settlement.";
	}
}
