package konradn24.tml.states.gamestates.settings;

import java.io.IOException;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.graphics.Assets;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.layouts.RowLayout;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.gui.graphics.widgets.tabs.TabManager;
import konradn24.tml.settings.Settings;
import konradn24.tml.states.State;
import konradn24.tml.states.gamestates.MenuState;
import konradn24.tml.states.gamestates.settings.tabs.GameplayTab;
import konradn24.tml.states.gamestates.settings.tabs.GraphicsTab;
import konradn24.tml.utils.Function;
import konradn24.tml.utils.Logging;

public class SettingsState extends State {

	private static final float TABS_Y = Display.y(.037f);
	private static final float TABS_MARGIN_BOTTOM = Display.y(.02f);
	
	public static final int TAB_COLUMNS = 2;
	public static final float ROW_HEIGHT = 100;
	
	public static final float CHECKBOX_SIZE = Display.y(.03f);
	public static final float SELECT_WIDTH = Display.x(.05625f);
	public static final float SELECT_HEIGHT = Display.y(.02222f);
	
	private static final float MENU_X = Display.x(.1f);
	private static final float MENU_Y = Display.y(.9f);
	private static final float MENU_WIDTH = Display.x(1f) - MENU_X * 2;
	private static final float MENU_HEIGHT = Display.y(.05f);
	
	private Texture background;
	private TabManager tabs;
	
	private RowLayout<Button> menu;
	
	public SettingsState(Handler handler) {
		super(handler);
		
		background = Assets.getTexture("background");
		
		tabs = new TabManager(MENU_X, TABS_Y, MENU_WIDTH, MENU_Y - TABS_Y - TABS_MARGIN_BOTTOM, 20, handler);
		
		menu = new RowLayout<Button>(
				Button.class, 3, MENU_X, MENU_Y, MENU_WIDTH, MENU_HEIGHT, 30, false, handler
		).customize((button, i) -> {
			String text = switch(i) {
				case 0 -> "Save";
				case 1 -> "Set default";
				case 2 -> "Cancel";
				default -> "";
			};
			
			Function function = switch(i) {
				case 0 -> () -> {
					Settings settings = new Settings(null);
					
					settings.setFullscreen(tabs.getTab(GraphicsTab.class).getFullscreen());
					settings.setFpsLimit(tabs.getTab(GraphicsTab.class).getFpsLimit());
					
					try {
						settings.save();
					} catch (IOException e) {
						Logging.error("Settings State: failed to save settings");
						Logging.error(e);
						
						MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Error", "Saving failed! Do you want to use these settings anyway (only for current session)?", handler);
						messageBox.setCallback((response) -> {
							if(response == MessageBox.YES) {
								handler.getSettings().set(settings, true);
								State.setState(MenuState.class, null);
							}
						});
						
						State.getCurrentState().getDialogsManager().showMessageBox(messageBox);
						
						return;
					}
					
					handler.getSettings().set(settings, true);
					State.backState(MenuState.class);
				};
				
				case 1 -> () -> {
					MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Set default", "Are you sure you want to set all settings to default?", handler);
					messageBox.setCallback((response) -> {
						if(response == MessageBox.YES) {
							setDefault();
						}
					});
					
					State.getCurrentState().getDialogsManager().showMessageBox(messageBox);
				};
				
				case 2 -> () -> {
					State.backState(MenuState.class);
				};
				
				default -> () -> {};
			};
			
			button.getLabel().setContent(text);
			button.getLabel().setFontSize(36f);
			button.setOnLeftClick(function);
			
			button.refreshLabelPosition();
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		tabs.getTabs().clear();
		tabs.addTabs(GraphicsTab.class, GameplayTab.class);
		tabs.pack();
	}
	
	@Override
	public void update(float dt) {
		tabs.update(dt);
		menu.update(dt);
	}

	@Override
	public void render() {
		
	}
	
	@Override
	public void renderGUI(long vg) {
		AssetsRenderer.renderTexture(vg, background, 0, 0, Display.x(1f), Display.y(1f));
		
		tabs.renderGUI(vg);
		menu.renderGUI(vg);
	}
	
	@Override
	public void cleanup() {
		
	}
	
	private void setDefault() {
		Settings settings = new Settings(null);
		
		try {
			settings.save();
		} catch (IOException e) {
			Logging.error("Settings State: failed to save settings");
			Logging.error(e);
			
			MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Error", "Saving failed! Do you want to use default settings anyway (only for current session)?", handler);
			messageBox.setCallback((response) -> {
				if(response == MessageBox.YES) {
					handler.getSettings().set(settings, true);
					State.backState(MenuState.class);
				}
			});
			
			State.getCurrentState().getDialogsManager().showMessageBox(messageBox);
			
			return;
		}
		
		handler.getSettings().set(settings, true);
		State.backState(MenuState.class);
	}
}
