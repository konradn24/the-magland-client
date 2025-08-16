package konradn24.tml.gui;

import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.glfw.GLFW;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.panels.DebugPanel;
import konradn24.tml.gui.panels.TopPanel;
import konradn24.tml.gui.panels.context.ContextPanel;
import konradn24.tml.gui.panels.notifications.NotificationsPanel;

public class PlayGUI {

	private TopPanel topBar;
	private ContextPanel contextPanel;
	private NotificationsPanel notificationsPanel;
	
	private DebugPanel debugPanel;
	
	public PlayGUI(Handler handler) {
		topBar = new TopPanel();
		contextPanel = new ContextPanel();
		notificationsPanel = new NotificationsPanel(handler);
		
		debugPanel = new DebugPanel(handler);
		debugPanel.setToggleKey(GLFW.GLFW_KEY_F1);
		debugPanel.loadFromFile("config/console.txt");
	}
	
	public void update(float dt) {
		if(!debugPanel.isOpen()) {
			topBar.update(dt);
			contextPanel.update(dt);
			notificationsPanel.update(dt);
		}
		
		debugPanel.update(dt);
	}
	
	public void renderGUI(long vg) {
		topBar.renderGUI(vg);
		contextPanel.renderGUI(vg);
		notificationsPanel.renderGUI(vg);
		
		debugPanel.renderGUI(vg);
	}
	
	public static void renderBackground(long vg, float x, float y, float width, float height) {
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, height);
		nvgFillColor(vg, Colors.COLOR_BACKGROUND);
		nvgFill(vg);
		
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, Colors.COLOR_OUTLINE);
		nvgStroke(vg);
	}
	
	public TopPanel getTopBar() {
		return topBar;
	}
	
	public void setTopBar(TopPanel topBar) {
		this.topBar = topBar;
	}

	public ContextPanel getContextPanel() {
		return contextPanel;
	}

	public void setContextPanel(ContextPanel contextPanel) {
		this.contextPanel = contextPanel;
	}

	public NotificationsPanel getNotificationsPanel() {
		return notificationsPanel;
	}

	public void setNotificationsPanel(NotificationsPanel notificationsPanel) {
		this.notificationsPanel = notificationsPanel;
	}

	public DebugPanel getDebugPanel() {
		return debugPanel;
	}

	public void setDebugPanel(DebugPanel debugPanel) {
		this.debugPanel = debugPanel;
	}
}
