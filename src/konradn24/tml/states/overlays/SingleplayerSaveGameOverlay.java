package konradn24.tml.states.overlays;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.saving.Save;
import konradn24.tml.saving.SavesManager;
import konradn24.tml.states.State;

@Deprecated
public class SingleplayerSaveGameOverlay extends Overlay {

	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	
	private Button back;
	
	private Save[] saves;
	private Button[] loadButtons;
	
	public SingleplayerSaveGameOverlay(Class<? extends State> stateClass, Handler handler) {
		super(stateClass, handler);
		
	}
	
	@Override
	public void onLoad() {
		if(handler.getStyle().getLayout("overlay_singleplayer_save") == null) {
			handler.getStyle().addLayout(stateClass, "overlay_singleplayer_save", 12, 1);
		}
		
		back = new Button(new AdvancedLabel("Back"), 
				handler.getStyle().centerX(BUTTON_SIZE_X), 
				handler.getStyle().positionCenterY("singleplayer_load_save", 12, BUTTON_SIZE_X), 
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		back.setColor(Presets.COLOR_PRIMARY);
		back.getLabel().setColor(Presets.COLOR_SECONDARY);
		back.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		back.refresh();
		
		back.setOnLeftClick(() -> {
			Overlay.setOverlay(handler.getGame().getGameState().getPauseOverlay());
		});
		
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
//					Save save = new Save(GameState.getSaveGameID(), "test ingame save", handler);
//					handler.getSavesManager().save(slot, save);
//					
//					GameState.setSave(save);
//					Overlay.setOverlay(handler.getGame().getGameState().getPauseOverlay());
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
				
				final int innerI = i;
				
				loadButtons[i].setOnLeftClick(() -> {
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Confirm", "Are you sure to overwrite save \"" + saves[innerI].getName() + "\"", handler);
					
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
//							State.getState().getDialogsManager().showMessageBox(messageBox);
//							
//							Save save = new Save(GameState.getSaveGameID(), "test ingame save", handler);
//							handler.getSavesManager().save(slot, save);
//							
//							State.getState().getDialogsManager().closeMessageBox(messageBox);
//							
//							GameState.setSave(save);
//							Overlay.setOverlay(handler.getGame().getGameState().getPauseOverlay());
						}
					});
					
					State.getState().getDialogsManager().showMessageBox(messageBox);
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
		for(Button button : loadButtons) {
			if(button == null) {
				break;
			}
			
			button.render(g);
		}
		
		back.render(g);
	}
}
