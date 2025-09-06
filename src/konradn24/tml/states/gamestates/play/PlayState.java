package konradn24.tml.states.gamestates.play;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Map;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import konradn24.tml.Handler;
import konradn24.tml.camera.GameCamera;
import konradn24.tml.entities.dynamic.characters.Player;
import konradn24.tml.graphics.renderer.BatchRenderer;
import konradn24.tml.gui.PlayGUI;
import konradn24.tml.items.Item;
import konradn24.tml.saving.Save;
import konradn24.tml.states.Overlay;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.MenuState;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Logging;
import konradn24.tml.worlds.FullPangea;
import konradn24.tml.worlds.generator.World;

public class PlayState extends State {
	
	// Worlds
	private World world;
	
	// Player
	private Player player;
	private GameCamera camera;
	
	// Overlays
	private PauseOverlay pauseOverlay;
	
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
		camera = new GameCamera(new Vector2d(0, 0));
		camera.centerOnEntity(player);
		
		// Worlds
		Tile.init(handler);
		
		// Items
		Item.init(handler);
		
		// Buildings
//		Building.init();
		
		// Overlays
		pauseOverlay = new PauseOverlay(PlayState.class, handler);
		
		// UI
		gui = new PlayGUI(handler);
	}
	
	@Override
	public void init() {
		if(!handler.getSavesManager().isSaveLoaded()) {
			Logging.error("Loaded Game State without current save! Redirecting to Menu State...");
			State.setState(MenuState.class, Map.of("error", "Cannot open world without current save!"));
		}
		
		Save save = handler.getSavesManager().getCurrentSave();

		try {
			world = new FullPangea(save.getSeed(), 100, 400, handler);
		} catch (IOException e) {
			Logging.error("World initialization error!");
			Logging.error(e);
			
			State.setState(MenuState.class, Map.of("error", "World initialization error! See logs for more information."));
			
			return;
		}
		
		player.setWorldX(save.getPlayerWorldX());
		player.setWorldY(save.getPlayerWorldY());
		
		world.getChunkManager().reload(player.getChunkX(), player.getChunkY());
		
		if(handler.getSettings().getAutoSaveInterval() < 0) {
			return;
		}
		
		autoSavesThread = new Thread(() -> {
			try {
				if(handler.getSavesManager().save()) {
					
				} else {
					
				}
				
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
		handler.getSavesManager().clearCurrentSave();
		
		world.cleanup();
	}
	
	@Override
	public void update(float dt) {
		int debugStatsKey = handler.getSettings().getControls().getDebugStatsKey();
		
		if(handler.getKeyManager().isPressed(debugStatsKey)) {
			handler.getKeyManager().lockKey(debugStatsKey);
			
			try {
				handler.getRenderingRules().set("stats", !handler.getRenderingRules().isEnabled("stats"));
			} catch (IllegalArgumentException | IllegalAccessException ignored) {}
		}
		
		if(!gui.getDebugPanel().isOpen()) {
			if(handler.getKeyManager().isPressed(GLFW.GLFW_KEY_ESCAPE)) {
				handler.getKeyManager().lockKey(GLFW.GLFW_KEY_ESCAPE);
				Overlay.setOverlay(pauseOverlay);
			}
			
			world.updateAll(dt);
		}
		
		gui.update(dt);
		
		if(handler.getRenderingRules().stats) {
			gui.updateDebugStats(
				dt, world.getEntityManager().getEntities().size(), 
				player.getWorldX(), player.getWorldY(),
				player.standsOnTile().getBiome()
			);
		}
	}

	@Override
	public void render() {
		world.renderAll();
		BatchRenderer.renderAllQuads();
	}
	
	@Override
	public void renderGUI(long vg) {
		world.renderGUI(vg);
		gui.renderGUI(vg);
		
		if(handler.getRenderingRules().stats) {
			gui.renderDebugStatsGUI(vg);
		}
		
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
	
	public Overlay getPauseOverlay() {
		return pauseOverlay;
	}

	public PlayGUI getGUI() {
		return gui;
	}
}
