package konradn24.tml.gfx.components;

import java.awt.Color;
import java.awt.Graphics2D;

import konradn24.tml.Handler;

public class ProgressBar extends Component {

	private static final int ROUND = 10;
	
	protected double minValue, maxValue, currentFactor;
	
	public ProgressBar(Handler handler) {
		super(handler);
		
		this.minValue = 0;
		this.maxValue = 1;
	}
	
	public ProgressBar(int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height, handler);
		
		this.minValue = 0;
		this.maxValue = 1;
	}
	
	public ProgressBar(int x, int y, int width, int height, double maxValue, Handler handler) {
		super(x, y, width, height, handler);
		
		this.minValue = 0;
		this.maxValue = maxValue;
	}
	
	public ProgressBar(int x, int y, int width, int height, double minValue, double maxValue, Handler handler) {
		super(x, y, width, height, handler);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public void tick(double value) {
		super.tick();
		
		if(value < minValue) value = minValue;
		if(value > maxValue) value = maxValue;
		
		currentFactor = (value - minValue) / (maxValue - minValue);
	}
	
	public void render(Graphics2D g) {
		Color color = null;
		int green = (int) (510 * currentFactor);
		
		if(green <= 255)
			color = new Color(255, green, 0);
		else color = new Color(510 - green, 255, 0);
		
		g.setColor(color);
		g.fillRoundRect(x, y, (int) (width * currentFactor), height, ROUND, ROUND);
		
		g.setColor(Color.black);
		g.drawRoundRect(x, y, width, height, ROUND, ROUND);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getCurrentFactor() {
		return currentFactor;
	}

	public void setCurrentFactor(double currentFactor) {
		this.currentFactor = currentFactor;
	}
}
