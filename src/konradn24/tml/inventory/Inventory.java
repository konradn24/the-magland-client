package konradn24.tml.inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.statics.Pouch;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.widgets.slots.SlotMenu;
import konradn24.tml.inventory.crafting.CraftingInterface;
import konradn24.tml.inventory.gfx.InventorySlot;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.states.GameState;

public class Inventory {
	
	public static final int DEFAULT_OPEN_KEY = KeyEvent.VK_E;
	private static final byte ROWS = 8;
	private static final byte COLUMNS = 3;
	private static final int POS_X = 20;
	private static final int POS_Y = 20;
	private static final int SECTION_WIDTH = 350;
	private static final int SECTION_HEIGHT = 600;
	private static final int SECTION_SPACE = 40;
//	private static final int SLOT_WIDTH = SECTION_WIDTH / COLUMNS;
//	private static final int SLOT_HEIGHT = SECTION_HEIGHT / ROWS;
	
	private SlotMenu slotMenu;
	
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
		
		handler.getStyle().addLayout(GameState.class, layoutID, POS_X, POS_Y, SECTION_WIDTH, SECTION_HEIGHT, ROWS, COLUMNS);
		
		slotMenu = new SlotMenu(layoutID + "_slots", handler, COLUMNS);
		slotMenu.setFixedIconsWidth(48);
		slotMenu.setFixedIconsHeight(48);
		slotMenu.setFont(Presets.FONT_INVENTORY);
		slotMenu.setTextColor(Color.WHITE);
		slotMenu.setPosition(POS_X, POS_Y, SECTION_WIDTH, SECTION_HEIGHT);
		
		InventorySlot[] slots = new InventorySlot[ROWS * COLUMNS];
		for(int i = 0; i < ROWS * COLUMNS; i++)
			slots[i] = new InventorySlot(slotMenu, handler);
		
		slotMenu.addSlots(slots);
		
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
	
	public void render(Graphics g) {
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
		slotMenu.getSlots().forEach(slot -> ((InventorySlot) slot).setItem(null, 0));
		
		int i = 0;
		for(Map.Entry<Item, Integer> entry : getItemsAndTools().entrySet()) {
			if(i >= slotMenu.getSlotsAmount())
				break;
			
			((InventorySlot) slotMenu.getSlot(i)).setItem(entry.getKey(), entry.getValue());
			
			i++;
		}
		
		Logging.info("Inventory (layoutID: " + layoutID + ") refreshed - " + i + "/" + slotMenu.getSlotsAmount() + " slots in use");
		
		crafting.refresh();
	}
	
	public void renderCooldown(Graphics g, int cooldownDuration, long cooldownEndTime) {
		g.setColor(Presets.COLOR_COOLDOWN);
		g.fillRect(handler.getWidth() / 2 - 70 / 2, handler.getHeight() - 9, 70, (int) -(((double) (cooldownEndTime - System.currentTimeMillis()) / cooldownDuration) * 58));
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
	private void renderCurrentItemSlot(Graphics g) {
		g.drawImage(Assets.currentItemSlot, handler.getWidth() / 2 - 70 / 2, handler.getHeight() - 72, 70, 70, null);
		
		if(selected != null) {
			g.drawImage(selected.getTexture(), handler.getWidth() / 2 - 70 / 2 + 4, handler.getHeight() - 77, 64, 70, null);
			
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
			g.setColor(Color.yellow);
			if(selected.isTool()) {
				Tool selectedTool = (Tool) selected;
				g.drawString(selectedTool.getDurability() + " / " + selectedTool.getMaxDurability(), handler.getWidth() / 2 - 70 / 2 + 30, handler.getHeight() - 77);
				
				g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
				g.drawString("Attack: " + selectedTool.getDamage(), handler.getWidth() / 2 - 70 / 2 + 75, handler.getHeight() - 55);
				g.drawString("Range: " + selectedTool.getRange(), handler.getWidth() / 2 - 70 / 2 + 75, handler.getHeight() - 37);
			}
		}
	}
	
	//GETTERS AND SETTERS
	
	public SlotMenu getSlotMenu() {
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
		if(item instanceof Tool) {
			for(int i = 0; i < amount; i++) {
				if(!tools.remove((Tool) item)) {
					if(i > 0) refresh();
					
					return false;
				}
			}
		} else if(items.get(item) != null) {
			items.replace(item, items.get(item) - amount);
			
			refresh();
			
			Logging.info("Inventory: removed " + amount + " " + item.getName());
			return items.get(item) >= 0;
		} else {
			Logging.info("Inventory: cannot remove " + amount + " " + item.getName() + " - item not found in inventory");
			return false;
		}
		
		refresh();
		
		return true;
	}
	
	public boolean remove(Tool tool) {
		if(tools.remove(tool)) {
			Logging.info("Inventory: removed 1 " + tool.getName());
			
			refresh();
			
			return true;
		}
		
		Logging.info("Inventory: cannot remove 1 " + tool.getName() + " - tool not found in inventory");
		
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
