package konradn24.tml.gui.graphics.widgets.tabs;

import static org.lwjgl.nanovg.NanoVG.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.gui.graphics.layouts.ColumnLayout;
import konradn24.tml.gui.graphics.layouts.RowLayout;
import konradn24.tml.utils.Logging;

public class TabManager extends Component {
	
	private static final float VERTICAL_MENU_WIDTH = Display.x(.025f);
	private static final float HORIZONTAL_MENU_HEIGHT = Display.y(.04f);
	
	public enum MenuOrientation { HORIZONTAL, VERTICAL }
	
	private List<Tab> tabs;
	private Tab current;
	
	private RowLayout<Button> menuHorizontal;
	private ColumnLayout<Button> menuVertical;
	private MenuOrientation menuOrientation;
	
	private float spacing;
	
	/** Tabs menu position and size **/
	public TabManager(float x, float y, float width, float height, float spacing, Handler handler) {
		super(x, y, width, height, handler);
		
		this.spacing = spacing;
		
		tabs = new ArrayList<Tab>();
		menuOrientation = MenuOrientation.HORIZONTAL;
	}
	
	public void pack() {
		if(tabs.size() == 0) {
			Logging.error("Tab Manager: cannot pack, no tabs added");
			return;
		}
		
		if(menuOrientation == MenuOrientation.HORIZONTAL) {
			menuHorizontal = new RowLayout<Button>(
				Button.class, tabs.size(), x, y, width, HORIZONTAL_MENU_HEIGHT, spacing, false, handler
			).customize((button, i) -> {
				button.getLabel().setContent(tabs.get(i).getTitle());
				button.refreshLabelPosition();
				
				button.setOnLeftClick(() -> {
					setCurrentTab(i);
				});
			});
		} else {
			menuVertical = new ColumnLayout<Button>(
				Button.class, tabs.size(), x, y, VERTICAL_MENU_WIDTH, height, spacing, false, handler
			).customize((button, i) -> {
				button.getLabel().setContent(tabs.get(i).getTitle());
				button.refreshLabelPosition();
				
				button.setOnLeftClick(() -> {
					setCurrentTab(i);
				});
			});
		}
		
		setCurrentTab(0);
	}
	
	@Override
	public void update(float dt) {
		if(tabs.isEmpty() 
		   || menuOrientation == MenuOrientation.HORIZONTAL && menuHorizontal == null 
		   || menuOrientation == MenuOrientation.VERTICAL && menuVertical == null) {
			
			return;
		}
		
		if(menuOrientation == MenuOrientation.HORIZONTAL) {
			menuHorizontal.update(dt);
		} else {
			menuVertical.update(dt);
		}
		
		if(current != null) {
			current.update(dt);
		}
	}
	
	@Override
	public void renderGUI(long vg) {
		if(tabs.isEmpty() 
		   || menuOrientation == MenuOrientation.HORIZONTAL && menuHorizontal == null 
		   || menuOrientation == MenuOrientation.VERTICAL && menuVertical == null) {
			
			return;
		}
		
		if(menuOrientation == MenuOrientation.HORIZONTAL) {
			menuHorizontal.renderGUI(vg);
		} else {
			menuVertical.renderGUI(vg);
		}
		
		if(current != null) {
			nvgSave(vg);
			nvgScissor(vg, current.x, current.y, current.width, current.height);
        	nvgTranslate(vg, current.x, current.y);
			
        	current.renderGUI(vg);

	        nvgRestore(vg);
		}
	}

	public void addTab(Class<? extends Tab> tabClass) {
		try {
			float tabX, tabY, tabWidth, tabHeight;
			
			if(menuOrientation == MenuOrientation.HORIZONTAL) {
				tabX = x;
				tabY = y + HORIZONTAL_MENU_HEIGHT;
				tabWidth = width;
				tabHeight = height - HORIZONTAL_MENU_HEIGHT;
			} else {
				tabX = x + VERTICAL_MENU_WIDTH;
				tabY = y;
				tabWidth = width - VERTICAL_MENU_WIDTH;
				tabHeight = height;
			}
			
			Tab tab = tabClass.getDeclaredConstructor(float.class, float.class, float.class, float.class, Handler.class)
					.newInstance(tabX, tabY, tabWidth, tabHeight, handler);
			tabs.add(tab);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logging.error("Tab Manager: cannot add tab - failed to create instance of " + tabClass.getSimpleName());
			Logging.error(e);
		}
	}
	
	public void addTabs(@SuppressWarnings("unchecked") Class<? extends Tab>... tabsClass) {
		for(Class<? extends Tab> tabClass : tabsClass) {
			addTab(tabClass);
		}
	}
	
	public void removeTab(String title) {
		tabs.removeIf(tab -> tab.getTitle() == title);
	}
	
	public void removeTab(Class<? extends Tab> tabClass) {
		tabs.removeIf(tab -> tabClass.isInstance(tab));
	}
	
	public void setCurrentTab(int index) {
		if(index < 0 || index >= tabs.size()) {
			Logging.error("Tab Manager: invalid tab index (" + index + ")");
		}
		
		if(menuHorizontal != null) {
			menuHorizontal.forEach((button, i) -> {
				button.setDisabled(false);
			});
		} else if(menuVertical != null) {
			menuVertical.forEach((button, i) -> {
				button.setDisabled(false);
			});
		}
		
		current = tabs.get(index);
		
		if(menuHorizontal != null && index < menuHorizontal.getComponents().length) {
			menuHorizontal.getComponents()[index].setDisabled(true);
		} else if(menuVertical != null && index < menuVertical.getComponents().length) {
			menuVertical.getComponents()[index].setDisabled(true);
		}
	}
	
	public void setCurrentTab(String title) {
		int index = -1;
		
		for(int i = 0; i < tabs.size(); i++) {
			if(tabs.get(i).getTitle().equals(title)) {
				index = i;
				break;
			}
		}
		
		if(index < 0) {
			Logging.error("Tab Manager: invalid tab title ('" + title + "')");
			return;
		}
		
		setCurrentTab(index);
	}
	
	public void setCurrentTab(Class<? extends Tab> tabClass) {
		int index = -1;
		
		for(int i = 0; i < tabs.size(); i++) {
			if(tabClass.isInstance(tabs.get(i))) {
				index = i;
				break;
			}
		}
		
		if(index < 0) {
			Logging.error("Tab Manager: invalid tab ('" + tabClass.getSimpleName() + "')");
			return;
		}
		
		setCurrentTab(index);
	}

	public List<Tab> getTabs() {
		return tabs;
	}
	
	public <T extends Tab> T getTab(Class<T> tabClass) {
		Tab tab = tabs.stream().filter(tabClass::isInstance).findFirst().orElse(null);
		
		if(tab == null) {
			Logging.error("Tab Manager: tab " + tabClass.getSimpleName() + " not found");
			return null;
		}
		
		return tabClass.cast(tab);
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

	public RowLayout<Button> getMenuHorizontal() {
		return menuHorizontal;
	}

	public void setMenuHorizontal(RowLayout<Button> menuHorizontal) {
		this.menuHorizontal = menuHorizontal;
	}

	public ColumnLayout<Button> getMenuVertical() {
		return menuVertical;
	}

	public void setMenuVertical(ColumnLayout<Button> menuVertical) {
		this.menuVertical = menuVertical;
	}

	public MenuOrientation getMenuOrientation() {
		return menuOrientation;
	}

	public void setMenuOrientation(MenuOrientation menuOrientation) {
		this.menuOrientation = menuOrientation;
	}
}
