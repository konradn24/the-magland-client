package konradn24.tml.inventory.items;

import java.awt.Graphics;

public class Stone extends Item {

	public Stone() {
		super();
		
		setAttributes(Item.ATTRIB_CRAFTABLE);
		
		weight = 0.8f;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {

	}

	public String getInfo() {
		return "Stones can be found on ground. This resource can be used to craft some simple items.";
	}
}
