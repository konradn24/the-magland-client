package konradn24.tml.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.buildings.Building;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.gfx.GameCamera;
import konradn24.tml.gfx.widgets.DebugConsole;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.saving.Save;
import konradn24.tml.states.overlays.Overlay;
import konradn24.tml.states.overlays.PauseOverlay;
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
	
	//Overlays
	private PauseOverlay pauseOverlay;
	
	//Debug mode
	public static final int DEBUG_CONSOLE_KEY = KeyEvent.VK_F1;
	public static final int DEBUG_MODE_KEY = KeyEvent.VK_F2;
	
	public static boolean debugMode, debugModeChanged;
	
	public static DebugConsole debugConsole;
	
	//Auto saves thread (3 min)
	private Thread autoSavesThread;
	
	public GameState(Handler handler){
		super(handler);
		
		noHistory = true;
		
		//Player
		player = new Player(handler, 0, 0);
		gameCamera = new GameCamera(handler, 0, 0);
		gameCamera.centerOnEntity(player);
		
		//Worlds
		Tile.init(handler);
		
		//Items
		Item.init(handler);
		
		//Buildings
		Building.init();
		
		//Overlays
		pauseOverlay = new PauseOverlay(GameState.class, handler);
		
		// Debug
		debugConsole = new DebugConsole(0, 0, handler.getDisplayWidth(), (int) (handler.getDisplayHeight() / 3), handler);
		debugConsole.setKey(DEBUG_CONSOLE_KEY);
		debugConsole.loadFromFile("config/console.txt");
		
		Logging.info("Game State initialized");
	}
	
	@Override
	public void onLoad() {
		if(!handler.getSavesManager().isSaveLoaded()) {
			Logging.error("Loaded Game State without current save! Redirecting to Menu State...");
			State.setState(handler.getGame().menuState);
		}
		
		Save save = handler.getSavesManager().getCurrentSave();
		
		earth = new Earth(save.getSeed(), handler);
		world = earth;
		
		handler.getPlayer().setX(save.getPlayerX());
		handler.getPlayer().setY(save.getPlayerY());
		
		autoSavesThread = new Thread(() -> {
			try {
				if(handler.getSavesManager().save()) {
					
				} else {
					
				}
				
				Thread.sleep(180_000);
			} catch(InterruptedException e) {
				Logging.warning("Auto saves: thread interrupted");
			}
		});
		
		autoSavesThread.setDaemon(true);
		autoSavesThread.start();
	}
	
	public void onClose() {
		autoSavesThread.interrupt();
		handler.getSavesManager().clearCurrentSave();
	}
	
	@Override
	public void tick() {
		if(handler.getKeyManager().getKeysReleased()[World.minimapKey]) {
			if(world.minimap) {
				world.minimap = false;
				return;
			} else {
				world.minimap = true;
				return;
			}
		}
		
		debugConsole.tick();
		if(debugConsole.isOpen()) return;
		
		if(handler.getKeyManager().isKeyPressed(KeyEvent.VK_ESCAPE)) {
			Overlay.setOverlay(pauseOverlay);
		}
		
		world.tickAll();
	}

	@Override
	public void render(Graphics2D g) {
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
	
	public Overlay getPauseOverlay() {
		return pauseOverlay;
	}
}
