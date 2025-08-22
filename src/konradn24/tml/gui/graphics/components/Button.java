package konradn24.tml.gui.graphics.components;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Cursor;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.utils.Function;

public class Button extends Component {
	
	private static final float ROUND = Display.y(.0093f);
	
	protected Label label;
	protected NVGColor color, outlineColor;
	protected boolean disabled;
	
	protected Function onLeftClick, onRightClick;
	
	protected boolean updateLabelPosition = true;
	
	public Button(Handler handler) {
		super(handler);
		init("", x, y, width, height);
	}
	
	public Button(String text, float x, float y, float width, float height, Handler handler) {
		super(x, y, width, height, handler);
		init(text, x, y, width, height);
	}
	
	private void init(String text, float x, float y, float width, float height) {
		this.label = new Label(text, x, y, width, height, 10, handler);
		this.label.setDisplayType(DisplayType.BOX);
		this.label.setColor(Colors.TEXT);
		
		color = Colors.BACKGROUND;
		outlineColor = Colors.OUTLINE;
	}
	
	@Override
	public void update(float dt) {
		if(invisible || disabled)
			return;
		
		hoverCursor(Cursor.HAND);
		
		if(isLeftPressed() && onLeftClick != null) {
			handler.getMouseManager().lockLeft();
			onLeftClick.run();
		} else if(isRightPressed() && onRightClick != null) {
			handler.getMouseManager().lockRight();
			onRightClick.run();
		}
	}
	
	@Override
	public void renderGUI(long vg) {
		if(invisible)
			return;
		
		boolean hovered = isOn() && !isLeftPressed() && !disabled;
		NVGColor color = hovered ? Colors.brighten(this.color, 0.15f) : this.color;

		// Background
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, width, height, ROUND);
		nvgFillColor(vg, color);
		nvgFill(vg);

		// Outline
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, outlineColor);
		nvgStroke(vg);
			
		if(label != null) {
			label.renderGUI(vg);
		}
		
		if(disabled) {
			NVGColor overlay = Colors.rgba(0, 0, 0, 128);
			nvgBeginPath(vg);
			nvgRoundedRect(vg, x, y, width, height, ROUND);
			nvgFillColor(vg, overlay);
			nvgFill(vg);
		}
	}
	
	public void setX(float x) {
		super.setX(x);
		
		if(label != null && updateLabelPosition) {
			label.setX(x);
		}
	}
	
	public void setY(float y) {
		super.setY(y);

		if(label != null && updateLabelPosition) {
			label.setY(y);
		}
	}
	
	public void setWidth(float width) {
		super.setWidth(width);

		if(label != null && updateLabelPosition) {
			label.setWidth(width);
		}
	}
	
	public void setHeight(float height) {
		super.setHeight(height);

		if(label != null && updateLabelPosition) {
			label.setHeight(height);
		}
	}
	
	public void refreshLabelPosition() {
		label.setPos(x, y, width, height);
	}
	
	public void setPos(float x, float y) {
		super.setPos(x, y, width, height);
		
		if(label != null && updateLabelPosition) {
			label.setPos(x, y);
		}
	}
	
	public void setPos(float x, float y, float width, float height) {
		super.setPos(x, y, width, height);
		
		if(label != null && updateLabelPosition) {
			label.setPos(x, y, width, height);
		}
	}
	
	public void setSize(float width, float height) {
		super.setPos(x, y, width, height);
		
		if(label != null && updateLabelPosition) {
			label.setWidth(width);
			label.setHeight(height);
		}
	}
	
	//GETTERS AND SETTERS
	
	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public NVGColor getColor() {
		return color;
	}

	public void setColor(NVGColor color) {
		this.color = color;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setOnLeftClick(Function function) {
		this.onLeftClick = function;
	}
	
	public void setOnRightClick(Function function) {
		this.onRightClick = function;
	}

	public boolean isUpdateLabelPosition() {
		return updateLabelPosition;
	}

	public void setUpdateLabelPosition(boolean updateLabelPosition) {
		this.updateLabelPosition = updateLabelPosition;
	}
}
