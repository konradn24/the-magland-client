package konradn24.tml.entities.buildings;

import java.awt.Graphics2D;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.inventory.items.Cord;
import konradn24.tml.inventory.items.Stick;
import konradn24.tml.inventory.items.Stone;
import konradn24.tml.tiles.Tile;

@PlayerBuildable
public class Shelter extends Building {

	public Shelter(Handler handler) {
		super(handler, Tile.TILE_WIDTH * 2, Tile.TILE_HEIGHT * 2);
	}

	@Override
	protected void onInit() {
		requiredLevel = 1;
		rewardExperiencePoints = 5;
		
		setRequiredMaterials(Map.of(
				Stick.class, 15,
				Cord.class, 6,
				Stone.class, 4));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics2D g) {

	}

	@Override
	public void onDead() {

	}
}
