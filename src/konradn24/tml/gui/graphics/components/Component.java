package konradn24.tml.gui.graphics.components;

import konradn24.tml.Handler;

public abstract class Component {
	
	protected float x, y, width, height;
	
	protected boolean invisible;
	
	protected Handler handler;
	
	public Component(Handler handler) {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.handler = handler;
	}
	
	public Component(float x, float y, Handler handler) {
		this.x = x;
		this.y = y;
		this.width = 0;
		this.height = 0;
		this.handler = handler;
	}
	
	public Component(float x, float y, float width, float height, Handler handler) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.handler = handler;
	}
	
	public abstract void update(float dt);
	public abstract void renderGUI(long vg);
	
	protected void hoverCursor(long cursor) {
		if(isOn()) {
			handler.getGame().getDisplay().setCursor(cursor);
		}
	}
	
	public boolean isOn() {
		if(handler == null)
			return false;
		
		if(handler.getMouseManager().isOn(x, y, width, height)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isLeftPressed() {
		if(handler == null)
			return false;
		
		if(handler.getMouseManager().isLeftPressed() && isOn())
			return true;
		else
			return false;
	}
	
	public boolean isRightPressed() {
		if(handler == null)
			return false;
		
		if(handler.getMouseManager().isRightPressed() && isOn())
			return true;
		else
			return false;
	}
	
	public boolean isLeftReleased() {
		if(handler == null)
			return false;
		
		if(handler.getMouseManager().isLeftReleased() && isOn())
			return true;
		else
			return false;
	}
	
	public boolean isRightReleased() {
		if(handler == null)
			return false;
		
		if(handler.getMouseManager().isRightReleased() && isOn())
			return true;
		else
			return false;
	}
	
	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPos(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
//	public int getWorldX() {
//		if(handler == null || handler.getGameCamera() == null)
//			return x;
//		
//		return (int) (initialX - (handler.getGameCamera().getxOffset()));
//	}
//	
//	public int getWorldY() {
//		if(handler == null || handler.getGameCamera() == null)
//			return y;
//		
//		return (int) (initialY - (handler.getGameCamera().getyOffset()));
//	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
}
