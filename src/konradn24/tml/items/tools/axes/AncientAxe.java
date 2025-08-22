package konradn24.tml.items.tools.axes;

import java.util.List;
import java.util.Map;

import konradn24.tml.inventory.CraftingRecipe;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.items.Item;
import konradn24.tml.items.tools.Tool;

public class AncientAxe extends Tool {

	public AncientAxe() {
		super();
		
		setAttributes(ACTION_CHOP);
		setAttributes(ATTRIB_CRAFTABLE, ATTRIB_HAND_CRAFTABLE, ATTRIB_EQUIPPABLE);
		
		damage = 20;
		weight = 2f;
	}

	@Override
	public void init() {
		CraftingRecipe recipe1 = new CraftingRecipe(
				"ancientAxe1", 
				Map.of(
					new InventoryProperty(Item.getItem(0), 2), false,
					new InventoryProperty(Item.getItem(1), 2), false,
					new InventoryProperty(Item.getItem(2), 6), false
				), true,
				List.of(new InventoryProperty(this, 1))
		);
		
		setRecipes(recipe1);
	}
	
	@Override
	public void onUse() {

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
		return "With axe you are able to cut down trees, collect wood and establish a settlement.";
	}
}
