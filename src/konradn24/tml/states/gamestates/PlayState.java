package konradn24.tml.states.gamestates;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.joml.Vector2f;

import konradn24.tml.Handler;
import konradn24.tml.camera.GameCamera;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.graphics.renderer.InstanceRenderer;
import konradn24.tml.gui.PlayGUI;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.states.State;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Logging;
import konradn24.tml.worlds.Earth;
import konradn24.tml.worlds.generator.World;

public class PlayState extends State {
	
	// Worlds
	private World world;
	
	// Player
	private Player player;
	private GameCamera camera;
	
	// Overlays
//	private PauseOverlay pauseOverlay;
	
	// UI
	private PlayGUI gui;
	
	// Debug mode
	public static final int DEBUG_MODE_KEY = KeyEvent.VK_F2;
	
	public static boolean debugMode, debugModeChanged;
	
	// Auto saves thread (3 min)
	private Thread autoSavesThread;
	
	public PlayState(Handler handler){
		super(handler);
		
		noHistory = true;
		
		// Player
		player = new Player(handler, 0, 0);
		camera = new GameCamera(new Vector2f(0, 0));
		camera.centerOnEntity(player);
		
		// Worlds
		Tile.init(handler);
		
		// Items
		Item.init(handler);
		
		// Buildings
//		Building.init();
		
		// Overlays
//		pauseOverlay = new PauseOverlay(GameState.class, handler);
		
		// UI
		gui = new PlayGUI(handler);
		
		Logging.info("Game State initialized");
	}
	
	@Override
	public void init() {
//		if(!handler.getSavesManager().isSaveLoaded()) {
//			Logging.error("Loaded Game State without current save! Redirecting to Menu State...");
//			State.setState(handler.getGame().menuState);
//		}
		
//		Save save = handler.getSavesManager().getCurrentSave();

		try {
			world = new Earth(/*save.getSeed()*/420, 100, 400, handler);
			InstanceRenderer.init();
		} catch (IOException e) {
			Logging.error("World initialization error!");
			Logging.error(e);
			
//				State.setState(handler.getGame().menuState, Map.of("ERR", "World initialization error! See logs for more information."));
			return;
		}
			
//			handler.getPlayer().setWorldX(save.getPlayerWorldX());
//			handler.getPlayer().setWorldY(save.getPlayerWorldY());
		
		autoSavesThread = new Thread(() -> {
			try {
//					if(handler.getSavesManager().save()) {
//						
//					} else {
//						
//					}
				
				Thread.sleep(handler.getSettings().getAutoSaveInterval() * 60_000);
			} catch(InterruptedException e) {
				Logging.warning("Auto saves: thread interrupted");
			}
		});
		
		autoSavesThread.setDaemon(true);
		autoSavesThread.start();
	}
	
	@Override
	public void cleanup() {
		autoSavesThread.interrupt();
//		handler.getSavesManager().clearCurrentSave();
		
		world.cleanup();
		InstanceRenderer.cleanup();
	}
	
	@Override
	public void update(float dt) {
		// FOR VIEW MATRIX TEST
//		if(handler.getKeyManager().isPressed(GLFW.GLFW_KEY_W)) camera.move(0, -5);
//		if(handler.getKeyManager().isPressed(GLFW.GLFW_KEY_S)) camera.move(0, 5);
//		if(handler.getKeyManager().isPressed(GLFW.GLFW_KEY_A)) camera.move(-5, 0);
//		if(handler.getKeyManager().isPressed(GLFW.GLFW_KEY_D)) camera.move(5, 0);
		
		if(!gui.getDebugPanel().isOpen()) {
//			if(handler.getKeyManager().isKeyPressed(KeyEvent.VK_ESCAPE)) {
//				Overlay.setOverlay(pauseOverlay);
//			}
			
			world.updateAll(dt);
		}
		
		gui.update(dt);
	}

	@Override
	public void render() {
		world.renderAll();
	}
	
	@Override
	public void renderGUI(long vg) {
		gui.renderGUI(vg);
		world.renderGUI(vg);
		
//		if(debugMode) {
//			g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
//			g.setColor(Color.yellow);
//			g.drawString("F2 DEBUG MODE", 10, 15);
//			
//			g.setColor(Color.magenta);
//			g.drawString("Black boxes are collision boxes.", 10, 30);
//			g.drawString("Red oval is range of current item.", 10, 45);
//			
//			g.setColor(Color.orange);
//			g.drawString(world.getEntityManager().getEntities().size() + "entities", 10, 65);
//			
//			g.setColor(Color.black);
//			g.drawString("Biome: " + player.standsOnTile().getBiome(), 10, 85);
//		}
//		
//		g.setColor(Color.magenta);
//		g.setFont(Assets.getGlobalFont(32f));
//		StyleText.drawString(g, handler.getPlayer().standsOnTile().getBiome() + "\\n" + handler.getWorld().getEntityManager().getEntities().size(), 0, 100, handler.getDisplayWidth(), 50, AlignX.CENTER, AlignY.CENTER, Overflow.WRAP);
	}
	
	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}

	public GameCamera getCamera() {
		return camera;
	}

	public void setCamera(GameCamera camera) {
		this.camera = camera;
	}
	
//	public Overlay getPauseOverlay() {
//		return pauseOverlay;
//	}

	public PlayGUI getGUI() {
		return gui;
	}
}
