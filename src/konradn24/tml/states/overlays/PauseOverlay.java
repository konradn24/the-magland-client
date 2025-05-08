package konradn24.tml.states.overlays;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.states.State;

public class PauseOverlay extends Overlay {

	private static final int BUTTON_SIZE_X = 212;
	private static final int BUTTON_SIZE_Y = 48;
	
	private Button resume, save, menu, quit;
	
	public PauseOverlay(Class<? extends State> stateClass, Handler handler) {
		super(stateClass, handler);
	}
	
	@Override
	public void onLoad() {
		if(handler.getStyle().getLayout("overlay_pause") == null) {
			handler.getStyle().addLayout(stateClass, "overlay_pause", 12, 1);
		}
		
		resume = new Button(new AdvancedLabel("Resume game"),
				handler.getStyle().centerX(BUTTON_SIZE_X),
				handler.getStyle().positionCenterY("overlay_pause", 4, BUTTON_SIZE_Y),
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		save = new Button(new AdvancedLabel("Save game"),
				handler.getStyle().centerX(BUTTON_SIZE_X),
				handler.getStyle().positionCenterY("overlay_pause", 5, BUTTON_SIZE_Y),
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		menu = new Button(new AdvancedLabel("Quit to menu"),
				handler.getStyle().centerX(BUTTON_SIZE_X),
				handler.getStyle().positionCenterY("overlay_pause", 6, BUTTON_SIZE_Y),
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		quit = new Button(new AdvancedLabel("Quit game"),
				handler.getStyle().centerX(BUTTON_SIZE_X),
				handler.getStyle().positionCenterY("overlay_pause", 7, BUTTON_SIZE_Y),
				BUTTON_SIZE_X, BUTTON_SIZE_Y, handler);
		
		resume.setColor(Presets.COLOR_PRIMARY);
		save.setColor(Presets.COLOR_PRIMARY);
		menu.setColor(Presets.COLOR_PRIMARY);
		quit.setColor(Presets.COLOR_PRIMARY);
		
		resume.getLabel().setColor(Presets.COLOR_SECONDARY);
		save.getLabel().setColor(Presets.COLOR_SECONDARY);
		menu.getLabel().setColor(Presets.COLOR_SECONDARY);
		quit.getLabel().setColor(Presets.COLOR_SECONDARY);
		
		resume.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		save.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		menu.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		quit.getLabel().setFont(Presets.FONT_GLOBAL.deriveFont(24f));
		
		resume.refresh();
		save.refresh();
		menu.refresh();
		quit.refresh();
		
		resume.setOnLeftClick(() -> {
			Overlay.clear();
		});
		
		save.setOnLeftClick(() -> {
			if(!handler.getSavesManager().save()) {
				MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Saving error", "Failed to save current progress!", handler);
				State.getState().getDialogsManager().showMessageBox(messageBox);
			}
		});
		
		menu.setOnLeftClick(() -> {
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
		});
		
		quit.setOnLeftClick(() -> {
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
		});
	}
	
	@Override
	public void tick() {
		resume.tick();
		save.tick();
		menu.tick();
		quit.tick();
	}

	@Override
	public void render(Graphics2D g) {
		resume.render(g);
		save.render(g);
		menu.render(g);
		quit.render(g);
	}
}
