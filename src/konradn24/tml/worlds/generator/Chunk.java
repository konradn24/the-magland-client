package konradn24.tml.worlds.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;

import konradn24.tml.entities.Entity;
import konradn24.tml.graphics.ColorUtils;
import konradn24.tml.graphics.renderer.ColorMesh;
import konradn24.tml.tiles.Tile;

public class Chunk {

	public static final int SIZE = 32;
	public final int chunkX, chunkY;
	
	public Tile[][] tiles = new Tile[SIZE][SIZE];
	private ColorMesh mesh;
	
	public List<Entity> entities = new ArrayList<>();
	
	public Chunk(int chunkX, int chunkY) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
	}
	
	public void generateMesh(int chunkX, int chunkY, int shaderProgram) {
        List<Float> vertexList = new ArrayList<>();
        
        int worldX = chunkX * SIZE * Tile.SIZE;
        int worldY = chunkY * SIZE * Tile.SIZE;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = tiles[x][y];
                
                int tileLocalX = x * Tile.SIZE;
                int tileLocalY = y * Tile.SIZE;
                
                addQuad(vertexList, tileLocalX, tileLocalY, Tile.SIZE, tile.getColor()[0], tile.getColor()[1], tile.getColor()[2]);
            
                Random rand = new Random(tile.getSeed());

                int dotCount = 8 + rand.nextInt(8);
                for (int i = 0; i < dotCount; i++) {
                    int dx = tileLocalX + rand.nextInt(Tile.SIZE);
                    int dy = tileLocalY + rand.nextInt(Tile.SIZE);
                    float size = 1 + rand.nextInt(3);

                    float[] dotColor = ColorUtils.adjustBrightness(tile.getColor()[0], tile.getColor()[1], tile.getColor()[2], (rand.nextFloat() - 0.5f) * 0.3f);

                    addQuad(vertexList, dx, dy, size, dotColor[0], dotColor[1], dotColor[2]);
                }
            }
        }

        float[] vertices = new float[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            vertices[i] = vertexList.get(i);
        }

        mesh = new ColorMesh(vertices, shaderProgram, new Matrix4f().translate(worldX, worldY, 0));
    }

	public void render(Matrix4f viewModel) {
		if(mesh != null) {
			mesh.render(viewModel);
		}
	}

	public void cleanup() {
		if(mesh != null) {
			mesh.cleanup();
		}
	}
	
	private void addQuad(List<Float> list, float x, float y, float size, float r, float g, float b) {
		// 2 triangles = 6 vertices, each with x, y, r, g, b
		float x1 = x;
		float y1 = y;
		float x2 = x + size;
		float y2 = y + size;
		
		// Bottom-left triangle
		list.add(x1); list.add(y1); list.add(r); list.add(g); list.add(b);
		list.add(x2); list.add(y1); list.add(r); list.add(g); list.add(b);
		list.add(x2); list.add(y2); list.add(r); list.add(g); list.add(b);
		
		// Top-right triangle
		list.add(x2); list.add(y2); list.add(r); list.add(g); list.add(b);
		list.add(x1); list.add(y2); list.add(r); list.add(g); list.add(b);
		list.add(x1); list.add(y1); list.add(r); list.add(g); list.add(b);
	}
	
	public Tile getTile(int localX, int localY) {
		if(localX < 0 || localX >= SIZE || localY < 0 || localY >= SIZE) {
			return null;
		}
		
		return tiles[localX][localY];
	}
}
