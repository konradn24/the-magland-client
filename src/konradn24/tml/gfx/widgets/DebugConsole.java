package konradn24.tml.gfx.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import konradn24.tml.Handler;
import konradn24.tml.debug.CommandHandler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.components.TextField;
import konradn24.tml.utils.Utils;

public class DebugConsole {
	
	public static final byte MINIMUM_LINE_HEIGHT = 15;
	public static final byte COMMAND_LINE_HEIGHT = 20;
	public static final byte TEXT_LINES_AMOUNT = 64;
	public static final byte COMMAND_LINE_HISTORY_LIMIT = 16;
	
	public static final Color DEFAULT_COLOR = new Color(255, 255, 255);
	public static final Color ERROR_COLOR = new Color(255, 0, 0);
	public static final Color WARNING_COLOR = new Color(255, 255, 0);
	public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 195);
	public static final Font DEFAULT_TEXT_FONT = new Font("Arial", 0, 14);
	public static final Font DEFAULT_COMMAND_LINE_FONT = new Font("Arial", 0, 16);
	
	private int x, y, width, height;
	private boolean open;
	
	private int key;
	
	private int lines;
	private int lineHeight, minimumLineHeight, textLinesHeight;
	private int commandLineHeight;
	
	private TextField commandLine;
	private Label[] textLines;
	private Font font;
	private Color color;
	
	private List<String> commandLineHistory;
	private byte commandLineHistoryPointer;
	private boolean commandLineHistoryPointerChanged;
	
	private List<String> commandQueue;
	private String loadedFrom;
	
	private Handler handler;
	
	private boolean clickCooldown;
	
	public DebugConsole(Handler handler) {
		this.handler = handler;
		this.x = 0;
		this.y = 0;
		this.width = handler.getDisplayWidth();
		this.height = handler.getDisplayHeight();
		this.minimumLineHeight = MINIMUM_LINE_HEIGHT;
		this.commandLineHeight = COMMAND_LINE_HEIGHT;
		this.textLinesHeight = (int) Math.floor(height - minimumLineHeight * 1.2);
		this.lines = (int) Math.floor(textLinesHeight / commandLineHeight);
		this.lineHeight = height / lines;
		
		this.commandLine = new TextField(x, (int) (textLinesHeight + commandLineHeight * 0.2), handler);
		this.textLines = new Label[TEXT_LINES_AMOUNT];
		
		this.commandLineHistory = new ArrayList<>();
		this.commandLineHistoryPointer = -1;
		
		this.commandQueue = new ArrayList<>();
		
		setFont(DEFAULT_TEXT_FONT, DEFAULT_COMMAND_LINE_FONT);
		setColor(DEFAULT_COLOR);
		
		useGraphics();
	}
	
	public DebugConsole(int x, int y, int width, int height, Handler handler) {
		this.handler = handler;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.minimumLineHeight = MINIMUM_LINE_HEIGHT;
		this.commandLineHeight = COMMAND_LINE_HEIGHT;
		this.textLinesHeight = (int) Math.floor(height - minimumLineHeight * 1.2);
		this.lines = (int) Math.floor(textLinesHeight / commandLineHeight);
		this.lineHeight = height / lines;
		
		this.commandLine = new TextField(x, (int) (textLinesHeight + commandLineHeight * 0.2), handler);
		this.textLines = new Label[TEXT_LINES_AMOUNT];
		
		this.commandLineHistory = new ArrayList<>();
		this.commandLineHistoryPointer = -1;
		
		this.commandQueue = new ArrayList<>();
		
		setFont(DEFAULT_TEXT_FONT, DEFAULT_COMMAND_LINE_FONT);
		setColor(DEFAULT_COLOR);
		
		useGraphics();
	}
	
	public void tick() {
		switchOpen(key);
		
		if(!open) return;
		
		if(!commandQueue.isEmpty()) {
			for(String command : commandQueue) command(command);
			
			print("Loaded " + commandQueue.size() + " commands from " + loadedFrom, Color.cyan, font);
			commandQueue.clear();
		}
		
		commandLine.tick();
		
		if(!commandLine.isFocus()) {
			commandLineHistoryPointer = -1;
			commandLineHistoryPointerChanged = false;
		}
		
		if(!handler.getKeyManager().getKeys()[KeyEvent.VK_UP] && !handler.getKeyManager().getKeys()[KeyEvent.VK_DOWN])
			commandLineHistoryPointerChanged = false;
		
		if(commandLine.isFocus()) {
			if(!commandLineHistory.isEmpty() && !commandLineHistoryPointerChanged && handler.getKeyManager().getKeys()[KeyEvent.VK_UP]) {
				commandLineHistoryPointer++;
				if(commandLineHistoryPointer >= commandLineHistory.size())
					commandLineHistoryPointer = -1;
				
				String content = commandLineHistoryPointer == -1 ? "" : commandLineHistory.get(commandLineHistoryPointer);
				commandLine.setContent(content);
				
				commandLineHistoryPointerChanged = true;
			}
			
			if(!commandLineHistory.isEmpty() && !commandLineHistoryPointerChanged && handler.getKeyManager().getKeys()[KeyEvent.VK_DOWN]) {
				commandLineHistoryPointer--;
				if(commandLineHistoryPointer < -1)
					commandLineHistoryPointer = (byte) (commandLineHistory.size() - 1);
				
				String content = commandLineHistoryPointer == -1 ? "" : commandLineHistory.get(commandLineHistoryPointer);
				commandLine.setContent(content);
				
				commandLineHistoryPointerChanged = true;
			}

			if(commandLine.getContent() != null && commandLine.getContent().trim().length() > 0 && handler.getKeyManager().getKeys()[KeyEvent.VK_ENTER]) {
				command(commandLine.getContent().trim());
				commandLine.setContent("");
			}
		}
	}
	
	public void render(Graphics2D g) {
		if(!open) return;
		
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(x, y, width, height + commandLineHeight);
		
		for(int i = 0; i < lines; i++) {
			textLines[i].render(g);
		}
		
		commandLine.render(g);
	}
	
	public void command(String command) {
		Logging.info("CMD In: " + command);
		print("> " + command, Color.magenta, DEFAULT_TEXT_FONT);
		
		if(commandLineHistory.contains(command))
			commandLineHistory.remove(command);
		
		commandLineHistory.add(0, command);
		
		if(commandLineHistory.size() > COMMAND_LINE_HISTORY_LIMIT)
			commandLineHistory.remove(commandLineHistory.size() - 1);
		
		commandLineHistoryPointer = -1;

		String response = CommandHandler.handle(this, command);
		
		if(CommandHandler.isError()) print(response, ERROR_COLOR, DEFAULT_TEXT_FONT);
		else if(CommandHandler.isWarning()) print(response, WARNING_COLOR, DEFAULT_TEXT_FONT);
		else if(!response.startsWith("~NULL")) print(response);
		
		Logging.info("CMD Out: " + response);
	}
	
	public boolean loadFromFile(String path) {
		File file = new File(path);
		
		try {
			Scanner reader = new Scanner(file);
			
			while(reader.hasNextLine()) {
				String command = reader.nextLine();
				commandQueue.add(command);
			}
			
			reader.close();
			
			loadedFrom = path;
			
			return true;
		} catch(FileNotFoundException e) {
			Logging.warning("DebugConsole: load from file failed - file does not exist");
			
			return false;
		}
	}
	
	public void print(String text) {
		int textWidth = handler.getGame().getDisplay().getFrame().getFontMetrics(font).stringWidth(text);
		float charWidth = (float) textWidth / text.length();
		int charsPerLine = (int) Math.floor((float) width * 0.98 / charWidth);
		
		List<String> textParts = new ArrayList<>();
		
		int lastCut = 0;
		int counter = 0;
		for(int i = 0; i < text.length(); i++) {
			counter++;
			
			if(i == text.length() - 1) {
				textParts.add(0, text.substring(lastCut, i + 1));
				break;
			}
			
			if(text.charAt(i) == '\n' || counter >= charsPerLine) {
				if(counter > 1) textParts.add(0, text.substring(lastCut, i));
				
				lastCut = i;
				counter = 0;
			}
		}
		
		int parts = textParts.size();
		
		for(int i = TEXT_LINES_AMOUNT - 1; i >= 0; i--) {
			if(i - parts >= 0) {
				textLines[i].setContent(textLines[i - parts].getContent());
				textLines[i].setColor(textLines[i - parts].getColor());
				textLines[i].setFont(textLines[i - parts].getCurrentFont());
			} else {
				textLines[i].setContent(textParts.get(i));
				textLines[i].setColor(color);
				textLines[i].setFont(font);
			}
		}
	}
	
	public void print(String text, Color color, Font font) {
		int textWidth = handler.getGame().getDisplay().getFrame().getFontMetrics(font).stringWidth(text);
		float charWidth = (float) textWidth / text.length();
		int charsPerLine = (int) Math.floor((float) width * 0.98 / charWidth);
		
		List<String> textParts = Utils.breakString(text, charsPerLine);
		int parts = textParts.size();
		
		for(int i = TEXT_LINES_AMOUNT - 1; i >= 0; i--) {
			if(i - parts >= 0) {
				textLines[i].setContent(textLines[i - parts].getContent());
				textLines[i].setColor(textLines[i - parts].getColor());
				textLines[i].setFont(textLines[i - parts].getCurrentFont());
			} else {
				textLines[i].setContent(textParts.get(i));
				textLines[i].setColor(color);
				textLines[i].setFont(font);
			}
		}
	}
	
	public void clear() {
		for(Label label : textLines) {
			label.setContent("");
			label.setColor(DEFAULT_COLOR);
			label.setFont(DEFAULT_TEXT_FONT);
		}
	}
	
	public void clearHistory() {
		commandLineHistory.clear();
		commandLineHistoryPointer = -1;
		commandLineHistoryPointerChanged = false;
	}
	
	public void switchOpen() {
		open = !open;
	}
	
	public void switchOpen(int key) {
		if(handler.getKeyManager().getKeys()[key]) {
			if(!clickCooldown) {
				open = !open;
				clickCooldown = true;
				
				if(open) commandLine.setFocus(true);
			}
		} else clickCooldown = false;
	}
	
	private void useGraphics() {
		for(int i = 0; i < TEXT_LINES_AMOUNT; i++) textLines[i] = new Label("", handler);
		
		for(int i = 0; i < lines; i++) {
			int index = lines - i - 1;
			textLines[index].setMarginX((int) (width * 0.02));
			textLines[index].setY(i * lineHeight);
		}
		
		commandLine.setX((int) ((width - width * 0.96) / 2));
		commandLine.setY((int) (lines * lineHeight + COMMAND_LINE_HEIGHT * 0.2));
		commandLine.setFixedWidth((int) (width * 0.96));
		commandLine.setPaddingX((int) (width * 0.008));
		commandLine.setPaddingY(0);
	}
	
	// GETTERS AND SETTERS

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

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public TextField getCommandLine() {
		return commandLine;
	}

	public Label[] getTextLines() {
		return textLines;
	}

	public int getLines() {
		return lines;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getTextLinesHeight() {
		return textLinesHeight;
	}

	public int getMinimumLineHeight() {
		return minimumLineHeight;
	}

	public void setMinimumLineHeight(int minimumLineHeight) {
		this.minimumLineHeight = minimumLineHeight;
	}

	public int getCommandLineHeight() {
		return commandLineHeight;
	}

	public void setCommandLineHeight(int commandLineHeight) {
		this.commandLineHeight = commandLineHeight;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		
		commandLine.setFont(font);
		
		for(Label label : textLines) {
			if(label == null) break;
			label.setFont(font);
		}
	}
	
	public void setFont(Font font, Font commandLineFont) {
		this.font = font;
		
		commandLine.setFont(commandLineFont);
		
		for(Label label : textLines) {
			if(label == null) break;
			label.setFont(font);
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		
		commandLine.setColor(color);
		
		for(Label label : textLines) {
			if(label == null) break;
			label.setColor(color);
		}
	}
}
