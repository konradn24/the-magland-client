package konradn24.tml.states.gamestates;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;

import java.util.Random;

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
import konradn24.tml.saving.Save;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.play.PlayState;

public class SingleplayerLoadSaveState extends State {

	private static final int BUTTONS_Y = 380;
	private static final int BUTTON_WIDTH = 636;
	private static final int BUTTON_HEIGHT = 48;
	private static final int SPACING = 20;
	
	private Texture background;
	private Label title;
	private Button back;
	
	private Save[] saves;
	private ColumnLayout<Button> buttons;
	
	public SingleplayerLoadSaveState(Handler handler) {
		super(handler);
		
		background = Assets.getTexture("background");
		
		title = new Label("The Magland", Display.LOGICAL_WIDTH / 2, 200, handler);
		title.setColor(Colors.SECONDARY);
		title.setFontSize(196f);
		
		back = new Button("Back", Style.centerX((int) (BUTTON_WIDTH / 1.5)), 960, (int) (BUTTON_WIDTH / 1.5), BUTTON_HEIGHT, handler);
		back.getLabel().setFontSize(36f);
		
		back.setOnLeftClick(() -> {
			State.setState(MenuState.class, null);
		});
	}
	
	@Override
	public void init() {
		saves = handler.getSavesManager().getSaves();
		
		buttons = new ColumnLayout<>(
				Button.class, 8, 
				Style.centerX(BUTTON_WIDTH), BUTTONS_Y,
				BUTTON_WIDTH, BUTTON_HEIGHT, SPACING, true, handler
		).customize((button, i) -> {
			final Integer slot = Integer.valueOf(i) + 1;
			
			if(saves[i] == null) {
				button.getLabel().setContent("[Slot " + slot + "] New world");
				
				button.setOnLeftClick(() -> {
					Random random = new Random();
					
					// TODO Letting player choose name and seed
					handler.getSavesManager().newSave(slot, "Save at slot " + slot, random.nextLong());
					State.setState(PlayState.class, null);
				});
			} else {
				button.getLabel().setContent("[Slot " + slot + "] " + saves[i].getName());
				
				button.setOnLeftClick(() -> {
					handler.getSavesManager().load(slot);
					State.setState(PlayState.class, null);
				});
			}
			
			button.getLabel().setFontSize(36f);
			
			button.refreshLabelPosition();
		});
	}

	@Override
	public void update(float dt) {
		buttons.update(dt);
		back.update(dt);
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
		back.renderGUI(vg);
	}

	@Override
	public void cleanup() {
		
	}
}
