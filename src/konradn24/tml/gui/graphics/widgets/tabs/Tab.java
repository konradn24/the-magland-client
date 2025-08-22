package konradn24.tml.gui.graphics.widgets.tabs;

import konradn24.tml.Handler;

public abstract class Tab {

	protected float x, y, width, height;
	
	protected Handler handler;
	
	public Tab(float x, float y, float width, float height, Handler handler) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.handler = handler;
	}
	
	public abstract void update(float dt);
	public abstract void renderGUI(long vg);
	public abstract String getTitle();
}
