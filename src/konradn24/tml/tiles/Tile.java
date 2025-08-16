package konradn24.tml.tiles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.graphics.ColorUtils;
import konradn24.tml.utils.Logging;

import static org.lwjgl.opengl.GL11.*;
public class Tile {
	
	//Tiles declarations
	public static Tile[] tiles = new Tile[256];

	//Attributes
	public static final int ATTRIB_NOT_PASSABLE = 0;
	public static final int ATTRIB_SWIMMABLE = 1;
	public static final int ATTRIB_BOAT_SWIMMABLE = 2;
	public static final int ATTRIB_NO_SPAWN = 3;
	public static final int ATTRIB_SEARCHABLE = 4;
	
	public static final int SIZE = Display.LOGICAL_WIDTH / 30;
	
	protected float[] color;
	protected int id;
	protected String name;
	protected long seed;
	
	protected String biome = "#null";
	
	protected List<Integer> attributes = new ArrayList<>();
	
	protected static Handler handler;
	
	public Tile() {
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		this.name = new String(c);
		this.color = new float[] { 0, 0, 0 };
	}
	
	public static void init(Handler handler) {
		Tile.handler = handler;
		
		registerTile(Grass.class);
		registerTile(Dirt.class);
		registerTile(BrickWall.class);
		registerTile(Concrete1.class);
		registerTile(Concrete2.class);
		registerTile(Gravel.class);
		registerTile(Water.class);
		registerTile(DeepWater.class);
		registerTile(Sand.class);
		registerTile(Snow.class);
		registerTile(Tundra.class);
		registerTile(Ice.class);
		registerTile(TaigaGrass.class);
		registerTile(RainforestGrass.class);
		registerTile(Mud.class);
		registerTile(SwampWater.class);
		
		Logging.info(declaredTilesAmount() + " tiles initialized");
	}
	
	public static int declaredTilesAmount() {
		int amount = 0;
		for(Tile tile : tiles)
			if(tile != null)
				amount++;
		
		return amount;
	}
	
	public void tick(){
		
	}
	
	public void render(int x, int y) {
		Random random = new Random(seed);
		
		float variation = (random.nextFloat() - 0.5f) * 0.05f;
        float[] baseColor = ColorUtils.adjustBrightness(color[0], color[1], color[2], variation);
        
        glColor3f(baseColor[0], baseColor[1], baseColor[2]);
        glBegin(GL_QUADS);
		glVertex2f(x, y);
		glVertex2f(x + SIZE, y);
		glVertex2f(x + SIZE, y + SIZE);
		glVertex2f(x, y + SIZE);
		glEnd();
        
        int dotCount = 10 + random.nextInt(10);

        for (int i = 0; i < dotCount; i++) {
            int dx = x + random.nextInt(SIZE);
            int dy = y + random.nextInt(SIZE);
            float dotVariation = (random.nextFloat() - 0.5f) * 0.3f;
            float[] dotColor = ColorUtils.adjustBrightness(color[0], color[1], color[2], dotVariation);
            
			glColor3f(dotColor[0], dotColor[1], dotColor[2]);
			glBegin(GL_QUADS);
			glVertex2f(dx, dy);
			glVertex2f(dx + 2, dy);
			glVertex2f(dx + 2, dy + 2);
			glVertex2f(dx, dy + 2);
			glEnd();
        }
	}
	
	private static void registerTile(Class<? extends Tile> tileClass) {
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] == null) {
				try {
					tiles[i] = tileClass.getDeclaredConstructor().newInstance();
					tiles[i].id = i;
					return;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					Logging.error("Register tile " + tileClass.getName() + " failed!");
					e.printStackTrace();
				}
			}
		}
		
		Logging.error("Register tile " + tileClass.getName() + " failed - tiles[] is full!");
	}
	
	@SuppressWarnings("unused")
	private static void registerTile(Class<? extends Tile> tileClass, int id) {
		if(id < 0 || id >= tiles.length) {
			Logging.error("Register tile " + tileClass.getName() + " failed - provided ID is out of range!");
			return;
		}
		
		if(tiles[id] != null) {
			Logging.error("Register tile " + tileClass.getName() + " failed - provided ID is already used!");
			return;
		}
		
		try {
			tiles[id] = tileClass.getConstructor().newInstance();
			tiles[id].id = id;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logging.error("Register tile " + tileClass.getName() + " failed!");
			e.printStackTrace();
		}
	}
	
	// GETTERS AND SETTERS
	
	public static Tile getTile(int id) {
		return tiles[id];
	}
	
	public static Tile getTile(String id) {
		for(Tile tile : tiles) {
			if(tile == null) continue;
			if(tile.name.matches(id)) return tile;
		}
		
		return null;
	}
	
	public static Tile getTile(Class<? extends Tile> tileClass) {
		for(Tile tile : tiles)
			if(tile.getClass().equals(tileClass))
				return tile;
		
		return null;
	}
	
	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBiome() {
		return biome;
	}
	
	public float[] getColor() {
		return color;
	}

	public Tile set(String biome, float[] color, long seed) {
		Tile tile;
		
		try {
			tile = this.getClass().getConstructor().newInstance();
			tile.biome = biome;
			tile.color = color;
			tile.seed = seed;
			
			return tile;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			Logging.error("Failed to create " + this.getClass().getSimpleName() + " tile instance with set()");
			e.printStackTrace();
			
			tile = new Tile();
			return tile;
		}
	}
	
	public List<Integer> getAttributes(){
		return attributes;
	}
	
	public boolean hasAttribute(int attribute) {
		if(attributes.contains(attribute))
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean hasAttributeAny(int... attributes) {
		for(int attribute : this.attributes) {
			if(Arrays.asList(attributes).contains(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasAttributeAny(List<Integer> attributes) {
		for(int attribute : this.attributes) {
			if(attributes.contains(attribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected void setAttributes(int... attributes) {
		for(int attribute : attributes)
			this.attributes.add(attribute);
	}
	
	public long getSeed() {
		return seed;
	}
}
