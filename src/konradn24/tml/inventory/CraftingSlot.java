package konradn24.tml.inventory;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.gui.graphics.widgets.slots.Slot;
import konradn24.tml.gui.graphics.widgets.slots.SlotMenu;
import konradn24.tml.states.State;

public class CraftingSlot extends Slot {

	private CraftingRecipe recipe;
	private int amount;
	
	public final SlotAction[] ACTIONS = {
		new SlotAction("Craft One", () -> {
			handler.getPlayer().getInventory().getCrafting().craft(recipe.getId(), 1);
			refreshActions();
		}),
		new SlotAction("Craft", () -> {
			handler.getPlayer().getInventory().getCrafting().craft(recipe.getId(), Integer.MAX_VALUE);
			refreshActions();
		}),
		new SlotAction("Information", () ->  {
			MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Item info", recipe.getResultProperties().get(0).item.getInfo(), handler);
			State.getState().getDialogsManager().showMessageBox(messageBox);
		}),
	};
	
	public CraftingSlot(Handler handler) {
		super(handler);
	}
	
	public CraftingSlot(SlotMenu<?> menu, Handler handler) {
		super(menu);
		
		this.handler = handler;
	}
	
	public void update(float dt) {
		super.update(dt);
		
		if(isOn() && menu.getSelectedSlot() == -1 && recipe != null) {
			String tooltip = "";
			
			for(InventoryProperty property : recipe.getRequiredProperties()) {
				tooltip += property.amount + "x " + property.item.getDisplayedName() + " + ";
			}
			
			tooltip = tooltip.substring(0, tooltip.length() - 2) + "-> ";
			
			for(InventoryProperty property : recipe.getResultProperties()) {
				tooltip += property.amount + "x " + property.item.getDisplayedName() + " + ";
			}
			
			tooltip = tooltip.substring(0, tooltip.length() - 3);
			
			handler.getMouseManager().enableTooltip(tooltip);
		}
	}

	public CraftingRecipe getCraftingRecipe() {
		return recipe;
	}

	public void setCraftingRecipe(CraftingRecipe recipe) {
		this.recipe = recipe;
		
		refreshActions();
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
		
		refreshActions();
	}

	public void refreshActions() {
		primaryAction = null;
		actions.clear();
		
		if(recipe == null) {
			this.invisible = true;
			return;
		}
		
		this.invisible = false;
		this.icon = recipe.getResultProperties().get(0).item.getTexture();
		this.bottomText.setContent(handler.getGameRules().infiniteResources ? "∞" : Integer.toString(amount));
		
		if(amount > 0) {
			primaryAction = ACTIONS[0].getCallback();
			actions.add(ACTIONS[1]);
		}
		
		actions.add(ACTIONS[2]);
	}
}