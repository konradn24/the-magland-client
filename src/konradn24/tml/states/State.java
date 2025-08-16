package konradn24.tml.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.widgets.msgbox.DialogsManager;
import konradn24.tml.utils.Logging;

public abstract class State {

	private static State currentState = null;
	private static List<State> history = new ArrayList<>();
	private static Map<String, String> payload = new HashMap<>();
	
	public static void setState(State state, Map<String, String> payload) {
		Logging.info("State change: " + (currentState != null ? currentState.getClass().getSimpleName() : "null") + " -> " + state.getClass().getSimpleName());
		
		if(currentState != null) {
			if(currentState.noHistory) {
				history.clear();
			} else {
				history.add(currentState);
			}
			
			currentState.cleanup();
		}
		
		if(payload == null) {
			State.payload.clear();
		} else {
			State.payload = payload;
		}
		
		currentState = state;
		
		Overlay.clear();
		state.init();
	}
	
	public static boolean backState() {
		if(history.size() <= 0 || currentState.noHistory)
			return false;
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		currentState.cleanup();
		currentState = lastState;
		
		Overlay.clear();
		lastState.init();
		
		history.remove(history.size() - 1);
		
		return true;
	}
	
	public static void backState(State defaultState) {
		if(history.size() <= 0 || currentState.noHistory) {
			currentState = defaultState;
			
			Overlay.clear();
			defaultState.init();
			
			return;
		}
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		currentState.cleanup();
		currentState = lastState;
		
		Overlay.clear();
		lastState.init();
		
		history.remove(history.size() - 1);
	}
	
	public static State getState(){
		return currentState;
	}
	
	//CLASS
	
	protected Handler handler;
	protected DialogsManager dialogsManager;
	
	protected boolean noHistory;
	
	public State(Handler handler) {
		this.handler = handler;
		
		dialogsManager = new DialogsManager(handler);
	}
	
	public abstract void init();
	public abstract void update(float dt);
	public abstract void render();
	public abstract void renderGUI(long vg);
	public abstract void cleanup();
	
	public void onBack() {
		backState();
	}
	
	public DialogsManager getDialogsManager() {
		return dialogsManager;
	}

	public boolean isNoHistory() {
		return noHistory;
	}
}
