// INV = inventory
// IP = inventory property

package konradn24.tml.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.statics.Pouch;
import konradn24.tml.gui.PlayGUI;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.widgets.slots.SlotMenu;
import konradn24.tml.gui.panels.context.ContextPanel;
import konradn24.tml.gui.panels.context.GUIContext;
import konradn24.tml.items.Item;
import konradn24.tml.utils.Logging;

public class Inventory implements GUIContext {
	
	private final UUID uuid = UUID.randomUUID();
	
	private static final byte COLUMNS = 4;
	private static final float CRAFTING_MARGIN_X = ContextPanel.X;
	
	private SlotMenu<InventorySlot> slotMenu;
	
	private int selectedIndex;
	private Handler handler;
	
	private List<InventoryProperty> items = new ArrayList<>();
	
	private Entity owner;
	private Crafting crafting;
	
	private String title;
	private NVGColor titleColor;
	private boolean storage;
	private float weightLimit;
	
	public Inventory(Entity owner, String title, boolean storage, float weightLimit, Handler handler) {
		this.owner = owner;
		this.handler = handler;
		this.title = title;
		this.titleColor = Colors.TEXT;
		this.storage = storage;
		this.weightLimit = weightLimit;
		
		this.selectedIndex = -1;
		
		slotMenu = new SlotMenu<>(InventorySlot.class, ContextPanel.X, ContextPanel.Y, ContextPanel.WIDTH / COLUMNS, COLUMNS, handler);
		slotMenu.setTextColor(Colors.rgba(255, 255, 255, 255));
		slotMenu.getDropdown().setWidth(150);
		
		for(InventorySlot slot : slotMenu.getSlots()) {
			slot.setInventory(this);
		}
		
		Logging.info("Inventory " + uuid + ": initialized");
	}
	
	public void update(float dt) {
		boolean modified = false;
		List<InventoryProperty> toAdd = new ArrayList<>();
		ListIterator<InventoryProperty> iterator = items.listIterator();
		
		while(iterator.hasNext()) {
			InventoryProperty property = iterator.next();
			
			if(property.amount <= 0) {
				iterator.remove();
				
				if(selectedIndex == iterator.previousIndex()) {
					selectedIndex = -1;
				}
				
				modified = true;
			} else if(property.item.isTool() && property.amount > 1) {
				toAdd.addAll(property.shatter());
				iterator.remove();
				
				modified = true;
			}
			
			property.item.update(dt);
		}
		
		items.addAll(toAdd);
		
		if(selectedIndex >= items.size()) {
			selectedIndex = -1;
		}
		
		if(modified) {
			refresh();
		}
	}
	
	@Override
	public void initContextGUI() {
		refresh();
	}
	
	@Override
	public void cleanupContextGUI() {
		
	}

	@Override
	public void updateContextGUI(float dt) {
		slotMenu.update(dt);
		if(crafting != null) crafting.update(dt);
	}

	@Override
	public void renderContextGUI(long vg) {
		if(crafting != null) {
			PlayGUI.renderBackground(vg, crafting.getX(), crafting.getY(), crafting.getWidth(), crafting.getHeight());
			crafting.renderGUI(vg);
		}
		
		slotMenu.renderGUI(vg);
	}
	
	public void refresh() {
		slotMenu.getSlotsLayout().forEach((slot, i) -> {
			slot.setProperty(null);
		});
		
		int i = 0;
		while(i < items.size()) {
			if(i >= slotMenu.getSlotsAmount())
				break;
			
			slotMenu.getSlot(i).setProperty(items.get(i));
			
			i++;
		}
		
		Logging.info("Inventory " + uuid + ": refreshed - " + i + "/" + slotMenu.getSlotsAmount() + " slots in use");
		
		if(crafting != null) crafting.refresh();
	}
	
	public void attachCrafting(Crafting crafting) {
		crafting.useGraphics(handler, ContextPanel.X + ContextPanel.WIDTH + CRAFTING_MARGIN_X, ContextPanel.Y, ContextPanel.WIDTH, ContextPanel.HEIGHT, COLUMNS);
		crafting.setInventory(this);
		
		this.crafting = crafting;
	}
	
