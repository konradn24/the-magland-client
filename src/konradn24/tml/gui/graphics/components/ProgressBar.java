package konradn24.tml.gui.graphics.components;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;

public class ProgressBar extends Component {

	private static final float ROUND = Display.y(.0093f);
	
	protected float minValue, maxValue, value, factor;
	protected NVGColor color;
	
	public ProgressBar(Handler handler) {
		super(handler);
		init(0, 1);
	}
	
	public ProgressBar(float x, float y, float width, float height, Handler handler) {
		super(x, y, width, height, handler);
		init(0, 1);
	}
	
	public ProgressBar(float x, float y, float width, float height, float maxValue, Handler handler) {
		super(x, y, width, height, handler);
		init(0, maxValue);
	}
	
	public ProgressBar(float x, float y, float width, float height, float minValue, float maxValue, Handler handler) {
		super(x, y, width, height, handler);
		init(minValue, maxValue);
	}
	
	private void init(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.color = Colors.GREEN;
	}
	
	@Override
	public void update(float dt) {
		float value = this.value;
		
		if(value < minValue) value = minValue;
		if(value > maxValue) value = maxValue;
		
		factor = (value - minValue) / (maxValue - minValue);
		int green = (int) (510 * factor);
		
		if(green <= 255)
			color = Colors.rgba(255, green, 0, 255);
		else color = Colors.rgba(510 - green, 255, 0, 255);
	}
	
	@Override
	public void renderGUI(long vg) {
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, width * factor, height, ROUND);
		nvgFillColor(vg, color);
		nvgFill(vg);
		
		nvgStrokeColor(vg, Colors.OUTLINE);
		nvgStrokeWidth(vg, 3f);
		nvgStroke(vg);
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
