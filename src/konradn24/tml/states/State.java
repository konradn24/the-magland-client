package konradn24.tml.states;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.widgets.msg.DialogsManager;


public abstract class State {

	private static State currentState = null;
	private static List<State> history = new ArrayList<>();
	
	public static void setState(State state) {
		Logging.info("State change: " + (currentState != null ? currentState.getClass().getSimpleName() : "null") + " -> " + state.getClass().getSimpleName());
		
		if(currentState != null)
			history.add(currentState);
		
		currentState = state;
	}
	
	public static void backState() {
		if(history.size() <= 0)
			return;
		
		State lastState = history.get(history.size() - 1);
		
		Logging.info("State change (back): " + (currentState.getClass().getSimpleName().toString()) + " -> " + lastState.getClass().getSimpleName().toString());
		
		currentState = lastState;
		history.remove(history.size() - 1);
	}
	
	public static State getState(){
		return currentState;
	}
	
	//CLASS
	
	protected Handler handler;
	protected DialogsManager dialogsManager;
	
	public State(Handler handler){
		this.handler = handler;
		
		dialogsManager = new DialogsManager(handler);
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
	
	public void onBack() {
		backState();
	}
	
	public DialogsManager getDialogsManager() {
		return dialogsManager;
	}
}
