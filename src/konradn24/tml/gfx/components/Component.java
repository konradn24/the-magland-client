package konradn24.tml.gfx.components;

import java.awt.Graphics2D;

import konradn24.tml.Handler;

public abstract class Component {
	
	protected int x, y, marginX, marginY, width, height;
	
	protected boolean centerX, centerY, positionCenterX, positionCenterY, positionX, positionY;
	protected String layoutID;
	protected int row, column;
	
	protected boolean cameraRelative, invisible;
	protected int initialX, initialY;
	protected float initialCameraXOffset, initialCameraYOffset;
	
	protected Handler handler;
	
	public Component(Handler handler) {
		this.x = 0;
		this.y = 0;
		this.marginX = 0;
		this.marginY = 0;
		this.width = 0;
		this.height = 0;
		this.handler = handler;
	}
	
	public Component(int x, int y, Handler handler) {
		this.x = x;
		this.y = y;
		this.marginX = 0;
		this.marginY = 0;
		this.width = 0;
		this.height = 0;
		this.handler = handler;
	}
	
	public Component(int x, int y, int width, int height, Handler handler) {
		this.x = x;
		this.y = y;
		this.marginX = 0;
		this.marginY = 0;
		this.width = width;
		this.height = height;
		this.handler = handler;
	}
	
	public Component(int x, int y, int marginX, int marginY, int width, int height, Handler handler) {
		this.x = x;
		this.y = y;
		this.marginX = marginX;
		this.marginY = marginY;
		this.width = width;
		this.height = height;
	}
	
	public void tick() {
		if(cameraRelative && handler != null && handler.getGameCamera() != null) {
			x = (int) (initialX - (handler.getGameCamera().getxOffset() - initialCameraXOffset));
			y = (int) (initialY - (handler.getGameCamera().getyOffset() - initialCameraYOffset));
		}
	}
	
	public void render(Graphics2D g) {}
	
	protected void hoverCursor(int cursor) {
		if(isOn()) handler.getGame().getDisplay().setCursor(cursor);
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
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPos(int x, int y, int width, int height) {
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
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isCenterX() {
		return centerX;
	}

	public void setCenterX(boolean centerX) {
		this.centerX = centerX;
	}

	public boolean isCenterY() {
		return centerY;
	}

	public void setCenterY(boolean centerY) {
		this.centerY = centerY;
	}

	public boolean isPositionCenterX() {
		return positionCenterX;
	}

	public void setPositionCenterX(boolean positionCenterX) {
		this.positionCenterX = positionCenterX;
	}
	
	public void setPositionCenterX(boolean positionCenterX, String layoutID, int column) {
		this.positionCenterX = positionCenterX;
		this.layoutID = layoutID;
		this.column = column;
	}

	public boolean isPositionCenterY() {
		return positionCenterY;
	}

	public void setPositionCenterY(boolean positionCenterY) {
		this.positionCenterY = positionCenterY;
	}
	
	public void setPositionCenterY(boolean positionCenterY, String layoutID, int row) {
		this.positionCenterY = positionCenterY;
		this.layoutID = layoutID;
		this.row = row;
	}
	
	public boolean isPositionX() {
		return positionX;
	}

	public void setPositionX(boolean positionX) {
		this.positionX = positionX;
	}
	
	public void setPositionX(boolean positionX, String layoutID, int row) {
		this.positionX = positionX;
		this.layoutID = layoutID;
		this.row = row;
	}
	
	public boolean isPositionY() {
		return positionY;
	}

	public void setPositionY(boolean positionY) {
		this.positionY = positionY;
	}
	
	public void setPositionY(boolean positionY, String layoutID, int row) {
		this.positionY = positionY;
		this.layoutID = layoutID;
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public boolean isCameraRelative() {
		return cameraRelative;
	}

	public void disableCameraRelative() {
		this.cameraRelative = false;
		
		x = initialX;
		y = initialY;
	}
	
	public void enableCameraRelative(Handler handler) {
		this.cameraRelative = true;
		
		initialX = x;
		initialY = y;
		initialCameraXOffset = handler.getGameCamera().getxOffset();
		initialCameraYOffset = handler.getGameCamera().getyOffset();
	}
}
