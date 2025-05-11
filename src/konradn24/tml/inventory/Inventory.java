package konradn24.tml.inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.entities.statics.Pouch;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.style.Style;
import konradn24.tml.gfx.style.StyleText;
import konradn24.tml.gfx.widgets.slots.InventorySlot;
import konradn24.tml.gfx.widgets.slots.SlotMenu;
import konradn24.tml.inventory.crafting.CraftingInterface;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.inventory.tools.Tool;

public class Inventory {
	
	public static final int DEFAULT_OPEN_KEY = KeyEvent.VK_E;
//	private static final byte ROWS = 8;
	private static final byte COLUMNS = 4;
	private static final int POS_X = 20;
	private static final int POS_Y = 20;
	private static final int SECTION_WIDTH = 350;
	private static final int SECTION_HEIGHT = 600;
	private static final int SECTION_SPACE = 40;
	private static final int CURRENT_ITEM_SLOT_SIZE = 100;
	
	private SlotMenu<InventorySlot> slotMenu;
	
	private Item selected;
	private Handler handler;
	
	private String layoutID;
	
	private Map<Item, Integer> items = new HashMap<>();
	private List<Tool> tools = new ArrayList<>();
	
	private CraftingInterface crafting;
	
	private boolean opened, changedSelected;
	private int keyToOpen;
	
	private List<Pouch> dropped = new ArrayList<>();
	
	public Inventory(String layoutID, Handler handler) {
		this.layoutID = layoutID;
		this.handler = handler;
		
		slotMenu = new SlotMenu<>(InventorySlot.class, POS_X, POS_Y, SECTION_WIDTH / COLUMNS, COLUMNS, handler);
		slotMenu.setFont(Presets.FONT_INVENTORY);
		slotMenu.setTextColor(Color.WHITE);
		slotMenu.getDropdown().setWidth(150);
		
		crafting = new CraftingInterface(this, Item.ATTRIB_HAND_CRAFTABLE);
		crafting.useGraphics("inventory_crafting", handler, POS_X + SECTION_WIDTH + SECTION_SPACE, POS_Y, SECTION_WIDTH, SECTION_HEIGHT);
		
		keyToOpen = DEFAULT_OPEN_KEY;
		
		Logging.info("Inventory initialized");
	}
	
	public void tick() {
		changedSelected = false;
		
		for(Map.Entry<Item, Integer> entry : items.entrySet()) {
			Item item = entry.getKey();
			int amount = entry.getValue();
			
			if(amount <= 0) {
				items.remove(item);
				refresh();
				
				return;
			}
			
			item.tick();
		}
		
		int i = 0;
		for(Tool tool : tools) {
			i++;
			
			if(tool.getDurability() <= 0) {
				tools.remove(i);
				refresh();
				
				if(selected == tool) {
					selected = null;
					changedSelected = true;
				}
				
				return;
			}
			
			tool.tick();
		}
		
		if(opened) {
			slotMenu.tick();
			crafting.tick();
		}

		checkInput();
	}
	
	public void render(Graphics2D g) {
		for(Item item : items.keySet()) item.render(g);
		
		if(opened) {
			g.setColor(Presets.COLOR_BACKGROUND);
			g.fillRect(20, 20, 350, 600);
			
			slotMenu.render(g);
			
//			if(Slot.getActionsItem() != null)
//				Slot.renderActions(g);
			
			crafting.render(g);
		}
		
		renderCurrentItemSlot(g);
	}
	
	public void refresh() {
		slotMenu.getSlotsLayout().forEach((slot, i) -> {
			((InventorySlot) slot).setItem(null, 0);
		});
		
		int i = 0;
		for(Map.Entry<Item, Integer> entry : getItemsAndTools().entrySet()) {
			if(i >= slotMenu.getSlotsAmount())
				break;
			
			((InventorySlot) slotMenu.getSlot(i)).setItem(entry.getKey(), entry.getValue());
			
			i++;
		}
		
		Logging.info("Inventory refreshed - " + i + "/" + slotMenu.getSlotsAmount() + " slots in use");
		
		crafting.refresh();
	}
	
	public void renderCooldown(Graphics2D g, int cooldownDuration, long cooldownEndTime) {
		int x = Style.centerX(CURRENT_ITEM_SLOT_SIZE);
		int y = Display.LOGICAL_HEIGHT - CURRENT_ITEM_SLOT_SIZE - 10;
		
		double yOffset = ((double) (cooldownEndTime - System.currentTimeMillis()) / cooldownDuration) * CURRENT_ITEM_SLOT_SIZE;
		
		g.setColor(Presets.COLOR_COOLDOWN);
		g.fillRect(x, y + CURRENT_ITEM_SLOT_SIZE - (int) yOffset, CURRENT_ITEM_SLOT_SIZE, (int) yOffset);
	}

