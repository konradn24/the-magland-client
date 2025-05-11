package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Label.DisplayType;
import konradn24.tml.gfx.style.StyleText.AlignX;
import konradn24.tml.gfx.style.StyleText.AlignY;

public class Dropdown extends Component {
	
	public static final int DEFAULT_WIDTH = 240;

	private LinkedHashMap<Label, Runnable> options;
	
	private Font font;
	private Color color, background;

	private int optionHeight;
	
	private boolean closeOnFocusLost;
	
	private Runnable onFocusLost;
	
	public Dropdown(Handler handler) {
		super(handler);
		
		width = DEFAULT_WIDTH;
		
		init();
	}
	
	public Dropdown(int optionHeight, Handler handler) {
		super(handler);
		
		this.optionHeight = optionHeight;
		
		width = DEFAULT_WIDTH;
		
		init();
	}
	
	public Dropdown(int x, int y, int optionHeight, Handler handler) {
		super(x, y, handler);
		
		this.optionHeight = optionHeight;
		
		width = DEFAULT_WIDTH;
		
		init();
	}

	private void init() {
		font = Presets.FONT_GLOBAL;
		color = Presets.COLOR_TEXT_LIGHT;
		background = Presets.COLOR_BACKGROUND;
		
		options = new LinkedHashMap<>();
		closeOnFocusLost = true;
	}
	
	public void pack() {
		if(options.isEmpty()) {
			Logging.warning("GFX: Cannot pack dropdown - no options provided");
			return;
		}
		
		int i = 0;
		
		for(Label label : options.keySet()) {
			label.setPos(x, y + i * optionHeight, width, optionHeight);
			label.setFont(font);
			label.setColor(color);
			label.setBackground(background);
			
			i++;
		}
		
		height = options.size() * optionHeight;
	}
	
	public void tick() {
		if(invisible)
			return;
		
		if(cameraRelative) {
			int i = 0;
			
			for(Label label : options.keySet()) {
				label.setPos(x, y + i * optionHeight);
				
				i++;
			}
		}
		
		super.tick();
		
		hoverCursor(Cursor.HAND_CURSOR);
		
		for(Map.Entry<Label, Runnable> entry : options.entrySet()) {
			Label label = entry.getKey();
			Runnable action = entry.getValue();
			
			if(handler.getMouseManager().isLeftReleasedOn(x, label.getY(), width, label.getHeight()) && action != null) {
				handler.getMouseManager().lock();
				
				action.run();
				if(onFocusLost != null) onFocusLost.run();
				
				setInvisible(true);
			}
		}
		
		if(closeOnFocusLost && (handler.getMouseManager().isLeftPressed() || handler.getMouseManager().isRightPressed()) && !isOn()) {
			handler.getMouseManager().lock();
			
			if(onFocusLost != null) onFocusLost.run();
			
			setInvisible(true);
		}
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		for(Label label : options.keySet()) {
			if(background != null) {
				Color tempBackground = background;
				if(label.isOn()) {
					tempBackground = Presets.brighten(background, 0.25);
				}
				
				g.setColor(tempBackground);
				g.fillRect(label.x, label.y, width, optionHeight);
			}
			
			label.render(g);
		}
	}
	
	public void addOption(String text, Runnable action) {
		Label label = new Label(text, handler);
		label.setDisplayType(DisplayType.BOX);
		label.setAlignX(AlignX.LEFT);
		label.setAlignY(AlignY.CENTER);
		
		options.put(label, action);
	}
	
	/** Use after changing position, size, font or color **/
//	public void refreshGraphics() {
//		width = width <= 0 ? DEFAULT_WIDTH : width;
//		height = height <= 0 ? DEFAULT_HEIGHT : height;
//		
//		int x = cameraRelative ? getWorldX() : this.x;
//		int y = cameraRelative ? getWorldY() : this.y;
//		
//		handler.getStyle().getLayout(layoutID).x = x;
//		handler.getStyle().getLayout(layoutID).y = y;
//		handler.getStyle().getLayout(layoutID).rows = options.size();
//		
//		if(autoWidth) width = 0;
//		if(autoHeight) height = 0;
//		
//		int i = 0;
//		for(AdvancedLabel label : options.keySet()) {
//			label.setFont(font);
//			label.setColor(color);
//			label.setMarginX(marginX);
//			label.setMarginY(marginY);
//			label.setCameraRelative(cameraRelative, handler);
//			
//			label.calculateSize(handler, font);
//			
//			label.setX(x);
//			label.setPositionY(true, layoutID, i);
//
//			if(autoWidth) width = Math.max(width, label.getWidth());
//			if(autoHeight) height += label.getHeight();
//			
//			i++;
//		}
//		
//		handler.getStyle().getLayout(layoutID).width = width;
//		handler.getStyle().getLayout(layoutID).height = height;
//		handler.getStyle().getLayout(layoutID).refresh();
//	}
	
	public String getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	public Map<Label, Runnable> getOptions() {
		return options;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		
		for(Label label : options.keySet())
			label.setFont(font);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		
		for(Label label : options.keySet())
			label.setColor(color);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
		
		for(Label label : options.keySet())
			label.setBackground(background);
	}

	public int getOptionHeight() {
		return optionHeight;
	}

	public void setOptionHeight(int optionHeight) {
		this.optionHeight = optionHeight;
	}

	public boolean isCloseOnFocusLost() {
		return closeOnFocusLost;
	}

	public void setCloseOnFocusLost(boolean closeOnFocusLost) {
		this.closeOnFocusLost = closeOnFocusLost;
	}

	public void setOnFocusLost(Runnable onFocusLost) {
		this.onFocusLost = onFocusLost;
	}
}
