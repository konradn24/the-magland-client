package konradn24.tml.gui.graphics.widgets.slots;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Cursor;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.gui.graphics.components.Label;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;

public class Slot extends Component {
	
	public static final int DEFAULT_WIDTH = 128;
	public static final int DEFAULT_HEIGHT = 96;
	public static final int ACTIONS_MARGIN_X = 4;
	public static final int ACTIONS_MARGIN_Y = 2;
	
	protected SlotMenu<?> menu;
	protected Texture icon;
	protected int index;
	
	protected Runnable primaryAction;
	protected List<SlotAction> actions;
	
	protected Label bottomText;
	
	protected NVGColor hoverColor;
	protected long hoverCursor;
	
	protected boolean disabled;
	protected NVGColor disabledColor;
	
	public Slot(Handler handler) {
		super(handler);
	}
	
	public Slot(SlotMenu<?> menu) {
		super(menu.getHandler());
		
		init(menu, null);
	}
	
	public Slot(SlotMenu<?> menu, Texture icon) {
		super(menu.getHandler());
		
		init(menu, icon);
	}
	
	private void init(SlotMenu<?> menu, Texture icon) {
		this.menu = menu;
		this.icon = icon;
		
		actions = new ArrayList<>();
		
		bottomText = new Label("", x + width / 2, y + height - menu.getBottomTextOffsetY(),  handler);
		bottomText.setFont(menu.getFont());
		bottomText.setColor(menu.getTextColor());
		
		hoverColor = Colors.COLOR_LIGHT;
		hoverCursor = Cursor.HAND;
		
		disabledColor = Colors.COLOR_LIGHT;
	}
	
	public void update(float dt) {
		if(disabled || invisible) {
			return;
		}
		
		bottomText.update(dt);
		
		if(isRightReleased()) {
			handler.getMouseManager().resetRightRelease();
			
			menu.setSelectedSlot(index);
			menu.refreshActionsDropdown();
		}
		
		if(menu.getSelectedSlot() != -1 && menu.getSelectedSlot() != index) {
			return;
		}
		
		if(hoverCursor != Cursor.ARROW) hoverCursor(hoverCursor);
		
		if(isLeftReleased() && primaryAction != null) {
			handler.getMouseManager().resetLeftRelease();
			
			primaryAction.run();
		}
	}
	
	public void renderGUI(long vg) {
		if(invisible)
			return;
		
		float iconWidth = (int) (width * menu.getIconsScale());
		float iconHeight = (int) (height * menu.getIconsScale());
		float iconX = x + width / 2 - iconWidth / 2;
		float iconY = y + 2;
		
		AssetsRenderer.renderTexture(vg, icon, iconX, iconY, iconWidth, iconHeight);
		bottomText.renderGUI(vg);
		
		if(disabled) {
			nvgBeginPath(vg);
			nvgRect(vg, x, y, width, height);
			nvgFillColor(vg, disabledColor);
			nvgFill(vg);
			
			return;
		}
		
		if(hoverColor != null) {
			if(isOn() && menu.getSelectedSlot() < 0 || menu.getSelectedSlot() == index) {
				nvgBeginPath(vg);
				nvgRect(vg, x, y, width, height);
				nvgFillColor(vg, hoverColor);
				nvgFill(vg);
			}
		}
	}
	
	public void refreshActions() {};
	
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
	
	public Texture getIcon() {
		return icon;
	}

	public void setIcon(Texture icon) {
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

	public NVGColor getHoverColor() {
		return hoverColor;
	}

	public void setHoverColor(NVGColor hoverColor) {
		this.hoverColor = hoverColor;
	}

	public long getHoverCursor() {
		return hoverCursor;
	}

	public void setHoverCursor(long hoverCursor) {
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

	public NVGColor getDisabledColor() {
		return disabledColor;
	}

	public void setDisabledColor(NVGColor disabledColor) {
		this.disabledColor = disabledColor;
	}
}
