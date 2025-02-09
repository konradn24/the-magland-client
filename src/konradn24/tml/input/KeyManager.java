package konradn24.tml.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import konradn24.tml.debug.Logging;

public class KeyManager implements KeyListener {
	
	private boolean[] keys;
	private boolean[] keysReleased;
	public boolean up, down, left, right;
	
	private char typed;
	
	public KeyManager(){
		keys = new boolean[256];
		keysReleased = new boolean[256];
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

	public boolean[] getKeysReleased() {
		return keysReleased;
	}
	
	public char getTyped() {
		return typed;
	}
}
