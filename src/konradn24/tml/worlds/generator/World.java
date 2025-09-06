package konradn24.tml.worlds.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import konradn24.tml.Handler;
import konradn24.tml.entities.EntityManager;
import konradn24.tml.graphics.renderer.BatchRenderer;
import konradn24.tml.graphics.shaders.Shader;
import konradn24.tml.tiles.Grass;
import konradn24.tml.tiles.Tile;
import konradn24.tml.tiles.TileData;

public class World {
	
	public static final double PLAYER_SPAWN_TILE_X = 0;
	public static final double PLAYER_SPAWN_TILE_Y = 0;
	
	public static final String BIOME_NULL = "#null";
	
	protected Handler handler;
	protected long seed;
	protected int sizeX, sizeY;
	protected float smoothness = 120, biomeSize = 150;
	
	protected ChunkManager chunkManager;
	protected Shader shader;
	
	protected Map<String, Vector3f> biomes = new HashMap<>();
	
	// Meta
	protected String worldName = "";
	protected float visibleDistance = 1200;
	protected float fogDistance = 1500;
	
	// Entities
	protected EntityManager entityManager;
	
	public World(int sizeX, int sizeY, Handler handler, Shader chunkShader, Shader batchShader) throws IOException {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.handler = handler;
		this.shader = chunkShader;
		chunkManager = new ChunkManager(this);
		entityManager = new EntityManager(handler);
		
		BatchRenderer.init(batchShader);
	}
	
	public World(Handler handler, String path, Shader chunkShader, Shader batchShader) throws IOException {
		this.handler = handler;
		this.shader = chunkShader;
		chunkManager = new ChunkManager(this);
		entityManager = new EntityManager(handler);
		
		BatchRenderer.init(batchShader);
		
//		loadWorld(path);
	}
	
	protected void update(float dt) {}
	protected void render() {}
	
	protected GenerationData generation(int x, int y, float altitude, float temperature, float humidity) {
		return new GenerationData(Tile.getTile(Grass.class), List.of());
	}
	
	public void updateAll(float dt) {
		update(dt);
		entityManager.update(dt);
		chunkManager.update(handler.getPlayer().getChunkX(), handler.getPlayer().getChunkY());
	}
	
	public void renderAll(){
//		int xStart = (int) handler.getGameCamera().getxOffset() / Tile.TILE_WIDTH - 2;
//		int xEnd = (int) Math.ceil((handler.getGameCamera().getxOffset() + Display.LOGICAL_WIDTH) / Tile.TILE_WIDTH) + 2;
//		int yStart = (int) handler.getGameCamera().getyOffset() / Tile.TILE_HEIGHT - 2;
//		int yEnd = (int) Math.ceil((handler.getGameCamera().getyOffset() + Display.LOGICAL_HEIGHT) / Tile.TILE_HEIGHT) + 2;
		
//		int xStart = -32;
//		int xEnd = 32;
//		int yStart = -32;
//		int yEnd = 32;
//		
//		for(int y = yStart; y < yEnd; y++) {
//			for(int x = xStart; x < xEnd; x++) {
//				chunkManager.getTile(x, y).render((int) (x * Tile.SIZE), (int) (y * Tile.SIZE));
//			}
//		}
		
		chunkManager.render(handler.getCamera().getPosition());
		render();
		entityManager.render();
//		renderFog(g); // TODO Weather
	}
	
	public void renderGUI(long vg) {
		entityManager.renderGUI(vg);
	}
	
	public void cleanup() {
		chunkManager.cleanup();
	}
	
	public TileData getMouseAtTile() {
		float worldX = (float) ((handler.getMouseManager().getMouseX() + handler.getCamera().getPosition().x) / Tile.SIZE);
		float worldY = (float) ((handler.getMouseManager().getMouseY() + handler.getCamera().getPosition().y) / Tile.SIZE);
	
		return new TileData(worldX, worldY, chunkManager.getTile(worldX, worldY));
	}
	
	public TileData getMouseAtTile(int offsetX, int offsetY) {
		float worldX = (float) ((handler.getMouseManager().getMouseX() + handler.getCamera().getPosition().x + offsetX) / Tile.SIZE);
		float worldY = (float) ((handler.getMouseManager().getMouseY() + handler.getCamera().getPosition().y + offsetY) / Tile.SIZE);
	
		return new TileData(worldX, worldY, chunkManager.getTile(worldX, worldY));
	}
	
	public ChunkManager getChunkManager() {
		return chunkManager;
	}

//	private void loadWorld(String path){
//		String file = Utils.loadFileAsString(path);
//		String[] tokens = file.split("\\s+");
//		width = Utils.parseInt(tokens[0]);
//		height = Utils.parseInt(tokens[1]);
//		float spawnX = Utils.parseInt(tokens[2]);
//		float spawnY = Utils.parseInt(tokens[3]);
//		
//		tiles = new Tile[width][height];
//		for(int y = 0;y < height;y++){
//			for(int x = 0;x < width;x++){
//				tiles[x][y] = Tile.getTile(Utils.parseInt(tokens[(x + y * width) + 4]));
//			}
//		}
//		
//		handler.getPlayer().setX(spawnX);
//		handler.getPlayer().setY(spawnY);
//		
//		Logging.info("Loaded world from file " + path);
//	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public String getWorldName() {
		return worldName;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Map<String, Vector3f> getBiomes() {
		return biomes;
	}
	
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public long getSeed() {
		return seed;
	}
	
	public String toString() {
		return "Name: " + worldName
				+ " | Tile width: " + Tile.SIZE + "px"
				+ " | Tile height: " + Tile.SIZE + "px\n"
				+ " | Seed: " + seed
				+ " | Spawn point: (" + 0 + ", " + 0 + ") !TODO\n"
				+ " Entities: " + entityManager.getEntities().size()
				+ " | Chunks: " + chunkManager.getLoadedChunks().size()
				+ " | Render distance: " + EntityManager.RENDER_DISTANCE + "px"
				+ " | Visible distance: " + EntityManager.VISIBLE_DISTANCE + "px (DEPRECATED)";
	}
}








