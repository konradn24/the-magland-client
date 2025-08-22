package konradn24.tml.states.gamestates;

import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.graphics.Assets;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.components.Label;
import konradn24.tml.gui.graphics.layouts.ColumnLayout;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.settings.SettingsState;
import konradn24.tml.utils.Function;

public class MenuState extends State {

	private static final int BUTTONS_Y = 400;
	private static final int BUTTON_WIDTH = 636;
	private static final int BUTTON_HEIGHT = 48;
	private static final int SPACING = 20;
	
	private Texture background;
	private Label title;
	private ColumnLayout<Button> buttons;
	
	public MenuState(Handler handler){
		super(handler);
		
		background = Assets.getTexture("background");
		
		title = new Label("The Magland", Display.LOGICAL_WIDTH / 2, 200, handler);
		title.setColor(Colors.SECONDARY);
		title.setFontSize(196f);
		
		buttons = new ColumnLayout<>(
				Button.class, 5, 
				Style.centerX(BUTTON_WIDTH), BUTTONS_Y,
				BUTTON_WIDTH, BUTTON_HEIGHT, SPACING, true, handler
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
					State.setState(SingleplayerLoadSaveState.class, Map.of());
				};
				
				case 1 -> () -> {
					// TODO multiplayer
				};
				
				case 2 -> () -> {
					State.setState(SettingsState.class, Map.of());
				};
								
				case 3 -> () -> {
					State.setState(CreditsState.class, Map.of());
				};
				
				case 4 -> () -> {
					handler.getGame().stop();
				};
				
				default -> () -> {};
			};
			
			button.getLabel().setContent(text);
			button.getLabel().setFontSize(36f);
			button.setOnLeftClick(function);
			
			button.refreshLabelPosition();
		});
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update(float dt) {
		buttons.update(dt);
	}

	@Override
	public void render() {
		
	}
	
	@Override
	public void renderGUI(long vg) {
		nvgBeginPath(vg);
		AssetsRenderer.renderTexture(vg, background, 0, 0, Display.x(1f), Display.y(1f));
		
		title.renderGUI(vg);
		buttons.renderGUI(vg);
	}
	
	@Override
	public void cleanup() {
		
	}
}
