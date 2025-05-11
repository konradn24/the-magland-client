package konradn24.tml.worlds.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.entities.EntityManager;
import konradn24.tml.tiles.Grass;
import konradn24.tml.tiles.Tile;
import konradn24.tml.tiles.TileData;
import konradn24.tml.utils.Utils;
import konradn24.tml.worlds.Earth;

public class World {

	public static final int WORLD_WIDTH = 1024;
	public static final int WORLD_HEIGHT = 1024;
	
	public static final float PLAYER_SPAWN_TILE_X = WORLD_WIDTH / 2;
	public static final float PLAYER_SPAWN_TILE_Y = WORLD_HEIGHT / 2;
	
	public static final List<String> ALL_BIOMES = List.of(Earth.FROZEN_OCEAN, Earth.HALF_FROZEN_OCEAN, 
			Earth.OCEAN, Earth.FROZEN_SEA, Earth.HALF_FROZEN_SEA, Earth.SEA, Earth.TUNDRA_BEACH, Earth.GRAVEL_BEACH,
			Earth.BEACH, Earth.SNOW_DESERT, Earth.TUNDRA, Earth.DRY_TAIGA, Earth.TAIGA, Earth.PLAINS, Earth.HILLS,
			Earth.FOREST, Earth.DESERT, Earth.SUBTROPICAL_RAINFOREST, Earth.RAINFOREST, Earth.DENSE_RAINFOREST);
	
	protected Handler handler;
	protected int width, height;
	protected long seed;
	protected Tile[][] tiles;
	
	public boolean minimap = true;
	public static int minimapKey = KeyEvent.VK_M;
	
	//Meta
	protected String worldName = "";
	protected Random random;
	protected float visibleDistance = 1200;
	protected float fogDistance = 1500;
	
	//Entities
	protected EntityManager entityManager;
	
	// Error handling
	private long lastGetSetTileError;
	
	public World(Handler handler) {
		this.handler = handler;
		entityManager = new EntityManager(handler);
		
		width = WORLD_WIDTH;
		height = WORLD_HEIGHT;
		tiles = new Tile[width][height];
	}
	
	public World(Handler handler, int width, int height) {
		this.handler = handler;
		entityManager = new EntityManager(handler);
		
		this.width = width;
		this.height = height;
		tiles = new Tile[width][height];
	}
	
	public World(Handler handler, String path){
		this.handler = handler;
		entityManager = new EntityManager(handler);
		
		loadWorld(path);
	}
	
	protected void tick() {}
	protected void render(Graphics2D g) {}
	
