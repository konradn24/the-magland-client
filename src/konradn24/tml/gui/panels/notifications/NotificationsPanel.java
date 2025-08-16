package konradn24.tml.gui.panels.notifications;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;
import konradn24.tml.gui.graphics.widgets.ScrollPanel;

public class NotificationsPanel {

	private static final float X = Display.x(.75f);
	private static final float Y = Display.y(.10f);
	private static final float WIDTH = Display.x(.20f);
	private static final float HEIGHT = Display.y(.70f);
	private static final float NOTIFICATION_HEIGHT = Display.y(.07f);
	
	public static final int SHOW_DURATION = 6000;
	public static final int FADE_DURATION = 2000;
	public static final int FADE_START = SHOW_DURATION - FADE_DURATION;
	public static final float BACKGROUND_ALPHA = 192;
	
	private List<Notification> queue;
	
	private ScrollPanel scrollPanel;
	
	public NotificationsPanel(Handler handler) {
		queue = new ArrayList<Notification>();
		scrollPanel = new ScrollPanel(X, Y, WIDTH, HEIGHT, handler);
	}
	
	public void update(float dt) {
		for(int i = queue.size() - 1; i >= 0; i--) {
			Notification notification = queue.get(i);
			
			if(notification == null) {
				remove(i);
				continue;
			}
			
			if(!notification.isFade()) {
				continue;
			}
			
			int notificationTimeElapsed = (int) (System.currentTimeMillis() - notification.getEmitTime());
			
			if(notificationTimeElapsed >= SHOW_DURATION) {
				remove(i);
				continue;
			} else if(notificationTimeElapsed >= SHOW_DURATION - FADE_DURATION) {
				float alphaFactor = 1 - (float) (notificationTimeElapsed - FADE_START) / FADE_DURATION;
				notification.setAlpha((int) (BACKGROUND_ALPHA * alphaFactor));
			}
		}
		
		scrollPanel.update(dt);
	}
	
	public void renderGUI(long vg) {
		scrollPanel.renderGUIArea(vg, () -> {
			int notificationCount = 0;
			for(int i = queue.size() - 1; i >= 0; i--) {
				Notification notification = queue.get(i);
				
				if(notification == null) {
					continue;
				}
				
				float y = notificationCount * NOTIFICATION_HEIGHT;
				
				nvgBeginPath(vg);
				nvgRect(vg, 0, y, WIDTH, NOTIFICATION_HEIGHT);
				nvgFillColor(vg, notification.getCurrentAlphaBackground());
				nvgFill(vg);
				
				nvgFillColor(vg, notification.getCurrentAlphaColor());
				TextRenderer.renderString(vg, notification.getText(), 0, y, WIDTH, NOTIFICATION_HEIGHT, AlignX.CENTER, AlignY.CENTER, Overflow.WRAP);
				
				notificationCount++;
			}
		});
	}
	
	public void add(Notification notification) {
		notification.setEmitTime(System.currentTimeMillis());
		
		queue.add(notification);
		scrollPanel.setScrollContentHeight(NOTIFICATION_HEIGHT * queue.size());
	}
	
	public void add(String text) {
		Notification notification = new Notification(text, Colors.COLOR_TEXT, true);
		notification.setEmitTime(System.currentTimeMillis());
		
		add(notification);
	}
	
	public void add(String text, NVGColor color) {
		Notification notification = new Notification(text, color, true);
		notification.setEmitTime(System.currentTimeMillis());
		
		add(notification);
	}
	
	public void addPermanent(String text) {
		Notification notification = new Notification(text, Colors.COLOR_TEXT, false);
		notification.setEmitTime(System.currentTimeMillis());
		
		add(notification);
	}
	
	public void addPermanent(String text, NVGColor color) {
		Notification notification = new Notification(text, color, false);
		notification.setEmitTime(System.currentTimeMillis());
		
		add(notification);
	}
	
	public void remove(Notification notification) {
		queue.remove(notification);
		scrollPanel.setScrollContentHeight(NOTIFICATION_HEIGHT * queue.size());
	}
	
	public void remove(int index) {
		queue.remove(index);
		scrollPanel.setScrollContentHeight(NOTIFICATION_HEIGHT * queue.size());
	}
}
