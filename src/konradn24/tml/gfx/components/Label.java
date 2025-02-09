package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;

import konradn24.tml.Handler;

public class Label extends Component {
	
	protected String content;
	protected Font font, currentFont;
	protected Color color, background;
	
	protected int cursor;
	
	public Label(String content) {
		super();
		
		this.content = content;
	}
	
	public Label(String content, int x, int y) {
		super(x, y);
		
		this.content = content;
	}
	
	public void render(Graphics g) {
		if(cursor != Cursor.DEFAULT_CURSOR) hoverCursor(cursor);
		
		Font font = this.font != null ? this.font : g.getFont();
		if(currentFont != font) currentFont = font;
		
		calculateSize(g, font);
		
		if(invisible)
			return;
		
		int x = cameraRelative ? getWorldX() : this.x;
		int y = cameraRelative ? getWorldY() : this.y;
		
		g.setFont(font);
		g.setColor(color);
		g.drawString(content, x + marginX, y + height - height / 5 + marginY);
	}
	
	public void render(Graphics g, Handler handler) {
		if(super.handler == null) super.handler = handler;
		
		if(cursor != Cursor.DEFAULT_CURSOR) hoverCursor(cursor);
		
		Font font = this.font != null ? this.font : g.getFont();
		if(currentFont != font) currentFont = font;
		
		calculateSize(g, font);
		
		if(invisible)
			return;
		
		if(centerX) x = handler.getStyle().centerX(width);
		if(centerY) y = handler.getStyle().centerY(height);
		if(positionCenterX) x = handler.getStyle().positionCenterX(layoutID, column, width);
		if(positionCenterY) y = handler.getStyle().positionCenterY(layoutID, row, height);
		if(positionX) x = handler.getStyle().positionX(layoutID, column);
		if(positionY) y = handler.getStyle().positionY(layoutID, row);
		
		int x = cameraRelative ? getWorldX() : this.x;
		int y = cameraRelative ? getWorldY() : this.y;

		if(background != null) {
			g.setColor(background);
			g.fillRect(x, y, width, height);
		}
		
		g.setFont(font);
		g.setColor(color);
		g.drawString(content, x + marginX, y + height - height / 5 + marginY / 2);
	}
	
	public void calculateSize(Graphics g, Font font) {
		width = g.getFontMetrics(font).stringWidth(content) + marginX * 2;
		height = g.getFontMetrics(font).getHeight() + marginY * 2;
	}
	
	public void calculateSize(Handler handler, Font font) {
		width = handler.getGame().getDisplay().getCanvas().getFontMetrics(font).stringWidth(content) + marginX * 2;
		height = handler.getGame().getDisplay().getCanvas().getFontMetrics(font).getHeight() + marginY * 2;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
