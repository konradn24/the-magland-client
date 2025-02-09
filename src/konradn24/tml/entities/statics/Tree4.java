package konradn24.tml.entities.statics;

import java.awt.Graphics;

import konradn24.tml.Handler;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.tiles.Tile;

public class Tree4 extends StaticEntity {

	public Tree4(Handler handler, float x, float y) {
		super(handler, x, (int) (y - Tile.TILE_HEIGHT * 3.5), Tile.TILE_WIDTH * 3, (int) (Tile.TILE_HEIGHT * 4.5));
	}
	
	public Tree4(Handler handler, int x, int y) {
		super(handler, x * Tile.TILE_WIDTH, (int) ((y - 3.5) * Tile.TILE_HEIGHT), Tile.TILE_WIDTH * 3, (int) (Tile.TILE_HEIGHT * 4.5));
	}

	@Override
	protected void onInit() {
		bounds.x = width / 2 - 8;
		bounds.y = (int) height - 10;
		bounds.width = 10;
		bounds.height = 10;
		
		originX = width / 2;
		originY = height;
		
		setHealth(160);
		
		actionsByAttribute.put(Tool.ACTION_CHOP, (item, handler) -> {
			damage(item);
		});
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Graphics g) {

	}

	@Override
	public void onDead() {

	}

}
