package konradn24.tml.inventory.tools;

import java.awt.Graphics;

public class Screwdriver extends Tool {

	public Screwdriver() {
		super();
		
		damage = 10;
		range = 100;
		weight = 0.1f;
	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		
	}
	
	@Override
	public void onUse() {
		
	}
	
	public String getInfo() {
		return "In development...";
	}
}
