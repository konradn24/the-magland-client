package konradn24.tml.gui.graphics.widgets.msgbox;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Button;
import konradn24.tml.gui.graphics.components.Label;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.gui.graphics.renderers.TextRenderer;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;
import konradn24.tml.states.State;
import konradn24.tml.utils.Logging;

public class MessageBox {

	public static final byte TYPE_OK = 0;
	public static final byte TYPE_YES_NO = 1;
	public static final byte TYPE_YES_NO_CANCEL = 2;

	public static final byte YES = 0;
	public static final byte NO = 1;
	public static final byte CANCEL = 2;
	
	private static final float MIN_HEIGHT = Display.y(.0667f);
	
	private static final float BAR_HEIGHT = Display.y(.0315f);
	private static final float TEXT_MARGIN_X = Display.x(.0042f);
	private static final float TEXT_MARGIN_Y = Display.y(.0046f);
	private static final float BUTTONS_MARGIN_X = Display.x(.0052f);
	private static final float BUTTONS_MARGIN_Y = Display.y(.0093f);
	private static final float BUTTON_WIDTH = Display.x(.0286f);
	private static final float BUTTON_HEIGHT = Display.y(.0185f);
	
	private float x, y, width, height;
	private int type;
	
	private Label title, message;
	
	private NVGColor barColor, windowColor;
	
	private List<Button> buttons;
	private Button buttonOk, buttonYes, buttonNo, buttonCancel;
	
	private List<Byte> responseCodes;
	private Callback callback;
	
	private Handler handler;
	
	public interface Callback {
		void execute(byte response);
	}
	
	public MessageBox(int type, String title, String message, float width, Handler handler) {
		this.type = type;
		this.width = width;
		this.handler = handler;
		
		this.x = Display.LOGICAL_WIDTH / 2 - width / 2;
		this.y = Display.LOGICAL_HEIGHT / 2;
		
		barColor = Colors.WINDOW_BAR_BACKGROUND;
		windowColor = Colors.WINDOW_BACKGROUND;
		
		this.title = new Label(title, x + TEXT_MARGIN_X, y + TEXT_MARGIN_Y, width - TEXT_MARGIN_X * 2, BAR_HEIGHT, AlignX.LEFT, AlignY.CENTER, handler);
		this.title.setFontSize(32f);
		this.title.setColor(Colors.TEXT);
		this.title.setDisplayType(DisplayType.BOX);
		this.title.setOverflow(Overflow.ELLIPSIS);
		
		this.message = new Label(message, x + TEXT_MARGIN_X, y + BAR_HEIGHT + TEXT_MARGIN_Y, width - TEXT_MARGIN_X * 2, MIN_HEIGHT, AlignX.LEFT, AlignY.TOP, handler);
		this.message.setFontSize(24f);
		this.message.setColor(Colors.TEXT);
		this.message.setDisplayType(DisplayType.BOX);
		this.message.setOverflow(Overflow.WRAP);
		
		buttons = new ArrayList<>();
		responseCodes = new ArrayList<>();
	}
	
	public MessageBox(int type, String title, String message, Handler handler) {
		this.type = type;
		this.handler = handler;
		
		this.width = Display.LOGICAL_WIDTH / 2;
		this.x = Display.LOGICAL_WIDTH / 2 - width / 2;
		this.y = Display.LOGICAL_HEIGHT / 2 - 100;
		
		barColor = Colors.WINDOW_BAR_BACKGROUND;
		windowColor = Colors.WINDOW_BACKGROUND;
		
		this.title = new Label(title, x + TEXT_MARGIN_X, y, width - TEXT_MARGIN_X * 2, BAR_HEIGHT, AlignX.LEFT, AlignY.CENTER, handler);
		this.title.setFontSize(32f);
		this.title.setColor(Colors.TEXT);
		this.title.setDisplayType(DisplayType.BOX);
		this.title.setOverflow(Overflow.ELLIPSIS);
		
		this.message = new Label(message, x + TEXT_MARGIN_X, y + BAR_HEIGHT, width - TEXT_MARGIN_X * 2, MIN_HEIGHT, AlignX.LEFT, AlignY.TOP, handler);
		this.message.setFontSize(24f);
		this.message.setColor(Colors.TEXT);
		this.message.setDisplayType(DisplayType.BOX);
		this.message.setOverflow(Overflow.WRAP);
		
		buttons = new ArrayList<>();
		responseCodes = new ArrayList<>();
	}
	
