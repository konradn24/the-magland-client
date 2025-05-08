package konradn24.tml.states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.images.ImageLoader;

public class MenuState extends State {

	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	
	private BufferedImage background;
	private Label title;
	private Button singleplayer, multiplayer, settings, credits, quit;
	
	public MenuState(Handler handler){
		super(handler);
		handler.getStyle().addLayout(this.getClass(), "menu", 12, 1);
		
		background = ImageLoader.loadImage("/textures/background.png");
		
		title = new Label("The Magland");
		title.setCenterX(true);
		title.setPositionCenterY(true, "menu", 2);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(68f));
		
		singleplayer = new Button(new AdvancedLabel("Singleplayer"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("menu", 4, BUTTON_SIZE_Y), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		multiplayer = new Button(new AdvancedLabel("Multiplayer"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("menu", 5, BUTTON_SIZE_Y), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		settings = new Button(new AdvancedLabel("Settings"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("menu", 6, BUTTON_SIZE_Y), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		credits = new Button(new AdvancedLabel("Credits"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("menu", 7, BUTTON_SIZE_Y), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		quit = new Button(new AdvancedLabel("Quit"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("menu", 8, BUTTON_SIZE_Y), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		singleplayer.setColor(Presets.COLOR_PRIMARY);
		multiplayer.setColor(Presets.COLOR_PRIMARY);
		settings.setColor(Presets.COLOR_PRIMARY);
		credits.setColor(Presets.COLOR_PRIMARY);
		quit.setColor(Presets.COLOR_PRIMARY);
		
		singleplayer.getLabel().setColor(Presets.COLOR_SECONDARY);
		multiplayer.getLabel().setColor(Presets.COLOR_SECONDARY);
		settings.getLabel().setColor(Presets.COLOR_SECONDARY);
		credits.getLabel().setColor(Presets.COLOR_SECONDARY);
		quit.getLabel().setColor(Presets.COLOR_SECONDARY);
		
		singleplayer.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		multiplayer.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		settings.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		credits.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		quit.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		
		singleplayer.refresh();
		multiplayer.refresh();
		settings.refresh();
		credits.refresh();
		quit.refresh();
		
		singleplayer.setOnLeftClick(() -> {
			State.setState(handler.getGame().singleplayerLoadSaveState);
		});
		
		settings.setOnLeftClick(() -> {
			SettingsState.lastState = this;
			State.setState(handler.getGame().settingsState);
		});
		
		credits.setOnLeftClick(() -> {
			State.setState(handler.getGame().creditsState);
		});
		
		quit.setOnLeftClick(() -> {
			handler.getGame().close();
		});
		
		Logging.info("Menu State initialized");
	}

	@Override
	public void tick() {
		singleplayer.tick();
		multiplayer.tick();
		settings.tick();
		credits.tick();
		quit.tick();
		
		//When mouse button released on btn
//		if(singleplayer.isLeftReleased()) {
//			State.setState(handler.getGame().gameState);
//		} else if(settings.isLeftReleased()) {
//			SettingsState.lastState = this;
//			State.setState(handler.getGame().settingsState);
//		} else if(credits.isLeftReleased()) {
//			State.setState(handler.getGame().creditsState);
//		} else if(quit.isLeftReleased())
//			handler.getGame().getDisplay().getFrame().dispose();
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(background, 0, 0, handler.getWidth(), handler.getHeight(), null);
		
		title.render(g, handler);
		
		singleplayer.render(g);
		multiplayer.render(g);
		settings.render(g);
		credits.render(g);
		quit.render(g);
	}
}
