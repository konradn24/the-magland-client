package konradn24.tml.gui.panels;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.display.Display;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;

public class TopPanel {
	
	public static final float WIDTH = Display.x(1);
	public static final float HEIGHT = Display.y(0.046f);
	public static final float RESOURCE_WIDTH = Display.x(0.1f);
	public static final float RESOURCE_HEIGHT = HEIGHT * 0.8f;
	public static final float RESOURCE_Y = HEIGHT / 2 - RESOURCE_HEIGHT / 2;
	public static final float RESOURCE_MARGIN_RIGHT = RESOURCE_WIDTH / 6;
	public static final float RESOURCE_ICON_MARGIN_RIGHT = RESOURCE_HEIGHT / 4;
	public static final float MARGIN_LEFT = RESOURCE_MARGIN_RIGHT;
	
	private LinkedHashMap<String, ResourceUI> resources = new LinkedHashMap<>();
	private byte era = 0;
	
	public TopPanel() {
		resources.put("tb_population", new ResourceUI(null, 1));
	}
	
	public void update(float dt) {
		
	}
	
	public void renderGUI(long vg) {
		nvgBeginPath(vg);
		nvgRect(vg, 0, 0, WIDTH, HEIGHT);
		nvgFillColor(vg, Colors.BACKGROUND);
		nvgFill(vg);
		
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, Colors.OUTLINE);
		nvgStroke(vg);
		
		for(Map.Entry<String, ResourceUI> entry : resources.entrySet()) {
			ResourceUI resource = entry.getValue();
			
			if(resource.icon != null) {
				nvgBeginPath(vg);
				AssetsRenderer.renderTexture(vg, resource.icon, resource.x, RESOURCE_Y, RESOURCE_HEIGHT, RESOURCE_HEIGHT);
				
				nvgBeginPath(vg);
				nvgFontFace(vg, Fonts.GLOBAL_FONT);
				nvgFontSize(vg, 24f);
				nvgFillColor(vg, Colors.TEXT);
				TextRenderer.renderString(
					vg, Double.toString(resource.value), 
					resource.x + RESOURCE_HEIGHT + RESOURCE_ICON_MARGIN_RIGHT, RESOURCE_Y, 
					RESOURCE_WIDTH - RESOURCE_ICON_MARGIN_RIGHT - RESOURCE_HEIGHT, RESOURCE_HEIGHT, 
					AlignX.LEFT, AlignY.CENTER, Overflow.HIDE
				);
			} else {
				nvgBeginPath(vg);
				nvgFontFace(vg, Fonts.GLOBAL_FONT);
				nvgFontSize(vg, 24f);
				nvgFillColor(vg, Colors.TEXT);
				
				TextRenderer.renderString(
					vg, entry.getKey() + ": " + Double.toString(resource.value), 
					resource.x + RESOURCE_HEIGHT + RESOURCE_ICON_MARGIN_RIGHT, RESOURCE_Y, 
					RESOURCE_WIDTH - RESOURCE_ICON_MARGIN_RIGHT - RESOURCE_HEIGHT, RESOURCE_HEIGHT, 
					AlignX.LEFT, AlignY.CENTER, Overflow.IGNORE
				);
			}
		}
		
		nvgBeginPath(vg);
		nvgFontFace(vg, Fonts.GLOBAL_FONT);
		nvgFontSize(vg, 24f);
		nvgFillColor(vg, Colors.TEXT);
		TextRenderer.renderString(vg, "Era: " + getEra(era), 0, 0, WIDTH - RESOURCE_MARGIN_RIGHT, HEIGHT, AlignX.RIGHT, AlignY.CENTER, Overflow.ELLIPSIS);
	}
	
	public void addResource(String id, Texture icon, int value) {
		resources.put(id, new ResourceUI(icon, value));
	}
	
	public void setResourceValue(String id, int value) {
		resources.get(id).value = value;
	}
	
	public void setEra(byte era) {
		this.era = era;
	}
	
	private class ResourceUI {
		public Texture icon;
		public double value;
		
		public static int index = 0;
		public final float x;
		
		public ResourceUI(Texture icon, double value) {
			this.icon = icon;
			this.value = value;
			
			x = MARGIN_LEFT + index * (RESOURCE_MARGIN_RIGHT + RESOURCE_WIDTH);
			index++;
		}
	}
	
	public static String getEra(byte era) {
		switch(era) {
			case 0: return "Paleolithic";
			case 1: return "Neolithic";
			case 2: return "Bronze";
			case 3: return "Iron";
			case 4: return "Antique";
			case 5: return "Middle Ages";
			case 6: return "Renaissance";
			case 7: return "Industrial";
			case 8: return "Modernism";
			case 9: return "Atomic";
			case 10: return "Information";
			default: return Byte.toString(era);
		}
	}
}
