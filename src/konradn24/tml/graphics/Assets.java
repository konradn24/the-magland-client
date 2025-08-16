package konradn24.tml.graphics;

import java.util.HashMap;
import java.util.Map;

import konradn24.tml.graphics.renderer.Animation;
import konradn24.tml.graphics.renderer.SpriteSheet;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.utils.Logging;

public class Assets {
	
	public static final long NO_NVG = 0L;
	
	private static final int WIDTH = 32, HEIGHT = 32;
	
	private static Map<String, SpriteSheet> sheets = new HashMap<>();
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, Animation> animations = new HashMap<>();
	
	public static void init(long vg){
		// Sheets
		registerSheet(vg, "general", "res/textures/sheet.png");
		registerSheet(NO_NVG, "tiles", "res/textures/tiles.png");
		registerSheet(vg, "items", "res/textures/items.png");
		registerSheet(NO_NVG, "plants", "res/textures/plants.png");
		registerSheet(vg, "buildings", "res/textures/buildings.png");
		registerSheet(NO_NVG, "nature1", "res/textures/nature_pack_1.png");
		registerSheet(NO_NVG, "nature2", "res/textures/nature_pack_2.png");
		
		register("general/null", 7, 7, false);
		
		// Entities
		register("general/playerDown", new float[] {4, 5}, new float[] {0, 0}, 500, true);
		register("general/playerUp", new float[] {6, 7}, new float[] {0, 0}, 500, true);
		register("general/playerRight", new float[] {4, 5}, new float[] {1, 1}, 500, true);
		register("general/playerLeft", new float[] {6, 7}, new float[] {1, 1}, 500, true);
		
		//GUI
		register("general/currentItemSlot", 6, 2, 1, 1, false);
		register("general/heart", 7, 2, 1, 1, false);
		
		register("general/arrow", 2, 5, 1, 1, false);
		register("general/handIcon", 3, 5, 1, 1, true);
		
		//tiles
		register("tiles/dirt", 				1, 0, false);
		register("tiles/grass", 			2, 0, false);
		register("tiles/brick", 			3, 0, false);
		register("tiles/concrete1", 		4, 0, false);
		register("tiles/concrete2", 		5, 0, false);
		register("tiles/gravel", 			6, 0, false);
		register("tiles/water", 			7, 0, false);
		register("tiles/deepWater", 		0, 1, false);
		register("tiles/sand", 				1, 1, false);
		register("tiles/snow", 				2, 1, false);
		register("tiles/tundra", 			3, 1, false);
		register("tiles/ice", 				4, 1, false);
		register("tiles/taigaGrass", 		5, 1, false);
		register("tiles/rainforestGrass", 	6, 1, false);
		register("tiles/mud", 				7, 1, false);
		register("tiles/swampWater", 		0, 2, false);
		
		//plants
		register("plants/tree1", 0, 0, 1f, 1.5f, false);
		register("plants/tree2", 1, 0, 1f, 1.5f, false);
		register("plants/tree3", 2, 0, 1f, 1.5f, false);
		register("plants/tree4", 3, 0, 1f, 1.5f, false);
		register("plants/taigaTree1", 4, 0, 1f, 1.5f, false);
		register("plants/taigaTree2", 0, 3, 1f, 2f, false);
		register("plants/taigaTree3", 1, 3, 1f, 2f, false);
		
		register("nature1/saguaroCactus1", 0, 0, 2, 4, false);
		register("nature1/saguaroCactus2", 2, 0, 2, 4, false);
		register("nature1/aloe1", 4, 0, 3, 3, false);
		register("nature1/yucca1", 7, 0, 3, 3, false);
		register("nature1/opuntia1", 10, 0, 2, 2, false);
		
		register("nature1/spruce1", 0, 4, 4, 4, false);
		
		register("nature1/fir1", 0, 8, 2, 4, false);
		register("nature1/pine1", 2, 8, 2, 4, false);
		register("nature1/pine2", 4, 8, 2, 4, false);
		register("nature1/dryOak1", 6, 8, 2, 4, false);
		register("nature1/oak1", 8, 8, 4, 4, false);
		
		register("nature2/jungleTree1", 0, 0, 3, 4, false);
		register("nature2/jungleTree2", 3, 0, 4, 4, false);
		
		//items
		register("items/stick", 0, 0, false);
		register("items/stone", 1, 0, false);
		register("items/thread", 2, 0, false);
		register("items/cord", 3, 0, false);
		register("items/rope", 4, 0, false);
		register("items/ancientAxe", 5, 0, true);
		register("items/pouch", 6, 0, false);
		
		//buildings
		register("buildings/shelter", 0, 0, 2, 2, false);
	}
	
