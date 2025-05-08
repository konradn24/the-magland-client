package konradn24.tml.display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import konradn24.tml.debug.Logging;

public class Display {

	public static final int LOGICAL_WIDTH = 1920;
	public static final int LOGICAL_HEIGHT = 1080;
	
	private JFrame frame;
	private Canvas canvas;
	
	private int loadingTime = 0; // [FPS]
	
	private String title;
	private int width, height;
	
	private int cursor;
	
	public Display(String title){
		this.title = title;
		this.width = LOGICAL_WIDTH;
		this.height = LOGICAL_HEIGHT;
		
		this.cursor = Cursor.DEFAULT_CURSOR;
		
		createDisplay();
	}
	
	private void createDisplay(){
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);
		
		frame.add(canvas);
		frame.pack();
		
		Logging.info("Created display and frame");
	}
	
	public void showLoadingScreen(String text) {
		BufferStrategy bs = canvas.getBufferStrategy();
		if(bs == null) {
			canvas.createBufferStrategy(2);
			return;
		}
		
		loadingTime++;
		
		byte dots = (byte) ((loadingTime / 60) % 4);
		String dotsStr = "";
		
		for(byte i = 0; i < dots; i++) {
			dotsStr += ".";
		}
		
		Graphics g = bs.getDrawGraphics();
		Font font = new Font("Arial", Font.BOLD, 24);
		
		g.setColor(Color.BLACK);
	    g.fillRect(0, 0, width, height);
	    g.setColor(Color.WHITE);
	    g.setFont(font);
	    g.drawString(text + dotsStr, width / 2 - g.getFontMetrics(font).stringWidth(text + dotsStr) / 2, height / 2);
		
		g.dispose();
		bs.show();
	}
	
	public void refreshCursor() {
		frame.setCursor(new Cursor(cursor));
		cursor = Cursor.DEFAULT_CURSOR;
	}
	
	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public Canvas getCanvas(){
		return canvas;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
}
