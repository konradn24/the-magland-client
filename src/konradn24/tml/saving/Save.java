package konradn24.tml.saving;

import konradn24.tml.Handler;
import konradn24.tml.Launcher;
import konradn24.tml.tiles.Tile;
import konradn24.tml.worlds.generator.World;

public class Save {

	private String version;
	private int slot;
	private String name;
	private long seed;
	private float playerX, playerY;
	
	public Save(int slot, String name, long seed) {
		// Meta data
		this.version = Launcher.VERSION;
		this.slot = slot;
		this.name = name;
		this.seed = seed;
		
		// Player data
		this.playerX = World.PLAYER_SPAWN_TILE_X * Tile.TILE_WIDTH;
		this.playerY = World.PLAYER_SPAWN_TILE_Y * Tile.TILE_HEIGHT;
	}
	
	public void update(Handler handler) {
		Save current = handler.getSavesManager().getCurrentSave();
		
		// Meta data
		this.version = current.version;
		this.slot = current.slot;
		this.name = current.name;
		this.seed = current.seed;
		
		// Player data
		this.playerX = handler.getPlayer().getX();
		this.playerY = handler.getPlayer().getY();
	}

	public int getSlot() {
		return slot;
	}
	
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public float getPlayerX() {
		return playerX;
	}

	public void setPlayerX(float playerX) {
		this.playerX = playerX;
	}

	public float getPlayerY() {
		return playerY;
	}

	public void setPlayerY(float playerY) {
		this.playerY = playerY;
	}

	public String getVersion() {
		return version;
	}
}
