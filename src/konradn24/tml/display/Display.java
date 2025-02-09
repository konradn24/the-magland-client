package konradn24.tml.display;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JFrame;

import konradn24.tml.debug.Logging;

public class Display {

	private JFrame frame;
	private Canvas canvas;
	
	private String title;
	private int width, height;
	
	private int cursor;
	
	public Display(String title, int width, int height){
		this.title = title;
		this.width = width;
		this.height = height;
		
		this.cursor = Cursor.DEFAULT_CURSOR;
		
		createDisplay();
	}
	
	private void createDisplay(){
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
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
