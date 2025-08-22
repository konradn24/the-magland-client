package konradn24.tml.gui;

import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Label;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.gui.graphics.layouts.GridLayout;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;
import konradn24.tml.gui.panels.DebugPanel;
import konradn24.tml.gui.panels.TopPanel;
import konradn24.tml.gui.panels.context.ContextPanel;
import konradn24.tml.gui.panels.notifications.NotificationsPanel;
import konradn24.tml.worlds.generator.World;

public class PlayGUI {

	public static final float DEBUG_STATS_X = Display.x(.15f);
	public static final float DEBUG_STATS_Y = TopPanel.HEIGHT + Display.y(.01f);
	public static final float DEBUG_STATS_WIDTH = Display.x(.7f);
	public static final float DEBUG_STATS_HEIGHT = Display.y(.2f);
	public static final int DEBUG_STATS_ROWS = 4;
	public static final int DEBUG_STATS_COLUMNS = 3;
	
	private TopPanel topBar;
	private ContextPanel contextPanel;
	private NotificationsPanel notificationsPanel;
	
	private DebugPanel debugPanel;
	private GridLayout<Label> debugStats;
	
	public PlayGUI(Handler handler) {
		topBar = new TopPanel();
		contextPanel = new ContextPanel();
		notificationsPanel = new NotificationsPanel(handler);
		
		debugPanel = new DebugPanel(handler);
		debugPanel.setToggleKey(handler.getSettings().getControls().getDebugPanelKey());
		debugPanel.loadFromFile("config/console.txt");
		
		debugStats = new GridLayout<Label>(
			Label.class, DEBUG_STATS_ROWS * DEBUG_STATS_COLUMNS, DEBUG_STATS_COLUMNS, 
			DEBUG_STATS_X, DEBUG_STATS_Y, 
			DEBUG_STATS_WIDTH / DEBUG_STATS_COLUMNS,
			DEBUG_STATS_HEIGHT / DEBUG_STATS_ROWS, 0, handler
		).customize((label, i) -> {
			label.setFontSize(20f);
			label.setColor(Colors.TEXT);
			label.setAlignX(AlignX.LEFT);
			label.setAlignY(AlignY.TOP);
			label.setDisplayType(DisplayType.BOX);
			label.setOverflow(Overflow.IGNORE);
		});
	}
	
	public void update(float dt) {
		if(!debugPanel.isOpen()) {
			topBar.update(dt);
			contextPanel.update(dt);
			notificationsPanel.update(dt);
		}
		
		debugPanel.update(dt);
	}
	
	public void updateDebugStats(float dt, int entitiesCount, float playerWorldX, float playerWorldY, String biome) {
		debugStats.forEach((label, i) -> {
			String text = switch(i) {
				case 0 -> "Entities: " + entitiesCount;
				case 1 -> String.format("X: %.1f, Y: %.1f", playerWorldX, playerWorldY);
				case 2 -> "Biome: " + biome;
				default -> "";
			};
			
			NVGColor color = switch(i) {
				case 0 -> entitiesCount <= 0 ? Colors.RED : Colors.PURPLE;
				case 2 -> biome == World.BIOME_NULL ? Colors.RED : Colors.PURPLE;
				default -> Colors.PURPLE;
			};
			
			if(text.isEmpty()) {
				label.setInvisible(true);
				return;
			}
			
			label.setContent(text);
			label.setColor(color);
		});
		
		debugStats.update(dt);
	}
	
	public void renderGUI(long vg) {
		topBar.renderGUI(vg);
		contextPanel.renderGUI(vg);
		notificationsPanel.renderGUI(vg);
		
		debugPanel.renderGUI(vg);
	}
	
	public void renderDebugStatsGUI(long vg) {
		if(debugPanel.isOpen()) {
			return;
		}
		
//		renderBackground(vg, DEBUG_STATS_X, DEBUG_STATS_Y, DEBUG_STATS_WIDTH, DEBUG_STATS_HEIGHT);
		debugStats.renderGUI(vg);
	}
	
	public static void renderBackground(long vg, float x, float y, float width, float height) {
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, height);
		nvgFillColor(vg, Colors.BACKGROUND);
		nvgFill(vg);
		
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, Colors.OUTLINE);
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
