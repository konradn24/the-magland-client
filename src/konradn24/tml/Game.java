package konradn24.tml;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.nanovg.NanoVG;
import static org.lwjgl.opengl.GL11.*;

import konradn24.tml.debug.commands.CommandHandler;
import konradn24.tml.display.Cursor;
import konradn24.tml.display.Display;
import konradn24.tml.graphics.Assets;
import konradn24.tml.input.KeyManager;
import konradn24.tml.input.MouseManager;
import konradn24.tml.states.Overlay;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.CreditsState;
import konradn24.tml.states.gamestates.MenuState;
import konradn24.tml.states.gamestates.SingleplayerLoadSaveState;
import konradn24.tml.states.gamestates.play.PlayState;
import konradn24.tml.states.gamestates.settings.SettingsState;
import konradn24.tml.timers.FrameLimiter;
import konradn24.tml.utils.Logging;

public class Game implements Runnable {

	// Window
	private Display display;
	private String windowTitle;
	
	// Inputs
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	// Initializing
	private String initializingInfo = "";
	private boolean initializing = true;
	private Throwable initializationError;
	
	private boolean running = false;
	private Thread thread;
	private boolean locked;
	
	// FPS
	private FrameLimiter frameLimiter;
	
	// Handler
	private Handler handler;
	
	public Game(String windowTitle) {
		this.windowTitle = windowTitle;
	}
	
	private void init() {
		display = new Display(windowTitle);
		Assets.init(display.getVG());
		
		new Thread(() -> {
			try {
				initializingInfo = "Creating handler";
				handler = new Handler(this);
				
				initializingInfo = "Initializing display";
				keyManager = new KeyManager();
				mouseManager = new MouseManager();
				
				keyManager.init(display);
				mouseManager.init(display);
				
				frameLimiter = new FrameLimiter(60);
				
				initializingInfo = "Initializing states";
				State.register(PlayState.class, handler);
				State.register(MenuState.class, handler);
				State.register(SettingsState.class, handler);
				State.register(CreditsState.class, handler);
				State.register(SingleplayerLoadSaveState.class, handler);
				
				initializingInfo = "Finishing";
				CommandHandler.init(handler);
				refreshSettings(List.of("ALL"));
				
				Logging.info("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
				((PlayState) State.getState(PlayState.class)).getGUI().getDebugPanel().print("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
				
		        initializing = false;
			} catch (Exception e) {
				initializationError = e;
			}
	    }).start();
		
		while(initializing && !display.shouldClose()) {
			try {
				display.showLoadingScreen(initializingInfo);
			} catch(IllegalStateException e) {}
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(initializationError != null) {
			throw new RuntimeException(initializationError);
		}
		
		State.setState(MenuState.class, null);
	}
	
	private void update(float dt) {
		display.setCursor(Cursor.ARROW);
		mouseManager.disableTooltip();
		
		if(State.getCurrentState() != null) {
			if(!locked) {
				if(Overlay.isActive()) {
					Overlay.getOverlay().update(dt);
					
					if(keyManager.isPressed(GLFW_KEY_ESCAPE)) {
						keyManager.lockKey(GLFW_KEY_ESCAPE);
						Overlay.clear();
					}
				} else {
					State.getCurrentState().update(dt);
					
					if(!State.getCurrentState().isNoHistory() && keyManager.isPressed(GLFW_KEY_ESCAPE)) {
						keyManager.lockKey(GLFW_KEY_ESCAPE);
						State.getCurrentState().onBack();
					}
				}
			}
			
			State.getCurrentState().getDialogsManager().update(dt);
		}
		
		keyManager.update();
		mouseManager.update();
	}
	
	private void render() {
		glClearColor(0, 0, 0, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		if(State.getCurrentState() != null) {
			State.getCurrentState().render();
		}
		
//		handler.getRenderingRules().render(handler);
	}
	
	private void renderGUI(long vg) {
		NanoVG.nvgBeginFrame(display.getVG(), Display.LOGICAL_WIDTH, Display.LOGICAL_HEIGHT, 1);
		
		if(State.getCurrentState() != null) {
			State.getCurrentState().renderGUI(vg);
			
			if(Overlay.isActive()) {
				Overlay.getOverlay().renderGUI(vg);
			}
		}
		
		State.getCurrentState().getDialogsManager().renderGUI(vg);
		
		mouseManager.renderGUITooltip(vg, display.getViewportWidth(), display.getViewportHeight());
		
		NanoVG.nvgEndFrame(display.getVG());
	}
	
	@Override
	public void run() {
		init();
		
		long lastTime = System.nanoTime();
		
		while(!display.shouldClose() && running) {
			long now = System.nanoTime();
			float dt = (now - lastTime) / 1_000_000_000.0f;
			lastTime = now;
			
			glfwPollEvents();
			
			update(dt);
			render();
			renderGUI(display.getVG());
			
			glfwSwapBuffers(display.getWindow());
			
			frameLimiter.sync();
		}
		
		display.destroy();
		glfwTerminate();
	}
	
	public synchronized void start(){
		if(running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void refreshSettings(List<String> changed) {
		if(changed.contains("fullscreen") || changed.contains("ALL")) {
			if(handler.getSettings().isFullscreen()) {
				display.enableFullscreen();
			} else {
				display.disableFullscreen();
			}
		}
		
		if(changed.contains("fps") || changed.contains("ALL")) {
			frameLimiter.setTargetFPS(handler.getSettings().getFpsLimit());
		}
	}
	
	public boolean tryAutoSave() {
		if(handler == null || handler.getSavesManager() == null) {
			return false;
		}
		
		return handler.getSavesManager().save();
	}
	
	public KeyManager getKeyManager(){
		return keyManager;
	}
	
	public MouseManager getMouseManager() {
		return mouseManager;
	}
	
	public Display getDisplay() {
		return display;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public FrameLimiter getFrameLimiter() {
		return frameLimiter;
	}
}
