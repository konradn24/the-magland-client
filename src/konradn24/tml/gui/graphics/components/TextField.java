package konradn24.tml.gui.graphics.components;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.Handler;
import konradn24.tml.display.Cursor;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;

public class TextField extends Component {
	
	public static final int CURSOR_BLINK_TIME = 1000;
	
	private Label placeholder;
	private Label text;
	private String fullContent = "";
	private boolean focus, disabled, contentUpdated;
	
	private float paddingX = Display.x(.0026f), paddingY = Display.y(.0019f);
	
	private byte cursorOffset = 5, cursorWidth = 1;
	private long lastCursorBlink;
	
	public TextField(Handler handler) {
		super(handler);
		
		placeholder = new Label(handler);
		placeholder.setDisplayType(DisplayType.BOX);
		placeholder.setColor(Colors.TEXT_PLACEHOLDER);
		
		text = new Label(handler);
		text.setDisplayType(DisplayType.BOX);
	}
	
	public TextField(float x, float y, float width, float height, Handler handler) {
		super(x, y, width, height, handler);
		
		placeholder = new Label("", x + paddingX, y + paddingY, width - paddingX, height - paddingY, AlignX.LEFT, AlignY.CENTER, handler);
		placeholder.setDisplayType(DisplayType.BOX);
		placeholder.setColor(Colors.TEXT_PLACEHOLDER);
		
		text = new Label("", x + paddingX, y + paddingY, width - paddingX, height - paddingY, AlignX.LEFT, AlignY.CENTER, handler);
		text.setDisplayType(DisplayType.BOX);
		text.setOverflow(Overflow.IGNORE);
	}
	
	@Override
	public void update(float dt) {
		contentUpdated = false;
		
		if(invisible || disabled) 
			return;
		
		hoverCursor(Cursor.IBEAM);
		
		if(handler.getMouseManager().isLeftReleased()) {
			handler.getMouseManager().resetLeftRelease();
			
			if(isOn()) {
				enableFocus();
			} else {
				disableFocus();
			}
		}
		
		if(focus) {
			String buffer = handler.getKeyManager().getTypingBuffer();
			
			if(!fullContent.contentEquals(buffer)) {
				fullContent = buffer;
				contentUpdated = true;
			}
		}
	}
	
	@Override
	public void renderGUI(long vg) {
		if(invisible)
			return;
		
		renderBackground(vg);
		renderText(vg);
		
		if(fullContent.isEmpty() && !placeholder.getContent().isEmpty()) {
			placeholder.renderGUI(vg);
		}
		
		if(!focus) {
			return;
		}
		
		if(System.currentTimeMillis() - lastCursorBlink <= CURSOR_BLINK_TIME / 2) {
			float[] bounds = new float[4];
			float cursorX = nvgTextBounds(vg, 0, 0, text.getContent(), bounds);
	        float cx = x + paddingX + cursorX + 1;

	        nvgBeginPath(vg);
	        nvgMoveTo(vg, cx, y + 4);
	        nvgLineTo(vg, cx, y + height - 4);
	        nvgStrokeColor(vg, text.getColor());
	        nvgStrokeWidth(vg, 1.0f);
	        nvgStroke(vg);
		} else if(System.currentTimeMillis() - lastCursorBlink > CURSOR_BLINK_TIME) {
			lastCursorBlink = System.currentTimeMillis();
		}
	}
	
	protected void renderBackground(long vg) {
		if(disabled) {
			nvgBeginPath(vg);
			nvgRoundedRect(vg, x, y, width, height, 5);
			nvgFillColor(vg, Colors.BACKGROUND_LIGHT);
			nvgFill(vg);
			
			return;
		}
		
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, width, height, 5);
		nvgFillColor(vg, Colors.BACKGROUND);
		nvgFill(vg);
		
		nvgStrokeWidth(vg, 2f);
		nvgStrokeColor(vg, focus ? Colors.OUTLINE : Colors.rgba(0, 0, 0, 255));
		nvgStroke(vg);
	}
	
	protected void renderText(long vg) {
		text.renderGUI(vg);
		
		if(contentUpdated) {
			text.setContent(fullContent);
			
			float contentWidth = TextRenderer.measureText(vg, text.getContent())[0];
			while(contentWidth > width - paddingX * 2) {
				text.setContent(text.getContent().substring(1));
				contentWidth = TextRenderer.measureText(vg, text.getContent())[0];
			}
		}
	}
	
	public void enableFocus() {
		focus = true;
		handler.getKeyManager().enableTyping();
		handler.getKeyManager().setTypingBuffer(fullContent);
	}
	
	public void disableFocus() {
		focus = false;
		handler.getKeyManager().disableTyping();
		handler.getKeyManager().clearTypingBuffer();
	}
	
	public boolean isOn() {
		if(handler.getMouseManager().getMouseX() >= x && handler.getMouseManager().getMouseX() <= x + width &&
		   handler.getMouseManager().getMouseY() >= y && handler.getMouseManager().getMouseY() <= y + height) {
			return true;
		} else {
			return false;
		}
	}

	public String getContent() {
		return fullContent;
	}
	
	public void setContent(String content) {
		this.fullContent = content;
		
		if(focus) {
			handler.getKeyManager().setTypingBuffer(content);
		}
		
		contentUpdated = true;
	}
	
	public Label getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(Label placeholder) {
		this.placeholder = placeholder;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		if(focus) enableFocus();
		else disableFocus();
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public float getPaddingX() {
		return paddingX;
	}

	public void setPaddingX(float padding) {
		this.paddingX = padding;
	}

	public float getPaddingY() {
		return paddingY;
	}

	public void setPaddingY(float padding) {
		this.paddingY = padding;
	}

	public byte getCursorOffset() {
		return cursorOffset;
	}

	public void setCursorOffset(byte cursorOffset) {
		this.cursorOffset = cursorOffset;
	}

	public byte getCursorWidth() {
		return cursorWidth;
	}

	public void setCursorWidth(byte cursorWidth) {
		this.cursorWidth = cursorWidth;
	}
}
