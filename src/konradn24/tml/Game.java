package konradn24.tml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import konradn24.tml.debug.CommandHandler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.images.ImageLoader;
import konradn24.tml.input.KeyManager;
import konradn24.tml.input.MouseManager;
import konradn24.tml.states.CreditsState;
import konradn24.tml.states.GameState;
import konradn24.tml.states.SingleplayerLoadSaveState;
import konradn24.tml.states.MenuState;
import konradn24.tml.states.SettingsState;
import konradn24.tml.states.State;
import konradn24.tml.states.overlays.Overlay;

public class Game implements Runnable {

	private Display display;
	public String title;
	
	private String initializingInfo = "";
	private boolean initializing = true;
	private boolean running = false;
	private Thread thread;
	
	private BufferStrategy bs;
	private Graphics2D g;
	
	private final BufferedImage defaultBackground;
	private BufferedImage background;
	
	// States
	public State gameState;
	public State menuState;
	public State settingsState;
	public State creditsState;
	public State singleplayerLoadSaveState;
	
	// Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	// Handler
	private Handler handler;
	
	// FPS
	private int fps = 60;
	private double timePerTick = 1000000000 / fps;
	private double delta = 0;
	private long now;
	private long lastTime = System.nanoTime();
	private long timer = 0;
	private int ticks = 0;
	private int TPS;
	
	private boolean locked;
	
	public Game(String title){
		this.title = title;
		this.defaultBackground = ImageLoader.loadImage("/textures/background.png");
		this.background = defaultBackground;
	}
	
	private void init(){
		display = new Display(title);
		
		new Thread(() -> {
			initializingInfo = "Creating handler";
			handler = new Handler(this);
			
			initializingInfo = "Initializing display";
			keyManager = new KeyManager();
			mouseManager = new MouseManager(handler);
			
			display.getFrame().addKeyListener(keyManager);
			display.getFrame().addMouseListener(mouseManager);
			display.getFrame().addMouseMotionListener(mouseManager);
			display.getCanvas().addMouseListener(mouseManager);
			display.getCanvas().addMouseMotionListener(mouseManager);
			
			initializingInfo = "Loading assets";
			Assets.init();
			
			initializingInfo = "Initializing game states";
			gameState = new GameState(handler);
			menuState = new MenuState(handler);
			settingsState = new SettingsState(handler);
			creditsState = new CreditsState(handler);
			singleplayerLoadSaveState = new SingleplayerLoadSaveState(handler);
			State.setState(menuState);
			
			initializingInfo = "Initializing debug commands handler";
			CommandHandler.init(handler);
			
			Logging.info("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
			GameState.getDebugConsole().print("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
			
	        initializing = false;
	    }).start();
		
		while(initializing) {
			display.showLoadingScreen(initializingInfo);
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void tick() {
		if(State.getState() != null) {
			if(!locked) {
				if(Overlay.active()) {
					Overlay.getOverlay().tick();
					
					if(keyManager.isKeyPressed(KeyEvent.VK_ESCAPE)) {
						Overlay.clear();
					}
				} else {
					State.getState().tick();
					
					if(keyManager.isKeyPressed(KeyEvent.VK_ESCAPE)) {
						State.getState().onBack();
					}
				}
			}
			
			State.getState().getDialogsManager().tick();
		}
		
		keyManager.tick();
		mouseManager.tick();
		
		display.refreshCursor();
	}
	
	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null) {
			display.getCanvas().createBufferStrategy(2);
			return;
		}
		
		g = (Graphics2D) bs.getDrawGraphics();
	    
	    if(background == null) {
	    	g.setColor(Color.black);
		    g.fillRect(0, 0, handler.getDisplayWidth(), handler.getDisplayHeight());
	    } else {
	    	g.drawImage(background, 0, 0, handler.getDisplayWidth(), handler.getDisplayHeight(), null);
	    }

	    g.translate(display.getXOffset(), display.getYOffset());
	    g.scale(display.getScale(), display.getScale());
	    
		if(State.getState() != null) {
			State.getState().render(g);
			Overlay.renderIfActive(g);
			State.getState().getDialogsManager().render(g);
		}
		
		if(SettingsState.showFPSCounter) {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
			g.setColor(Color.yellow);
			g.drawString(TPS + " FPS", 900, 30);
		}
		
		handler.getRenderingRules().render(g, handler);
		
		bs.show();
		g.dispose();
	}
	
	public void setBackground(BufferedImage background) {
		this.background = background;
	}
	
	public void clearBackground() {
		this.background = defaultBackground;
	}
	
	public void run(){
		init();
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				ticks++;
				delta = 0;
			}
			
			if(timer >= 1000000000){
				TPS = ticks;
				ticks = 0;
				timer = 0;
			}
		}
		
		stop();
	}
	
	public double getDelta() {
		return delta;
	}
	
	public GameState getGameState() {
		return (GameState) gameState;
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

	public synchronized void start(){
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop(){
		display.getFrame().dispose();
		System.exit(0);
	}
	
	public void close() {
		running = false;
	}
	
	boolean tryAutoSave() {
		if(handler == null || handler.getSavesManager() == null) {
			return false;
		}
		
		return handler.getSavesManager().save();
	}
}