package konradn24.tml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import konradn24.tml.debug.CommandHandler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.input.KeyManager;
import konradn24.tml.input.MouseManager;
import konradn24.tml.states.CreditsState;
import konradn24.tml.states.GameState;
import konradn24.tml.states.LoadSaveState;
import konradn24.tml.states.MenuState;
import konradn24.tml.states.SettingsState;
import konradn24.tml.states.State;

public class Game implements Runnable {

	private Display display;
	private int width, height;
	public String title;
	
	private boolean running = false;
	private Thread thread;
	
	private BufferStrategy bs;
	private Graphics g;
	
	// States
	public State gameState;
	public State menuState;
	public State settingsState;
	public State creditsState;
	public State loadSaveState;
	
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
	
	public Game(String title, int width, int height){
		this.width = width;
		this.height = height;
		this.title = title;
		keyManager = new KeyManager();
		mouseManager = new MouseManager();
	}
	
	private void init(){
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		Assets.init();
		
		handler = new Handler(this);
		
		gameState = new GameState(handler);
		menuState = new MenuState(handler);
		settingsState = new SettingsState(handler);
		creditsState = new CreditsState(handler);
		loadSaveState = new LoadSaveState(handler);
		State.setState(menuState);
		
		CommandHandler.init(handler);
		
		Logging.info("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
		GameState.getDebugConsole().print("Initialization completed. Elapsed time: " + (System.currentTimeMillis() - Launcher.time) + "ms");
	}

	private void tick() {
		if(State.getState() != null) {
			if(!locked) {
				State.getState().tick();
				
				if(keyManager.getKeysReleased()[KeyEvent.VK_ESCAPE]) {
					State.getState().onBack();
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
		if(bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		if(State.getState() != null) {
			State.getState().render(g);
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
	
	public void run(){
		
		init();
		
		while(running){
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
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
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
	
	public synchronized void stop(){
		if(!running)
			return;
		
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}