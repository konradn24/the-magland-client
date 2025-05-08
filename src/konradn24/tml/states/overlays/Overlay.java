package konradn24.tml.states.overlays;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.states.State;

public abstract class Overlay {
	
	private static Overlay currentOverlay = null;
	
	public static void setOverlay(Overlay overlay) {
		overlay.onLoad();
		currentOverlay = overlay;
	}
	
	public static Overlay getOverlay() {
		return currentOverlay;
	}
	
	public static void clear() {
		currentOverlay = null;
	}
	
	public static boolean active() {
		return currentOverlay != null;
	}
	
	public static void renderIfActive(Graphics2D g) {
		if(active()) {
			currentOverlay.render(g);
		}
	}
	
	// CLASS

	public Overlay(Class<? extends State> stateClass, Handler handler) {
		this.stateClass = stateClass;
		this.handler = handler;
	}
	
	protected Class<? extends State> stateClass;
	protected Handler handler;
	
	public abstract void onLoad();
	public abstract void tick();
	public abstract void render(Graphics2D g);
}
