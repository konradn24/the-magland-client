package konradn24.tml.gfx.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;

import konradn24.tml.Handler;
import konradn24.tml.utils.Utils;

public class TextField extends Label {
	
	public static final int CURSOR_BLINK_TIME = 1000;
	
	private String placeholder, fullContent;
	private boolean focus, disabled;
	
	private int fixedWidth;
	private int fullWidth, paddingX, paddingY;
	
	private byte cursorOffset, cursorWidth;
	private long lastCursorBlink;
	
	private boolean awaitUpdateContent;
	
	public TextField(Handler handler) {
		super("");

		this.handler = handler;
		this.cursorOffset = 5;
		this.cursorWidth = 1;
		this.content = "";
		this.fullContent = "";
	}
	
	public TextField(int x, int y, Handler handler) {
		super("", x, y);
		
		this.handler = handler;
		this.cursorOffset = 5;
		this.cursorWidth = 1;
		this.content = "";
		this.fullContent = "";
	}
	
	public boolean isOn() {
		if(handler.getMouseManager().getMouseX() >= x && handler.getMouseManager().getMouseX() <= x + fullWidth &&
		   handler.getMouseManager().getMouseY() >= y && handler.getMouseManager().getMouseY() <= y + height) {
			return true;
		} else {
			return false;
		}
	}
	
	public void tick() {
		if(disabled || invisible) return;
		
		hoverCursor(Cursor.TEXT_CURSOR);
		
		if(handler.getMouseManager().isLeftReleased()) {
			focus = isOn();
		}
		
		if(focus) onFocus();
		if(awaitUpdateContent) updateContent();
	}
	
	private void onFocus() {
		char typed = handler.getKeyManager().getTyped();
		
		if(typed != 0) {
			if(typed == KeyEvent.VK_BACK_SPACE) fullContent = fullContent.length() > 1 ? fullContent.substring(0, fullContent.length() - 1) : "";
			else if(Utils.isPrintableChar(typed)) fullContent += typed;
			else return;
			
			updateContent();
		}
	}
	
	private void updateContent() {
		content = fullContent;
		
		if(currentFont == null) return;
		
		int contentWidth = handler.getGame().getDisplay().getFrame().getFontMetrics(currentFont).stringWidth(content) + paddingX;
		while(contentWidth > fixedWidth) {
			content = content.substring(1);
			contentWidth = handler.getGame().getDisplay().getFrame().getFontMetrics(currentFont).stringWidth(content) + paddingX;
		}
		
		awaitUpdateContent = false;
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		renderBorder(g);
		super.render(g, handler);
		renderBlinkingCursor(g);
	}
	
	protected void renderBorder(Graphics2D g) {
		fullWidth = (fixedWidth > 0 ? fixedWidth : width) + paddingX - cursorOffset - cursorWidth;
		
		if(disabled) {
			g.setColor(Color.gray);
			g.drawRoundRect(x, y, fullWidth, height, 5, 5);
			
			return;
		}

		Stroke defaultStroke = g.getStroke();
		
		g.setStroke(new BasicStroke(isOn() ? 2 : 1));
		g.setColor(focus ? Color.gray : Color.black);
		g.drawRoundRect(x, y, fullWidth, height, 5, 5);
		g.setStroke(defaultStroke);
	}
	
	protected void renderBlinkingCursor(Graphics2D g) {
		if(!focus) return;
		
		if(System.currentTimeMillis() - lastCursorBlink <= CURSOR_BLINK_TIME / 2) {
			g.setColor(color);
			g.fillRect(x + g.getFontMetrics(currentFont).stringWidth(content) + paddingX / 2 + cursorOffset, y + 2, cursorWidth, height - 3);
		} else if(System.currentTimeMillis() - lastCursorBlink > CURSOR_BLINK_TIME) {
			lastCursorBlink = System.currentTimeMillis();
		}
	}
	
	protected void calculateSize(Graphics2D g, Font font, String content) {
		width = fullWidth;
		height = g.getFontMetrics(font).getHeight() + paddingY;
	}

	public String getContent() {
		return fullContent;
	}
	
	public void setContent(String content) {
		this.fullContent = content;
		this.awaitUpdateContent = true;
	}
	
	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(int fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

	public int getFullWidth() {
		return fullWidth;
	}

	public int getPaddingX() {
		return paddingX;
	}

	public void setPaddingX(int padding) {
		this.paddingX = padding;
		this.marginX = padding;
	}

	public int getPaddingY() {
		return paddingY;
	}

	public void setPaddingY(int padding) {
		this.paddingY = padding;
		this.marginY = padding;
	}

	public byte getCursorOffset() {
		return cursorOffset;
	}

	public void setCursorOffset(byte cursorOffset) {
		this.cursorOffset = cursorOffset;
	}

	public byte getCursorWidth() {
		return cursorWidth;
	}

	public void setCursorWidth(byte cursorWidth) {
		this.cursorWidth = cursorWidth;
	}
}
