package konradn24.tml.gfx.widgets.slots;

import konradn24.tml.Handler;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.states.State;

public class InventorySlot extends Slot {

	private Item item;
	private int amount;
	
	public final SlotAction[] ACTIONS = {
		new SlotAction("Equip", () -> {
			handler.getPlayer().getInventory().setCurrent(item);
			refreshActions();
		}),
		new SlotAction("Unequip", () -> {
			handler.getPlayer().getInventory().setCurrent(null);
			refreshActions();
		}),
		new SlotAction("Eat", () -> {}),
		new SlotAction("Drop", () -> handler.getPlayer().getInventory().drop(item, 1)),
		new SlotAction("Information", () ->  {
			MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Item info", item.getInfo(), handler);
			State.getState().getDialogsManager().showMessageBox(messageBox);
		}),
	};
	
	public final int[] ACTIONS_ATTRIBUTES = {
		0,
		0,
		Item.ATTRIB_EATABLE,
		-1,
		-1
	};
	
	public InventorySlot(Handler handler) {
		super(handler);
	}
	
	public InventorySlot(SlotMenu<?> menu, Handler handler) {
		super(menu);
		
		this.handler = handler;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item, int amount) {
		this.item = item;
		this.amount = amount;
		
		refreshActions();
	}
	
	public void refreshActions() {
		actions.clear();
		
		if(item != null && amount > 0) {
			this.invisible = false;
			this.icon = item.getTexture();
			this.bottomText.setContent("" + amount);
			this.tooltip.setContent(item.getDisplayedName());
			
			if(item.hasAttribute(Item.ATTRIB_EQUIPPABLE)) {
				if(handler.getPlayer().getInventory().currentItemEquals(item))
					actions.add(ACTIONS[1]);
				else actions.add(ACTIONS[0]);
			}
			
			for(int i = 2; i < ACTIONS.length; i++) {
				if(item.hasAttribute(ACTIONS_ATTRIBUTES[i]) || ACTIONS_ATTRIBUTES[i] == -1)
					actions.add(ACTIONS[i]);
			}
		} else {
			this.invisible = true;
		}
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
		
		if(item != null && amount > 0) {
			this.invisible = false;
			this.bottomText.setContent("" + amount);
		} else {
			this.invisible = true;
		}
	}
}
