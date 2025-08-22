package konradn24.tml.gui.graphics.components;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Cursor;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.utils.Logging;

public class Dropdown extends Component {
	
	public static final float DEFAULT_WIDTH = Display.x(.125f);
	public static final float LABEL_MARGIN_LEFT = 5;

	private LinkedHashMap<Label, Runnable> options;
	
	private String font;
	private float fontSize;
	private NVGColor color, background;

	private float optionHeight;
	
	private boolean closeOnFocusLost;
	
	private Runnable onFocusLost;
	
	public Dropdown(Handler handler) {
		super(handler);
		
		width = DEFAULT_WIDTH;
		
		init();
	}
	
	public Dropdown(float optionHeight, Handler handler) {
		super(handler);
		
		this.optionHeight = optionHeight;
		
		width = DEFAULT_WIDTH;
		
		init();
	}
	
	public Dropdown(int x, int y, float optionHeight, Handler handler) {
		super(x, y, handler);
		
		this.optionHeight = optionHeight;
		
		width = DEFAULT_WIDTH;
		
		init();
	}

	private void init() {
		font = Fonts.GLOBAL_FONT;
		fontSize = Fonts.DEFAULT_SIZE;
		color = Colors.TEXT_LIGHT;
		background = Colors.BACKGROUND;
		
		options = new LinkedHashMap<>();
		closeOnFocusLost = true;
	}
	
	public void pack() {
		if(options.isEmpty()) {
			Logging.warning("UI.Graphics: Cannot pack dropdown - no options provided");
			return;
		}
		
		int i = 0;
		
		for(Label label : options.keySet()) {
			label.setPos(x + LABEL_MARGIN_LEFT, y + i * optionHeight, width, optionHeight);
			label.setFont(font);
			label.setFontSize(fontSize);
			label.setColor(color);
			
			i++;
		}
		
		height = options.size() * optionHeight;
	}
	
	@Override
	public void update(float dt) {
		if(invisible)
			return;
		
		hoverCursor(Cursor.HAND);
		
		for(Map.Entry<Label, Runnable> entry : options.entrySet()) {
			Label label = entry.getKey();
			Runnable action = entry.getValue();
			
			if(handler.getMouseManager().isLeftReleasedOn(x + hoverOffsetX, label.y + label.hoverOffsetY, width, label.height) && action != null) {
				handler.getMouseManager().resetLeftRelease();
				handler.getMouseManager().lockLeft();
				
				action.run();
				if(onFocusLost != null) onFocusLost.run();
				
				setInvisible(true);
				
				return;
			}
		}
		
		if(closeOnFocusLost && (handler.getMouseManager().isLeftPressed() || handler.getMouseManager().isRightPressed()) && !isOn()) {
			handler.getMouseManager().lockLeft();
			
			if(onFocusLost != null) onFocusLost.run();
			
			setInvisible(true);
		}
	}
	
	@Override
	public void renderGUI(long vg) {
		if(invisible)
			return;
		
		for(Label label : options.keySet()) {
			if(background != null) {
				NVGColor tempBackground = background;
				if(label.isOn()) {
					tempBackground = Colors.brighten(background, 0.25);
				}
				
				nvgBeginPath(vg);
				nvgRect(vg, label.x - LABEL_MARGIN_LEFT, label.y, width, optionHeight + 1);
				nvgFillColor(vg, tempBackground);
				nvgFill(vg);
			}
			
			label.renderGUI(vg);
		}
		
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, optionHeight * options.size());
		nvgStrokeColor(vg, Colors.OUTLINE);
		nvgStrokeWidth(vg, 2f);
		nvgStroke(vg);
	}
	
	@Override
	public void setHoverOffsetX(float hoverOffsetX) {
		super.setHoverOffsetX(hoverOffsetX);
		
		options.keySet().forEach(label -> {
			label.setHoverOffsetX(hoverOffsetX);
		});
	}
	
	@Override
	public void setHoverOffsetY(float hoverOffsetY) {
		super.setHoverOffsetY(hoverOffsetY);

		options.keySet().forEach(label -> {
			label.setHoverOffsetY(hoverOffsetY);
		});
	}
	
	public void addOption(String text, Runnable action) {
		Label label = new Label(text, handler);
		label.setDisplayType(DisplayType.BOX);
		label.setAlignX(AlignX.LEFT);
		label.setAlignY(AlignY.CENTER);
		
		options.put(label, action);
	}
	
	public Map<Label, Runnable> getOptions() {
		return options;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
		
		for(Label label : options.keySet())
			label.setFont(font);
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
		
		for(Label label : options.keySet())
			label.setColor(color);
	}

	public NVGColor getBackground() {
		return background;
	}

	public void setBackground(NVGColor background) {
		this.background = background;
		
		for(Label label : options.keySet())
			label.setBackground(background);
	}

	public float getOptionHeight() {
		return optionHeight;
	}

	public void setOptionHeight(float optionHeight) {
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
