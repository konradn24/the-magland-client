package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.states.MenuState;
import konradn24.tml.states.State;

public class Dropdown extends Component {
	
	public static final int DEFAULT_WIDTH = 120;
	public static final int DEFAULT_HEIGHT = 180;

	private String layoutID;
	private LinkedHashMap<AdvancedLabel, Runnable> options;
	
	private Font font;
	private Color color, background;
	
	private boolean autoWidth, autoHeight;
	private boolean closeOnFocusLost;
	
	private Runnable onFocusLost;
	
	public Dropdown(String layoutID, int x, int y, Handler handler) {
		this.layoutID = layoutID;
		this.x = x;
		this.y = y;
		this.handler = handler;
		
		font = Presets.FONT_GLOBAL;
		color = Presets.COLOR_TEXT_LIGHT;
		background = Presets.COLOR_BACKGROUND;
		
		autoWidth = true;
		autoHeight = true;
		
		options = new LinkedHashMap<>();
		closeOnFocusLost = true;
		
		State state = State.getState();
		handler.getStyle().addLayout(state != null ? state.getClass() : MenuState.class, layoutID, x, y, width, height, 1, 1);
	}
	
	public void tick() {
		if(invisible)
			return;
		
		hoverCursor(Cursor.HAND_CURSOR);
		
		for(Map.Entry<AdvancedLabel, Runnable> entry : options.entrySet()) {
			AdvancedLabel label = entry.getKey();
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
		
		for(AdvancedLabel label : options.keySet()) {
			if(background != null) {
				int x = label.isCameraRelative() ? label.getWorldX() : label.x;
				int y = label.isCameraRelative() ? label.getWorldY() : label.y;
				
				Color tempBackground = background;
				if(isOn() && handler.getMouseManager().getMouseY() >= y && 
						handler.getMouseManager().getMouseY() < y + label.getHeight())
					tempBackground = Presets.brighten(background, 0.25);
				
				g.setColor(tempBackground);
				g.fillRect(x, y, width, label.getHeight());
			}
			
			label.render(g, handler);
		}
		
		if(height == 0 || width == 0)
			refreshGraphics();
	}
	
	public void addOption(AdvancedLabel label, Runnable action) {
		options.put(label, action);
	}
	
	/** Use after changing position, size, font or color **/
	public void refreshGraphics() {
		width = width <= 0 ? DEFAULT_WIDTH : width;
		height = height <= 0 ? DEFAULT_HEIGHT : height;
		
		int x = cameraRelative ? getWorldX() : this.x;
		int y = cameraRelative ? getWorldY() : this.y;
		
		handler.getStyle().getLayout(layoutID).x = x;
		handler.getStyle().getLayout(layoutID).y = y;
		handler.getStyle().getLayout(layoutID).rows = options.size();
		
		if(autoWidth) width = 0;
		if(autoHeight) height = 0;
		
		int i = 0;
		for(AdvancedLabel label : options.keySet()) {
			label.setFont(font);
			label.setColor(color);
			label.setMarginX(marginX);
			label.setMarginY(marginY);
			label.setCameraRelative(cameraRelative, handler);
			
			label.calculateSize(handler, font);
			
			label.setX(x);
			label.setPositionY(true, layoutID, i);

			if(autoWidth) width = Math.max(width, label.getWidth());
			if(autoHeight) height += label.getHeight();
			
			i++;
		}
		
		handler.getStyle().getLayout(layoutID).width = width;
		handler.getStyle().getLayout(layoutID).height = height;
		handler.getStyle().getLayout(layoutID).refresh();
	}
	
	public String getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	public Map<AdvancedLabel, Runnable> getOptions() {
		return options;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		
		for(AdvancedLabel label : options.keySet())
			label.setFont(font);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		
		for(AdvancedLabel label : options.keySet())
			label.setColor(color);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
		
		for(AdvancedLabel label : options.keySet())
			label.setBackground(background);
	}

	public boolean isAutoWidth() {
		return autoWidth;
	}

	public void setAutoWidth(boolean autoWidth) {
		this.autoWidth = autoWidth;
	}

	public boolean isAutoHeight() {
		return autoHeight;
	}

	public void setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;
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
