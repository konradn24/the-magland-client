package konradn24.tml.states.gamestates.play;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.Style;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.states.Overlay;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.MenuState;
import konradn24.tml.utils.Function;

public class PauseOverlay extends Overlay {

	private static final int BUTTONS_Y = 300;
	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	private static final int SPACING = 20;
	
	private Button[] buttons = new Button[4];
	
	public PauseOverlay(Class<? extends State> stateClass, Handler handler) {
		super(stateClass, handler);
		
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
						State.showMessageBox(messageBox);
					}
				};
				
				case 2 -> () -> { // Quit to menu
					MessageBox failedSaveMessageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit to menu", "Saving error. Do you want to quit without saving?", handler);
					failedSaveMessageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							State.setState(MenuState.class, null);
						}
					});
					
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit to menu", "Do you want to quit to menu? Progress will be saved automatically", handler);
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							if(handler.getSavesManager().save()) {
								State.setState(MenuState.class, null);
							} else {
								State.closeMessageBox(messageBox);
								State.showMessageBox(failedSaveMessageBox);
							}
						}
					});
					
					State.showMessageBox(messageBox);
				};
				
				case 3 -> () -> { // Quit game
					MessageBox failedSaveMessageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit game", "Saving error. Do you want to quit without saving?", handler);
					failedSaveMessageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							handler.getGame().stop();
						}
					});
					
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Quit game", "Do you want to quit game? Progress will be saved automatically", handler);
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							if(handler.getSavesManager().save()) {
								State.setState(MenuState.class, null);
								handler.getGame().stop();
							} else {
								State.closeMessageBox(messageBox);
								State.showMessageBox(failedSaveMessageBox);
							}
						}
					});
					
					State.showMessageBox(messageBox);
				};
				
				default -> () -> {};
			};
			
			buttons[i] = new Button(text, Style.centerX(BUTTON_SIZE_X), BUTTONS_Y + (BUTTON_SIZE_Y + SPACING) * i,
									BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
			buttons[i].getLabel().setFontSize(28f);
			buttons[i].setOnLeftClick(function);
		}
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void update(float dt) {
		for(Button button : buttons) {
			button.update(dt);
		}
	}

	@Override
	public void renderGUI(long vg) {
		for(Button button : buttons) {
			button.renderGUI(vg);
		}
	}

	@Override
	public void cleanup() {
		
	}
}
