package konradn24.tml.gui.graphics.components;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.utils.Logging;

public class FormField<T extends Component> extends Component {

	private Label label;
	private T input;
	
	protected float inputOffsetX = Display.x(.115f);
	
	public FormField(Handler handler) {
		super(handler);
		
		label = new Label("form_field_label", x, y, handler);
		label.setDisplayType(DisplayType.DEFAULT);
	}
	
	public FormField(Class<T> inputClass, String labelContent, float x, float y, float width, float height, float inputWidth, float inputHeight, Handler handler) {
		super(x, y, width, height, handler);
		
		label = new Label(labelContent, x, y, width, height, AlignX.LEFT, AlignY.CENTER, handler);
		label.setDisplayType(DisplayType.BOX);
		
		try {
			input = inputClass.getDeclaredConstructor(Handler.class).newInstance(handler);
			
			input.setX(x + inputOffsetX);
			input.setY(y + height / 2 - inputHeight / 2);
			input.setWidth(inputWidth);
			input.setHeight(inputHeight);
		} catch (Exception e) {
			Logging.error("FormField: could not instantiate input");
		}
	}

	@Override
	public void update(float dt) {
		label.update(dt);
		
		if(input != null) {
			input.update(dt);
		}
	}
	
	@Override
	public void renderGUI(long vg) {
		label.renderGUI(vg);
		
		if(input != null) {
			input.renderGUI(vg);
		}
	}

	@Override
	public void setHoverOffsetX(float hoverOffsetX) {
		super.setHoverOffsetX(hoverOffsetX);
		label.setHoverOffsetX(hoverOffsetX);
		input.setHoverOffsetX(hoverOffsetX);
	}
	
	@Override
	public void setHoverOffsetY(float hoverOffsetY) {
		super.setHoverOffsetY(hoverOffsetY);
		label.setHoverOffsetY(hoverOffsetY);
		input.setHoverOffsetY(hoverOffsetY);
	}
	
	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public T getInput() {
		return input;
	}

	public void setInput(T input) {
		this.input = input;
	}

	public float getInputOffsetX() {
		return inputOffsetX;
	}

	public void setInputOffsetX(float inputOffsetX) {
		this.inputOffsetX = inputOffsetX;
		this.input.setX(x + inputOffsetX);
	}
}
