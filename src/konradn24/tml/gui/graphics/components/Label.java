package konradn24.tml.gui.graphics.components;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Cursor;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;

public class Label extends Component {
	
	public enum DisplayType { DEFAULT, ORIGIN, BOX }
	
	protected DisplayType displayType = DisplayType.DEFAULT;
	
	protected String content;
	protected float padding;
	protected AlignX alignX = AlignX.CENTER;
	protected AlignY alignY = AlignY.CENTER;
	protected Overflow overflow = Overflow.WRAP;
	protected String font;
	protected float fontSize;
	protected NVGColor color, background;
	protected int fade = 0;
	
	protected long cursor;
	
	public Label(Handler handler) {
		super(handler);
		init("", Fonts.GLOBAL_FONT, Colors.TEXT);
	}
	
	public Label(String content, Handler handler) {
		super(handler);
		init(content, Fonts.GLOBAL_FONT, Colors.TEXT);
	}
	
	public Label(String content, float x, float y, Handler handler) {
		super(x, y, handler);
		init(content, Fonts.GLOBAL_FONT, Colors.TEXT);
	}
	
	public Label(String content, float x, float y, float width, float height, AlignX alignX, AlignY alignY, Handler handler) {
		super(x, y, width, height, handler);
		init(content, Fonts.GLOBAL_FONT, Colors.TEXT);
		
		this.alignX = alignX;
		this.alignY = alignY;
	}
	
	public Label(String content, float x, float y, float width, float height, float padding, Handler handler) {
		super(x, y, width, height, handler);
		init(content, Fonts.GLOBAL_FONT, Colors.TEXT);
		
		this.padding = padding;
	}
	
	private void init(String content, String font, NVGColor color) {
		this.content = content;
		this.font = font;
		this.color = color;
		this.fontSize = Fonts.DEFAULT_SIZE;
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void renderGUI(long vg) {
		if(invisible || content == null)
			return;
		
		if(cursor != Cursor.NORMAL) hoverCursor(cursor);
		
		if(background != null) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, width, height);
			nvgFillColor(vg, background);
			nvgFill(vg);
		}
		
		nvgBeginPath(vg);
		nvgFontFace(vg, font);
		nvgFontSize(vg, fontSize);
		nvgFillColor(vg, color);
		
		switch(displayType) {
			case DEFAULT: {
				nvgBeginPath(vg);
				nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
				nvgText(vg, x, y, content);
				
				float[] size = TextRenderer.measureText(vg, content);
				
				width = size[0];
				height = size[1];
				
				break;
			}
			
			case ORIGIN: {
				nvgBeginPath(vg);
				nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
				nvgText(vg, x, y, content);
				
				float[] size = TextRenderer.measureText(vg, content);
				
				width = size[0];
				height = size[1];
				
				break;
			}
			
			case BOX: {
				nvgBeginPath(vg);
				TextRenderer.renderString(vg, content, x, y, width, height, alignX, alignY, overflow);
				
				break;
			}
		}
	}
	
	public DisplayType getDisplayType() {
		return displayType;
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public float getPadding() {
		return padding;
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

	public AlignX getAlignX() {
		return alignX;
	}

	public void setAlignX(AlignX alignX) {
		this.alignX = alignX;
	}

	public AlignY getAlignY() {
		return alignY;
	}

	public void setAlignY(AlignY alignY) {
		this.alignY = alignY;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	
	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public NVGColor getColor() {
		return color;
	}

	public void setColor(NVGColor color) {
		this.color = color;
	}

	public NVGColor getBackground() {
		return background;
	}

	public void setBackground(NVGColor background) {
		this.background = background;
	}
	
	public int getFade() {
		return fade;
	}

	public void setFade(int fade) {
		this.fade = fade;
	}

	public Overflow getOverflow() {
		return overflow;
	}

	public void setOverflow(Overflow overflow) {
		this.overflow = overflow;
	}

	public long getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}
}
