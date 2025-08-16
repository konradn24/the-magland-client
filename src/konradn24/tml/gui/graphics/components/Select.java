package konradn24.tml.gui.graphics.components;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.utils.Logging;

public class Select extends Component {

	public static final float DEFAULT_OPTION_HEIGHT = Display.y(.0296f);
	public static final float SELECT_ICON_OFFSET = Display.x(.0042f);
	
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
		
		selectIconSize = (int) (height * 0.8);
		
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
		
//		g.drawImage(
//				Assets.selectIcon, button.x + button.width - selectIconSize - SELECT_ICON_OFFSET, button.y + button.height / 2 - selectIconSize / 2, 
//				selectIconSize, selectIconSize, null
//		);
		
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
	
	public void setX(int x) {
		super.setX(x);
		button.setX(x);
		dropdown.setX(x);
	}
	
	public void setY(int y) {
		super.setY(y);
		button.setY(y);
		dropdown.setY(y + height);
	}
	
	public void setWidth(int width) {
		super.setWidth(width);
		button.setWidth(width);
		button.label.setWidth(width - selectIconSize - SELECT_ICON_OFFSET);
		dropdown.setWidth(width);
	}
	
	public void setHeight(int height) {
		super.setHeight(height);
		button.setHeight(height);
		dropdown.setY(y + height);
		selectIconSize = (int) (height * 0.8);
		button.label.setWidth(width - selectIconSize - SELECT_ICON_OFFSET);
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
