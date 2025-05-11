package konradn24.tml.gfx.widgets.msgbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.states.State;
import konradn24.tml.utils.Utils;

public class MessageBox {

	public static final byte TYPE_OK = 0;
	public static final byte TYPE_YES_NO = 1;
	public static final byte TYPE_YES_NO_CANCEL = 2;

	public static final byte YES = 0;
	public static final byte NO = 1;
	public static final byte CANCEL = 2;
	
	private static final int MIN_WIDTH = 210;
	private static final int MAX_WIDTH = 420;
	private static final int MIN_HEIGHT = 72;
	
	private static final int BAR_HEIGHT = 28;
	private static final int TEXT_MARGIN_Y = 5;
	private static final int TEXT_MARGIN_X = 4;
	private static final int BUTTON_MARGIN = 10;
	private static final int BUTTON_WIDTH = 55;
	private static final int BUTTON_HEIGHT = 20;
	
	private int x, y, width, height;
	private int type;
	
	private String title, message;
	private List<Label> labels;
	
	private Font titleFont, messageFont;
	private Color barColor, windowColor;
	
	private List<Button> buttons;
	private Button buttonOk, buttonYes, buttonNo, buttonCancel;
	
	private List<Byte> responseCodes;
	private Callback callback;
	
	private Handler handler;
	
	public interface Callback {
		void execute(byte response);
	}
	
	public MessageBox(int type, String title, String message, Handler handler) {
		this.type = type;
		this.title = title;
		this.message = message;
		this.handler = handler;
		
		titleFont = Presets.FONT_GLOBAL.deriveFont(Font.BOLD, 20f);
		messageFont = Presets.FONT_GLOBAL;
		
		barColor = Presets.COLOR_WINDOW_BAR_BACKGROUND;
		windowColor = Presets.COLOR_WINDOW_BACKGROUND;
		
		labels = new ArrayList<>();
		buttons = new ArrayList<>();
		responseCodes = new ArrayList<>();
		
		calculateSize();
		initButtons(null, null);
	}
	
	public void calculateSize() {
		FontMetrics metrics = handler.getGame().getDisplay().getCanvas().getFontMetrics(messageFont);
		int charsPerLine = MAX_WIDTH / metrics.charWidth(32);
		
		List<String> breakedMessage = Utils.breakString(message, charsPerLine, true);
		width = Math.max(MIN_WIDTH, metrics.stringWidth(Utils.getLongest(breakedMessage))) + TEXT_MARGIN_X * 2;
		height = MIN_HEIGHT + breakedMessage.size() * metrics.getHeight();
		
		labels.clear();
		
		Label titleLabel = new Label(title, x + TEXT_MARGIN_X, y, handler);
		titleLabel.setFont(titleFont);
		titleLabel.setColor(Presets.COLOR_WINDOW_TEXT);
		labels.add(titleLabel);
		
		for(int i = 0; i < breakedMessage.size(); i++) {
			Label label = new Label(breakedMessage.get(i), x + TEXT_MARGIN_X, y + (i * metrics.getHeight()) + BAR_HEIGHT + TEXT_MARGIN_Y, handler);
			label.setFont(messageFont);
			label.setColor(Presets.COLOR_WINDOW_TEXT);
			
			labels.add(label);
		}
	}
	
	public void initButtons(List<Button> custom, List<Byte> responseCodes) {
		if(custom != null && responseCodes != null) {
			this.buttons = custom;
			this.responseCodes = responseCodes;
			return;
		}
		
		int buttonsY = y + height - BUTTON_HEIGHT - BUTTON_MARGIN;
		
		switch(type) {
		default: type = TYPE_OK;
		
		case TYPE_OK: {
			buttonOk = new Button("Ok", x + width / 2 - BUTTON_WIDTH / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			
			buttonOk.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
				if(callback != null)
					callback.execute(YES);
				
				Logging.info("MessageBox (title: " + title + ") callback: " + YES);
			});
			
			this.buttons.add(buttonOk);
			this.responseCodes.add(YES);
			
			break;
		}
		
		case TYPE_YES_NO: {
			int buttonsWidth = BUTTON_WIDTH * 2 + BUTTON_MARGIN;
			
			buttonYes = new Button("Yes", x + width / 2 - buttonsWidth / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			buttonNo = new Button("No", x + width / 2 - buttonsWidth / 2 + BUTTON_WIDTH + BUTTON_MARGIN, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
		
			buttonYes.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
				if(callback != null)
					callback.execute(YES);
				
				Logging.info("MessageBox (title: " + title + ") callback: " + YES);
			});
			
			buttonNo.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
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
			int buttonsWidth = BUTTON_WIDTH * 3 + BUTTON_MARGIN * 2;
			
			buttonYes = new Button("Yes", x + width / 2 - buttonsWidth / 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			buttonNo = new Button("No", x + width / 2 - buttonsWidth / 2 + BUTTON_WIDTH + BUTTON_MARGIN, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
			buttonCancel = new Button("Cancel", x + width / 2 - buttonsWidth / 2 + (BUTTON_WIDTH + BUTTON_MARGIN) * 2, buttonsY, BUTTON_WIDTH, BUTTON_HEIGHT, handler);
		
			buttonYes.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
				if(callback != null)
					callback.execute(YES);
				
				Logging.info("MessageBox (title: " + title + ") callback: " + YES);
			});
			
			buttonNo.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
				if(callback != null)
					callback.execute(NO);
				
				Logging.info("MessageBox (title: " + title + ") callback: " + NO);
			});
			
			buttonCancel.setOnLeftClick(() -> {
				State.getState().getDialogsManager().queueCloseMessageBox(this);
				
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
	}
	
	public void tick() {
		for(int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			button.tick();
			
//			if(button.isLeftPressed()) {
//				State.getState().getDialogsManager().queueCloseMessageBox(this);
//				
//				byte response = responseCodes.get(i) != null ? responseCodes.get(i) : 0;
//				
//				if(callback != null)
//					callback.execute(response);
//				
//				Logging.info("MessageBox (title: " + title + ") callback: " + response);
//			}
		}
	}
	
	public void render(Graphics2D g) {
		g.setColor(windowColor);
		g.fillRect(x, y + BAR_HEIGHT, width, height - BAR_HEIGHT);
		
		g.setColor(barColor);
		g.fillRect(x, y, width, BAR_HEIGHT);
		g.drawRect(x, y, width, height);
		
		for(Label label : labels) label.render(g);
		for(Button button : buttons) button.render(g);
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

	public int getHeight() {
		return height;
	}

	public int getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public Font getMessageFont() {
		return messageFont;
	}

	public void setMessageFont(Font messageFont) {
		this.messageFont = messageFont;
	}
	
	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	public Color getWindowColor() {
		return windowColor;
	}

	public void setWindowColor(Color windowColor) {
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
	
	public void setTitleTextColor(Color color) {
		labels.get(0).setColor(color);
	}
	
	public void setTextColor(Color color) {
		for(int i = 1; i < labels.size(); i++)
			labels.get(i).setColor(color);
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}
