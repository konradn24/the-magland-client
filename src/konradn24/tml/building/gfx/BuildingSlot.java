package konradn24.tml.building.gfx;

import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.entities.buildings.Building;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.gfx.widgets.slots.Slot;
import konradn24.tml.gfx.widgets.slots.SlotMenu;
import konradn24.tml.states.State;

public class BuildingSlot extends Slot {

	private Building building;
	private Handler handler;
	
	public final SlotAction[] ACTIONS = {
		new SlotAction(new AdvancedLabel("Required resources"), () -> {
			// TODO show MessageBox
		}),
		new SlotAction(new AdvancedLabel("Information"), () ->  {
			MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Item info", building.getInfo(), handler);
			State.getState().getDialogsManager().showMessageBox(messageBox);
		}),
	};
	
	public BuildingSlot(SlotMenu menu, Handler handler) {
		super(menu);
		
		this.handler = handler;
		this.actions = List.of(ACTIONS);
		
		this.setInvisible(true);
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building, boolean locked) {
		this.building = building;
		
		this.setIcon(building.getTexture());
		this.setBottomTextContent(building.getDisplayedName());
		this.setInvisible(false);
		
//		if(locked)
	}
}
