package konradn24.tml.entities.statics;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Dropdown;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.widgets.msgbox.MessageBox;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.states.State;
import konradn24.tml.tiles.Tile;

public class Pouch extends StaticEntity {

	public static final int WIDTH = Tile.TILE_WIDTH / 2;
	public static final int HEIGHT = Tile.TILE_WIDTH / 2;
	
	private Dropdown dropdown;
	
	private Map<Class<? extends Item>, Integer> items;
	
	public Pouch(Handler handler, float x, float y) {
		super(handler, x, y, WIDTH, HEIGHT);
	}

	@Override
	protected void onInit() {
		bounds = null;
		
		killable = false;
		
		customActionIcon = Assets.handIcon;
		
		dropdown = new Dropdown("Pouch_" + this.hashCode() + "_Dropdown", 0, 0, handler);
		
		dropdown.addOption(new AdvancedLabel("Take"), () -> collect());
		dropdown.addOption(new AdvancedLabel("Examine"), () -> examine());
		
		dropdown.setMarginX(10);
		dropdown.setMarginY(2);
		dropdown.setInvisible(true);
		
		actionsByAttribute.put(-1, (item, handler) -> {
			dropdown.setInvisible(false);
			dropdown.setCameraRelative(true, handler);
			
			dropdown.setX(handler.getMouseManager().getMouseX());
			dropdown.setY(handler.getMouseManager().getMouseY());
			
			dropdown.refreshGraphics();
		});
		
		items = new HashMap<>();
	}

	@Override
	public void tick() {		
		dropdown.tick();
	}

	@Override
	public void render(Graphics2D g) {
		
	}
	
	@Override
	public void renderGUI(Graphics2D g) {
		dropdown.render(g);
	}

	@Override
	public void onDead() {

	}
	
	public void collect() {
		for(Map.Entry<Class<? extends Item>, Integer> entry : items.entrySet()) {
			Class<? extends Item> item = entry.getKey();
			int amount = entry.getValue();
			
			handler.getPlayer().getInventory().add(item, amount);
		}
		
		vanish();
	}
	
	public void examine() {
		String text = "Items:\n";
		for(Map.Entry<Class<? extends Item>, Integer> entry : items.entrySet()) {
			Class<? extends Item> itemClass = entry.getKey();
			Item item = Item.getItem(itemClass);
			
			int amount = entry.getValue();
			
			text += "  •" + amount + "x " + item.getName() + "\n";
		}
		
		text += "\nDo you want to take those items now?";
		
		MessageBox messageBox = new MessageBox(MessageBox.TYPE_YES_NO, "Examine", text, handler);
		
		messageBox.setCallback(code -> {
			if(code == MessageBox.NO)
				return;
			
			if(code == MessageBox.YES)
				collect();
		});
		
		State.getState().getDialogsManager().showMessageBox(messageBox);
	}

	public Map<Class<? extends Item>, Integer> getItems() {
		return items;
	}

	public void setItems(Map<Class<? extends Item>, Integer> items) {
		this.items = items;
	}
	
	public void addItems(Map<Class<? extends Item>, Integer> items) {
		Map<Class<? extends Item>, Integer> temp = new HashMap<>(this.items);
		
		for(Map.Entry<Class<? extends Item>, Integer> entry : items.entrySet()) {
			Class<? extends Item> itemClass = entry.getKey();
			int amount = entry.getValue();
			
			if(temp.containsKey(itemClass)) {
				int oldAmount = temp.get(itemClass);
				
				temp.replace(itemClass, oldAmount + amount);
			} else {
				temp.put(itemClass, amount);
			}
		}
		
		this.items = temp;
	}
}	