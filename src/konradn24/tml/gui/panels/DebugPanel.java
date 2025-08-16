package konradn24.tml.gui.panels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Style;
import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;
import konradn24.tml.gui.graphics.components.Label;
import konradn24.tml.gui.graphics.components.Label.DisplayType;
import konradn24.tml.gui.graphics.components.TextField;
import konradn24.tml.gui.graphics.layouts.ColumnLayout;
import konradn24.tml.gui.graphics.renderers.TextRenderer.Overflow;
import konradn24.tml.utils.Logging;

public class DebugPanel {
	
	public static final byte CONSOLE_LINES_LIMIT = 16;
	public static final byte COMMAND_LINE_HISTORY_LIMIT = 32;
	
	public static final NVGColor DEFAULT_COLOR = Colors.rgba(255, 255, 255, 255);
	public static final NVGColor ERROR_COLOR = Colors.rgba(255, 0, 0, 255);
	public static final NVGColor WARNING_COLOR = Colors.rgba(255, 255, 0, 255);
	
	public static final float WIDTH = Display.x(0.96f);
	public static final float LINE_HEIGHT = Display.y(0.008f);
	public static final float X = Style.centerX(WIDTH);
	public static final float Y = 0;
	public static final float PADDING_X = Display.x(0.005f);
	public static final float PADDING_Y = Display.y(0.01f);
	public static final float COMMAND_LINE_HEIGHT = Display.y(0.02f);
	
	private final float height;
	
	private boolean open;
	
	private int toggleKey;
	
	private ColumnLayout<Label> console;
	private TextField commandLine;
	
	private List<String> commandLineHistory;
	private byte commandLineHistoryPointer;
	private boolean commandLineHistoryPointerChanged;
	
	private List<String> commandQueue;
	private String loadedFrom;
	
	private Handler handler;
	
	private boolean clickCooldown;
	
	public DebugPanel(Handler handler) {
		this.handler = handler;
		
		this.console = new ColumnLayout<Label>(
			Label.class, CONSOLE_LINES_LIMIT, 
			X + PADDING_X, Y + PADDING_Y, WIDTH - PADDING_X * 2, LINE_HEIGHT,
			PADDING_Y, true, handler
		).customize((label, i) -> {
			label.setDisplayType(DisplayType.BOX);
			label.setAlignX(AlignX.LEFT);
			label.setAlignY(AlignY.CENTER);
			label.setOverflow(Overflow.ELLIPSIS);
			label.setColor(DEFAULT_COLOR);
			
			if(i < CONSOLE_LINES_LIMIT - 1) {
				return;
			}
			
			label.setContent("Welcome to Debug Console! Instantiated at: " + Logging.now());
		});
		
		this.commandLine = new TextField(X + PADDING_X, Y + console.getHeight() + PADDING_Y, WIDTH - PADDING_X * 2, COMMAND_LINE_HEIGHT, handler);
		this.commandLine.getPlaceholder().setContent("Type 'help' for commands list...");
		
		this.height = commandLine.getY() + commandLine.getHeight() + PADDING_Y;
		
		this.commandLineHistory = new ArrayList<>();
		this.commandLineHistoryPointer = -1;
		
		this.commandQueue = new ArrayList<>();
	}
	
	public void update(float dt) {
		getInput(toggleKey);
		
		if(!open) return;
		
		if(!commandQueue.isEmpty()) {
			for(String command : commandQueue) command(command);
			
			print("Loaded " + commandQueue.size() + " commands from " + loadedFrom, Colors.rgba(0, 255, 255, 255));
			commandQueue.clear();
		}
		
		commandLine.update(dt);
		
		if(!commandLine.isFocus()) {
			commandLineHistoryPointer = -1;
			commandLineHistoryPointerChanged = false;
		}
		
		if(!handler.getKeyManager().isPressed(GLFW_KEY_UP) && !handler.getKeyManager().isPressed(GLFW_KEY_DOWN))
			commandLineHistoryPointerChanged = false;
		
		if(commandLine.isFocus()) {
			if(!commandLineHistory.isEmpty() && !commandLineHistoryPointerChanged && handler.getKeyManager().isPressed(GLFW_KEY_UP)) {
				commandLineHistoryPointer++;
				if(commandLineHistoryPointer >= commandLineHistory.size())
					commandLineHistoryPointer = -1;
				
				String content = commandLineHistoryPointer == -1 ? "" : commandLineHistory.get(commandLineHistoryPointer);
				commandLine.setContent(content);
				
				commandLineHistoryPointerChanged = true;
			}
			
			if(!commandLineHistory.isEmpty() && !commandLineHistoryPointerChanged && handler.getKeyManager().isPressed(GLFW_KEY_DOWN)) {
				commandLineHistoryPointer--;
				if(commandLineHistoryPointer < -1)
					commandLineHistoryPointer = (byte) (commandLineHistory.size() - 1);
				
				String content = commandLineHistoryPointer == -1 ? "" : commandLineHistory.get(commandLineHistoryPointer);
				commandLine.setContent(content);
				
				commandLineHistoryPointerChanged = true;
			}

			if(commandLine.getContent() != null && commandLine.getContent().trim().length() > 0 && handler.getKeyManager().isPressed(GLFW_KEY_ENTER)) {
				command(commandLine.getContent().trim());
				commandLine.setContent("");
			}
		}
	}
	
