package konradn24.tml.building;

import java.awt.Graphics;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.buildings.Building;
import konradn24.tml.gfx.Presets;
import konradn24.tml.tiles.Tile;
import konradn24.tml.tiles.TileData;

public class BuildingsPlacer {

	private boolean active;
	private Building building;
	
	private Handler handler;
	
	public BuildingsPlacer(Handler handler) {
		this.handler = handler;
	}
	
	public void tick() {
		if(!active)
			return;
		
		if(building == null) {
			active = false;
			return;
		}
		
		int offsetX = Tile.TILE_WIDTH / 2 - building.getWidth() / 2;
		int offsetY = Tile.TILE_HEIGHT / 2 - building.getHeight() / 2;
		TileData pointingAt = handler.getWorld().getMouseAtTile(offsetX, offsetY);
		
		building.setTileX(pointingAt.x);
		building.setTileY(pointingAt.y);
		
		if(isPlaceAvailable() && handler.getMouseManager().isLeftPressed())
			place();
	}
	
	public void render(Graphics g) {
		if(!active)
			return;
		
		g.drawImage(building.getTexture(), building.getScreenX(), building.getScreenY(), building.getWidth(), building.getHeight(), null);
		
		g.setColor(isPlaceAvailable() ? Presets.COLOR_GREEN_LIGHT : Presets.COLOR_RED_LIGHT);
		g.fillRect(building.getScreenX(), building.getScreenY(), building.getWidth(), building.getHeight());
	}
	
	public void activate(Building building) {
		this.active = true;
		this.building = building;
		
		Logging.info("Buildings Placer: activated for " + building.getClass().getSimpleName());
	}
	
	public void deactivate() {
		this.active = false;
		this.building = null;
		
		Logging.info("Buildings Placer: deactivated");
	}
	
	public boolean isPlaceAvailable() {
		if(building == null)
			return false;
		
		return !building.collidesWithAny(0, 0);
	}
	
	public void place() {
		if(building == null) {
			Logging.error("Buildings Placer: cannot place - building is null");
			deactivate();
		}
		
		handler.getWorld().getEntityManager().addEntity(building);
		
		Logging.info("Buildings Placer: placed " + building.getClass().getSimpleName() + " at " + building.getX() + ", " + building.getY());
		
		deactivate();
	}
	
	public boolean isActive() {
		return active;
	}

	public Building getBuilding() {
		return building;
	}
}
