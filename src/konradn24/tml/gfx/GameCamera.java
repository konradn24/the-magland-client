package konradn24.tml.gfx;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.entities.Entity;
import konradn24.tml.tiles.Tile;

public class GameCamera {
	
	private Handler handler;
	private float xOffset, yOffset;
	
	private boolean blocking;
	
	public GameCamera(Handler handler, float xOffset, float yOffset){
		this.handler = handler;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		Logging.info("Game Camera initialized");
	}
	
	public void checkBlankSpace() {
		if(xOffset < 0) {
			xOffset = 0;
			blocking = true;
		} else if(xOffset > handler.getWorld().getWidth() * Tile.TILE_WIDTH - Display.LOGICAL_WIDTH) {
			xOffset = handler.getWorld().getWidth() * Tile.TILE_WIDTH - Display.LOGICAL_WIDTH;
			blocking = true;
		} else
			blocking = false;
		
		if(yOffset < 0) {
			yOffset = 0;
			blocking = true;
		} else if(yOffset > handler.getWorld().getHeight() * Tile.TILE_HEIGHT - Display.LOGICAL_HEIGHT) {
			yOffset = handler.getWorld().getHeight() * Tile.TILE_HEIGHT - Display.LOGICAL_HEIGHT;
			blocking = true;
		} else
			blocking = false;
	}
	
	public void centerOnEntity(Entity e){
		xOffset = e.getX() - Display.LOGICAL_WIDTH / 2 + e.getWidth() / 2;
		yOffset = e.getY() - Display.LOGICAL_HEIGHT / 2 + e.getHeight() / 2;
		checkBlankSpace();
	}
	
	public void move(float xAmt, float yAmt){
		xOffset += xAmt;
		yOffset += yAmt;
	}

	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	public boolean isBlocking() {
		return blocking;
	}
}