	public static void registerSheet(long vg, String id, String path) {
		if(sheets.containsKey(id)) {
			Logging.warning("Assets: sheet \"" + id + "\" has been already registered");
			return;
		}
		
		sheets.put(id, new SpriteSheet(vg, path));
		
		Logging.info("Assets: registered sheet \"" + id + "\"");
	}
	
	public static void register(String id, int x, int y, boolean generateCursor) {
		if(textures.containsKey(id)) {
			Logging.warning("Assets: texture \"" + id + "\" has been already registered");
			return;
		}
		
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String textureID = splittedID[1];
		
		Texture texture = sheets.get(sheetID).crop(WIDTH * x, HEIGHT * y, WIDTH, HEIGHT, generateCursor);
		
		textures.put(textureID, texture);
	}
	
	public static void register(String id, float x, float y, float width, float height, boolean generateCursor) {
		if(textures.containsKey(id)) {
			Logging.warning("Assets: texture \"" + id + "\" has been already registered");
			return;
		}
		
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String textureID = splittedID[1];
		
		Texture texture = sheets.get(sheetID).crop(WIDTH * x, HEIGHT * y, WIDTH * width, HEIGHT * height, generateCursor);
		
		textures.put(textureID, texture);
	}
	
	public static void register(String id, float[] x, float[] y, int interval, boolean loop) {
		if(x.length != y.length) {
			Logging.error("Assets: cannot load animation \"" + id + "\" - x[] and y[] are not the same length");
			return;
		}
		
		if(textures.containsKey(id)) {
			Logging.warning("Assets: animation \"" + id + "\" has been already registered");
			return;
		}
		
		int length = x.length;
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String animationID = splittedID[1];
		
		Texture[] frames = new Texture[length];
		
		for(int i = 0; i < length; i++)
			frames[i] = sheets.get(sheetID).crop(WIDTH * x[i], HEIGHT * y[i], WIDTH, HEIGHT, false);
		
		animations.put(animationID, new Animation(interval, frames, loop));
	}
	
	public static void register(String id, float[] x, float[] y, float width, float height, int interval, boolean loop) {
		if(x.length != y.length) {
			Logging.error("Assets: cannot load animation \"" + id + "\" - x[] and y[] are not the same length");
			return;
		}
		
		if(textures.containsKey(id)) {
			Logging.warning("Assets: animation \"" + id + "\" has been already registered");
			return;
		}
		
		int length = x.length;
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String animationID = splittedID[1];
		
		Texture[] frames = new Texture[length];
		
		for(int i = 0; i < length; i++)
			frames[i] = sheets.get(sheetID).crop(Assets.WIDTH * x[i], Assets.HEIGHT * y[i], Assets.WIDTH * width, Assets.HEIGHT * height, false);
		
		animations.put(animationID, new Animation(interval, frames, loop));
	}
	
	public static SpriteSheet getSheet(String id) {
		return sheets.get(id);
	}
	
	public static Texture getTexture(String id) {
		return textures.getOrDefault(id, textures.get("null"));
	}
	
	public static Animation getAnimation(String id) {
		if(animations.containsKey(id)) {
			return animations.get(id);
		}
		
		Animation defaultAnimation = new Animation(Integer.MAX_VALUE, new Texture[] { textures.get("null") }, false);
		defaultAnimation.setPlay(false);
		
		return defaultAnimation;
	}
	
	public static boolean textureExists(String id) {
		return textures.containsKey(id);
	}
	
	public static boolean animationExists(String id) {
		return animations.containsKey(id);
	}
	
//	public static class Asset {
//		private BufferedImage[] texture;
//		private float sheetWidth, sheetHeight;
//		
//		public Asset(BufferedImage texture, float sheetWidth, float sheetHeight) {
//			this.texture = new BufferedImage[] { texture };
//			this.sheetWidth = sheetWidth;
//			this.sheetHeight = sheetHeight;
//		}
//		
//		public Asset(BufferedImage[] texture, float sheetWidth, float sheetHeight) {
//			this.texture = texture;
//			this.sheetWidth = sheetWidth;
//			this.sheetHeight = sheetHeight;
//		}
//
//		public BufferedImage getTexture() {
//			return texture[0];
//		}
//		
//		public BufferedImage[] getAnimationTexture() {
//			return texture;
//		}
//
//		public float getSheetWidth() {
//			return sheetWidth;
//		}
//
//		public float getSheetHeight() {
//			return sheetHeight;
//		}
//	}
}
