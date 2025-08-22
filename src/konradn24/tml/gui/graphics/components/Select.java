package konradn24.tml.gui.graphics.components;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.utils.Logging;

public class Select extends Component {

	public static final float DEFAULT_OPTION_HEIGHT = Display.y(.0296f);
	public static final float SELECT_ICON_OFFSET = Display.x(.0042f);
	public static final float SELECT_ICON_SIZE_FACTOR = 0.6f;
	
	protected String selected;
	
	protected Button button;
	protected Dropdown dropdown;
	
	protected int selectIconSize;
	
	public Select(Handler handler) {
		super(handler);
		
		button = new Button(handler);
		button.setOnLeftClick(() -> {
			dropdown.pack();
			dropdown.setInvisible(!dropdown.isInvisible());
		});
		
		dropdown = new Dropdown(handler);
		dropdown.setOptionHeight(DEFAULT_OPTION_HEIGHT);
		dropdown.setInvisible(true);
	}
	
	public Select(int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height, handler);
		
		selectIconSize = (int) (height * SELECT_ICON_SIZE_FACTOR);
		
		button = new Button(selected, x, y, width, height, handler);
		button.label.setWidth(width - selectIconSize - SELECT_ICON_OFFSET);
		button.setOnLeftClick(() -> {
			dropdown.pack();
			dropdown.setInvisible(!dropdown.isInvisible());
		});
		
		dropdown = new Dropdown(x, y + height, DEFAULT_OPTION_HEIGHT, handler);
		dropdown.setInvisible(true);
	}
	
	@Override
	public void update(float dt) {
		button.update(dt);
		dropdown.update(dt);
	}
	
	@Override
	public void renderGUI(long vg) {
		button.renderGUI(vg);
		
		nvgBeginPath(vg);
		AssetsRenderer.renderDropdownIcon(vg, 
			button.x + button.width - selectIconSize - SELECT_ICON_OFFSET, 
			button.y + button.height / 2 - selectIconSize / 2, selectIconSize, 
			Colors.rgba(196, 196, 196, 255)
		);
		
		dropdown.renderGUI(vg);
	}
	
	public void setOptions(String... options) {
		if(options.length == 0) {
			Logging.error("Select: cannot set options, no options' list provided");
			return;
		}
		
		dropdown.getOptions().clear();
		
		for(String option : options) {
			dropdown.addOption(option, () -> {
				setSelected(option);
			});
		}
		
		setSelected(options[0]);
	}
	
	@Override
	public void setX(float x) {
		super.setX(x);
		button.setX(x);
		dropdown.setX(x);
	}
	
	@Override
	public void setY(float y) {
		super.setY(y);
		button.setY(y);
		dropdown.setY(y + height);
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		button.setWidth(width);
		button.label.setWidth(width - selectIconSize - SELECT_ICON_OFFSET);
		dropdown.setWidth(width);
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		button.setHeight(height);
		dropdown.setY(y + height);
		selectIconSize = (int) (height * SELECT_ICON_SIZE_FACTOR);
		button.label.setWidth(width - selectIconSize - SELECT_ICON_OFFSET);
	}
	
	@Override
	public void setHoverOffsetX(float hoverOffsetX) {
		super.setHoverOffsetX(hoverOffsetX);
		button.setHoverOffsetX(hoverOffsetX);
		dropdown.setHoverOffsetX(hoverOffsetX);
	}
	
	@Override
	public void setHoverOffsetY(float hoverOffsetY) {
		super.setHoverOffsetY(hoverOffsetY);
		button.setHoverOffsetY(hoverOffsetY);
		dropdown.setHoverOffsetY(hoverOffsetY);
	}
	
	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
		this.button.label.setContent(selected);
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public Dropdown getDropdown() {
		return dropdown;
	}
	
	public void setDropdown(Dropdown dropdown) {
		this.dropdown = dropdown;
	}
}
