package konradn24.tml.states;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.widgets.msgbox.DialogsManager;
import konradn24.tml.states.overlays.Overlay;


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
			
			currentState.onClose();
		}
		
		state.handler.getGame().clearBackground();
		
		Overlay.clear();
		state.onLoad();
		
		currentState = state;
	}
	
	public static void backState() {
		if(history.size() <= 0 || currentState.noHistory)
			return;
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		Overlay.clear();
		lastState.onLoad();
		
		currentState = lastState;
		history.remove(history.size() - 1);
	}
	
	public static State getState(){
		return currentState;
	}
	
	//CLASS
	
	protected Handler handler;
	protected DialogsManager dialogsManager;
	
	protected boolean noHistory;
	
	public State(Handler handler){
		this.handler = handler;
		
		dialogsManager = new DialogsManager(handler);
	}
	
	public void onLoad() {}
	public abstract void tick();
	public abstract void render(Graphics2D g);
	public void onClose() {}
	
	public void onBack() {
		backState();
	}
	
	public DialogsManager getDialogsManager() {
		return dialogsManager;
	}
}
