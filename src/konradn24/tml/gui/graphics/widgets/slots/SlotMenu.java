package konradn24.tml.gui.graphics.widgets.slots;

import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.gui.graphics.components.Dropdown;
import konradn24.tml.gui.graphics.layouts.GridLayout;

public class SlotMenu<T extends Slot> {
	private float slotSize;
	
	private String font;
	private float fontSize;
	private NVGColor textColor;
	private float bottomTextOffsetY;
	
	private GridLayout<T> slots;
	private int selectedSlot;
	private Dropdown dropdown;
	
	private float iconsScale;
	
	private Handler handler;
	
	public SlotMenu(Class<T> slotClass, float x, float y, float slotSize, int columns, Handler handler) {
		this.handler = handler;
		
		font = Fonts.GLOBAL_FONT;
		fontSize = Fonts.DEFAULT_SIZE;
		textColor = Colors.TEXT;
		bottomTextOffsetY = 15;
		
		slots = new GridLayout<T>(
			slotClass, 32, columns, x, y, slotSize, slotSize, 0, handler
		).customize((slot, i) -> {
			slot.setMenu(this);
			slot.setIndex(i);
		});
		
		selectedSlot = -1;
		
		dropdown = new Dropdown(30, handler);
		dropdown.setInvisible(true);
		
		dropdown.setOnFocusLost(() -> {
			selectedSlot = -1;
		});
		
		iconsScale = 0.7f;
	}
	
	public void update(float dt) {
		dropdown.update(dt);
		slots.update(dt);
	}
	
	public void renderGUI(long vg) {
		slots.renderGUI(vg);
		dropdown.renderGUI(vg);
	}
	
	public void refreshActions() {
		for(Slot slot : slots.getComponents()) {
			slot.refreshActions();
		}
	}
	
	public void refreshActionsDropdown() {
		if(selectedSlot < 0)
			return;
		
		Slot slot = getSlot(selectedSlot);
		
		dropdown.getOptions().clear();
		
		slot.actions.forEach(action -> {
			dropdown.addOption(action.getText(), action.getCallback());
		});
		
		dropdown.setX((float) handler.getMouseManager().getMouseX());
		dropdown.setY((float) handler.getMouseManager().getMouseY());
		
		dropdown.pack();
		
		dropdown.setInvisible(false);
	}
	
	public float getX() {
		return slots.x;
	}

	public void setX(float x) {
		slots.update(slots.columns, x, slots.y, slots.componentWidth, slots.componentHeight, slots.spacing);
	}

	public float getY() {
		return slots.y;
	}

	public void setY(float y) {
		slots.update(slots.columns, slots.x, y, slots.componentWidth, slots.componentHeight, slots.spacing);
	}
	
	public void setPosition(float x, float y) {
		slots.update(slots.columns, x, y, slots.componentWidth, slots.componentHeight, slots.spacing);
	}

	public float getSlotSize() {
		return slotSize;
	}

	public void setSlotSize(float slotSize) {
		this.slotSize = slotSize;
	}

	public void setSlots(GridLayout<T> slots) {
		this.slots = slots;
	}

	public GridLayout<T> getSlotsLayout() {
		return slots;
	}
	
	public T[] getSlots() {
		return slots.getComponents();
	}
	
	public T getSlot(int index) {
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
	
	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	
	public float getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public NVGColor getTextColor() {
		return textColor;
	}

	public void setTextColor(NVGColor textColor) {
		this.textColor = textColor;
	}
	
	public float getBottomTextOffsetY() {
		return bottomTextOffsetY;
	}

	public void setBottomTextOffsetY(float bottomTextOffsetY) {
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
}
