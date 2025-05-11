package konradn24.tml.display;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.style.StyleText;

public class Display {

	public static final int LOGICAL_WIDTH = 1920;
	public static final int LOGICAL_HEIGHT = 1080;
	
	private JFrame frame;
	private Canvas canvas;
	
	private int loadingTime = 0; // [FPS]
	
	private String title;
	private int width, height;
	private double scaleX, scaleY, scale;
	private int xOffset, yOffset;
	
	private Cursor cursor;
	
	public Display(String title){
		this.title = title;
		
		this.cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		
		createDisplay();
	}
	
	private void createDisplay(){
		frame = new JFrame(title);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode displayMode = gd.getDisplayMode();

        int screenWidth = displayMode.getWidth();
        int screenHeight = displayMode.getHeight();
        
        frame.setSize(screenWidth, screenHeight);
        frame.setPreferredSize(new Dimension(screenWidth, screenHeight));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(screenWidth, screenHeight));
		canvas.setFocusable(false);
		
		frame.add(canvas, BorderLayout.CENTER);
		frame.setVisible(true);
		
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onResize();
			}
		});
		
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
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		Font font = new Font("Arial", Font.BOLD, 24);
		
		g.setColor(Color.BLACK);
	    g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
	    g.setColor(Color.WHITE);
	    g.setFont(font);
	    StyleText.drawCenteredString(g, text + dotsStr, LOGICAL_WIDTH / 2, LOGICAL_HEIGHT / 2);
		
		g.dispose();
		bs.show();
	}
	
	private void onResize() {
		width = (int) canvas.getSize().getWidth();
	    height = (int) canvas.getSize().getHeight();
	    
	    scaleX = width / (double) Display.LOGICAL_WIDTH;
	    scaleY = height / (double) Display.LOGICAL_HEIGHT;
	    scale = Math.min(scaleX, scaleY);

	    xOffset = (int) ((width - (Display.LOGICAL_WIDTH * scale)) / 2);
	    yOffset = (int) ((height - (Display.LOGICAL_HEIGHT * scale)) / 2);
	}
	
	public void refreshCursor() {
		frame.setCursor(cursor);
		cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	}
	
	public void setCursor(int cursor) {
		this.cursor = new Cursor(cursor);
	}
	
	public void setCursor(BufferedImage image) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor cursor = toolkit.createCustomCursor(image, new Point(0, 0), "EntityHoverCursor");
		
		this.cursor = cursor;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public double getScale() {
		return scale;
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}

	public Canvas getCanvas(){
		return canvas;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
}
