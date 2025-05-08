package konradn24.tml.inventory;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.states.State;

@Deprecated
public class Slot {

	private static final int DEFAULT_WIDTH = 128;
	private static final int DEFAULT_HEIGHT = 96;
	private static final int ACTIONS_MARGIN_X = 4;
	private static final int ACTIONS_MARGIN_Y = 2;
	
	private static int actionsX, actionsY, actionsWidth, actionsHeight;
	private static boolean actionsInitialized, actionsItemChanged;
	private static Item actionsItem;
	private static Inventory inventory;
	
	private static Handler handler;
	
	private static AdvancedLabel[] actions = {
			new AdvancedLabel("Equip"),
			new AdvancedLabel("Unequip"),
			new AdvancedLabel("Eat"),
			new AdvancedLabel("Drop"),
			new AdvancedLabel("Information")
	};
	
	private static int[] actionsAttributes = {
			Item.ATTRIB_EQUIPPABLE,
			Item.ATTRIB_EQUIPPABLE,
			Item.ATTRIB_EATABLE,
			-1,
			-1
	};
	
	private static boolean[] actionsActive = { false, false, false, false, false };
	
	private static Runnable[] actionsCallbacks = {
			() -> inventory.setCurrent(actionsItem),
			() -> inventory.setCurrent(null),
			() -> Logging.warning("Eating in development..."),
			() -> inventory.remove(actionsItem, 1),
			() -> {
				MessageBox messageBox = new MessageBox(MessageBox.TYPE_OK, "Item info", actionsItem.getInfo(), handler);
				State.getState().getDialogsManager().showMessageBox(messageBox);
			}
	};
	
	private int index, columns;
	
	private Item item;
	private int amount;
	private int x, y, width, height;
	private boolean hover;
	
	public Slot(Inventory inventory, int index, byte columns, Handler handler) {
		if(Slot.handler == null || (Slot.inventory != null && !Slot.inventory.equals(inventory))) {
			Slot.inventory = inventory;
			Slot.handler = handler;
		}
		
		this.index = index;
		this.columns = columns;
		
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
		
		if(!actionsInitialized) {
			for(int i = 0; i < actions.length; i++) {
				actions[i].setFont(Presets.FONT_INVENTORY.deriveFont(20f));
				actions[i].setColor(Color.WHITE);
				actions[i].setMarginX(ACTIONS_MARGIN_X);
				actions[i].setMarginY(ACTIONS_MARGIN_Y);
			}
		}
		
		refreshPosition();
	}
	
	public void tick() {
		if(item == null)
			return;
		
		hover = handler.getMouseManager().isOn(x, y, width, height);
		boolean pressedAnyMouseButton = handler.getMouseManager().isLeftPressed() || handler.getMouseManager().isRightPressed();
		boolean releasedAnyButton = handler.getMouseManager().isLeftReleased() || handler.getMouseManager().isRightReleased();
		
		if(actionsItem == null) {
			if(actionsItemChanged) {
				if(!pressedAnyMouseButton) actionsItemChanged = false;
			} else {
				boolean slotHover = handler.getMouseManager().isOn(x, y, width, height);
				
				if(slotHover) {
					if(handler.getMouseManager().isLeftReleased() && item.hasAttribute(Item.ATTRIB_EQUIPPABLE) && !inventory.currentItemEquals(item))
						inventory.setCurrent(item);
					
					if(handler.getMouseManager().isRightReleased()) {
						actionsItem = item;
						actionsItemChanged = true;
						positionActions();
					}
				}
			}
		}
		
		if(actionsItem != null) {
			if(actionsItemChanged) {
				if(!pressedAnyMouseButton) actionsItemChanged = false;
			} else {
				for(int i = 0; i < actions.length; i++) {
					if(!actionsActive[i])
						continue;
					
					int y = actions[i].getY() + ACTIONS_MARGIN_Y;
					
					if(handler.getMouseManager().isOn(actionsX, y, actionsWidth, actions[i].getHeight())) {
						handler.getGame().getDisplay().setCursor(Cursor.HAND_CURSOR);
						
						if(handler.getMouseManager().isLeftReleased())
							actionsCallbacks[i].run();
					}
				}
				
				if(releasedAnyButton) {
					actionsItem = null;
					actionsItemChanged = true;
				}
			}
		}
	}
	
