package konradn24.tml.input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import konradn24.tml.display.Display;
import konradn24.tml.utils.Logging;

public class KeyManager {
	
	private boolean[] keysPressed;
	private boolean[] keysReleased;
	private boolean[] keysLocked;
	
	private StringBuilder typingBuffer = new StringBuilder();
	private boolean typingEnabled;
	
	public KeyManager() {
		keysPressed = new boolean[GLFW_KEY_LAST + 1];
		keysReleased = new boolean[GLFW_KEY_LAST + 1];
		keysLocked = new boolean[GLFW_KEY_LAST + 1];
		
		Logging.info("Key Manager: initialized");
	}
	
	public void init(Display display) {
		glfwSetKeyCallback(display.getWindow(), (win, key, scancode, action, mods) -> {
			if(key < 0 || key > GLFW_KEY_LAST) {
				Logging.error("Key Manager: invalid key (" + key + ")");
				return;
			}
			
			if(typingEnabled && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				if(key == GLFW_KEY_BACKSPACE && typingBuffer.length() > 0) {
					typingBuffer.deleteCharAt(typingBuffer.length() - 1);
					return;
				}
			}
			
			if(action == GLFW_PRESS) {
				keysPressed[key] = true;
			} else if(action == GLFW_RELEASE) {
				if(keysPressed[key]) {
					keysReleased[key] = true;
					keysPressed[key] = false;
				}
				
				keysLocked[key] = false;
			}
		});
		
		glfwSetCharCallback(display.getWindow(), (win, codepoint) -> {
			if(!typingEnabled) {
				return;
			}
			
			typingBuffer.append((char) codepoint);
		});
	}
	
	public void update(){
		for(int i = 0; i < GLFW_KEY_LAST + 1; i++)
			keysReleased[i] = false;
	}
	
	public void lockKey(int key) {
		keysLocked[key] = true;
	}
	
	public void unlockKey(int key) {
		keysLocked[key] = false;
	}
	
	public boolean isPressed(int key) {
		return keysPressed[key] && !keysLocked[key];
	}
	
	public boolean isReleased(int key) {
		return keysReleased[key];
	}
	
	public void resetReleased(int key) {
		keysReleased[key] = false;
	}
	
	public void enableTyping() {
		typingEnabled = true;
	}
	
	public void disableTyping() {
		typingEnabled = false;
	}
	
	public String getTypingBuffer() {
		return typingBuffer.toString();
	}
	
	public void setTypingBuffer(String buffer) {
		typingBuffer = new StringBuilder(buffer);
	}
	
	public void clearTypingBuffer() {
		typingBuffer = new StringBuilder();
	}
}