	public SlotMenu<InventorySlot> getSlotMenu() {
		return slotMenu;
	}

	public List<InventoryProperty> getItems() {
		return items;
	}
	
	public InventoryProperty getItem(Item item) {
		return items.stream().filter(property -> property.item.equals(item)).findFirst().orElse(null);
	}
	
	public InventoryProperty getItem(String name) {
		return items.stream().filter(property -> property.item.getName().equals(name)).findFirst().orElse(null);
	}
	
	/** Only item class compare **/
	public boolean hasItem(Item item) {
		return getItem(item) != null;
	}
	
	/** Exact compare **/
	public boolean hasItem(InventoryProperty property) {
		return items.stream().filter(p -> p.isEqual(property)).count() > 0;
	}
	
	/** Only item class compare **/
	public int getItemIndex(Item item) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).item.equals(item)) {
				return i;
			}
		}
		
		return -1;
	}

	/** Exact compare **/
	public int getItemIndex(InventoryProperty property) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).isEqual(property)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/** Only item class compare **/
	public int getItemAmount(Item item) {
		int amount = 0;
		
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).item.equals(item)) {
				amount += items.get(i).amount;
			}
		}
		
		return amount;
	}
	
	/** Exact compare **/
	public int getItemAmount(InventoryProperty property) {
		int amount = 0;
		
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).isEqual(property)) {
				amount += items.get(i).amount;
			}
		}
		
		return amount;
	}
	
	public boolean add(InventoryProperty property) {
		if(storage && getWeight() + getWeight(items) > weightLimit) {
			Logging.info("Inventory " + uuid + ": cannot add " + property.amount + " " + property.item.getName() + " - weight limit exceeded");
			return false;
		}
		
		if(property.item.isTool()) {
			items.addAll(property.shatter());
		} else {
			int index = getItemIndex(property);
			
			if(index == -1) {
				items.add(property);
			} else {
				items.get(index).amount += property.amount;
			}
		}
		
		refresh();
		
		Logging.info("Inventory " + uuid + ": added " + property.amount + " " + property.item.getName());
		
		return true;
	}
	
	public boolean add(List<InventoryProperty> properties) {
		if(storage && getWeight() + getWeight(properties) > weightLimit) {
			Logging.info("Inventory " + uuid + ": cannot add items collection - weight limit exceeded");
			return false;
		}
		
		for(InventoryProperty property : properties) {
			if(property.item.isTool()) {
				items.addAll(property.shatter());
			} else {
				int index = getItemIndex(property);
				
				if(index == -1) {
					this.items.add(property);
				} else {
					this.items.get(index).amount += property.amount;
				}
			}
		}
		
		refresh();
		
		Logging.info("Inventory " + uuid + ": added items collection of " + items.size() + " elements");
		
		return true;
	}
	
	/** Only item class equality **/
	public boolean remove(Item item, int amount) {
		int deleted = 0;
		
		while(deleted < amount) {
			int index = getItemIndex(item);
			
			if(index == -1) {
				break;
			}
			
			int remaining = amount - deleted;
			
			if(items.get(index).amount <= remaining) {
				deleted += items.get(index).amount;
				items.remove(index);
			} else {
				deleted += remaining;
				items.get(index).amount -= remaining;
			}
		}
		
		if(deleted == 0) {
			Logging.error("Inventory " + uuid + ": cannot remove " + amount + " " + item.getName() + " - item not found in inventory");
			return false;
		}
		
		refresh();
		
		Logging.info("Inventory " + uuid + ": removed " + deleted + " " + item.getName());
		return true;
	}
	
	/** Only item class equality **/
	public boolean remove(Item item, boolean once) {
		int deleted = 0;
		
		for(int i = 0; i < items.size(); i++) {
			if(!items.get(i).item.equals(item)) {
				continue;
			}
			
			if(once) {
				if(items.get(i).amount == 1) {
					items.remove(i);
				} else {
					items.get(i).amount--;
				}
				
				deleted++;
				
				break;
			} else {
				deleted += items.get(i).amount;
				items.remove(i);
				i--;
			}
		}
		
		if(deleted == 0) {
			Logging.error("Inventory " + uuid + ": cannot remove " + (once ? "1" : "all") + " " + item.getName() + " - item not found in inventory");
			return false;
		}
		
		refresh();
		
		Logging.info("Inventory " + uuid + ": removed " + deleted + " " + item.getName());
		return true;
	}
	
	/** Exact equality **/
	public boolean remove(InventoryProperty property, int amount) {
		int deleted = 0;
		
		int i = 0;
		while(deleted < amount) {
			if(i >= items.size()) {
				break;
			}
			
			if(!items.get(i).isEqual(property)) {
				i++;
				continue;
			}
			
			int remaining = amount - deleted;
			
			if(items.get(i).amount <= remaining) {
				deleted += items.get(i).amount;
				items.remove(i);
				i--;
			} else {
				deleted += remaining;
				items.get(i).amount -= remaining;
			}
			
			i++;
		}
		
		if(deleted == 0) {
			Logging.error("Inventory " + uuid + ": cannot remove " + amount + " " + property.item.getName() + " - item not found in inventory");
			return false;
		}
		
		refresh();
		
		Logging.info("Inventory " + uuid + ": removed " + deleted + " " + property.item.getName());
		return true;
	}
	
	/** Exact equality **/
	public boolean remove(InventoryProperty property) {
		return remove(property, property.amount);
	}
	
	/** Exact or only item class equality **/
	public void remove(Map<InventoryProperty, Integer> properties, boolean onlyItemClassEquality) {
		for(Map.Entry<InventoryProperty, Integer> entry : properties.entrySet()) {
			if(onlyItemClassEquality) {
				remove(entry.getKey().item, entry.getValue());
			} else {
				remove(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/** Exact or only item class equality **/
	public void remove(List<InventoryProperty> properties, boolean onlyItemClassEquality) {
		for(InventoryProperty property : properties) {
			if(onlyItemClassEquality) {
				remove(property.item, property.amount);
			} else {
				remove(property);
			}
		}
	}
	
	/** Exact equality **/
	public boolean drop(InventoryProperty property, int amount) {
		if(!hasItem(property)) {
			Logging.error("Inventory " + uuid + ": cannot drop " + amount + " " + property.item.getName() + " - property not found in inventory (not contains)");
			return false;
		}
		
		int dropped = 0;
		
		int i = 0;
		while(dropped < amount) {
			if(i >= items.size()) {
				break;
			}
			
			if(!items.get(i).isEqual(property)) {
				i++;
				continue;
			}
			
			int remaining = amount - dropped;
			
			if(items.get(i).amount <= remaining) {
				dropped += items.get(i).amount;
				items.remove(i);
				i--;
			} else {
				dropped += remaining;
				items.get(i).amount -= remaining;
			}
			
			i++;
		}
		
		if(dropped == 0) {
			Logging.error("Inventory " + uuid + ": cannot drop " + amount + " " + property.item.getName() + " - item not found in inventory (amount < 1)");
			return false;
		}
		
		refresh();
		
		Pouch pouch = (Pouch) handler.getWorld().getEntityManager().getEntityByPosition(Pouch.class, owner.getWorldX(), owner.getWorldY());
		
		if(pouch == null) {
			pouch = new Pouch(handler, owner.getWorldX(), owner.getWorldY());
			pouch.getInventory().add(property.copy(dropped));
			
			handler.getWorld().getEntityManager().addEntity(pouch);
		} else {
			pouch.getInventory().add(property.copy(dropped));
		}
		
		Logging.info("Inventory " + uuid + ": dropped " + dropped + " " + property.item.getName());
		
		return true;
	}
	
	/** Exact equality **/
	public boolean drop(InventoryProperty property) {
		return drop(property, property.amount);
	}
	
	/** Exact equality **/
	public void drop(List<InventoryProperty> properties) {
		for(InventoryProperty property : properties) {
			drop(property);
		}
	}
	
	public void dropAll() {
		if(items.isEmpty()) {
			Logging.info("Inventory " + uuid + ": cannot drop all - no items");
			return;
		}
		
		Pouch pouch = (Pouch) handler.getWorld().getEntityManager().getEntityByPosition(Pouch.class, owner.getWorldX(), owner.getWorldY());
		
		if(pouch == null) {
			pouch = new Pouch(handler, owner.getWorldX(), owner.getWorldY());
			pouch.getInventory().add(items);
			
			handler.getWorld().getEntityManager().addEntity(pouch);
		} else {
			pouch.getInventory().add(items);
		}
		
		items.clear();
		
		Logging.info("Inventory " + uuid + ": dropped all items");
	}
	
	/** Only item class equality **/
	public boolean move(Item item, int amount, Inventory destination) {
		if(!hasItem(item)) {
			Logging.error("Inventory " + uuid + ": cannot move property " + amount + " " + item.getName() + " (" + uuid + " -> " + destination.uuid + ") - item not found in inventory (not contains)");
			return false;
		}
		
		if(destination.isStorage() && destination.getWeight() + item.getWeight() * amount > destination.getWeightLimit()) {
			Logging.info("Inventory " + uuid + ": cannot move " + amount + " " + item.getName() + " - weight limit exceeded at destination");
			return false;
		}
		
		List<InventoryProperty> destinationItems = new ArrayList<>();
		int moved = 0;
		
		while(moved < amount) {
			int index = getItemIndex(item);
			
			if(index == -1) {
				break;
			}
			
			int remaining = amount - moved;
			
			if(items.get(index).amount <= remaining) {
				destinationItems.add(items.get(index));
				moved += items.get(index).amount;
				items.remove(index);
			} else {
				destinationItems.add(items.get(index).copy(remaining));
				moved += remaining;
				items.get(index).amount -= remaining;
			}
		}
		
		if(moved == 0) {
			Logging.error("Inventory " + uuid + ": cannot move " + amount + " " + item.getName() + " (" + uuid + " -> " + destination.uuid + ") - item not found in inventory (amount < 1)");
			return false;
		}
		
		refresh();
		
		destination.add(destinationItems);
		
		Logging.info("Inventory " + uuid + ": moved " + moved + " " + item.getName() + " (" + uuid + " -> " + destination.uuid + ")");
		
		return true;
	}
	
	/** Exact equality **/
	public boolean move(InventoryProperty property, int amount, Inventory destination) {
		if(!hasItem(property)) {
			Logging.error("Inventory " + uuid + ": cannot move property " + amount + " " + property.item.getName() + " (" + uuid + " -> " + destination.uuid + ") - item not found in inventory (not contains)");
			return false;
		}
		
		if(destination.isStorage() && destination.getWeight() + property.item.getWeight() * amount > destination.getWeightLimit()) {
			Logging.info("Inventory " + uuid + ": cannot move property " + amount + " " + property.item.getName() + " - weight limit exceeded at destination");
			return false;
		}
		
		List<InventoryProperty> destinationItems = new ArrayList<>();
		int moved = 0;
		
		while(moved < amount) {
			int index = getItemIndex(property);
			
			if(index == -1) {
				break;
			}
			
			int remaining = amount - moved;
			
			if(items.get(index).amount <= remaining) {
				destinationItems.add(items.get(index));
				moved += items.get(index).amount;
				items.remove(index);
			} else {
				destinationItems.add(items.get(index).copy(remaining));
				moved += remaining;
				items.get(index).amount -= remaining;
			}
		}
		
		if(moved == 0) {
			Logging.error("Inventory " + uuid + ": cannot move " + amount + " " + property.item.getName() + " (" + uuid + " -> " + destination.uuid + ") - item not found in inventory (amount < 1)");
			return false;
		}
		
		refresh();
		
		destination.add(destinationItems);
		
		Logging.info("Inventory " + uuid + ": moved " + moved + " " + property.item.getName() + " (" + uuid + " -> " + destination.uuid + ")");
		
		return true;
	}
	
	/** Exact equality **/
	public boolean move(InventoryProperty property, Inventory destination) {
		return move(property, property.amount, destination);
	}
	
	/** Exact equality **/
	public void move(List<InventoryProperty> properties, Inventory destination) {
		for(InventoryProperty property : properties) {
			move(property, destination);
		}
	}
	
	public void moveAll(Inventory destination) {
		float weightLeft = destination.getWeightLimit() - destination.getWeight();
		
		List<InventoryProperty> destinationItems = new ArrayList<>();
		int moved = 0;
		
		while(items.size() > 0) {
			int toMove = (int) Math.min(items.get(0).amount, Math.floor(weightLeft / items.get(0).item.getWeight()));
			moved += toMove;
			
			if(toMove < items.get(0).amount) {
				destinationItems.add(items.get(0).copy(toMove));
				items.get(0).amount -= toMove;
				
				break;
			} else {
				destinationItems.add(items.get(0));
				items.remove(0);
			}
		}
		
		destination.add(destinationItems);
		
		Logging.info("Inventory " + uuid + ": moved " + moved + " items (" + uuid + " -> " + destination.uuid + ")");
	}
	
	public float getWeight() {
		return getWeight(items);
	}
	
	public float getWeight(List<InventoryProperty> items) {
		float weight = 0;
		
		for(InventoryProperty property : items)
			weight += property.getWeight();
		
		return weight;
	}

	public boolean isSelected(InventoryProperty property) {
		if(selectedIndex < 0 || selectedIndex >= items.size())
			return false;
		
		return items.get(selectedIndex).equals(property);
	}
	
	public boolean isSelected(Item item) {
		if(selectedIndex < 0 || selectedIndex >= items.size())
			return false;
		
		return items.get(selectedIndex).item.equals(item);
	}
	
	public InventoryProperty getSelected() {
		if(selectedIndex < 0 || selectedIndex >= items.size())
			return null;
		
		return items.get(selectedIndex);
	}
	
	public Item getSelectedItem() {
		if(selectedIndex < 0 || selectedIndex >= items.size())
			return null;
		
		return items.get(selectedIndex).item;
	}

	public boolean isAnyItemSelected() {
		return selectedIndex > -1;
	}
	
	public boolean select(int index) {
		if(index < 0 || index >= items.size()) {
			Logging.error("Inventory " + uuid + ": cannot select INV_IP_" + index + " - invalid index");
			return false;
		}
		
		this.selectedIndex = index;
		
		return true;
	}
	
	public boolean select(InventoryProperty property) {
		int index = items.indexOf(property);
		
		if(index == -1) {
			Logging.error("Inventory " + uuid + ": cannot select INV_IP_" + index + " - property not found in inventory");
			return false;
		}
		
		this.selectedIndex = index;
		
		return true;
	}
	
	public boolean select(Item item) {
		int index = getItemIndex(item);
		
		if(index == -1) {
			Logging.error("Inventory " + uuid + ": cannot select " + item.getName() + " - item not found in inventory");
			return false;
		}
		
		this.selectedIndex = index;
		
		return true;
	}
	
	public void resetSelected() {
		selectedIndex = -1;
	}
	
	// GETTERS AND SETTERS
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Crafting getCrafting() {
		return crafting;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleColor(NVGColor titleColor) {
		this.titleColor = titleColor;
	}

	public boolean isStorage() {
		return storage;
	}

	public void setStorage(boolean storage) {
		this.storage = storage;
	}

	public float getWeightLimit() {
		return weightLimit;
	}

	public void setWeightLimit(float weightLimit) {
		this.weightLimit = weightLimit;
	}

	@Override
	public String getContextTitle() {
		return title;
	}

	@Override
	public NVGColor getContextTitleColor() {
		return titleColor;
	}
}
