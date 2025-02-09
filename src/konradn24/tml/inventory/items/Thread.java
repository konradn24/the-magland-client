package konradn24.tml.inventory.items;

import java.awt.Graphics;

public class Thread extends Item {

	public Thread() {
		super();
		
		weight = 0.01f;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {

	}

	public String getInfo() {
		return "Threads can be found on ground. You can use this to craft thicker strings.";
	}
}
