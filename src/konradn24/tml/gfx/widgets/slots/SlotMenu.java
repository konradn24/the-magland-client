package konradn24.tml.gfx.widgets.slots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Dropdown;
import konradn24.tml.gfx.style.layouts.GridLayout;

public class SlotMenu<T extends Slot> {

	private int x, y, width, height;
	private int slotSize;
	
	private Font font;
	private Color textColor;
	private int bottomTextOffsetY;
	
	private GridLayout<T> slots;
	private int selectedSlot;
	private Dropdown dropdown;
	
	private float iconsScale;
	
	private Handler handler;
	
	public SlotMenu(Class<T> slotClass, int x, int y, int slotSize, int columns, Handler handler) {
		this.handler = handler;
		
		font = Presets.FONT_GLOBAL;
		textColor = Presets.COLOR_TEXT_DARK;
		
		slots = new GridLayout<T>(
			slotClass, 32, columns, x, y, slotSize, slotSize, 0, handler
		).customize((slot, i) -> {
			slot.setMenu(this);
		});
		
		selectedSlot = -1;
		
		dropdown = new Dropdown(30, handler);
		dropdown.setFont(Presets.FONT_GLOBAL.deriveFont(22f));
		dropdown.setInvisible(true);
		
		dropdown.setOnFocusLost(() -> selectedSlot = -1);
		
		iconsScale = 1f;
	}
	
	public void tick() {
		slots.tick();
		dropdown.tick();
	}
	
	public void render(Graphics2D g) {
		slots.render(g);
		dropdown.render(g);
	}
	
	public void refreshActionsDropdown() {
		if(selectedSlot < 0)
			return;
		
		Slot slot = getSlot(selectedSlot);
		
		dropdown.getOptions().clear();
		
		slot.actions.forEach(action -> {
			dropdown.addOption(action.getText(), action.getCallback());
		});
		
		dropdown.setX(handler.getMouseManager().getMouseX());
		dropdown.setY(handler.getMouseManager().getMouseY());
		
		dropdown.pack();
		
		dropdown.setInvisible(false);
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

	public int getSlotSize() {
		return slotSize;
	}

	public void setSlotSize(int slotSize) {
		this.slotSize = slotSize;
	}

	public void setSlots(GridLayout<T> slots) {
		this.slots = slots;
	}

	public GridLayout<T> getSlotsLayout() {
		return slots;
	}
	
	public Slot[] getSlots() {
		return slots.getComponents();
	}
	
	public Slot getSlot(int index) {
		return slots.getComponents()[index];
	}
	
	public int getSlotsAmount() {
		return slots.getComponents().length;
	}

	public void setSlotsBottomTextContent(String... contents) {
		slots.forEach((slot, i) -> {
			slot.setBottomTextContent(contents[i]);
		});
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
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
	
	public int getBottomTextOffsetY() {
		return bottomTextOffsetY;
	}

	public void setBottomTextOffsetY(int bottomTextOffsetY) {
		this.bottomTextOffsetY = bottomTextOffsetY;
	}

	public float getIconsScale() {
		return iconsScale;
	}

	public void setIconsScale(float iconsScale) {
		this.iconsScale = iconsScale;
	}
	
	public Dropdown getDropdown() {
		return dropdown;
	}

	public void setDropdown(Dropdown dropdown) {
		this.dropdown = dropdown;
	}

	Handler getHandler() {
		return handler;
	}
	
	boolean isDropdownLocked() {
//		return dropdownLocked;
		return false;
	}
}
