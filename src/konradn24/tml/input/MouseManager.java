package konradn24.tml.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import konradn24.tml.debug.Logging;

public class MouseManager implements MouseListener, MouseMotionListener {

	private boolean leftPressed, rightPressed, leftReleased, rightReleased;
	private int mouseX, mouseY;
	
	public MouseManager() {
		Logging.info("Mouse Manager initialized");
	}
	
	public boolean isLeftPressed() {
		return leftPressed;
	}
	
	public boolean isRightPressed() {
		return rightPressed;
	}
	
	public boolean isLeftReleased() {
		return leftReleased;
	}
	
	public boolean isRightReleased() {
		return rightReleased;
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public boolean isOn(int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
	
	public boolean isLeftPressedOn(int x, int y, int width, int height) {
		return leftPressed && isOn(x, y, width, height);
	}
	
	public boolean isRightPressedOn(int x, int y, int width, int height) {
		return rightPressed && isOn(x, y, width, height);
	}
	
	public boolean isLeftReleasedOn(int x, int y, int width, int height) {
		return leftReleased && isOn(x, y, width, height);
	}
	
	public boolean isRightReleasedOn(int x, int y, int width, int height) {
		return rightReleased && isOn(x, y, width, height);
	}
	
	public void tick() {
		if(leftReleased)
			leftReleased = false;
		if(rightReleased)
			rightReleased = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftPressed = true;
		else if(e.getButton() == MouseEvent.BUTTON3)
			rightPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			leftPressed = false;
			leftReleased = true;
		} else if(e.getButton() == MouseEvent.BUTTON3) {
			rightPressed = false;
			rightReleased = true;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
