package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import konradn24.tml.Handler;
import konradn24.tml.gfx.style.StyleText;
import konradn24.tml.gfx.style.StyleText.AlignX;
import konradn24.tml.gfx.style.StyleText.AlignY;

public class Label extends Component {
	
	public enum DisplayType { DEFAULT, ORIGIN, BOX, BOX_FITTING, ICONS }
	
	protected DisplayType displayType = DisplayType.DEFAULT;
	
	protected String content;
	protected int padding;
	protected AlignX alignX = AlignX.CENTER;
	protected AlignY alignY = AlignY.CENTER;
	protected Font font, currentFont;
	protected Color color, background;
	protected int fade = 0;
	
	protected int cursor;
	
	public Label(Handler handler) {
		super(handler);
		
		this.content = "";
	}
	
	public Label(String content, Handler handler) {
		super(handler);
		
		this.content = content;
	}
	
	public Label(String content, int x, int y, Handler handler) {
		super(x, y, handler);
		
		this.content = content;
	}
	
	public Label(String content, int x, int y, int width, int height, AlignX alignX, AlignY alignY, Handler handler) {
		super(x, y, width, height, handler);
		
		this.content = content;
		this.alignX = alignX;
		this.alignY = alignY;
	}
	
	public Label(String content, int x, int y, int width, int height, int padding, Handler handler) {
		super(x, y, width, height, handler);
		
		this.content = content;
		this.padding = padding;
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		if(cursor != Cursor.DEFAULT_CURSOR) hoverCursor(cursor);
		
		Font font = this.font != null ? this.font : g.getFont();
		if(currentFont != font) currentFont = font;
		
		g.setFont(font);
		g.setColor(color);
		
		switch(displayType) {
			case DEFAULT: {
				StyleText.drawCenteredString(g, content, x, y);
				
				Point size = StyleText.getStringSize(g, content);
				width = size.x;
				height = size.y;
				
				break;
			}
			
			case ORIGIN: {
				StyleText.drawString(g, content, x, y);
				
				Point size = StyleText.getStringSize(g, content);
				width = size.x;
				height = size.y;
				
				break;
			}
			
			case BOX: {
				StyleText.drawString(g, content, x, y, width, height, alignX, alignY);
				
				break;
			}
			
			case BOX_FITTING: {
				StyleText.drawStringWithAutoSizing(g, content, x, y, width, height, padding);
				
				break;
			}
			
			case ICONS: {
				StyleText.drawStringWithIcons(g, content, x, y);
				
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

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
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

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}
	
	public int getFade() {
		return fade;
	}

	public void setFade(int fade) {
		this.fade = fade;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public Font getCurrentFont() {
		return currentFont;
	}
}
