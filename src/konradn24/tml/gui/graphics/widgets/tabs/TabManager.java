package konradn24.tml.gui.graphics.widgets.tabs;

import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.gui.graphics.layouts.ColumnLayout;
import konradn24.tml.gui.graphics.layouts.RowLayout;
import konradn24.tml.utils.Logging;

public class TabManager extends Component {
	
	public enum MenuOrientation { HORIZONTAL, VERTICAL }
	
	private List<Tab> tabs;
	private Tab current;
	
	private RowLayout<Button> menuHorizontal;
	private ColumnLayout<Button> menuVertical;
	private MenuOrientation menuOrientation;
	
	private int spacing;
	
	/** Tabs menu position and size **/
	public TabManager(int x, int y, int width, int height, int spacing, Handler handler) {
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
				Button.class, tabs.size(), x, y, width, height, spacing, false, handler
			).customize((button, i) -> {
				button.getLabel().setContent(tabs.get(i).getName());
				button.refreshLabelPosition();
				
				button.setOnLeftClick(() -> {
					setCurrentTab(i);
				});
			});
		} else {
			menuVertical = new ColumnLayout<Button>(
				Button.class, tabs.size(), x, y, width, height, spacing, false, handler
			).customize((button, i) -> {
				button.getLabel().setContent(tabs.get(i).getName());
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
			current.renderGUI(vg);
		}
	}

	public void addTab(Tab tab) {
		tabs.add(tab);
	}
	
	public void removeTab(String name) {
		tabs.removeIf(tab -> tab.getName() == name);
	}
	
	public void addTabs(Tab... tabs) {
		this.tabs.addAll(List.of(tabs));
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
	
	public void setCurrentTab(String name) {
		if(menuHorizontal != null) {
			menuHorizontal.forEach((button, i) -> {
				button.setDisabled(false);
			});
		} else if(menuVertical != null) {
			menuVertical.forEach((button, i) -> {
				button.setDisabled(false);
			});
		}
		
		current = tabs.stream().filter(tab -> tab.getName() == name)
		.findFirst().orElse(null);
		
		if(current == null) {
			Logging.error("Tab Manager: invalid tab ID ('" + name + "')");
			
			if(tabs.size() > 0) {
				current = tabs.get(0);
			}
		}
		
		int index = current == null ? 0 : tabs.indexOf(current);
		
		if(menuHorizontal != null && index < menuHorizontal.getComponents().length) {
			menuHorizontal.getComponents()[index].setDisabled(true);
		} else if(menuVertical != null && index < menuVertical.getComponents().length) {
			menuVertical.getComponents()[index].setDisabled(true);
		}
	}

	public List<Tab> getTabs() {
		return tabs;
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
