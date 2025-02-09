package konradn24.tml.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.buildings.Building;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.gfx.GameCamera;
import konradn24.tml.gfx.widgets.DebugConsole;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.tiles.Tile;
import konradn24.tml.worlds.Earth;
import konradn24.tml.worlds.generator.World;

public class GameState extends State {
	
	//Worlds
	private static World world;
	
	private World earth;
	
	//Player
	private static Player player;
	private static GameCamera gameCamera;
	
	//Randomizing item when playing first time
//	private boolean playingFirstTime = true, endAnim;
//	private BufferedImage[] itemsTextures;
//	private BufferedImage item;
//	private Animation randomizing;
//	private int index, animInterval = 1;
//	private int endAnimYOffset = 390;
	
//	private long interval = 100, timer, lastTime = System.currentTimeMillis();
	
	//Debug mode
	public static final int DEBUG_CONSOLE_KEY = KeyEvent.VK_F1;
	public static final int DEBUG_MODE_KEY = KeyEvent.VK_F2;
	
	public static boolean debugMode, debugModeChanged;
	
	public static DebugConsole debugConsole;
	
	public GameState(Handler handler){
		super(handler);
		//Randomizing
//		itemsTextures = new BufferedImage[2];
//		itemsTextures[0] = Assets.waterBottle;
//		itemsTextures[1] = Assets.holyCross;
//		randomizing = new Animation(1, itemsTextures, true);
		
		//Player
		player = new Player(handler, 0, 0); //TODO: save game
		gameCamera = new GameCamera(handler, 0, 0);
		gameCamera.centerOnEntity(player);
		
		//Worlds
		Tile.init(handler);
		
		earth = new Earth(handler); //TODO: save game
		world = earth;
		
		//Items
		Item.init(handler);
		
		//Buildings
		Building.init();
		
		// Debug
		debugConsole = new DebugConsole("debug_console", 0, 0, handler.getWidth(), (int) (handler.getHeight() / 3), handler);
		debugConsole.setKey(DEBUG_CONSOLE_KEY);
		debugConsole.loadFromFile("config/console.txt");
		
		Logging.info("Game State initialized");
	}
	
	@Override
	public void tick() {
		if(handler.getKeyManager().getKeysReleased()[World.minimapKey]) {
			if(earth.minimap) {
				earth.minimap = false;
				return;
			} else {
				earth.minimap = true;
				return;
			}
		}
		
		debugConsole.tick();
		
		if(debugConsole.isOpen()) return;
		
		if(handler.getKeyManager().getKeys()[DEBUG_MODE_KEY]) {
			if(!debugModeChanged) {
				debugMode = !debugMode;
				debugModeChanged = true;
			}
		} else debugModeChanged = false;
		
		if(handler.getKeyManager().getKeysReleased()[KeyEvent.VK_F3]) {
			handler.getSavesManager().save();
		}
		
//		if(playingFirstTime) {
//			if(index == 0) {
//				Random r = new Random();
//				index = r.nextInt(20) + 25;
//			}
//			
//			randomizing.tick();
//			
//			timer = System.currentTimeMillis();
//			if(timer - lastTime >= interval && index > 0) {
//				animInterval += 10;
//				index--;
//				lastTime = System.currentTimeMillis();
//			}
//			
//			if(index == 0) {
//				randomizing.setPlay(false);
//				playingFirstTime = false;
//				endAnim = true;
//				item = randomizing.getCurrentFrame();
//			}
//			
//			randomizing.setInterval(animInterval);
//			
//			return;
//		}
		
//		if(!playingFirstTime && !endAnim) handler.getWorld().tickAll();
		
		world.tickAll();
	}

	@Override
	public void render(Graphics g) {
		world.renderAll(g);
		
		debugConsole.render(g);
		
		if(debugMode) {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			g.setColor(Color.yellow);
			g.drawString("F2 DEBUG MODE", 10, 15);
			
			g.setColor(Color.magenta);
			g.drawString("Black boxes are collision boxes.", 10, 30);
			g.drawString("Red oval is range of current item.", 10, 45);
			
			g.setColor(Color.orange);
			g.drawString(world.getEntityManager().getEntities().size() + "entities", 10, 65);
			
			g.setColor(Color.black);
			g.drawString("Biome: " + player.standsOnTile().getBiome(), 10, 85);
		}
		
//		if(playingFirstTime) {
//			g.drawImage(randomizing.getCurrentFrame(), handler.getWidth() / 2 - 70 / 2, handler.getHeight() - 72 - endAnimYOffset, 70, 70, null);
//			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
//			g.drawString("randomizing your item...", handler.getWidth() / 2 - 70 / 2 - 60, handler.getHeight() - 76 - endAnimYOffset);
//		}
		
//		if(endAnim) {
//			g.drawImage(item, handler.getWidth() / 2 - 70 / 2, handler.getHeight() - 72 - endAnimYOffset, 70, 70, null);
//			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
//			g.drawString("nice!", handler.getWidth() / 2 - 70 / 2 + 5, handler.getHeight() - 76 - 390);
//			if(endAnimYOffset > 100) endAnimYOffset -= 6;
//			else endAnimYOffset -= 3;
//			
//			if(endAnimYOffset == 0) {
//				endAnim = false;
//				
//				for(int i = 0; i < Item.allItems.size(); i++) {
//					if(Item.allItems.get(i).getTexture() == item) {
//						switch(Item.allItems.get(i).getName()) {
//						case "Water Bottle": Item.allItems.get(i).randomUses(5, 10);
//						case "Holy Cross": Item.allItems.get(i).setPossesion(true);
//						}
//						
//						player.getInventory().setCurrent(Item.allItems.get(i));
//					}
//				}
//			}
//		}
		
//		world.renderMinimap(g);
	}

	public static DebugConsole getDebugConsole() {
		return debugConsole;
	}

	public static GameCamera getGameCamera(){
		return gameCamera;
	}
	
	public static Player getPlayer() {
		return player;
	}

	public static World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		GameState.world = world;
	}
}
