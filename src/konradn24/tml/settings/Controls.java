package konradn24.tml.settings;

import org.lwjgl.glfw.GLFW;

public class Controls {

	public static final transient int DEFAULT_DEBUG_PANEL_KEY = GLFW.GLFW_KEY_F1;
	public static final transient int DEFAULT_DEBUG_STATS_KEY = GLFW.GLFW_KEY_F3;
	
	private int debugPanelKey;
	private int debugStatsKey;
	
	public Controls() {
		debugPanelKey = DEFAULT_DEBUG_PANEL_KEY;
		debugStatsKey = DEFAULT_DEBUG_STATS_KEY;
	}

	public int getDebugPanelKey() {
		return debugPanelKey;
	}

	public void setDebugPanelKey(int debugPanelKey) {
		this.debugPanelKey = debugPanelKey;
	}

	public int getDebugStatsKey() {
		return debugStatsKey;
	}

	public void setDebugStatsKey(int debugStatsKey) {
		this.debugStatsKey = debugStatsKey;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Controls)) {
			return false;
		}
		
		Controls c = (Controls) o;
		
		return c.debugPanelKey == debugPanelKey && c.debugStatsKey == debugStatsKey;
	}
}
