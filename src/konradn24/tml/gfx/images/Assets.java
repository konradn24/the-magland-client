package konradn24.tml.gfx.images;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import konradn24.tml.debug.Logging;

public class Assets {
	
	private static final int width = 32, height = 32;
	
	private static Map<String, SpriteSheet> sheets;
	private static Map<String, BufferedImage> textures;
	private static Map<String, BufferedImage[]> animations;
	
	//sheet
	public static BufferedImage viewfinder, disabledViewfinder;
	public static BufferedImage equipBtn, unequipBtn, infoBtn, eatBtn;
	public static BufferedImage currentItemSlot, heart, arrow, handIcon;
	
	//others
	public static BufferedImage house;

	public static void init(){
		sheets = new HashMap<>();
		textures = new HashMap<>();
		animations = new HashMap<>();
		
		// Sheets
		registerSheet("general", "/textures/sheet.png");
		registerSheet("tiles", "/textures/tiles.png");
		registerSheet("items", "/textures/items.png");
		registerSheet("plants", "/textures/plants.png");
		registerSheet("buildings", "/textures/buildings.png");
		
		SpriteSheet sheet = getSheet("general");
		
		register("general/null", 7, 7);
		
		// Entities
		register("general/playerDown", new int[] {4, 5}, new int[] {0, 0});
		register("general/playerUp", new int[] {6, 7}, new int[] {0, 0});
		register("general/playerRight", new int[] {4, 5}, new int[] {1, 1});
		register("general/playerLeft", new int[] {6, 7}, new int[] {1, 1});
		
		register("general/zombieDown", new int[] {0, 0}, new int[] {0, 0});
		
		//GUI
		viewfinder = sheet.crop(width * 4, height * 2, width, height);
		disabledViewfinder = sheet.crop(width * 5, height * 2, width, height);
		
		equipBtn = sheet.crop(0, height * 2, width, height);
		unequipBtn = sheet.crop(width, height * 2, width, height);
		infoBtn = sheet.crop(width * 2, height * 2, width, height);
		eatBtn = sheet.crop(width * 3, height * 2, width, height);
		
		register("general/btnPlay", new int[] {0, 6}, new int[] {3, 3}, 2, 1);
		register("general/btnSettings", new int[] {2, 0}, new int[] {3, 4}, 2, 1);
		register("general/btnQuit", new int[] {4, 2}, new int[] {3, 4}, 2, 1);
		
		register("general/iconBack", new int[] {4, 5}, new int[] {4, 4});
		register("general/iconUnchecked", new int[] {6, 7}, new int[] {4, 4});
		register("general/iconChecked", new int[] {0, 1}, new int[] {5, 5});
		
//		play[0] = sheet.crop(0, height * 3, width * 2, height);
//		settings[0] = sheet.crop(width * 2, height * 3, width * 2, height);
//		quit[0] = sheet.crop(width * 4, height * 3, width * 2, height);
//		
//		play[1] = sheet.crop(width * 6, height * 3, width * 2, height);
//		settings[1] = sheet.crop(0, height * 4, width * 2, height);
//		quit[1] = sheet.crop(width * 2, height * 4, width * 2, height);
		
		currentItemSlot = sheet.crop(width * 6, height * 2, width, height);
		heart = sheet.crop(width * 7, height * 2, width, height);
		
		arrow = sheet.crop(width * 2, height * 5, width, height);
		handIcon = sheet.crop(width * 3, height * 5, width, height);
		
		//tiles
		register("tiles/dirt", 				1, 0);
		register("tiles/grass", 			2, 0);
		register("tiles/brick", 			3, 0);
		register("tiles/concrete1", 		4, 0);
		register("tiles/concrete2", 		5, 0);
		register("tiles/gravel", 			6, 0);
		register("tiles/water", 			7, 0);
		register("tiles/deepWater", 		0, 1);
		register("tiles/sand", 				1, 1);
		register("tiles/snow", 				2, 1);
		register("tiles/tundra", 			3, 1);
		register("tiles/ice", 				4, 1);
		register("tiles/taigaGrass", 		5, 1);
		register("tiles/rainforestGrass", 	6, 1);
		register("tiles/mud", 				7, 1);
		register("tiles/swampWater", 		0, 2);
		
		//plants
		register("plants/tree1", 0, 0, 1f, 1.5f);
		register("plants/tree2", 1, 0, 1f, 1.5f);
		register("plants/tree3", 2, 0, 1f, 1.5f);
		register("plants/tree4", 3, 0, 1f, 1.5f);
		register("plants/taigaTree1", 4, 0, 1f, 1.5f);
		register("plants/taigaTree2", 0, 3, 1f, 2f);
		register("plants/taigaTree3", 1, 3, 1f, 2f);
		register("plants/jungleTree1", 5, 0, 1f, 1.5f);
		
		//items
		register("items/stick", 0, 0);
		register("items/stone", 1, 0);
		register("items/thread", 2, 0);
		register("items/cord", 3, 0);
		register("items/rope", 4, 0);
		register("items/ancientAxe", 5, 0);
		register("items/pouch", 6, 0);
		
		register("items/screwDriver", 5, 0);
		register("items/waterBottle", 6, 0);
		register("items/holyCross", 7, 0);
		
		//buildings
		register("buildings/shelter", 0, 0, 2, 2);
		
		house = ImageLoader.loadImage("/textures/house.png");
	}
	
