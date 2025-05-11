package konradn24.tml.states.overlays;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.style.Style;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.states.State;
import konradn24.tml.utils.Function;

public class PauseOverlay extends Overlay {

	private static final int BUTTONS_Y = 300;
	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	private static final int SPACING = 20;
	
	private Button[] buttons = new Button[4];
	
	public PauseOverlay(Class<? extends State> stateClass, Handler handler) {
		super(stateClass, handler);
	}
	
	@Override
	public void onLoad() {
		for(int i = 0; i < buttons.length; i++) {
			String text = switch(i) {
				case 0 -> "Resume game";
				case 1 -> "Save game";
				case 2 -> "Quit to menu";
				case 3 -> "Quit game";
				default -> "";
			};
			
			Function function = switch(i) {
				case 0 -> () -> { // Resume game
					Overlay.clear();
				};
				
				case 1 -> () -> { // Save game
					if(!handler.getSavesManager().save()) {
						MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Saving error", "Failed to save current progress!", handler);
						State.getState().getDialogsManager().showMessageBox(messageBox);
					}
				};
				
				case 2 -> () -> {
					MessageBox failedSaveMessageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit to menu", "Saving error. Do you want to quit without saving?", handler);
					failedSaveMessageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							State.setState(handler.getGame().menuState);
						}
					});
					
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit to menu", "Do you want to quit to menu? Progress will be saved automatically", handler);
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							if(handler.getSavesManager().save()) {
								State.setState(handler.getGame().menuState);
							} else {
								State.getState().getDialogsManager().closeMessageBox(messageBox);
								State.getState().getDialogsManager().showMessageBox(failedSaveMessageBox);
							}
						}
					});
					
					State.getState().getDialogsManager().showMessageBox(messageBox);
				};
				
				case 3 -> () -> {
					MessageBox failedSaveMessageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit game", "Saving error. Do you want to quit without saving?", handler);
					failedSaveMessageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							handler.getGame().close();
						}
					});
					
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit game", "Do you want to quit game? Progress will be saved automatically", handler);
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							if(handler.getSavesManager().save()) {
								State.setState(handler.getGame().menuState);
								handler.getGame().close();
							} else {
								State.getState().getDialogsManager().closeMessageBox(messageBox);
								State.getState().getDialogsManager().showMessageBox(failedSaveMessageBox);
							}
						}
					});
					
					State.getState().getDialogsManager().showMessageBox(messageBox);
				};
				
				default -> () -> {};
			};
			
			buttons[i] = new Button(text, Style.centerX(BUTTON_SIZE_X), BUTTONS_Y + (BUTTON_SIZE_Y + SPACING) * i,
									BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
			buttons[i].setColor(Presets.COLOR_PRIMARY);
			buttons[i].getLabel().setColor(Presets.COLOR_SECONDARY);
			buttons[i].getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
			buttons[i].setOnLeftClick(function);
		}
	}
	
	@Override
	public void tick() {
		for(Button button : buttons) {
			button.tick();
		}
	}

	@Override
	public void render(Graphics2D g) {
		for(Button button : buttons) {
			button.render(g);
		}
	}
}
