package konradn24.tml.states;

import java.awt.Graphics2D;
import java.util.Random;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.style.Style;
import konradn24.tml.gfx.style.layouts.ColumnLayout;
import konradn24.tml.saving.Save;

public class SingleplayerLoadSaveState extends State {

	private static final int BUTTONS_Y = 380;
	private static final int BUTTON_WIDTH = 636;
	private static final int BUTTON_HEIGHT = 48;
	private static final int SPACING = 20;
	
	private Label title;
	private Button back;
	
	private Save[] saves;
	private ColumnLayout<Button> buttons;
	
	public SingleplayerLoadSaveState(Handler handler) {
		super(handler);
		
		title = new Label("The Magland", Display.LOGICAL_WIDTH / 2, 200, handler);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(114f));
		
		Logging.info("Singleplayer Load Save State initialized");
	}
	
	@Override
	public void onLoad() {
		saves = handler.getSavesManager().getSaves();
		
		buttons = new ColumnLayout<>(
				Button.class, 8, 
				Style.centerX(BUTTON_WIDTH), BUTTONS_Y,
				BUTTON_WIDTH, BUTTON_HEIGHT, SPACING, handler
		).customize((button, i) -> {
			final Integer slot = Integer.valueOf(i) + 1;
			
			if(saves[i] == null) {
				button.getLabel().setContent("[Slot " + slot + "] New world");
				
				button.setOnLeftClick(() -> {
					Random random = new Random();
					
					// TODO Letting player choose name and seed
					handler.getSavesManager().newSave(slot, "Save at slot " + slot, random.nextLong());
					State.setState(handler.getGame().gameState);
				});
			} else {
				button.getLabel().setContent("[Slot " + slot + "] " + saves[i].getName());
				
				button.setOnLeftClick(() -> {
					handler.getSavesManager().load(slot);
					State.setState(handler.getGame().gameState);
				});
			}
			
			button.setColor(Presets.COLOR_PRIMARY);
			button.getLabel().setColor(Presets.COLOR_SECONDARY);
			button.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
			button.setHoverSizeFactor(1.04f);
			
			button.refreshLabelPosition();
		});
		
		back = new Button("Back", Style.centerX((int) (BUTTON_WIDTH / 1.5)), 960, (int) (BUTTON_WIDTH / 1.5), BUTTON_HEIGHT, handler);
		back.setColor(Presets.COLOR_PRIMARY);
		back.getLabel().setColor(Presets.COLOR_SECONDARY);
		back.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		
		back.setOnLeftClick(() -> {
			State.setState(handler.getGame().menuState);
		});
	}

	@Override
	public void tick() {
		buttons.tick();
		back.tick();
	}

	@Override
	public void render(Graphics2D g) {
		title.render(g);
		
		buttons.render(g);
		back.render(g);
	}
}
