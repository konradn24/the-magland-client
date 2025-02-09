package konradn24.tml.inventory.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class HolyCross extends Tool {
	
	private long timer, lastTime = System.currentTimeMillis();
	private byte seconds;
	private boolean canHeal;
	
	public HolyCross() {
		super();
		
		damage = 0;
		range = 300;
		weight = 0.05f;
	}

	@Override
	public void tick() {
		timer = System.currentTimeMillis();
		if(timer - lastTime >= 1000 && seconds < 40) {
			seconds++;
			lastTime = System.currentTimeMillis();
		}
		
		if(seconds == 40)
			canHeal = true;
		else
			canHeal = false;
	}

	@Override
	public void render(Graphics g) {
		if(handler.getPlayer().getInventory().getCurrentItem() == this) {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
			g.setColor(Color.MAGENTA);
			
			if(canHeal) {
				if(handler.getPlayer().getHealth() >= 100)
					g.drawString("FULL HP", 430, 640);
				else
					g.drawString("F TO HEAL", 430, 640);
				
				if(handler.getKeyManager().getKeys()[KeyEvent.VK_F]) {
					handler.getPlayer().heal(this);
					seconds = 0;
					lastTime = System.currentTimeMillis();
				}
			} else {
				g.drawString(40 - seconds + " seconds", 430, 640);
			}
		}
	}

	@Override
	public void onUse() {
		
	}
	
	public String getInfo() {
		return "In development...";
	}
}
