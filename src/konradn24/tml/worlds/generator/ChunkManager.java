package konradn24.tml.worlds.generator;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import konradn24.tml.tiles.Tile;

public class ChunkManager {

	public static final int LOAD_RADIUS = 2;
	
	private final Map<Point, Chunk> loadedChunks = new HashMap<>();
	private final World world;
	
	private float lastUpdateChunkX, lastUpdateChunkY;
	
	public ChunkManager(World world) {
		this.world = world;
	}
	
	public void update(int chunkX, int chunkY) {
		if(lastUpdateChunkX == chunkX && lastUpdateChunkY == chunkY) {
			return;
		}
		
		reload(chunkX, chunkY);
	}
	
	public void reload(int chunkX, int chunkY) {
		loadNearChunks(chunkX, chunkY, LOAD_RADIUS);
		unloadFarChunks(chunkX, chunkY);
	}
	
	public void render(Matrix4f viewMatrix) {
		for(Chunk chunk : loadedChunks.values()) {
			chunk.render(viewMatrix);
		}
	}
	
//	public void renderChunk(Chunk chunk) {
//	    for (int tx = 0; tx < Chunk.SIZE; tx++) {
//	        for (int ty = 0; ty < Chunk.SIZE; ty++) {
//	            Tile tile = chunk.tiles[tx][ty];
//	            if (tile == null) continue;
//
//	            int worldX = chunk.chunkX * Chunk.SIZE + tx;
//	            int worldY = chunk.chunkY * Chunk.SIZE + ty;
//
//	            int screenX = (int) (worldX * Tile.SIZE);
//	            int screenY = (int) (worldY * Tile.SIZE);
//
//	            tile.render(screenX, screenY);
//	        }
//	    }
//	}
	
	public Chunk getChunk(int chunkX, int chunkY) {
		Point key = new Point(chunkX, chunkY);
		
		if(!loadedChunks.containsKey(key)) {
			Chunk chunk = generateChunk(chunkX, chunkY);
			loadedChunks.put(key, chunk);
		}
		
		return loadedChunks.get(key);
	}
	
	public void loadNearChunks(int chunkX, int chunkY, int radius) {
		for(int x = chunkX - radius; x < chunkX + radius; x++) {
			for(int y = chunkY - radius; y < chunkY + radius; y++) {
				Point key = new Point(x, y);
				
				if(!loadedChunks.containsKey(key)) {
					Chunk chunk = generateChunk(x, y);
					loadedChunks.put(key, chunk);
				}
			}
		}
	}
	
	public void unloadFarChunks(int chunkX, int chunkY) {
		loadedChunks.entrySet().removeIf(entry -> {
			int iChunkX = entry.getKey().x;
			int iChunkY = entry.getKey().y;
			
			if(Math.abs(iChunkX - chunkX) > LOAD_RADIUS || Math.abs(iChunkY - chunkY) > LOAD_RADIUS) {
				world.getEntityManager().removeEntitiesFromChunk(entry.getValue());
				entry.getValue().cleanup();
				return true;
			}
			
			return false;
		});
	}
	
	private Chunk generateChunk(int chunkX, int chunkY) {
		Chunk chunk = new Chunk(chunkX, chunkY);
		
		for(int tx = 0; tx < Chunk.SIZE; tx++) {
			for(int ty = 0; ty < Chunk.SIZE; ty++) {
				int worldX = chunkX * Chunk.SIZE + tx;
				int worldY = chunkY * Chunk.SIZE + ty;
				
				float altitude = OpenSimplex2S.noise2_ImproveX(world.seed, worldX / world.smoothness, worldY / world.smoothness);
				float temperature = OpenSimplex2S.noise2_ImproveX(world.seed + 420, worldX / world.biomeSize, worldY / world.biomeSize);
				float humidity = OpenSimplex2S.noise2_ImproveX(world.seed + 840, worldX / world.biomeSize, worldY / world.biomeSize);
                
                GenerationData data = world.generation(worldX, worldY, altitude, temperature, humidity);
                chunk.tiles[tx][ty] = data.tile;
                chunk.entities.addAll(data.entities);
			}
		}
		
		chunk.generateMesh(chunkX, chunkY, world.getShader());
		
		world.getEntityManager().addEntitiesFromChunk(chunk);
		
		return chunk;
	}
	
	public void cleanup() {
		for(Chunk chunk : loadedChunks.values()) {
			chunk.cleanup();
		}
		
		loadedChunks.clear();
		world.entityManager.getEntities().clear();
	}
	
	public Tile getTile(float worldX, float worldY) {
		int chunkX = (int) Math.floor(worldX / Chunk.SIZE);
		int chunkY = (int) Math.floor(worldY / Chunk.SIZE);
		
		int localX = Math.floorMod((int) Math.floor(worldX), Chunk.SIZE);
		int localY = Math.floorMod((int) Math.floor(worldY), Chunk.SIZE);
		
		Chunk chunk = getChunk(chunkX, chunkY);
		
		return chunk.getTile(localX, localY);
	}
	
	public static Point getChunkLocation(float worldX, float worldY) {
		int chunkX = (int) Math.floor(worldX / Chunk.SIZE);
		int chunkY = (int) Math.floor(worldY / Chunk.SIZE);
		
		return new Point(chunkX, chunkY);
	}
	
	public Map<Point, Chunk> getLoadedChunks() {
		return loadedChunks;
	}
}
