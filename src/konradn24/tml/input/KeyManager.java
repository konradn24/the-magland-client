package konradn24.tml.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import konradn24.tml.debug.Logging;

public class KeyManager implements KeyListener {
	
	private boolean[] keys;
	private boolean[] keysReleased;
	private boolean[] keysLocked;
	public boolean up, down, left, right;
	
	private char typed;
	
	public KeyManager(){
		keys = new boolean[1024];
		keysReleased = new boolean[1024];
		keysLocked = new boolean[1024];
		typed = 0;
		
		Logging.info("Key Manager initialized");
	}
	
	public void tick(){
		up = keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		
		for(int i = 0; i < 256; i++)
			keysReleased[i] = false;
		
		for(int i = 0; i < 256; i++) {
			if(keysLocked[i] == false) {
				continue;
			}
			
			if(!keys[i]) unlockKey(i);
		}
		
		typed = 0;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
		keysReleased[e.getKeyCode()] = true;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		typed = e.getKeyChar();
	}

	public boolean[] getKeys() {
		return keys;
	}
	
	public boolean isKeyPressed(int key) {
		if(keys[key]) {
			if(keysLocked[key]) {
				return false;
			}
			
			lockKey(key);
			
			return true;
		} else return false;
	}

	public boolean[] getKeysReleased() {
		return keysReleased;
	}
	
	public void lockKey(int key) {
		keysLocked[key] = true;
	}
	
	public void unlockKey(int key) {
		keysLocked[key] = false;
	}
	
	public char getTyped() {
		return typed;
	}
}