	public void render(Graphics2D g) {
		if(item == null)
			return;
		
		int imgSize = (int) (width / 2.5);
		int imgX = x + (width - imgSize) / 2;
		int imgY = y + (height - imgSize) / 3;
		
		//texture
		g.drawImage(item.getTexture(), imgX, imgY, imgSize, imgSize, null);
			
		//info
		int strX = x + 3;
		int strY = y + height - 3;
		
		g.setFont(Presets.FONT_INVENTORY.deriveFont(20f));
		g.setColor(Color.white);
		
		if(item.isTool())
			g.drawString(((Tool) item).getDurability() + " / " + ((Tool) item).getMaxDurability(), strX, strY);
		else
			g.drawString("" + amount, strX, strY);
		
		if(hover) {
			g.setColor(Presets.COLOR_LIGHT);
			g.fillRect(x, y, width, height);
		}
	}
	
	public static void renderActions(Graphics2D g) {
		g.setColor(Presets.COLOR_BACKGROUND);
		g.fillRect(actionsX, actionsY, actionsWidth, actionsHeight);
		
		int counter = 0;
		for(int i = 0; i < actions.length; i++) {
			if(actionsAttributes[i] != -1 && !actionsItem.hasAttribute(actionsAttributes[i]))
				continue;
			
			if(i == 0 && inventory.getCurrentItem() != null && inventory.getCurrentItem().equals(actionsItem))
				continue;
			else if(i == 1 && inventory.getCurrentItem() == null)
				continue;
			else if(i == 1 && inventory.getCurrentItem() != null && !inventory.getCurrentItem().equals(actionsItem))
				continue;
			
			int y = actions[i].getY() + ACTIONS_MARGIN_Y;
			
			if(counter > 0) {
				g.setColor(Color.gray);
				g.drawLine(actionsX, y, actionsX + actionsWidth - 1, y);
			}
			
			actions[i].render(g, handler);
			
			if(handler.getMouseManager().isOn(actionsX, y, actionsWidth, actions[i].getHeight())) {
				g.setColor(Presets.COLOR_BACKGROUND);
				g.fillRect(actionsX, y + 1, actionsWidth, actions[i].getHeight() - 1);
			}
			
			counter++;
		}
	}
	
	public void refreshPosition() {
		x = handler.getStyle().positionX(inventory.getLayoutID(), index % columns);
		y = handler.getStyle().positionY(inventory.getLayoutID(), index / columns);
	}
	
	private void positionActions() {
		actionsX = handler.getMouseManager().getMouseX();
		actionsY = handler.getMouseManager().getMouseY();
		
		int width = 0, height = 0;
		
		for(int i = 0; i < actions.length; i++) {
			if(actionsAttributes[i] != -1 && !item.hasAttribute(actionsAttributes[i])) {
				actionsActive[i] = false;
				continue;
			}
			
			if(i == 0 && inventory.getCurrentItem() != null && inventory.getCurrentItem().equals(actionsItem)) {
				actionsActive[i] = false;
				continue;
			} else if(i == 1 && inventory.getCurrentItem() == null) {
				actionsActive[i] = false;
				continue;
			} else if(i == 1 && inventory.getCurrentItem() != null && !inventory.getCurrentItem().equals(actionsItem)) {
				actionsActive[i] = false;
				continue;
			}
			
			actionsActive[i] = true;
			
			actions[i].setX(actionsX);
			actions[i].setY(actionsY + height - ACTIONS_MARGIN_Y);
			
			actions[i].calculateSize(handler, actions[i].getFont());
			
			width = Math.max(actions[i].getWidth(), width);
			height += actions[i].getHeight();
		}
		
		actionsWidth = width + ACTIONS_MARGIN_X * 2;
		actionsHeight = height;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public void setItem(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public static AdvancedLabel[] getActions() {
		return actions;
	}

	public static int[] getActionsAttributes() {
		return actionsAttributes;
	}

	public static Item getActionsItem() {
		return actionsItem;
	}

	public static void setActionsItem(Item actionsItem) {
		Slot.actionsItem = actionsItem;
	}

	public boolean isHover() {
		return hover;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		
		refreshPosition();
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
