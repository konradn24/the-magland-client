package konradn24.tml.gui.panels.notifications;

import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.utils.Function;

public class Notification {

	private String text;
	private NVGColor color, background;
	private float alpha;
	private boolean fade;
	
	private Function onClick;
	
	private long emitTime;
	
	public Notification(String text, NVGColor color, boolean fade) {
		this.text = text;
		this.color = color;
		this.fade = fade;
		
		this.background = Colors.COLOR_BACKGROUND;
		this.alpha = NotificationsPanel.BACKGROUND_ALPHA;
	}

	public String getText() {
		return text;
	}

	public NVGColor getColor() {
		return color;
	}
	
	public NVGColor getCurrentAlphaColor() {
		return color.a(alpha);
	}
	
	public NVGColor getBackground() {
		return background;
	}
	
	public NVGColor getCurrentAlphaBackground() {
		return background.a(alpha);
	}
	
	public void setBackground(NVGColor color) {
		this.background = color;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
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
