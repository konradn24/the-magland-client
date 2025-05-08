package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.utils.Function;

public class Button extends Component {
	
	private static final int LABEL_PADDING = 10;
	private static final float HOVER_SIZE_FACTOR = 1.08f;
	private static final int ROUND = 20;
	
	protected BufferedImage[] texture;
	protected BufferedImage currentTexture;
	protected Label label;
	protected Color color, currentColor;
	
	protected int realX, realY, realWidth, realHeight;
	protected int labelPadding = LABEL_PADDING;
	protected float hoverSizeFactor = HOVER_SIZE_FACTOR;
	
	protected boolean autoSize;
	
	protected Function onLeftClick, onRightClick;
	
	public Button(BufferedImage[] texture, int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height);
		
		this.texture = texture;
		this.currentTexture = texture[0];
		this.realX = x;
		this.realY = y;
		this.realWidth = width;
		this.realHeight = height;
		this.handler = handler;
		
		autoSize = false;
	}
	
	public Button(AdvancedLabel label, int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height);
		
		this.label = label;
		this.realX = x;
		this.realY = y;
		this.realWidth = width;
		this.realHeight = height;
		this.handler = handler;
		
		Font font = label.font != null ? label.font : handler.getGame().getDisplay().getCanvas().getFont();
		FontMetrics metrics = handler.getGame().getDisplay().getCanvas().getFontMetrics(font);
		int labelWidth = metrics.stringWidth(label.getContent());
		int labelHeight = metrics.getHeight();
		
		this.label.setX((int) (x + width / 2 - labelWidth / 1.5) + 2);
		this.label.setY((int) (y + height / 2 - labelHeight / 1.5));
		this.label.setColor(Presets.COLOR_WINDOW_TEXT.brighter());
		
		color = Presets.COLOR_BUTTON;
		currentColor = color;
		
		autoSize = false;
	}
	
	public Button(AdvancedLabel label, int x, int y, Handler handler) {
		super(x, y, 0, 0);
		
		this.label = label;
		this.realX = x;
		this.realY = y;
		this.handler = handler;
		
		Font font = label.font != null ? label.font : handler.getGame().getDisplay().getCanvas().getFont();
		FontMetrics metrics = handler.getGame().getDisplay().getCanvas().getFontMetrics(font);
		int labelWidth = metrics.stringWidth(label.getContent());
		int labelHeight = metrics.getHeight();
		
		super.width = labelWidth + labelPadding * 2;
		super.height = labelHeight + labelPadding * 2;
		this.realWidth = width;
		this.realHeight = height;
		
		this.label.setX((int) (x + width / 2 - labelWidth / 2) + 2);
		this.label.setY((int) (y + height / 2 - labelHeight / 2));
		this.label.setColor(Presets.COLOR_WINDOW_TEXT.brighter());
		
		color = Presets.COLOR_BUTTON;
		currentColor = color;
		
		autoSize = true;
	}
	
	public void tick() {
		if(invisible)
			return;
		
		hoverCursor(Cursor.HAND_CURSOR);
		
		if(isOn()) {
			if(isLeftPressed() && onLeftClick != null) {
				handler.getMouseManager().lock();
				onLeftClick.run();
			} else if(isRightPressed() && onRightClick != null) {
				handler.getMouseManager().lock();
				onRightClick.run();
			} else {
				realWidth = (int) (width * hoverSizeFactor);
				realHeight = (int) (height * hoverSizeFactor);
				
				realX = x - (realWidth - width) / 2;
				realY = y - (realHeight - height) / 2;
				
				if(color != null)
					currentColor = isLeftPressed() ? color : color.brighter();
				
				if(texture != null)
					currentTexture = isLeftPressed() ? texture[0] : texture[1];
			}
		} else {
			realWidth = width;
			realHeight = height;
			
			realX = x;
			realY = y;
			
			if(color != null)
				currentColor = color;
			
			if(texture != null)
				currentTexture = texture[0];
		}
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		int x = cameraRelative ? getWorldX() : this.realX;
		int y = cameraRelative ? getWorldY() : this.realY;
		
		if(color != null) {
			g.setColor(currentColor);
			g.fillRoundRect(x, y, realWidth, realHeight, ROUND, ROUND);
			
			g.setColor(currentColor.darker());
			g.drawRoundRect(x, y, realWidth, realHeight, ROUND, ROUND);
		}
		
		if(texture != null) {
			g.drawImage(currentTexture, x, y, realWidth, realHeight, null);
		}
			
		if(label != null) {
			label.render(g, handler);
		}
	}
	
	public void refresh() {
		Font font = label.font != null ? label.font : handler.getGame().getDisplay().getCanvas().getFont();
		FontMetrics metrics = handler.getGame().getDisplay().getCanvas().getFontMetrics(font);
		int labelWidth = metrics.stringWidth(label.getContent());
		int labelHeight = metrics.getHeight();
		
		if(autoSize) {
			super.width = labelWidth + labelPadding * 2;
			super.height = labelHeight + labelPadding * 2;
			this.realWidth = width;
			this.realHeight = height;
		}
		
		this.label.setX((int) (x + width / 2 - labelWidth / 2) + 2);
		this.label.setY((int) (y + height / 2 - labelHeight / 2));
	}
	
	//GETTERS AND SETTERS
	
	public BufferedImage getTexture() {
		return texture[0];
	}
	
	public BufferedImage getTextureFocused() {
		return texture[1];
	}

	public void setTexture(BufferedImage texture) {
		this.texture[0] = texture;
	}
	
	public void setTextureFocused(BufferedImage texture) {
		this.texture[1] = texture;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getLabelPadding() {
		return labelPadding;
	}

	public void setLabelPadding(int labelPadding) {
		this.labelPadding = labelPadding;
	}

	public float getHoverSizeFactor() {
		return hoverSizeFactor;
	}

	public void setHoverSizeFactor(float hoverSizeFactor) {
		this.hoverSizeFactor = hoverSizeFactor;
	}
	
	public void setOnLeftClick(Function function) {
		this.onLeftClick = function;
	}
	
	public void setOnRightClick(Function function) {
		this.onRightClick = function;
	}
}
