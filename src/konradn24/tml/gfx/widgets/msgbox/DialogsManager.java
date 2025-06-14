package konradn24.tml.gfx.widgets.msgbox;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;

public class DialogsManager {

	private List<MessageBox> messageBoxes = new ArrayList<>();
	private List<MessageBox> removeQueue = new ArrayList<>();
	
	private Handler handler;
	
	public DialogsManager(Handler handler) {
		this.handler = handler;
	}
	
	public void tick() {
		if(!messageBoxes.isEmpty() && !handler.getGame().isLocked())
			handler.getGame().setLocked(true);
		else if(messageBoxes.isEmpty() && handler.getGame().isLocked())
			handler.getGame().setLocked(false);
		
		if(!messageBoxes.isEmpty())
			messageBoxes.get(0).tick();
		
		for(MessageBox toRemove : removeQueue) {
			int index = messageBoxes.indexOf(toRemove);
			messageBoxes.remove(index);
			
			Logging.info("Removed MessageBox (ID: " + index + ") - title: " + toRemove.getTitle());
		}
		
		removeQueue.clear();
	}
	
	public void render(Graphics2D g) {
		if(!messageBoxes.isEmpty())
			messageBoxes.get(0).render(g);
	}
	
	public void showMessageBox(MessageBox messageBox) {
		messageBoxes.add(messageBox);
		
		Logging.info("Added MessageBox (ID: " + (messageBoxes.size() - 1) + ") - title: " + messageBox.getTitle());
	}
	
	public void closeMessageBox(MessageBox messageBox) {
		int index = messageBoxes.indexOf(messageBox);
		if(index < 0) {
			Logging.warning("Cannot remove MessageBox - provided object (title: " + messageBox.getTitle() + ") does not exist");
			return;
		}
		
		messageBoxes.remove(messageBox);
		
		Logging.info("Removed MessageBox (ID: " + index + ") - title: " + messageBox.getTitle());
	}
	
	public void closeMessageBox(int index) {
		if(index < 0 || index >= messageBoxes.size()) {
			Logging.warning("Cannot remove MessageBox - this object of ID " + index + " does not exist");
			return;
		}
		
		MessageBox messageBox = messageBoxes.get(index);
		messageBoxes.remove(index);
		
		Logging.info("Removed MessageBox (ID: " + index + ") - title: " + messageBox.getTitle());
	}
	
	public void queueCloseMessageBox(MessageBox messageBox) {
		int index = messageBoxes.indexOf(messageBox);
		if(index < 0) {
			Logging.warning("Cannot queue MessageBox removal - provided object (title: " + messageBox.getTitle() + ") does not exist");
			return;
		}
		
		removeQueue.add(messageBox);
		
		Logging.info("Queued MessageBox removal (ID: " + index + ") - title: " + messageBox.getTitle());
	}
}
