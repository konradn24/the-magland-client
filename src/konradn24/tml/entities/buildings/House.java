package konradn24.tml.entities.buildings;

import java.awt.Graphics;

import konradn24.tml.Handler;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.tiles.Tile;

@PlayerBuildable
public class House extends Building {

	public House(Handler handler) {
		super(handler, Tile.TILE_WIDTH * 5, Tile.TILE_HEIGHT * 5);
	}

	@Override
	protected void onInit() {
		bounds.x = 0;
		bounds.y = 0;
		bounds.width = width;
		bounds.height = height;
		
		originX = width / 2;
		originY = height;
		
		killable = false;
	}
	
	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.house, getScreenX(), getScreenY(), width, height, null);
	}

	@Override
	public void onDead() {
		
	}
}
