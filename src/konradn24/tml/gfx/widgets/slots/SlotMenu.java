package konradn24.tml.gfx.widgets.slots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.Style.GridLayout;
import konradn24.tml.gfx.components.Dropdown;
import konradn24.tml.states.GameState;

public class SlotMenu {

	private int x, y, width, height;
	private int columns, rows;
	private String layoutID;
	
	private Font font;
	private Color textColor;
	
	private List<Slot> slots;
	private int selectedSlot;
	private Dropdown dropdown;
	
	private int fixedIconsWidth, fixedIconsHeight, iconsOffsetX, iconsOffsetY;
	private float iconsScale;
	private boolean autoSlotHeight;
	
	private Handler handler;
	
	public SlotMenu(String layoutID, Handler handler, int columns) {
		this.layoutID = layoutID;
		this.handler = handler;
		this.columns = columns;
		
		font = Presets.FONT_GLOBAL;
		textColor = Presets.COLOR_TEXT_DARK;
		
		slots = new ArrayList<>();
		selectedSlot = -1;
		
		dropdown = new Dropdown(layoutID + "_dropdown", 0, 0, handler);
		
		dropdown.setMarginX(5);
		dropdown.setMarginY(2);
		dropdown.setInvisible(true);
		
		dropdown.setOnFocusLost(() -> selectedSlot = -1);
		
		iconsScale = 1f;
		autoSlotHeight = true;
	}
	
	public void tick() {
		slots.forEach(slot -> slot.tick());
		
		dropdown.tick();
	}
	
	public void render(Graphics g) {
		slots.forEach(slot -> slot.render(g));
		
		dropdown.render(g);
	}
	
	public void refresh() {
		rows = (int) Math.ceil((double) (slots.size()) / columns);
		
		GridLayout layout = handler.getStyle().getLayout(layoutID);
		if(layout == null) {
			handler.getStyle().addLayout(GameState.class, layoutID, rows, columns);
			
			layout = handler.getStyle().getLayout(layoutID);
		}
		
		layout.x = x;
		layout.y = y;
		layout.width = width;
		layout.height = height;
		
		layout.refresh();
		
		int slotsWidth = width / columns;
		int slotsHeight = height / rows;
		int iconsWidth = fixedIconsWidth > 0 ? fixedIconsWidth : (int) (slotsWidth * iconsScale);
		int iconsHeight = fixedIconsHeight > 0 ? fixedIconsHeight : (int) (slotsHeight * iconsScale);
		iconsOffsetX = (slotsWidth - iconsWidth) / 2;
		iconsOffsetY = (slotsHeight - iconsHeight) / 2;
		
		for(int i = 0; i < slots.size(); i++) {
			int slotRow = Math.floorDiv(i, columns);
			int slotColumn = i % columns;
			
			slots.get(i).setX(handler.getStyle().positionX(layoutID, slotColumn));
			slots.get(i).setY(handler.getStyle().positionY(layoutID, slotRow));
			slots.get(i).setWidth(slotsWidth);
			
			if(autoSlotHeight)
				slots.get(i).setHeight(slotsHeight);
			
			slots.get(i).setRow(slotRow);
			slots.get(i).setColumn(slotColumn);
			
			slots.get(i).refresh();
		}
	}
	
	public void refreshSlots() {
		slots.forEach(slot -> slot.refresh());
	}
	
	public void refreshActionsDropdown() {
		if(selectedSlot < 0)
			return;
		
		dropdown.getOptions().clear();
		
		Slot slot = getSlot(selectedSlot);
		slot.actions.forEach(action -> {
			dropdown.addOption(action.getLabel(), action.getCallback());
		});
		
		dropdown.setX(handler.getMouseManager().getMouseX());
		dropdown.setY(handler.getMouseManager().getMouseY());
		
		dropdown.setInvisible(false);
		dropdown.refreshGraphics();
	}
	
	public void addSlots(Slot... slots) {
		for(int i = 0; i < slots.length; i++) {
			slots[i].setIndex(this.slots.size());
			this.slots.add(slots[i]);
		}
		
		refresh();
	}
	
	public void addSlots(BufferedImage... icons) {
		for(int i = 0; i < icons.length; i++) {
			Slot slot = new Slot(this, icons[i]);
			slot.setIndex(this.slots.size());
			
			this.slots.add(slot);
		}
		
		refresh();
	}
	
	public List<Slot> getSlots() {
		return slots;
	}
	
	public Slot getSlot(int index) {
		return slots.get(index);
	}
	
	public int getSlotsAmount() {
		return slots.size();
	}

	public void setSlotsBottomTextContent(String... contents) {
		for(int i = 0; i < slots.size(); i++) {
			if(i >= contents.length)
				break;
			
			slots.get(i).setBottomTextContent(contents[i]);
		}
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
	}

	public void setSlotHeight(int height) {
		slots.forEach(slot -> slot.setHeight(height));
		
		autoSlotHeight = false;
	}
	
	public void useSlotDefaultHeight() {
		slots.forEach(slot -> slot.setHeight(Slot.DEFAULT_HEIGHT));
		
		autoSlotHeight = false;
	}
	
	public void setPosition(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
	
	public int getFixedIconsWidth() {
		return fixedIconsWidth;
	}

	public void setFixedIconsWidth(int fixedIconsWidth) {
		this.fixedIconsWidth = fixedIconsWidth;
	}

	public int getFixedIconsHeight() {
		return fixedIconsHeight;
	}

	public void setFixedIconsHeight(int fixedIconsHeight) {
		this.fixedIconsHeight = fixedIconsHeight;
	}

	public int getIconsOffsetX() {
		return iconsOffsetX;
	}

	public void setIconsOffsetX(int iconsOffsetX) {
		this.iconsOffsetX = iconsOffsetX;
	}

	public int getIconsOffsetY() {
		return iconsOffsetY;
	}

	public void setIconsOffsetY(int iconsOffsetY) {
		this.iconsOffsetY = iconsOffsetY;
	}

	public float getIconsScale() {
		return iconsScale;
	}

	public void setIconsScale(float iconsScale) {
		this.iconsScale = iconsScale;
	}

	Handler getHandler() {
		return handler;
	}
	
	String getLayoutID() {
		return layoutID;
	}
	
	boolean isDropdownLocked() {
//		return dropdownLocked;
		return false;
	}
}
