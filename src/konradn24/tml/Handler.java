package konradn24.tml;

import konradn24.tml.debug.Logging;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.gfx.GameCamera;
import konradn24.tml.gfx.Style;
import konradn24.tml.input.KeyManager;
import konradn24.tml.input.MouseManager;
import konradn24.tml.rules.GameRules;
import konradn24.tml.rules.RenderingRules;
import konradn24.tml.saving.SavesManager;
import konradn24.tml.states.GameState;
import konradn24.tml.worlds.generator.World;

public class Handler {
	
	private Game game;
	private Style style;
	
	private GameRules gameRules;
	private RenderingRules renderingRules;
	
	private SavesManager savesManager;
	
	public Handler(Game game){
		this.game = game;
		this.style = new Style(game.getDisplay());
		
		this.gameRules = new GameRules();
		this.renderingRules = new RenderingRules();
		
		this.savesManager = new SavesManager(this);
		
		Logging.info("Handler instance initialized");
	}
	
	public GameCamera getGameCamera(){
		return GameState.getGameCamera();
	}
	
	public Player getPlayer() {
		return GameState.getPlayer();
	}
	
	public World getWorld() {
		return GameState.getWorld();
	}
	
	public KeyManager getKeyManager(){
		return game.getKeyManager();
	}
	
	public MouseManager getMouseManager(){
		return game.getMouseManager();
	}
	
	public int getWidth(){
		return game.getWidth();
	}
	
	public int getHeight(){
		return game.getHeight();
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Style getStyle() {
		return style;
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