	protected void generate(long seed, float smoothness, float biomeSize) {
		this.random = new Random(seed);
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				generation(x, y, OpenSimplex2S.noise2_ImproveX(seed, x / smoothness, y / smoothness), OpenSimplex2S.noise2_ImproveX(seed + 420, x / biomeSize, y / biomeSize), OpenSimplex2S.noise2_ImproveX(seed + 420 * 2, x / biomeSize, y / biomeSize));
			}
		}
		
		Logging.info("Generated world (worldName: " + worldName + "; width: " + WORLD_WIDTH + "; height: " + WORLD_HEIGHT + ") with seed " + seed);
	}
	
	protected void generation(int x, int y, float altitude, float temperature, float humidity) {
		setTile(x, y, Tile.getTile(Grass.class));
	}
	
	public void tickAll() {
		tick();
		entityManager.tick();
	}
	
	public void renderAll(Graphics2D g){
		int xStart = (int) Math.max(0, handler.getGameCamera().getxOffset() / Tile.TILE_WIDTH) - 1;
		int xEnd = (int) Math.min(width, Math.ceil((handler.getGameCamera().getxOffset() + Display.LOGICAL_WIDTH) / Tile.TILE_WIDTH) + 1);
		int yStart = (int) Math.max(0, handler.getGameCamera().getyOffset() / Tile.TILE_HEIGHT) - 1;
		int yEnd = (int) Math.min(height, Math.ceil((handler.getGameCamera().getyOffset() + Display.LOGICAL_HEIGHT) / Tile.TILE_HEIGHT) + 1);
		for(int y = (int) (yStart);y < yEnd;y++) {
			for(int x = (int) (xStart);x < xEnd;x++) {
				getTile(x, y).render(g, (int) (x * Tile.TILE_WIDTH - handler.getGameCamera().getxOffset()),
										(int) (y * Tile.TILE_HEIGHT - handler.getGameCamera().getyOffset()));
			}
		}
		
		render(g);
		entityManager.render(g);
//		renderFog(g); // TODO Weather
	}
	
	@SuppressWarnings("unused")
	private void renderFog(Graphics2D g) {
		float[] dist = { 
			0.0f,
			visibleDistance / fogDistance,
			1.0f
		};
		
		Color[] colors = {
		    new Color(255, 255, 255, 0),
		    new Color(0, 0, 0, 0),
		    new Color(255, 255, 255, 255)
		};

		RadialGradientPaint fog = new RadialGradientPaint(
		    new Point(Display.LOGICAL_WIDTH / 2, Display.LOGICAL_HEIGHT / 2),
		    visibleDistance,
		    dist,
		    colors
		);

		g.setPaint(fog);
		g.fillRect(-Display.LOGICAL_WIDTH / 3 - handler.getGame().getDisplay().getXOffset(), -Display.LOGICAL_HEIGHT / 3 -handler.getGame().getDisplay().getYOffset(), (int) ((Display.LOGICAL_WIDTH / handler.getGame().getDisplay().getScale()) * 2), (int) ((Display.LOGICAL_HEIGHT / handler.getGame().getDisplay().getScale()) * 2));
	}
	
	@Deprecated
	public void renderMinimap(Graphics2D g) {
		if(minimap) {
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					getTile(x, y).render(g, x * 7 + (960 - width * 7), y * 7, 7, 7);
					
					int pX = (int) (handler.getPlayer().getX());
					int pY = (int) (handler.getPlayer().getY());
					
					if(pX / 64 == x && pY / 64 == y) {
						g.setColor(Color.red);
						g.fillRoundRect(x * 7 + (960 - width * 7), y * 7, 7, 7, 100, 100);
					}
				}
			}
			
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			g.setColor(Color.white);
			g.drawString(KeyEvent.getKeyText(minimapKey) + " to hide minimap.", 960 - width * 7 - 150, 30);
		} else {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			g.setColor(Color.white);
			g.drawString("M to show minimap.", 960 - width * 7 - 150, 30);
		}
	}
	
	public TileData getMouseAtTile() {
		int x = (int) ((handler.getMouseManager().getMouseX() + handler.getGameCamera().getxOffset()) / Tile.TILE_WIDTH);
		int y = (int) ((handler.getMouseManager().getMouseY() + handler.getGameCamera().getyOffset()) / Tile.TILE_HEIGHT);
	
		return new TileData(x, y, getTile(x, y, true));
	}
	
	public TileData getMouseAtTile(int offsetX, int offsetY) {
		int x = (int) ((handler.getMouseManager().getMouseX() + handler.getGameCamera().getxOffset() + offsetX) / Tile.TILE_WIDTH);
		int y = (int) ((handler.getMouseManager().getMouseY() + handler.getGameCamera().getyOffset() + offsetY) / Tile.TILE_HEIGHT);
	
		return new TileData(x, y, getTile(x, y, true));
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}

	public Tile getTile(int x, int y) {
		if((x < 0 || y < 0 || x >= width || y >= height)) {
			if(System.currentTimeMillis() - lastGetSetTileError > 5000) {
				lastGetSetTileError = System.currentTimeMillis();
				
				Logging.error("Cannot get tile at (" + x + ", " + y + ") - no such position!");
			}
			
			return Tile.getTile(Grass.class);
		}
		
		return tiles[x][y];
	}
	
	public Tile getTile(int x, int y, boolean returnNull) {
		if((x < 0 || y < 0 || x >= width || y >= height)) {
			if(System.currentTimeMillis() - lastGetSetTileError > 5000) {
				lastGetSetTileError = System.currentTimeMillis();
				
				Logging.error("Cannot get tile at (" + x + ", " + y + ") - no such position!");
			}
			
			return returnNull ? null : Tile.getTile(Grass.class);
		}
		
		return tiles[x][y];
	}
	
	protected void setTile(int x, int y, Tile tile) {
		if((x < 0 || y < 0 || x >= width || y >= height)) {
			if(System.currentTimeMillis() - lastGetSetTileError > 5000) {
				lastGetSetTileError = System.currentTimeMillis();
				
				Logging.error("Cannot get tile at (" + x + ", " + y + ") - no such position!");
			}
			
			return;
		}
		
		this.tiles[x][y] = tile;
	}
	
	private void loadWorld(String path){
		String file = Utils.loadFileAsString(path);
		String[] tokens = file.split("\\s+");
		width = Utils.parseInt(tokens[0]);
		height = Utils.parseInt(tokens[1]);
		float spawnX = Utils.parseInt(tokens[2]);
		float spawnY = Utils.parseInt(tokens[3]);
		
		tiles = new Tile[width][height];
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				tiles[x][y] = Tile.getTile(Utils.parseInt(tokens[(x + y * width) + 4]));
			}
		}
		
		handler.getPlayer().setX(spawnX);
		handler.getPlayer().setY(spawnY);
		
		Logging.info("Loaded world from file " + path);
	}
	
	public Random getRandom() {
		return random;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public long getSeed() {
		return seed;
	}
	
	public String toString() {
		return "Name: " + worldName
				+ " | Width: " + width
				+ " | Height: " + height
				+ " | Tile width: " + Tile.TILE_WIDTH + "px"
				+ " | Tile height: " + Tile.TILE_HEIGHT + "px\n"
				+ "Minimap key: " + KeyEvent.getKeyText(minimapKey)
				+ " | Minimap: " + minimap
				+ " | Seed: " + seed
				+ " | Spawn point: (" + PLAYER_SPAWN_TILE_X + ", " + PLAYER_SPAWN_TILE_Y + ")\n"
				+ "Tiles: " + width * height
				+ " | Entities: " + entityManager.getEntities().size()
				+ " | Render distance: " + EntityManager.RENDER_DISTANCE + "px"
				+ " | Visible distance: " + EntityManager.VISIBLE_DISTANCE + "px (DEPRECATED)";
	}
}








