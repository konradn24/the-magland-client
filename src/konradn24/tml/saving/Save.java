package konradn24.tml.saving;

import konradn24.tml.Handler;
import konradn24.tml.Launcher;
import konradn24.tml.worlds.generator.World;

public class Save {

	private String version;
	private int slot;
	private String name;
	private long seed;
	private double playerWorldX, playerWorldY;
	
	public Save(int slot, String name, long seed) {
		// Meta data
		this.version = Launcher.VERSION;
		this.slot = slot;
		this.name = name;
		this.seed = seed;
		
		// Player data
		this.playerWorldX = World.PLAYER_SPAWN_TILE_X;
		this.playerWorldY = World.PLAYER_SPAWN_TILE_Y;
	}
	
	public void update(Handler handler) {
		Save current = handler.getSavesManager().getCurrentSave();
		
		// Meta data
		this.version = current.version;
		this.slot = current.slot;
		this.name = current.name;
		this.seed = current.seed;
		
		// Player data
		this.playerWorldX = handler.getPlayer().getWorldX();
		this.playerWorldY = handler.getPlayer().getWorldY();
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

	public double getPlayerWorldX() {
		return playerWorldX;
	}

	public void setPlayerWorldX(double playerWorldX) {
		this.playerWorldX = playerWorldX;
	}

	public double getPlayerWorldY() {
		return playerWorldY;
	}

	public void setWorldPlayerY(double playerWorldY) {
		this.playerWorldY = playerWorldY;
	}

	public String getVersion() {
		return version;
	}
}