	public void initButtons(List<Button> custom, List<Byte> responseCodes) {
		if(custom != null && responseCodes != null) {
			this.buttons = custom;
			this.responseCodes = responseCodes;
			return;
		}
		
		float buttonsY = y + height - BUTTON_HEIGHT - BUTTONS_MARGIN_Y;
		
		switch(type) {
			default: {
				type = TYPE_OK;
				break;
			}
			
			case TYPE_OK: {
				buttonOk = new Button("Ok", x + width / 2 - BUTTON_WIDTH / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
				
				buttonOk.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(YES);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + YES);
				});
				
				this.buttons.add(buttonOk);
				this.responseCodes.add(YES);
				
				break;
			}
			
			case TYPE_YES_NO: {
				float buttonsWidth = BUTTON_WIDTH * 2 + BUTTONS_MARGIN_X;
				
				buttonYes = new Button("Yes", x + width / 2 - buttonsWidth / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
				buttonNo = new Button("No", x + width / 2 - buttonsWidth / 2 + BUTTON_WIDTH + BUTTONS_MARGIN_X, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			
				buttonYes.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(YES);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + YES);
				});
				
				buttonNo.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(NO);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + NO);
				});
				
				this.buttons.add(buttonYes);
				this.buttons.add(buttonNo);
				this.responseCodes.add(YES);
				this.responseCodes.add(NO);
				
				break;
			}
			
			case TYPE_YES_NO_CANCEL: {
				float buttonsWidth = BUTTON_WIDTH * 3 + BUTTONS_MARGIN_X * 2;
				
				buttonYes = new Button("Yes", x + width / 2 - buttonsWidth / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
				buttonNo = new Button("No", x + width / 2 - buttonsWidth / 2 + BUTTON_WIDTH + BUTTONS_MARGIN_X, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
				buttonCancel = new Button("Cancel", x + width / 2 - buttonsWidth / 2 + (BUTTON_WIDTH + BUTTONS_MARGIN_X) * 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			
				buttonYes.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(YES);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + YES);
				});
				
				buttonNo.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(NO);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + NO);
				});
				
				buttonCancel.setOnLeftClick(() -> {
					State.getCurrentState().getDialogsManager().queueCloseMessageBox(this);
					
					if(callback != null)
						callback.execute(CANCEL);
					
					Logging.info("MessageBox (title: " + title + ") callback: " + CANCEL);
				});
				
				this.buttons.add(buttonYes);
				this.buttons.add(buttonNo);
				this.buttons.add(buttonCancel);
				this.responseCodes.add(YES);
				this.responseCodes.add(NO);
				this.responseCodes.add(CANCEL);
				
				break;
			}
		}
		
		this.buttons.forEach(button -> {
			button.getLabel().setFontSize(20f);
		});
	}
	
	public void update(float dt) {
		for(int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			button.update(dt);
		}
	}
	
	public void renderGUI(long vg) {
		if(height == 0) {
			nvgFontFace(vg, message.getFont());
			nvgFontSize(vg, message.getFontSize());
			height = BAR_HEIGHT + TextRenderer.measureText(vg, message.getContent(), width, message.getOverflow())[1] + BUTTON_HEIGHT + BUTTONS_MARGIN_Y * 2;
			initButtons(null, null);
		}
		
		nvgBeginPath(vg);
		nvgRect(vg, x, y + BAR_HEIGHT, width, height - BAR_HEIGHT);
		nvgFillColor(vg, windowColor);
		nvgFill(vg);
		
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, BAR_HEIGHT);
		nvgFillColor(vg, barColor);
		nvgFill(vg);
		
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, height);
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, Colors.OUTLINE);
		nvgStroke(vg);
		
		title.renderGUI(vg);
		message.renderGUI(vg);
		
		for(Button button : buttons) button.renderGUI(vg);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public Label getTitle() {
		return title;
	}

	public void setTitle(Label title) {
		this.title = title;
	}

	public Label getMessage() {
		return message;
	}

	public void setMessage(Label message) {
		this.message = message;
	}

	public NVGColor getBarColor() {
		return barColor;
	}

	public void setBarColor(NVGColor barColor) {
		this.barColor = barColor;
	}

	public NVGColor getWindowColor() {
		return windowColor;
	}

	public void setWindowColor(NVGColor windowColor) {
		this.windowColor = windowColor;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public Button getButtonOk() {
		return buttonOk;
	}

	public void setButtonOk(Button buttonOk) {
		this.buttonOk = buttonOk;
	}

	public Button getButtonYes() {
		return buttonYes;
	}

	public void setButtonYes(Button buttonYes) {
		this.buttonYes = buttonYes;
	}

	public Button getButtonNo() {
		return buttonNo;
	}

	public void setButtonNo(Button buttonNo) {
		this.buttonNo = buttonNo;
	}

	public Button getButtonCancel() {
		return buttonCancel;
	}

	public void setButtonCancel(Button buttonCancel) {
		this.buttonCancel = buttonCancel;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}
