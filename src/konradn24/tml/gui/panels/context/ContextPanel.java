package konradn24.tml.gui.panels.context;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.display.Display;
import konradn24.tml.gui.PlayGUI;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;
import konradn24.tml.gui.panels.TopPanel;

public class ContextPanel {

	public static final float WIDTH = Display.x(0.1823f);
	public static final float HEIGHT = Display.y(0.56f);
	public static final float TITLE_HEIGHT = Display.y(0.025f);
	public static final float X = Display.x(0.01f);
	public static final float TITLE_Y = TopPanel.HEIGHT + Display.y(0.03f);
	public static final float Y = TITLE_Y + TITLE_HEIGHT + Display.y(0.015f);
	
	private GUIContext context;
	
	public ContextPanel() {
		
	}

	public void update(float dt) {
		if(context == null) {
			return;
		}
		
		context.updateContextGUI(dt);
	}
	
	public void renderGUI(long vg) {
		if(context == null) {
			return;
		}
		
		PlayGUI.renderBackground(vg, X, TITLE_Y, WIDTH, TITLE_HEIGHT);
		
		nvgBeginPath(vg);
		nvgFillColor(vg, context.getContextTitleColor());
		TextRenderer.renderString(vg, context.getContextTitle(), X, TITLE_Y, WIDTH, TITLE_HEIGHT, AlignX.CENTER, AlignY.CENTER, Overflow.ELLIPSIS);
		
		PlayGUI.renderBackground(vg, X, Y, WIDTH, HEIGHT);
		
		context.renderContextGUI(vg);
	}
	
	public GUIContext getContext() {
		return context;
	}

	public void setContext(GUIContext context) {
		if(this.context == context) {
			return;
		}
		
		if(this.context != null) {
			this.context.cleanupContextGUI();
		}
		
		this.context = context;
		this.context.initContextGUI();
	}
	
	public void toggleContext(GUIContext context) {
		if(this.context == context) {
			this.context.cleanupContextGUI();
			this.context = null;
			
			return;
		}
		
		if(this.context != null) {
			this.context.cleanupContextGUI();
		}
		
		this.context = context;
		this.context.initContextGUI();
	}
}
