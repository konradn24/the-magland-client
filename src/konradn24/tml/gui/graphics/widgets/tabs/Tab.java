package konradn24.tml.gui.graphics.widgets.tabs;

import konradn24.tml.Handler;

public abstract class Tab {

	protected String name;
	protected Handler handler;
	
	public Tab(String name, Handler handler) {
		this.name = name;
		this.handler = handler;
	}
	
	public abstract void update(float dt);
	public abstract void renderGUI(long vg);
	
	public String getName() {
		return name;
	}
}
