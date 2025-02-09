package konradn24.tml.entities.statics;

import java.awt.Graphics;

import konradn24.tml.Handler;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.tiles.Tile;

public class Tree2 extends StaticEntity {

	public Tree2(Handler handler, float x, float y) {
		super(handler, x, y - Tile.TILE_HEIGHT * 2, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 3);
	}
	
	public Tree2(Handler handler, int x, int y) {
		super(handler, x * Tile.TILE_WIDTH, (y - 2) * Tile.TILE_HEIGHT, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 3);
	}

	@Override
	protected void onInit() {
		bounds.x = width / 2 - 8;
		bounds.y = (int) height - 10;
		bounds.width = 10;
		bounds.height = 10;
		
		originX = width / 2;
		originY = height;
		
		setHealth(140);
		
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
