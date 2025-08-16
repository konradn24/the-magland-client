package konradn24.tml.gui.panels.context;

import org.lwjgl.nanovg.NVGColor;

public interface GUIContext {

	void initContextGUI();
	void cleanupContextGUI();
	void updateContextGUI(float dt);
	void renderContextGUI(long vg);
	
	String getContextTitle();
	NVGColor getContextTitleColor();
}