	private void checkInput() {
		if(handler.getKeyManager().getKeysReleased()[keyToOpen]) {
			if(!opened) {
				opened = true;
				refresh();
				
				return;
			} else {
				opened = false;
				return;
			}
		}
	}
	
	// Current item slot rendering
	private void renderCurrentItemSlot(Graphics2D g) {
		int x = Style.centerX(CURRENT_ITEM_SLOT_SIZE);
		int y = Display.LOGICAL_HEIGHT - CURRENT_ITEM_SLOT_SIZE - 10;
		
		g.drawImage(Assets.currentItemSlot, x, y, CURRENT_ITEM_SLOT_SIZE, CURRENT_ITEM_SLOT_SIZE, null);
		
		if(selected != null) {
			g.drawImage(selected.getTexture(), x + 6, y + 6, CURRENT_ITEM_SLOT_SIZE - 12, CURRENT_ITEM_SLOT_SIZE - 12, null);
			
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
			g.setColor(Color.yellow);
			if(selected.isTool()) {
				Tool selectedTool = (Tool) selected;
				StyleText.drawCenteredString(g, selectedTool.getDurability() + " / " + selectedTool.getMaxDurability(), x + CURRENT_ITEM_SLOT_SIZE / 2, y - 20);
			}
		} else {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
			g.setColor(Color.black);
			StyleText.drawCenteredString(g, "[SPACE] to search ground...", x + CURRENT_ITEM_SLOT_SIZE / 2, y - 20);
		}
	}
	
	//GETTERS AND SETTERS
	
	public SlotMenu<InventorySlot> getSlotMenu() {
		return slotMenu;
	}