	public static void registerSheet(String id, String path) {
		if(sheets.containsKey(id)) {
			Logging.warning("Assets: sheet \"" + id + "\" has been already registered");
			return;
		}
		
		sheets.put(id, new SpriteSheet(ImageLoader.loadImage(path)));
		
		Logging.info("Assets: registered sheet \"" + id + "\"");
	}
	
	public static void register(String id, int x, int y) {
		if(textures.containsKey(id)) {
			Logging.warning("Assets: texture \"" + id + "\" has been already registered");
			return;
		}
		
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String textureID = splittedID[1];
		
		BufferedImage texture = sheets.get(sheetID).crop(width * x, height * y, width, height);
		
		textures.put(textureID, texture);
	}
	
	public static void register(String id, int x, int y, float width, float height) {
		if(textures.containsKey(id)) {
			Logging.warning("Assets: texture \"" + id + "\" has been already registered");
			return;
		}
		
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String textureID = splittedID[1];
		
		BufferedImage texture = sheets.get(sheetID).crop(Assets.width * x, Assets.height * y, (int) (Assets.width * width), (int) (Assets.height * height));
		
		textures.put(textureID, texture);
	}
	
	public static void register(String id, int[] x, int[] y) {
		if(x.length != y.length) {
			Logging.error("Assets: cannot load animation \"" + id + "\" - x[] and y[] are not the same length");
			return;
		}
		
		if(animations.containsKey(id)) {
			Logging.warning("Assets: animation \"" + id + "\" has been already registered");
			return;
		}
		
		int length = x.length;
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String animationID = splittedID[1];
		
		BufferedImage[] frames = new BufferedImage[length];
		
		for(int i = 0; i < length; i++)
			frames[i] = sheets.get(sheetID).crop(width * x[i], height * y[i], width, height);
		
		animations.put(animationID, frames);
	}
	
	public static void register(String id, int[] x, int[] y, int width, int height) {
		if(x.length != y.length) {
			Logging.error("Assets: cannot load animation \"" + id + "\" - x[] and y[] are not the same length");
			return;
		}
		
		if(animations.containsKey(id)) {
			Logging.warning("Assets: animation \"" + id + "\" has been already registered");
			return;
		}
		
		int length = x.length;
		String[] splittedID = id.split("/");
		String sheetID = splittedID[0];
		String animationID = splittedID[1];
		
		BufferedImage[] frames = new BufferedImage[length];
		
		for(int i = 0; i < length; i++)
			frames[i] = sheets.get(sheetID).crop(Assets.width * x[i], Assets.height * y[i], Assets.width * width, Assets.height * height);
		
		animations.put(animationID, frames);
	}
	
//	public static BufferedImage get(String id) {
//		String[] splittedID = id.split("/");
//		if(splittedID.length < 2) {
//			Logging.error("Assets: cannot get \"" + id + "\" - wrong ID provided");
//			return getTexture("null");
//		}
//		
//		String sheetID = splittedID[0];
//		String animationID = splittedID[1];
//		
//		
//	}
	
	public static SpriteSheet getSheet(String id) {
		return sheets.get(id);
	}
	
	public static BufferedImage getTexture(String id) {
		return textures.get(id);
	}
	
	public static BufferedImage[] getAnimation(String id) {
		return animations.get(id);
	}
}