	public void renderGUI(long vg) {
		if(!open) return;
		
		nvgBeginPath(vg);
		nvgRect(vg, X, Y, WIDTH, height);
		nvgFillColor(vg, Colors.COLOR_BACKGROUND);
		nvgFill(vg);
		
		nvgStrokeWidth(vg, 3f);
		nvgStrokeColor(vg, Colors.COLOR_OUTLINE);
		nvgStroke(vg);
		
		console.renderGUI(vg);
		commandLine.renderGUI(vg);
	}
	
	public void command(String command) {
		Logging.info("CMD In: " + command);
		print("> " + command, Colors.rgba(255, 0, 255, 255));
		
		if(commandLineHistory.contains(command))
			commandLineHistory.remove(command);
		
		commandLineHistory.add(0, command);
		
		if(commandLineHistory.size() > COMMAND_LINE_HISTORY_LIMIT)
			commandLineHistory.remove(commandLineHistory.size() - 1);
		
		commandLineHistoryPointer = -1;

//		String response = CommandHandler.handle(this, command);
//		
//		if(CommandHandler.isError()) print(response, ERROR_COLOR);
//		else if(CommandHandler.isWarning()) print(response, WARNING_COLOR);
//		else if(!response.startsWith("~NULL")) print(response);
//		
//		Logging.info("CMD Out: " + response);
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
		if(text.contains("\n")) {
			String[] lines = text.split("\n");
			
			for(String line : lines) {
				line = line.replaceAll("\n", " ");
				print(line);
			}
			
			return;
		}
		
		for(int i = 1; i < console.getComponents().length; i++) {
			console.getComponents()[i - 1].setContent(console.getComponents()[i].getContent());
			console.getComponents()[i - 1].setColor(console.getComponents()[i].getColor());
	    }

		console.getComponents()[console.getComponents().length - 1].setContent(text);
		console.getComponents()[console.getComponents().length - 1].setColor(DEFAULT_COLOR);
	}
	
	public void print(String text, NVGColor color) {
		for(int i = 1; i < console.getComponents().length; i++) {
			console.getComponents()[i - 1].setContent(console.getComponents()[i].getContent());
			console.getComponents()[i - 1].setColor(console.getComponents()[i].getColor());
	    }

		console.getComponents()[console.getComponents().length - 1].setContent(text);
		console.getComponents()[console.getComponents().length - 1].setColor(color);
	}
	
	public void clear() {
		for(Label label : console.getComponents()) {
			label.setContent("");
			label.setColor(DEFAULT_COLOR);
		}
	}
	
	public void clearHistory() {
		commandLineHistory.clear();
		commandLineHistoryPointer = -1;
		commandLineHistoryPointerChanged = false;
	}
	
	public void toggle() {
		open = !open;
	}
	
	public void getInput(int toggleKey) {
		if(handler.getKeyManager().isPressed(toggleKey)) {
			if(!clickCooldown) {
				open = !open;
				clickCooldown = true;
				
				if(open) commandLine.setFocus(true);
			}
		} else clickCooldown = false;
		
		if(open) {
			if(handler.getKeyManager().isPressed(GLFW_KEY_ESCAPE)) {
				handler.getKeyManager().lockKey(GLFW_KEY_ESCAPE);
				open = false;
			}
			
			if(handler.getKeyManager().isPressed(GLFW_KEY_TAB)) {
				handler.getKeyManager().lockKey(GLFW_KEY_TAB);
				commandLine.setFocus(true);
			}
		}
	}
	
	// GETTERS AND SETTERS

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getToggleKey() {
		return toggleKey;
	}

	public void setToggleKey(int toggleKey) {
		this.toggleKey = toggleKey;
	}

	public ColumnLayout<Label> getConsole() {
		return console;
	}
	
	public TextField getCommandLine() {
		return commandLine;
	}
}