	public Map<Item, Integer> getItems() {
		return items;
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	
	public Map<Item, Integer> getItemsAndTools() {
		Map<Item, Integer> itemsAndTools = new HashMap<>();
		
		itemsAndTools.putAll(items);
		
		for(Tool tool : tools) {
			itemsAndTools.put(tool, 1);
		}
		
		return itemsAndTools;
	}
	
	public boolean add(Item item, int amount) {
		if(getWeight() + item.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add " + amount + " " + item.getName() + " - weight limit exceeded");
			return false;
		}
		
		if(item instanceof Tool)
			for(int i = 0; i < amount; i++) 
				tools.add((Tool) item);
		else if(items.putIfAbsent(item, amount) != null)
			items.replace(item, items.get(item) + amount);
		
		refresh();
		
		Logging.info("Inventory: added " + amount + " " + item.getName());
		
		return true;
	}
	
	public boolean add(Item item, int amount, boolean ignoreCarryLimit) {
		if(!ignoreCarryLimit && getWeight() + item.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add " + amount + " " + item.getName() + " - weight limit exceeded");
			return false;
		}
			
		if(item instanceof Tool)
			for(int i = 0; i < amount; i++) 
				tools.add((Tool) item);
		else if(items.putIfAbsent(item, amount) != null)
			items.replace(item, items.get(item) + amount);
		
		refresh();
		
		Logging.info("Inventory: added " + amount + " " + item.getName());
		
		return true;
	}
	
	public boolean add(Tool tool) {
		if(getWeight() + tool.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add 1 " + tool.getName() + " - weight limit exceeded");
			return false;
		}
		
		tools.add(tool);
		
		refresh();
		
		Logging.info("Inventory: added 1 " + tool.getName());
		
		return true;
	}
	
	public boolean add(Tool tool, boolean ignoreCarryLimit) {
		if(!ignoreCarryLimit && getWeight() + tool.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add 1 " + tool.getName() + " - weight limit exceeded");
			return false;
		}
		
		tools.add(tool);
		
		refresh();
		
		Logging.info("Inventory: added 1 " + tool.getName());
		
		return true;
	}
	
	public boolean add(Class<? extends Item> itemClass, int amount) {
		Item item = Item.getItem(itemClass);
		if(item == null) {
			Logging.error("Inventory: cannot add " + itemClass.getName() + " - item class not registered");
			return false;
		}
		
		if(getWeight() + item.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add " + amount + " " + item.getName() + " - weight limit exceeded");
			return false;
		}
		
		if(item instanceof Tool)
			for(int i = 0; i < amount; i++) 
				tools.add((Tool) item);
		else if(items.putIfAbsent(item, amount) != null)
			items.replace(item, items.get(item) + amount);
		
		refresh();
		
		Logging.info("Inventory: added " + amount + " " + item.getName());
		
		return true;
	}
	
	public boolean add(Class<? extends Item> itemClass, int amount, boolean ignoreCarryLimit) {
		Item item = Item.getItem(itemClass);
		if(item == null) {
			Logging.error("Inventory: cannot add " + itemClass.getName() + " - item class not registered");
			return false;
		}
		
		if(!ignoreCarryLimit && getWeight() + item.getWeight() > handler.getPlayer().getCarryLimit()) {
			Logging.info("Inventory: cannot add " + amount + " " + item.getName() + " - weight limit exceeded");
			return false;
		}
		
		if(item instanceof Tool)
			for(int i = 0; i < amount; i++) 
				tools.add((Tool) item);
		else if(items.putIfAbsent(item, amount) != null)
			items.replace(item, items.get(item) + amount);
		
		refresh();
		
		Logging.info("Inventory: added " + amount + " " + item.getName());
		
		return true;
	}
	
	public boolean remove(Item item, int amount) {
		if(item instanceof Tool && tools.contains(item)) {
			int i = 0;
			
			while(i < amount) {
				if(tools.remove((Tool) item)) {
					i++;
				} else {
					break;
				}
			}
			
			Logging.info("Inventory: removed " + i + " " + item.getName());
			
			return true;
		} else if(items.get(item) != null) {
			int currentAmount = items.get(item);
			int removeCount = currentAmount <= amount ? currentAmount : amount;
			
			if(removeCount == currentAmount) {
				items.remove(item);
			} else {
				items.replace(item, currentAmount - removeCount);
			}
			
			refresh();
			
			Logging.info("Inventory: removed " + removeCount + " " + item.getName());
			
			return true;
		} else {
			Logging.error("Inventory: cannot remove " + amount + " " + item.getName() + " - item not found in inventory");
			return false;
		}
	}
	
	public boolean remove(Tool tool) {
		if(tools.remove(tool)) {
			Logging.info("Inventory: removed 1 " + tool.getName());
			
			refresh();
			
			return true;
		}
		
		Logging.error("Inventory: cannot remove 1 " + tool.getName() + " - tool not found in inventory");
		
		return false;
	}
	
	public boolean drop(Item item, int amount) {
		boolean removed = remove(item, amount);
		
		Pouch pouch = (Pouch) handler.getWorld().getEntityManager().getEntityByPosition(Pouch.class, handler.getPlayer().getX(), handler.getPlayer().getY());
		
		if(pouch == null) {
			pouch = new Pouch(handler, handler.getPlayer().getX(), handler.getPlayer().getY());
			pouch.setItems(Map.of(item.getClass(), amount));
			
			handler.getWorld().getEntityManager().addEntity(pouch);
			
			return removed;
		}
		
		pouch.addItems(Map.of(item.getClass(), amount));
		
		return removed;
	}
	
	public boolean drop(Class<? extends Item> itemClass, int amount) {
		boolean removed = remove(Item.getItem(itemClass), amount);
		
		Pouch pouch = (Pouch) handler.getWorld().getEntityManager().getEntityByPosition(Pouch.class, handler.getPlayer().getX(), handler.getPlayer().getY());
		
		if(pouch == null) {
			pouch = new Pouch(handler, handler.getPlayer().getX(), handler.getPlayer().getY());
			pouch.setItems(Map.of(itemClass, amount));
			
			handler.getWorld().getEntityManager().addEntity(pouch);
			
			return removed;
		}
		
		pouch.addItems(Map.of(itemClass, amount));
		
		return removed;
	}
	
	public float getWeight() {
		float weight = 0;
		
		for(Map.Entry<Item, Integer> entry : items.entrySet())
			weight += entry.getKey().getWeight() * entry.getValue();
		
		for(Tool tool : tools)
			weight += tool.getWeight();
		
		return weight;
	}

	public boolean currentItemEquals(Item item) {
		if(selected == null)
			return false;
		
		return selected.equals(item);
	}
	
	public Item getCurrentItem() {
		return selected;
	}

	public void setCurrent(Item current) {
		this.selected = current;
		changedSelected = true;
	}
	
	public boolean isCurrent(Class<? extends Item> itemClass) {
		if(itemClass == null && selected == null)
			return true;
		else if(itemClass == null || selected == null)
			return false;
		
		return selected.equals(Item.getItem(itemClass));
	}

	public String getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	public boolean isOpened() {
		return opened;
	}

	public boolean isChangedCurrent() {
		return changedSelected;
	}

	public CraftingInterface getCrafting() {
		return crafting;
	}

	public List<Pouch> getDropped() {
		return dropped;
	}
}
