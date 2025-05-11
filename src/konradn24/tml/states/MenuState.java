package konradn24.tml.states;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.style.Style;
import konradn24.tml.gfx.style.layouts.ColumnLayout;
import konradn24.tml.utils.Function;

public class MenuState extends State {

	private static final int BUTTONS_Y = 400;
	private static final int BUTTON_WIDTH = 636;
	private static final int BUTTON_HEIGHT = 48;
	private static final int SPACING = 20;
	
	private Label title;
	
	ColumnLayout<Button> buttons;
	
	public MenuState(Handler handler){
		super(handler);
		
		title = new Label("The Magland", Display.LOGICAL_WIDTH / 2, 200, handler);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(114f));
		
		buttons = new ColumnLayout<>(
				Button.class, 5, 
				Style.centerX(BUTTON_WIDTH), BUTTONS_Y,
				BUTTON_WIDTH, BUTTON_HEIGHT, SPACING, handler
		).customize((button, i) -> {
			String text = switch(i) {
				case 0 -> "Singleplayer";
				case 1 -> "Multiplayer";
				case 2 -> "Settings";
				case 3 -> "Credits";
				case 4 -> "Quit";
				default -> "";
			};
			
			Function function = switch(i) {
				case 0 -> () -> {
					State.setState(handler.getGame().singleplayerLoadSaveState);
				};
				
				case 1 -> () -> {
					// TODO multiplayer
				};
				
				case 2 -> () -> {
					SettingsState.lastState = this;
					State.setState(handler.getGame().settingsState);
				};
								
				case 3 -> () -> {
					State.setState(handler.getGame().creditsState);
				};
				
				case 4 -> () -> {
					handler.getGame().close();
				};
				
				default -> () -> {};
			};
			
			button.getLabel().setContent(text);
			button.getLabel().setColor(Presets.COLOR_SECONDARY);
			button.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
			button.setHoverSizeFactor(1.04f);
			button.setColor(Presets.COLOR_PRIMARY);
			button.setOnLeftClick(function);
			
			button.refreshLabelPosition();
		});
		
		Logging.info("Menu State initialized");
	}
	
	@Override
	public void onLoad() {
		
	}

	@Override
	public void tick() {
		buttons.tick();
	}

	@Override
	public void render(Graphics2D g) {
		title.render(g);
		buttons.render(g);
	}
}
