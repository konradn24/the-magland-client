package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Label.DisplayType;
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
	
	protected Function onLeftClick, onRightClick;
	
	protected boolean sizeFromLabel;
	
	public Button(Handler handler) {
		super(handler);
		
		this.label = new Label("", x, y, width, height, LABEL_PADDING, handler);
		this.label.setDisplayType(DisplayType.BOX);
		
		init();
		
		sizeFromLabel = false;
	}
	
	public Button(BufferedImage[] texture, int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height, handler);
		
		this.texture = texture;
		this.currentTexture = texture[0];
		this.realX = x;
		this.realY = y;
		this.realWidth = width;
		this.realHeight = height;
		
		init();
		
		sizeFromLabel = false;
	}
	
	public Button(String text, int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height, handler);
		
		this.label = new Label(text, x, y, width, height, 10, handler);
		this.label.setDisplayType(DisplayType.BOX);
		this.realX = x;
		this.realY = y;
		this.realWidth = width;
		this.realHeight = height;
		
		this.label.setColor(Presets.COLOR_WINDOW_TEXT.brighter());
		
		init();
		
		sizeFromLabel = false;
	}
	
	public Button(String text, int x, int y, Handler handler) {
		super(x, y, 0, 0, handler);
		
		this.label = new Label(text, x, y, handler);
		this.label.setDisplayType(DisplayType.ORIGIN);
		this.x = x - LABEL_PADDING;
		this.y = y - LABEL_PADDING;
		
		super.width = 0;
		super.height = 0;
		this.realWidth = width;
		this.realHeight = height;
		
		this.label.setColor(Presets.COLOR_WINDOW_TEXT.brighter());
		
		init();
		
		sizeFromLabel = true;
	}
	
	private void init() {
		color = Presets.COLOR_BUTTON;
		currentColor = color;
	}
	
	public void tick() {
		if(invisible)
			return;
		
		super.tick();
		
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
		
		if(color != null && texture == null) {
			g.setColor(currentColor);
			g.fillRoundRect(x, y, realWidth, realHeight, ROUND, ROUND);
			
			g.setColor(currentColor.darker());
			g.drawRoundRect(x, y, realWidth, realHeight, ROUND, ROUND);
		}
		
		if(texture != null) {
			g.drawImage(currentTexture, x, y, realWidth, realHeight, null);
		}
			
		if(label != null) {
			label.render(g);
			
			if(sizeFromLabel) {
				width = label.width + LABEL_PADDING * 2;
				height = label.height + LABEL_PADDING * 2;
			}
		}
	}
	
	public void refreshLabelPosition() {
		label.setPos(x, y, width, height);
	}
	
	public void setPos(int x, int y) {
		super.setPos(x, y, width, height);
		
		if(label != null) {
			label.setPos(x, y, width, height);
		}
	}
	
	public void setPos(int x, int y, int width, int height) {
		super.setPos(x, y, width, height);
		
		if(label != null) {
			label.setPos(x, y, width, height);
		}
	}
	
	public void setSize(int width, int height) {
		super.setPos(x, y, width, height);
		
		if(label != null) {
			label.setPos(x, y, width, height);
		}
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

	public boolean isSizeFromLabel() {
		return sizeFromLabel;
	}

	public void setSizeFromLabel(boolean sizeFromLabel) {
		this.sizeFromLabel = sizeFromLabel;
	}
}
