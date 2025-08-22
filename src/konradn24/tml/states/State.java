package konradn24.tml.states;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.widgets.msgbox.DialogsManager;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.utils.Logging;

public abstract class State {

	private static List<State> states = new ArrayList<>();
	
	private static State currentState = null;
	private static List<State> history = new ArrayList<>();
	private static Map<String, String> payload = new HashMap<>();
	
	public static void setState(Class<? extends State> stateClass, Map<String, String> payload) {
		State state = getState(stateClass);
		
		if(state == null) {
			Logging.error("State change: failed to change states - state " + stateClass.getSimpleName() + " not found");
			return;
		}
		
		Logging.info("State change: " + (currentState != null ? currentState.getClass().getSimpleName() : "null") + " -> " + stateClass.getSimpleName());
		
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
			State.payload = new HashMap<>(payload);
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
	
	public static void backState(Class<? extends State> defaultStateClass) {
		if(history.size() <= 0 || currentState.noHistory) {
			State defaultState = getState(defaultStateClass);
			
			if(defaultState == null) {
				Logging.error("State change (back): failed to change states - state " + defaultStateClass.getSimpleName() + " not found");
				return;
			}
			
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
	
	public static void register(Class<? extends State> stateClass, Handler handler) throws Exception {
		try {
			State state = stateClass.getConstructor(Handler.class).newInstance(handler);
			states.add(state);
			
			Logging.info("State: registered " + stateClass.getSimpleName());
		} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logging.error("State: failed to register " + stateClass.getSimpleName());
			Logging.error(e);
			
			throw e;
		}
	}
	
	public static State getState(Class<? extends State> stateClass) {
		State state = states.stream().filter(s -> s.classEquals(stateClass)).findFirst().orElse(null);
		
		if(state == null) {
			Logging.error("State: " + stateClass.getSimpleName() + " not found in registered states");
		}
		
		return state;
	}
	
	public static State getCurrentState() {
		return currentState;
	}
	
	public static boolean isCurrentState(Class<? extends State> stateClass) {
		if(currentState == null) {
			return false;
		}
		
		return currentState.classEquals(stateClass);
	}
	
	public static void showMessageBox(MessageBox messageBox) {
		if(currentState == null) {
			Logging.error("State: cannot show message box - current state is null");
			return;
		}
		
		currentState.dialogsManager.showMessageBox(messageBox);
	}
	
	public static void closeMessageBox(MessageBox messageBox) {
		if(currentState == null) {
			Logging.error("State: cannot close message box - current state is null");
			return;
		}
		
		currentState.dialogsManager.closeMessageBox(messageBox);
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
	
	public boolean classEquals(Class<? extends State> stateClass) {
		return this.getClass().equals(stateClass);
	}
}
