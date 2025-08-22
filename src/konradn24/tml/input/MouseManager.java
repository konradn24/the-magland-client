package konradn24.tml.input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.utils.Logging;

public class MouseManager {

	private boolean[] buttonsPressed;
	private boolean[] buttonsReleased;
	private boolean[] buttonsLocked;
	private double mouseX, mouseY;
	private double scrollX, scrollY;
	
	private boolean dragging;
	private double dragStartX, dragStartY, dragLastX, dragLastY, dragX, dragY;
	
	private String tooltip;
	private NVGColor tooltipColor;
	
	public MouseManager() {
		buttonsPressed = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
		buttonsReleased = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
		buttonsLocked = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
		
		Logging.info("Mouse Manager: initialized");
	}
	
	public void init(Display display) {
		glfwSetMouseButtonCallback(display.getWindow(), (win, button, action, mods) -> {
			if(button < 0 || button > GLFW_MOUSE_BUTTON_LAST) {
				Logging.error("Mouse Manager: invalid button (" + button + ")");
				return;
			}
			
			if(action == GLFW_PRESS) {
				buttonsPressed[button] = true;
			} else if(action == GLFW_RELEASE) {
				if(buttonsPressed[button]) {
					buttonsReleased[button] = true;
					buttonsPressed[button] = false;
				}
				
				buttonsLocked[button] = false;
			}
		});
		
		glfwSetCursorPosCallback(display.getWindow(), (win, xPos, yPos) -> {
	        double relativeX = xPos - display.getViewportX();
	        double relativeY = yPos - display.getViewportY();

	        if (relativeX < 0 || relativeX >= display.getViewportWidth()) {
	        	mouseX = -1;
	        } else {
	        	mouseX = relativeX / display.getViewportWidth() * Display.LOGICAL_WIDTH;
	        }

	        if (relativeY < 0 || relativeY >= display.getViewportHeight()) {
	        	mouseY = -1;
	        } else {
	        	mouseY = relativeY / display.getViewportHeight() * Display.LOGICAL_HEIGHT;
	        }
	        
	        if(isLeftPressed()) {
	        	if(!dragging) {
	        		dragging = true;
	        		
	        		dragStartX = mouseX;
	        		dragStartY = mouseY;
	        		
	        		dragLastX = mouseX;
	        		dragLastY = mouseY;
	        	} else {
	        		dragX = mouseX - dragLastX;
	        		dragY = mouseY - dragLastY;
	        		
	        		dragLastX = mouseX;
	        		dragLastY = mouseY;
	        	}
	        } else {
	        	dragging = false;
	        }
		});
		
		glfwSetScrollCallback(display.getWindow(), (win, xOffset, yOffset) -> {
			scrollX = xOffset;
			scrollY = yOffset;
		});
	}
	
	public void update() {
		for(int i = 0; i < GLFW_MOUSE_BUTTON_LAST + 1; i++)
			buttonsReleased[i] = false;
		
		scrollX = 0;
		scrollY = 0;
		dragX = 0;
		dragY = 0;
	}
	
	public boolean isPressed(int button) {
		return buttonsPressed[button] && !buttonsLocked[button];
	}
	
	public boolean isReleased(int button) {
		return buttonsReleased[button];
	}
	
	public boolean isLeftPressed() {
		return buttonsPressed[GLFW_MOUSE_BUTTON_LEFT] && !buttonsLocked[GLFW_MOUSE_BUTTON_LEFT];
	}
	
