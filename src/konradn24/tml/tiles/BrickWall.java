package konradn24.tml.tiles;

import konradn24.tml.gfx.images.Assets;

public class BrickWall extends Tile {

	public BrickWall() {
		super();
		
		texture = Assets.getTexture("brick");
		
		setAttributes(Tile.ATTRIB_NOT_PASSABLE, Tile.ATTRIB_NO_SPAWN);
	}
}
