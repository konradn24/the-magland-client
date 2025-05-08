package konradn24.tml.gfx.widgets.slots;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Component;

public class Slot extends Component {
	
	public static final int DEFAULT_WIDTH = 128;
	public static final int DEFAULT_HEIGHT = 96;
	public static final int ACTIONS_MARGIN_X = 4;
	public static final int ACTIONS_MARGIN_Y = 2;
	
	public static final int TOOLTIP_TOP_OFFSET = 28;
	
	protected SlotMenu menu;
	protected BufferedImage icon;
	protected int index;
	
	protected float iconScale;
	
	protected Runnable primaryAction;
	protected List<SlotAction> actions;
	
	protected AdvancedLabel bottomText;
	
	protected Color hoverColor;
	protected int hoverCursor;
	
	protected boolean disabled;
	protected Color disabledColor;
	
	protected AdvancedLabel tooltip;
	protected Color tooltipBackgroundColor;
	
	public Slot(SlotMenu menu) {
		init(menu, null);
	}
	
	public Slot(SlotMenu menu, BufferedImage icon) {
		init(menu, icon);
	}
	
	private void init(SlotMenu menu, BufferedImage icon) {
		super.handler = menu.getHandler();
		super.layoutID = menu.getLayoutID();
		
		this.menu = menu;
		this.icon = icon;
		
		actions = new ArrayList<>();
		
		bottomText = new AdvancedLabel("");
		bottomText.setFont(menu.getFont());
		bottomText.setColor(menu.getTextColor());
		
		hoverColor = Presets.COLOR_LIGHT;
		hoverCursor = Cursor.HAND_CURSOR;
		
		disabledColor = Presets.COLOR_LIGHT;
		
		tooltip = Presets.emptyLabel();
		tooltip.setFont(Presets.FONT_GLOBAL);
		tooltip.setColor(Presets.COLOR_TEXT_LIGHT);
		tooltip.setBackground(Presets.COLOR_SHADE_3);
		tooltip.setMarginX(6);
		tooltip.setMarginY(1);
	}
	
	public void tick() {
		if(disabled || invisible)
			return;
		
		if(hoverCursor != Cursor.DEFAULT_CURSOR) hoverCursor(hoverCursor);
		
		if(isLeftReleased() && primaryAction != null)
			primaryAction.run();
		
		if((isLeftPressed() || isRightPressed()) && menu.getSelectedSlot() != -1)
			menu.setSelectedSlot(-1);
		
		if(isRightReleased()) {
			menu.setSelectedSlot(index);
			menu.refreshActionsDropdown();
		}
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		g.drawImage(icon, x + menu.getIconsOffsetX(), y + menu.getIconsOffsetY(), 
				menu.getFixedIconsWidth() > 0 ? menu.getFixedIconsWidth() : (width - menu.getIconsOffsetX() * 2), 
				menu.getFixedIconsHeight() > 0 ? menu.getFixedIconsHeight() : (height - menu.getIconsOffsetY() * 2), null);
		
		bottomText.render(g, handler);
		
		if(disabled) {
			g.setColor(Presets.COLOR_LIGHT);
			g.fillRect(x, y, width, height);
			return;
		}
		
		if(hoverColor != null) {
			if(isOn()) {
				g.setColor(hoverColor);
				g.fillRect(x, y, width, height);
				
				// Tooltip
				if(!tooltip.getContent().isEmpty() && menu.getSelectedSlot() == -1) {
					tooltip.setX(handler.getMouseManager().getMouseX());
					tooltip.setY(handler.getMouseManager().getMouseY() + TOOLTIP_TOP_OFFSET);
					tooltip.render(g, handler);
				}
			}
		}
	}
	
	public void refresh() {
		bottomText.calculateSize(handler, bottomText.getFont());
		
		bottomText.setPositionCenterX(true, layoutID, column);
		bottomText.setY(y + height - bottomText.getHeight());
	}
	
	public class SlotAction {
		private AdvancedLabel label;
		private Runnable callback;
		
		public SlotAction(AdvancedLabel label, Runnable callback) {
			this.label = label;
			this.callback = callback;
		}

		public AdvancedLabel getLabel() {
			return label;
		}

		public void setLabel(AdvancedLabel label) {
			this.label = label;
		}

		public Runnable getCallback() {
			return callback;
		}

		public void setCallback(Runnable callback) {
			this.callback = callback;
		}
	}
	
	public void addSlotActions(SlotAction... actions) {
		this.actions.addAll(List.of(actions));
	}
	
	// GETTERS AND SETTERS

	public void setColumn(int column) {
		super.column = column;
	}
	
	public void setRow(int row) {
		super.row = row;
	}
	
	public SlotMenu getMenu() {
		return menu;
	}

	public void setMenu(SlotMenu menu) {
		this.menu = menu;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	public int getIndex() {
		return index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}

	public float getIconScale() {
		return iconScale;
	}

	public void setIconScale(float iconScale) {
		this.iconScale = iconScale;
	}

	public Runnable getPrimaryAction() {
		return primaryAction;
	}

	public void setPrimaryAction(Runnable primaryAction) {
		this.primaryAction = primaryAction;
	}

	public Color getHoverColor() {
		return hoverColor;
	}

	public void setHoverColor(Color hoverColor) {
		this.hoverColor = hoverColor;
	}

	public int getHoverCursor() {
		return hoverCursor;
	}

	public void setHoverCursor(int hoverCursor) {
		this.hoverCursor = hoverCursor;
	}

	public List<SlotAction> getActions() {
		return actions;
	}

	public AdvancedLabel getBottomText() {
		return bottomText;
	}
	
	public void setBottomTextContent(String content) {
		bottomText.setContent(content);
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Color getDisabledColor() {
		return disabledColor;
	}

	public void setDisabledColor(Color disabledColor) {
		this.disabledColor = disabledColor;
	}

	public AdvancedLabel getTooltip() {
		return tooltip;
	}

	public void setTooltip(AdvancedLabel tooltip) {
		this.tooltip = tooltip;
	}

	public Color getTooltipBackgroundColor() {
		return tooltipBackgroundColor;
	}

	public void setTooltipBackgroundColor(Color tooltipBackgroundColor) {
		this.tooltipBackgroundColor = tooltipBackgroundColor;
	}
}
