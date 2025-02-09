package konradn24.tml.saving;

import konradn24.tml.Handler;
import konradn24.tml.Launcher;

public class Save {

	private String version;
	private String gameID;
	private long seed;
	private float playerX, playerY;
	
	public Save(Handler handler) {
		// Meta data
		this.version = Launcher.VERSION;
		this.gameID = "devtest";
		
		// World data
		this.seed = handler.getWorld().getSeed();
		
		// Player data
		this.playerX = handler.getPlayer().getX();
		this.playerY = handler.getPlayer().getY();
	}

	public String getGameID() {
		return gameID;
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
