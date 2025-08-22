package konradn24.tml.states;

import konradn24.tml.Handler;

public abstract class Overlay {
	
	private static Overlay currentOverlay = null;
	
	public static void setOverlay(Overlay overlay) {
		if(currentOverlay != null) {
			currentOverlay.cleanup();
		}
		
		currentOverlay = overlay;
		currentOverlay.init();
	}
	
	public static Overlay getOverlay() {
		return currentOverlay;
	}
	
	public static void clear() {
		currentOverlay = null;
	}
	
	public static boolean isActive() {
		return currentOverlay != null;
	}
	
	// CLASS

	public Overlay(Class<? extends State> stateClass, Handler handler) {
		this.stateClass = stateClass;
		this.handler = handler;
	}
	
	protected Class<? extends State> stateClass;
	protected Handler handler;
	
	public abstract void init();
	public abstract void update(float dt);
	public abstract void renderGUI(long vg);
	public abstract void cleanup();
}
