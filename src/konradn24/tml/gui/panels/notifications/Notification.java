package konradn24.tml.gui.panels.notifications;

import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.utils.Function;

public class Notification {

	private String text;
	private NVGColor color, background;
	private int alpha;
	private boolean fade;
	
	private Function onClick;
	
	private long emitTime;
	
	public Notification(String text, NVGColor color, boolean fade) {
		this.text = text;
		this.color = color;
		this.fade = fade;
		
		this.background = Colors.BACKGROUND;
		this.alpha = NotificationsPanel.BACKGROUND_ALPHA;
	}

	public String getText() {
		return text;
	}

	public NVGColor getColor() {
		return color;
	}
	
	public NVGColor getCurrentAlphaColor() {
		return Colors.alpha(color, alpha);
	}
	
	public NVGColor getBackground() {
		return background;
	}
	
	public NVGColor getCurrentAlphaBackground() {
		return Colors.alpha(background, alpha);
	}
	
	public void setBackground(NVGColor color) {
		this.background = color;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public boolean isFade() {
		return fade;
	}

	public Function getOnClick() {
		return onClick;
	}

	public void setOnClick(Function onClick) {
		this.onClick = onClick;
	}

	public long getEmitTime() {
		return emitTime;
	}

	public void setEmitTime(long emitTime) {
		this.emitTime = emitTime;
	}
}
