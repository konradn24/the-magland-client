package konradn24.tml.gfx.widgets.slots;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.components.Label.DisplayType;
import konradn24.tml.gfx.components.Component;

public class Slot extends Component {
	
	public static final int DEFAULT_WIDTH = 128;
	public static final int DEFAULT_HEIGHT = 96;
	public static final int ACTIONS_MARGIN_X = 4;
	public static final int ACTIONS_MARGIN_Y = 2;
	
	protected SlotMenu<?> menu;
	protected BufferedImage icon;
	protected int index;
	
	protected Runnable primaryAction;
	protected List<SlotAction> actions;
	
	protected Label bottomText;
	
	protected Color hoverColor;
	protected int hoverCursor;
	
	protected boolean disabled;
	protected Color disabledColor;
	
	protected Label tooltip;
	protected Color tooltipBackgroundColor;
	
	public Slot(Handler handler) {
		super(handler);
	}
	
	public Slot(SlotMenu<?> menu) {
		super(menu.getHandler());
		
		init(menu, null);
	}
	
	public Slot(SlotMenu<?> menu, BufferedImage icon) {
		super(menu.getHandler());
		
		init(menu, icon);
	}
	
	private void init(SlotMenu<?> menu, BufferedImage icon) {
		this.menu = menu;
		this.icon = icon;
		
		actions = new ArrayList<>();
		
		bottomText = new Label("", x + width / 2, y + height - menu.getBottomTextOffsetY(),  handler);
		bottomText.setFont(menu.getFont());
		bottomText.setColor(menu.getTextColor());
		
		hoverColor = Presets.COLOR_LIGHT;
		hoverCursor = Cursor.HAND_CURSOR;
		
		disabledColor = Presets.COLOR_LIGHT;
		
		tooltip = new Label(handler);
		tooltip.setFont(Presets.FONT_GLOBAL);
		tooltip.setColor(Presets.COLOR_TEXT_LIGHT);
		tooltip.setBackground(Presets.COLOR_SHADE_3);
		tooltip.setDisplayType(DisplayType.ORIGIN);
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
		
		int iconWidth = (int) (width * menu.getIconsScale());
		int iconHeight = (int) (height * menu.getIconsScale());
		int iconX = x + width / 2 - iconWidth / 2;
		int iconY = y + height / 2 - iconHeight / 2;
		
		g.drawImage(icon, iconX, iconY, iconWidth, iconHeight, null);
		
		bottomText.render(g);
		
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
					tooltip.setY(handler.getMouseManager().getMouseY());
					tooltip.render(g);
				}
			}
		}
	}
	
	public class SlotAction {
		private String text;
		private Runnable callback;
		
		public SlotAction(String text, Runnable callback) {
			this.text = text;
			this.callback = callback;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
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

	public SlotMenu<?> getMenu() {
		return menu;
	}
	
	public void setMenu(SlotMenu<?> menu) {
		this.menu = menu;
		
		init(menu, null);
	}
	
	public void setColumn(int column) {
		super.column = column;
	}
	
	public void setRow(int row) {
		super.row = row;
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

	public Label getBottomText() {
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

	public Label getTooltip() {
		return tooltip;
	}

	public void setTooltip(Label tooltip) {
		this.tooltip = tooltip;
	}

	public Color getTooltipBackgroundColor() {
		return tooltipBackgroundColor;
	}

	public void setTooltipBackgroundColor(Color tooltipBackgroundColor) {
		this.tooltipBackgroundColor = tooltipBackgroundColor;
	}
}
