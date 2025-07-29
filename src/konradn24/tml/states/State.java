package konradn24.tml.states;

import java.util.ArrayList;
import java.util.List;

//import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
//import konradn24.tml.gfx.widgets.msgbox.DialogsManager;
//import konradn24.tml.states.overlays.Overlay;

public abstract class State {

	private static State currentState = null;
	private static List<State> history = new ArrayList<>();
	
	public static void setState(State state) {
		Logging.info("State change: " + (currentState != null ? currentState.getClass().getSimpleName() : "null") + " -> " + state.getClass().getSimpleName());
		
		if(currentState != null) {
			if(currentState.noHistory) {
				history.clear();
			} else {
				history.add(currentState);
			}
			
			currentState.cleanup();
		}
		
		currentState = state;
		
//		Overlay.clear();
		state.init();
	}
	
	public static boolean backState() {
		if(history.size() <= 0 || currentState.noHistory)
			return false;
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		currentState.cleanup();
		currentState = lastState;
		
//		Overlay.clear();
		lastState.init();
		
		history.remove(history.size() - 1);
		
		return true;
	}
	
	public static void backState(State defaultState) {
		if(history.size() <= 0 || currentState.noHistory) {
			currentState = defaultState;
			
//			Overlay.clear();
			defaultState.init();
			
			return;
		}
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		currentState.cleanup();
		currentState = lastState;
		
//		Overlay.clear();
		lastState.init();
		
		history.remove(history.size() - 1);
	}
	
	public static State getState(){
		return currentState;
	}
	
	//CLASS
	
//	protected Handler handler;
//	protected DialogsManager dialogsManager;
	
	protected boolean noHistory;
	
	public State(/*Handler handler*/) {
//		this.handler = handler;
//		
//		dialogsManager = new DialogsManager(handler);
	}
	
	public void init() {}
	public abstract void update(float dt);
	public abstract void render();
	public abstract void renderGUI(long vg);
	public void cleanup() {};
	
	public void onBack() {
		backState();
	}
	
//	public DialogsManager getDialogsManager() {
//		return dialogsManager;
//	}

	public boolean isNoHistory() {
		return noHistory;
	}
}
