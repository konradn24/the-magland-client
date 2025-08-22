package konradn24.tml.inventory;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.gui.graphics.widgets.slots.Slot;
import konradn24.tml.gui.graphics.widgets.slots.SlotMenu;
import konradn24.tml.items.Item;
import konradn24.tml.states.State;
import konradn24.tml.utils.Logging;

public class InventorySlot extends Slot {

	private Inventory inventory;
	private InventoryProperty property;
	
	public final SlotAction[] ACTIONS = {
		new SlotAction("Take one", () -> {
			inventory.move(property, 1, handler.getPlayer().getInventory());
		}),
		
		new SlotAction("Equip", () -> {
			inventory.select(property);
			menu.refreshActions();
		}),
		
		new SlotAction("Unequip", () -> {
			inventory.resetSelected();
			menu.refreshActions();
		}),
		
		new SlotAction("Eat", () -> {}),
		
		new SlotAction("Drop", () -> {
			inventory.drop(property, 1);
		}),
		
		new SlotAction("Information", () ->  {
			MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Item info", property.item.getInfo(), handler);
			State.showMessageBox(messageBox);
		}),
	};
	
	public final int[] ACTIONS_ATTRIBUTES = {
		-1,
		0,
		0,
		Item.ATTRIB_EATABLE,
		0,
		0
	};
	
	public InventorySlot(Handler handler) {
		super(handler);
	}
	
	public InventorySlot(SlotMenu<?> menu, Inventory inventory, Handler handler) {
		super(menu);
		
		this.inventory = inventory;
		this.handler = handler;
	}
	
	public void update(float dt) {
		super.update(dt);
		
		if(property == null)
			return;
		
		if(isOn() && menu.getSelectedSlot() == -1) {
			handler.getMouseManager().enableTooltip(property.item.getDisplayedName());
		}
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public InventoryProperty getProperty() {
		return property;
	}

	public void setProperty(InventoryProperty property) {
		this.property = property;
		
		refreshActions();
	}
	
	public void setAmount(int amount) {
		if(property == null) {
			Logging.error("Inventory INV_" + inventory.hashCode() + "_SLOT_" + index + ": cannot set amount - property is null");
			return;
		}
		
		property.amount = amount;
		
		refreshActions();
	}
	
	public void refreshActions() {
		primaryAction = null;
		actions.clear();
		
		if(property != null && property.amount > 0) {
			this.invisible = false;
			this.icon = property.item.getTexture();
			this.bottomText.setContent("" + property.amount);
			
			if(inventory.isStorage()) {
				primaryAction = ACTIONS[0].getCallback();
				actions.add(ACTIONS[0]);
			} else if(property.item.hasAttribute(Item.ATTRIB_EQUIPPABLE)) {
				if(inventory.isSelected(property)) {
					actions.add(ACTIONS[2]);
				} else {
					primaryAction = ACTIONS[1].getCallback();
					actions.add(ACTIONS[1]);
				}
			}
			
			for(int i = 3; i < ACTIONS.length; i++) {
				if(property.item.hasAttribute(ACTIONS_ATTRIBUTES[i]) || ACTIONS_ATTRIBUTES[i] == -1)
					actions.add(ACTIONS[i]);
			}
		} else {
			this.invisible = true;
		}
	}
}