	public boolean isLeftPressed(boolean lock) {
		if(isLeftPressed()) {
			if(lock) {
				lockLeft();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRightPressed() {
		return buttonsPressed[GLFW_MOUSE_BUTTON_RIGHT] && !buttonsLocked[GLFW_MOUSE_BUTTON_RIGHT];
	}
	
	public boolean isRightPressed(boolean lock) {
		if(isRightPressed()) {
			if(lock) {
				lockRight();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isLeftReleased() {
		return buttonsReleased[GLFW_MOUSE_BUTTON_LEFT];
	}
	
	public boolean isRightReleased() {
		return buttonsReleased[GLFW_MOUSE_BUTTON_RIGHT];
	}
	
	public boolean isLeftReleased(boolean reset) {
		if(isLeftReleased()) {
			if(reset) resetLeftRelease();
			return true;
		}
		
		return false;
	}
	
	public boolean isRightReleased(boolean reset) {
		if(isRightReleased()) {
			if(reset) resetRightRelease();
			return true;
		}
		
		return false;
	}
	
	public double getMouseX() {
		return mouseX;
	}
	
	public double getMouseY() {
		return mouseY;
	}
	
	public double getScrollX() {
		return scrollX;
	}

	public double getScrollY() {
		return scrollY;
	}

	public boolean isOn(float x, float y, float width, float height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
	
	public boolean isLeftPressedOn(float x, float y, float width, float height) {
		return isLeftPressed() && isOn(x, y, width, height);
	}
	
	public boolean isLeftPressedOn(float x, float y, float width, float height, boolean lock) {
		return isLeftPressed(lock) && isOn(x, y, width, height);
	}
	
	public boolean isRightPressedOn(float x, float y, float width, float height) {
		return isRightPressed() && isOn(x, y, width, height);
	}
	
	public boolean isRightPressedOn(float x, float y, float width, float height, boolean lock) {
		return isRightPressed(lock) && isOn(x, y, width, height);
	}
	
	public boolean isLeftReleasedOn(float x, float y, float width, float height) {
		return isLeftReleased() && isOn(x, y, width, height);
	}
	
	public boolean isRightReleasedOn(float x, float y, float width, float height) {
		return isRightReleased() && isOn(x, y, width, height);
	}
	
	public boolean isLeftReleasedOn(float x, float y, float width, float height, boolean reset) {
		return isLeftReleased(reset) && isOn(x, y, width, height);
	}
	
	public boolean isRightReleasedOn(float x, float y, float width, float height, boolean reset) {
		return isRightReleased(reset) && isOn(x, y, width, height);
	}
	
	public boolean isLocked(int button) {
		return buttonsLocked[button];
	}
	
	public void lock(int button) {
		buttonsLocked[button] = true;
	}
	
	public void lockLeft() {
		lock(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public void lockRight() {
		lock(GLFW_MOUSE_BUTTON_RIGHT);
	}
	
	public void unlock(int button) {
		buttonsLocked[button] = false;
	}
	
	public void unlockLeft() {
		unlock(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public void unlockRight() {
		unlock(GLFW_MOUSE_BUTTON_RIGHT);
	}
	
	public void resetReleased(int button) {
		buttonsReleased[button] = false;
	}
	
	public void resetLeftRelease() {
		resetReleased(GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public void resetRightRelease() {
		resetReleased(GLFW_MOUSE_BUTTON_RIGHT);
	}
	
	public boolean isDragging() {
		return dragging;
	}
	
	public boolean isDraggingOn(float x, float y, float width, float height) {
		return dragging && dragStartX >= x && dragStartX < x + width && dragStartY >= y && dragStartY < y + height;
	}
	
	public double getDragX() {
		return dragX;
	}
	
	public double getDragY() {
		return dragY;
	}
	
	public String getTooltip() {
		return tooltip;
	}

	public void enableTooltip(String tooltip) {
		this.tooltip = tooltip;
		this.tooltipColor = null;
	}
	
	public void enableTooltip(String tooltip, NVGColor tooltipColor) {
		this.tooltip = tooltip;
		this.tooltipColor = tooltipColor;
	}
	
	public void disableTooltip() {
		this.tooltip = "";
		this.tooltipColor = null;
	}
	
	public void renderGUITooltip(long vg, int viewportWidth, int viewportHeight) {
		if(tooltip == null || tooltip.isEmpty() || tooltip.isBlank()) {
			return;
		}
		
	    try (MemoryStack stack = MemoryStack.stackPush()) {
	        final float padding = 8.0f;
	        final float fontSize = 20.0f;
	        final float cornerRadius = 4.0f;
	        final int offsetFromCursor = 16;

	        // Ustaw czcionkę i zmierz rozmiar tekstu
	        nvgFontSize(vg, fontSize);
	        nvgFontFace(vg, Fonts.GLOBAL_FONT);
	        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

	        var bounds = stack.mallocFloat(4);
	        nvgTextBounds(vg, 0, 0, tooltip, bounds);
	        float textWidth = bounds.get(2) - bounds.get(0);
	        float textHeight = bounds.get(3) - bounds.get(1);

	        float boxX = 0;
	        float boxY = 0;

	        float boxWidth = textWidth + padding * 2;
	        float boxHeight = textHeight + padding * 2;
	        
	        if(mouseX - offsetFromCursor - boxWidth < 0) {
	        	boxX = (float) (mouseX + offsetFromCursor);
	        } else {
	        	boxX = (float) (mouseX - offsetFromCursor - boxWidth);
	        }
	        
	        if(mouseY + offsetFromCursor + boxHeight < Display.LOGICAL_HEIGHT) {
	        	boxY = (float) (mouseY + offsetFromCursor);
	        } else {
	        	boxY = (float) (mouseY - offsetFromCursor - boxHeight);
	        }

	        nvgBeginPath(vg);
	        nvgRoundedRect(vg, boxX, boxY, boxWidth, boxHeight, cornerRadius);
	        nvgFillColor(vg, Colors.BACKGROUND);
	        nvgFill(vg);
	        
	        nvgStrokeColor(vg, Colors.OUTLINE);
	        nvgStrokeWidth(vg, 1.0f);
	        nvgStroke(vg);

	        nvgFillColor(vg, tooltipColor != null ? tooltipColor : Colors.TEXT);
	        nvgText(vg, boxX + padding, boxY + padding, tooltip);
	    }
	}
}
