package konradn24.tml.building;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.building.gfx.BuildingSlot;
import konradn24.tml.entities.buildings.Building;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.widgets.slots.SlotMenu;

public class BuildingsMenu {
	
	private static final int ROWS = 6;
	private static final int COLUMNS = 4;
	private static final int POS_X = 20;
	private static final int POS_Y = 20;
	private static final int SECTION_WIDTH = 350;
	private static final int SECTION_HEIGHT = 600;
	
	public static final int DEFAULT_OPEN_KEY = KeyEvent.VK_B;
	
	private Map<Building, Boolean> buildings;
	
	private String layoutID;
	private SlotMenu slotMenu;
	
	private int keyToOpen;
	private boolean opened;
	
	private Handler handler;
	
	public BuildingsMenu(String layoutID, Handler handler) {
		this.layoutID = layoutID;
		this.handler = handler;
		
		buildings = new LinkedHashMap<>();
		
//		slotMenu = new SlotMenu(layoutID + "_slotMenu", handler, COLUMNS); // TODO: init slot menu
//		slotMenu.setFixedIconsWidth(48);
//		slotMenu.setFixedIconsHeight(48);
//		slotMenu.setFont(Presets.FONT_INVENTORY);
//		slotMenu.setTextColor(Color.WHITE);
//		slotMenu.setPosition(POS_X, POS_Y, SECTION_WIDTH, SECTION_HEIGHT);
//		
//		BuildingSlot[] slots = new BuildingSlot[ROWS * COLUMNS];
//		for(int i = 0; i < ROWS * COLUMNS; i++)
//			slots[i] = new BuildingSlot(slotMenu, handler);
//		
//		slotMenu.addSlots(slots);
		
		keyToOpen = DEFAULT_OPEN_KEY;
	}
	
	public void tick() {
		checkInput();
		
		if(opened) {
			slotMenu.tick();
		}
	}
	
	public void render(Graphics2D g) {
		if(opened) {
			g.setColor(Presets.COLOR_BACKGROUND);
			g.fillRect(POS_X, POS_Y, SECTION_WIDTH, SECTION_HEIGHT);
			
			slotMenu.render(g);
		}
	}
	
	public void refresh() {
		if(handler == null)
			return;
		
		buildings.clear();
		
		int i = 0;
		for(String buildingID : Building.getBuildings()) {
			Building building = Building.getBuilding(buildingID, handler);
			if(building == null)
				continue;
			
			boolean available = building.isUnlocked();
			
			buildings.put(building, available);
			
			int levelDifference = handler.getPlayer().getExperienceLevel() - building.getRequiredLevel();
			if(levelDifference >= -1) {
				((BuildingSlot) slotMenu.getSlot(i)).setBuilding(building, true);
				
				i++;
			}
		}
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

	public Map<Building, Boolean> getBuildings() {
		return buildings;
	}
	
	public int getAvailableBuildingsAmount() {
		int count = 0;
		for(boolean affordable : buildings.values())
			if(affordable) count++;
		
		return count;
	}

	public String getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	public SlotMenu getSlotMenu() {
		return slotMenu;
	}
}