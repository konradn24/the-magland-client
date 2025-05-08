package konradn24.tml.states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.images.ImageLoader;
import konradn24.tml.saving.Save;
import konradn24.tml.saving.SavesManager;

public class SingleplayerLoadSaveState extends State {

	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	
	private BufferedImage background;
	private Label title;
	private Button back;
	
	private Save[] saves;
	private Button[] loadButtons;
	
	public SingleplayerLoadSaveState(Handler handler) {
		super(handler);
		handler.getStyle().addLayout(this.getClass(), "singleplayer_load_save", 12, 1);
		
		background = ImageLoader.loadImage("/textures/background.png");
		
		title = new Label("Load save");
		title.setCenterX(true);
		title.setPositionCenterY(true, "menu", 1);
		title.setMarginY(-20);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(68f));
		
		back = new Button(new AdvancedLabel("Back"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("singleplayer_load_save", 12, BUTTON_SIZE_X), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		back.setColor(Presets.COLOR_PRIMARY);
		back.getLabel().setColor(Presets.COLOR_SECONDARY);
		back.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		back.refresh();
		
		back.setOnLeftClick(() -> {
			State.setState(handler.getGame().menuState);
		});
		
		Logging.info("Singleplayer Load Save State initialized");
	}
	
	@Override
	public void onLoad() {
		saves = handler.getSavesManager().getSaves();
		loadButtons = new Button[SavesManager.MAX_SAVES];
		
		for(int i = 0; i < saves.length; i++) {
			final Integer slot = Integer.valueOf(i) + 1;
			
			if(saves[i] == null) {
				loadButtons[i] = new Button(new AdvancedLabel("[Slot " + slot + "] New world"),
						handler.getStyle().centerX((int) (handler.getWidth() / 1.5)),
						handler.getStyle().positionCenterY("singleplayer_load_save", i + 2, BUTTON_SIZE_Y),
						(int) (handler.getWidth() / 1.5), BUTTON_SIZE_Y, handler);
				
				loadButtons[i].setColor(Presets.COLOR_PRIMARY);
				loadButtons[i].getLabel().setColor(Presets.COLOR_SECONDARY);
				loadButtons[i].getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
				loadButtons[i].setHoverSizeFactor(1.04f);
				loadButtons[i].refresh();
				
				loadButtons[i].setOnLeftClick(() -> {
					Random random = new Random();
					
					// TODO Letting player choose name and seed
					handler.getSavesManager().newSave(slot, "Save at slot " + slot, random.nextLong());
					State.setState(handler.getGame().gameState);
				});
			} else {
				loadButtons[i] = new Button(new AdvancedLabel("[Slot " + slot + "] " + saves[i].getName()),
						handler.getStyle().centerX((int) (handler.getWidth() / 1.5)),
						handler.getStyle().positionCenterY("singleplayer_load_save", i + 2, BUTTON_SIZE_Y),
						(int) (handler.getWidth() / 1.5), BUTTON_SIZE_Y, handler);
				
				loadButtons[i].setColor(Presets.COLOR_PRIMARY);
				loadButtons[i].getLabel().setColor(Presets.COLOR_SECONDARY);
				loadButtons[i].getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
				loadButtons[i].setHoverSizeFactor(1.04f);
				loadButtons[i].refresh();
				
				loadButtons[i].setOnLeftClick(() -> {
					handler.getSavesManager().load(slot);
					State.setState(handler.getGame().gameState);
				});
			}
		}
	}

	@Override
	public void tick() {
		for(Button button : loadButtons) {
			if(button == null) {
				break;
			}
			
			button.tick();
		}
		
		back.tick();
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(background, 0, 0, handler.getWidth(), handler.getHeight(), null);
		
		title.render(g, handler);
		
		for(Button button : loadButtons) {
			if(button == null) {
				break;
			}
			
			button.render(g);
		}
		
		back.render(g);
	}
}
