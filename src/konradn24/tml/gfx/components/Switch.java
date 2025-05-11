package konradn24.tml.gfx.components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;

public class Switch extends Button {

	private boolean state;
	private BufferedImage[] secondaryTexture;
	
	private BufferedImage currentTexture;
	
	public Switch(Handler handler) {
		super(handler);
	}
	
	public Switch(BufferedImage[] texture1, BufferedImage[] texture2, int x, int y, int width, int height, Handler handler) {
		super(texture1, x, y, width, height, handler);
		
		this.secondaryTexture = texture2;
	}

	public void tick() {
		if(invisible)
			return;
		
		super.tick();
		
		hoverCursor(Cursor.HAND_CURSOR);
		
		if(isLeftReleased()) state = !state;
		
		if(state) currentTexture = isOn() ? secondaryTexture[1] : secondaryTexture[0];
		else currentTexture = isOn() ? texture[1] : texture[0];
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		g.drawImage(currentTexture, x, y, width, height, null);
	}

	public BufferedImage getSecondaryTexture() {
		return secondaryTexture[0];
	}
	
	public BufferedImage getSecondaryTextureFocused() {
		return secondaryTexture[1];
	}

	public void setSecondaryTexture(BufferedImage secondaryTexture) {
		this.secondaryTexture[0] = secondaryTexture;
	}
	
	public void setSecondaryTextureFocused(BufferedImage secondaryTexture) {
		this.secondaryTexture[1] = secondaryTexture;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
