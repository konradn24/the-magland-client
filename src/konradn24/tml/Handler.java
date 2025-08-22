package konradn24.tml;

import java.io.IOException;

import konradn24.tml.camera.GameCamera;
import konradn24.tml.debug.rules.GameRules;
import konradn24.tml.debug.rules.RenderingRules;
import konradn24.tml.entities.dynamic.characters.Player;
import konradn24.tml.gui.PlayGUI;
import konradn24.tml.input.KeyManager;
import konradn24.tml.input.MouseManager;
import konradn24.tml.saving.SavesManager;
import konradn24.tml.settings.Settings;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.play.PlayState;
import konradn24.tml.utils.Logging;
import konradn24.tml.worlds.generator.World;

public class Handler {
	
	private Game game;
	
	private Settings settings;
	private GameRules gameRules;
	private RenderingRules renderingRules;
	
	private SavesManager savesManager;
	
	public Handler(Game game){
		this.game = game;
		
		this.settings = new Settings(this);
		
		try {
			this.settings.load(false);
		} catch (IOException e) {
			Logging.error("Handler: failed to load user settings file");
			Logging.error(e);
		}
		
		this.gameRules = new GameRules();
		this.renderingRules = new RenderingRules();
		
		this.savesManager = new SavesManager(this);
		
		Logging.info("Handler: instance initialized");
	}
	
	public PlayState getPlayState() {
		return (PlayState) State.getState(PlayState.class);
	}
	
	public Player getPlayer() {
		return getPlayState().getPlayer();
	}
	
	public World getWorld() {
		return getPlayState().getWorld();
	}
	
	public GameCamera getCamera() {
		return getPlayState().getCamera();
	}
	
	public PlayGUI getPlayGUI() {
		return getPlayState().getGUI();
	}
	
	public KeyManager getKeyManager(){
		return game.getKeyManager();
	}
	
	public MouseManager getMouseManager(){
		return game.getMouseManager();
	}
	
	public int getViewportWidth(){
		return game.getDisplay().getViewportWidth();
	}
	
	public int getViewportHeight(){
		return game.getDisplay().getViewportHeight();
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Settings getSettings() {
		return settings;
	}

	public GameRules getGameRules() {
		return gameRules;
	}

	public void setGameRules(GameRules gameRules) {
		this.gameRules = gameRules;
	}

	public RenderingRules getRenderingRules() {
		return renderingRules;
	}

	public void setRenderingRules(RenderingRules renderingRules) {
		this.renderingRules = renderingRules;
	}

	public SavesManager getSavesManager() {
		return savesManager;
	}
}
