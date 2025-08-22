package konradn24.tml.debug.commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import konradn24.tml.Handler;
import konradn24.tml.Launcher;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.EntityManager;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.gui.panels.DebugPanel;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.items.Item;
import konradn24.tml.states.State;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Logging;
import konradn24.tml.utils.Modules;

public final class DebugCommands {
	
	public static final String NULL = "~NULL";
	public static final String UNDEFINED = "???";
	public static final String ERROR = "~E";
	public static final String WARNING = "~W";
	
	// Arguments amount
	private static final byte SYSTEM = 1;
	private static final byte LOGGING = 1;
	private static final byte MESSAGE_BOX = 3;
	private static final byte ADD = 1;
	private static final byte REMOVE = 1;
	private static final byte EQUIP = 1;
	private static final byte TP = 2;
	private static final byte GAME = 2;
	private static final byte RENDERING = 2;
	private static final byte SPAWN = 1;
	private static final byte KILL = 1;
	private static final byte KILL_ALL = 2;
	private static final byte FIND = 2;
	private static final byte XP = 2;
	
	private static Handler handler;
	
	private static boolean argAssertError;
	private static String argAssertErrorMsg;
	
	@Command(description = "Get help about all commands", 
			args = "[command/page] [argName]", 
			argsHint = {"Command name or commands list page number", "Argument name or number (zero-indexed) for detailed help"})
	public static String help(DebugPanel panel, String... args) {
		if(args.length == 0)
			return getCommandsList(0, 9);
		
		String command = args[0];
		String arg = args.length > 1 ? args[1] : null;
		
		try {
			int page = Math.max(Integer.parseInt(command) - 1, 0);
			
			return getCommandsList(page, 9);
		} catch(NumberFormatException e) {}
		
		if(arg == null)
			return getCommandDetails(command);
		
		return getArgDetails(command, arg);
	}
	
	public static String getCommandsList(int page, int elements) {
		String str = "Commands list " + (page + 1) + " ('help <command>' for details)";
		List<Method> methods = Arrays.asList(DebugCommands.class.getMethods());
		methods = methods.subList(page * elements, page * elements + elements);
		
		for(Method method : methods) {
			if(elements <= 0)
				break;
			
			if(!method.isAnnotationPresent(Command.class))
				continue;
			
			str = str.concat("\n" + method.getName());
			
			elements--;
		}
		
		return str;
	}
	
	public static String getCommandDetails(String commandName) {
		try {
			Method method = DebugCommands.class.getMethod(commandName, DebugPanel.class, String[].class);
			
			if(!method.isAnnotationPresent(Command.class))
				throw new NoSuchMethodException();
			
			Command command = method.getAnnotation(Command.class);
			String[] args = command.args().split(" ");
			String usage = args.length > 0 ? commandName + " " + String.join(" ", command.args()) : UNDEFINED;
			
			return "Usage: " + usage + "\nDescription: " + command.description();
		} catch (NoSuchMethodException | SecurityException e) {
			return "~E_BAD_ARGS: command " + commandName + " does not exist";
		}
	}
	
	public static String getArgDetails(String commandName, String arg) {
		try {
			Method method = DebugCommands.class.getMethod(commandName, DebugPanel.class, String[].class);
			
			if(!method.isAnnotationPresent(Command.class))
				throw new NoSuchMethodException();
			
			int argIndex = -1;
			try {
				argIndex = Integer.parseInt(arg);
			} catch(NumberFormatException e) {}
			
			Command command = method.getAnnotation(Command.class);
			String[] args = command.args().split(" ");
			
			for(int i = 0; i < args.length; i++)
				args[i] = args[i].replace("<", "").replace(">", "").replace("[", "").replace("]", "");
			
			argIndex = argIndex == -1 ? Arrays.asList(args).indexOf(arg) : argIndex;
			
			if(argIndex == -1 || argIndex < 0 || argIndex >= args.length)
				return "~E_BAD_ARGS: argument " + arg + " does not exist";
			
			return argIndex < command.argsHint().length ? command.argsHint()[argIndex] : "No description :(";
		} catch (NoSuchMethodException | SecurityException e) {
			return "~E_BAD_ARGS: command " + commandName + " does not exist";
		}
	}
	
	@Command(description = "System information",
			args = "<module>",
			argsHint = {"Specifies which information to show [time, info]"})
	public static String system(DebugPanel panel, String... args) {
		if(args.length < SYSTEM)
			return missingArgsString(args, SYSTEM);
		
		String module = args[0];
		
		if(module.equals("time"))
			return "Uptime: " + (System.currentTimeMillis() - Launcher.time) / 1000 / 60 + " minutes";
		
		if(module.equals("info"))
			return "Game version: " + Launcher.VERSION + "\n"
				 	+ "Date and time: " + Logging.now() + "\n"
				 	+ "Uptime: " + (System.currentTimeMillis() - Launcher.time) / 1000 / 60 + " minutes\n"
		 			+ "Java version: " + System.getProperty("java.version") + "\n"
 					+ "OS: " + System.getProperty("os.name") + "\n"
					+ "OS architecture: " + System.getProperty("os.arch") + "\n"
					+ "OS version: " + System.getProperty("os.version") + "\n"
					+ "OS user: " + System.getProperty("user.name");
		
		return "~E_BAD_ARGS: " + module + " cannot be recognized";
	}
	
	@Command(description = "Logging configuration",
			args = "<module>",
			argsHint = {"Specifies action or information to show [path, start, stop, clear, clearAll, date]"})
	public static String logging(DebugPanel panel, String... args) throws IOException {
		if(args.length < LOGGING)
			return missingArgsString(args, LOGGING);
		
		String module = args[0];
		
		if(module.equals("path"))
			return Logging.getFile().getAbsolutePath();
		
		if(module.equals("start")) {
			Logging.startFileWriter();
			return NULL;
		}
		
		if(module.equals("stop")) {
			Logging.stopFileWriter();
			return NULL;
		}
		
		if(module.equals("clear")) {
			Logging.getFileWriter().close();
		
			Logging.getFile().delete();
			
			Logging.init();
			
			if(Logging.isFileWriterStopped())
				return "~E_IO_FS: check logs for more information";
			
			return "Cleared current logs file";
		}
		
		if(module.equals("clearAll")) {
			int[] result = Logging.clearLogs();
			
			return "Deleted " + result[0] + " of " + result[1] + " files";
		}
		
		if(module.equals("date")) {
			return Logging.now();
		}
		
		return "~E_BAD_ARGS: " + module + " cannot be recognized";
	}
	
	@Command(description = "Shows popup message box",
			args = "<type> <title> <message>")
	public static String messageBox(DebugPanel panel, String... args) {
		if(args.length < MESSAGE_BOX)
			return missingArgsString(args, MESSAGE_BOX);
		
		byte type = Byte.parseByte(args[0]);
		String title = args[1];
		String message = args[2];
		
		MessageBox messageBox = new MessageBox(type, title, message, handler);
		State.showMessageBox(messageBox);
		
		return NULL;
	}
	
	@Command(description = "Clears panel")
	public static String clear(DebugPanel panel, String... args) {
		panel.clear();
		return NULL;
	}
	
	@Command(description = "Clears command input history")
	public static String clearHistory(DebugPanel panel, String... args) {
		panel.clearHistory();
		return NULL;
	}
	
	@Command(description = "Returns player data")
	public static String player(DebugPanel panel, String... args) {
		return handler.getPlayer().toString();
	}
	
	@Command(description = "Returns world data")
	public static String world(DebugPanel panel, String... args) {
		return handler.getWorld().toString();
	}
	
	@Command(description = "Adds item to player's inventory",
			args = "<item> [amount]",
			argsHint = {"Item name or numeric ID", "Amount of specified item to add (ignores inventory's weight limit)"})
	public static String add(DebugPanel panel, String... args) {
		if(args.length < ADD)
			return missingArgsString(args, ADD);
		
		String itemID = args[0];
		int amount = args.length > ADD ? Integer.parseInt(args[1]) : 1;
		
		Item item = Item.getItem(itemID);
		if(item == null)
			return "~E_BAD_ARGS: item " + itemID + " does not exist";
		
		boolean success = handler.getPlayer().getInventory().add(new InventoryProperty(item, amount));
		
		return success ? NULL : "~E_???: failed to add item";
	}
	
	@Command(description = "Removes item from player's inventory",
			args = "<item> [amount]",
			argsHint = {"Item name", "Amount of specified item to remove"})
	public static String remove(DebugPanel panel, String... args) {
		if(args.length < REMOVE)
			return missingArgsString(args, REMOVE);
		
		String itemID = args[0];
		int amount = args.length > REMOVE ? Integer.parseInt(args[1]) : 1;
		
		Item item = Item.getItem(itemID);
		if(item == null)
			return "~E_BAD_ARGS: item " + itemID + " does not exist";
		
		boolean success = handler.getPlayer().getInventory().remove(item, amount);
		
		return success ? NULL : "~E_???: failed to remove item (maybe not enough items to remove?)";
	}
	
	@Command(description = "Removes item from player's inventory by property index",
			args = "<property index> [amount]",
			argsHint = {"Inventory property index", "Amount of specified item to remove"})
	public static String removeProperty(DebugPanel panel, String... args) {
		if(args.length < REMOVE)
			return missingArgsString(args, REMOVE);
		
		int index = Integer.parseInt(args[0]);
		int amount = args.length > REMOVE ? Integer.parseInt(args[1]) : 1;
		
		InventoryProperty property = handler.getPlayer().getInventory().getItems().get(index);
		boolean success = handler.getPlayer().getInventory().remove(property, amount);
		
		return success ? NULL : "~E_???: failed to remove property";
	}
	
	@Command(description = "Equips provided item",
			args = "<item>",
			argsHint = "Item name or inventory property index")
	public static String equip(DebugPanel panel, String... args) {
		if(args.length < EQUIP)
			return missingArgsString(args, EQUIP);
		
		String itemID = args[0];
		int index = -1;
		
		Item item;
		
		try {
			index = Integer.parseInt(itemID);
			handler.getPlayer().getInventory().select(index);
			return NULL;
		} catch(NumberFormatException e) {
			item = Item.getItem(itemID);
		}
		
		if(item == null)
			return "~E_BAD_ARGS: item " + itemID + " does not exist";
		
		handler.getPlayer().getInventory().select(item);
		
		return NULL;
	}
	
	@Command(description = "Teleports player to specified location",
			args = "<x> <y>",
			argsHint = {"Teleport location's X coordinate", "Teleport location's Y coordinate"})
	public static String tp(DebugPanel panel, String... args) {
		if(args.length < TP)
			return missingArgsString(args, TP);
		
		float x = Float.parseFloat(args[0]);
		float y = Float.parseFloat(args[1]);
		
		handler.getPlayer().setWorldX(x);
		handler.getPlayer().setWorldY(y);
		
		return NULL;
	}
	
	@Command(description = "Game rules options",
			args = "<action> <rule>",
			argsHint = {"Action to perform [get, enable, disable]", "Rule name ('get gameRules' to see possible rules)"})
	public static String game(DebugPanel panel, String... args) throws IllegalArgumentException, IllegalAccessException {
		if(args.length < GAME)
			return missingArgsString(args, GAME);
		
		String actionStr = args[0];
		String rule = args[1];
		
		if(!actionStr.equals("get") && !actionStr.equals("enable") && !actionStr.equals("disable"))
			return "~E_BAD_ARGS: action must be 'get', 'enable' or 'disable'";
		
		if(!handler.getGameRules().checkExistance(rule))
			return "~E_BAD_ARGS: rule " + rule + " does not exist";
		
		boolean get = actionStr.equals("get");
		boolean action = actionStr.equals("enable");
		
		if(get) {
			boolean enabled = handler.getGameRules().isEnabled(rule);
			
			return "Rule " + rule + " is currently " + (enabled ? "enabled" : "disabled");
		}
		
		if(!handler.getGameRules().set(rule, action))
			return "~E_???: failed to change mode";
		
		return "Game rule " + rule + " " + actionStr + "d";
	}
	
	@Command(description = "Renderer options",
			args = "<action> <rule>",
			argsHint = {"Action to perform [get, enable, disable]", "Rule name ('get renderingRules' to see possible rules)"})
	public static String rendering(DebugPanel panel, String... args) throws IllegalArgumentException, IllegalAccessException {
		if(args.length < RENDERING)
			return missingArgsString(args, RENDERING);
		
		String actionStr = args[0];
		String rule = args[1];
		
		boolean get = actionStr.equals("get");
		boolean action = actionStr.equals("enable");
		boolean isRuleDebug = rule.equals("debug");
		
		if(!get && !actionStr.equals("enable") && !actionStr.equals("disable"))
			return "~E_BAD_ARGS: action must be 'get', 'enable' or 'disable'";
		
		if(!isRuleDebug && !handler.getRenderingRules().checkExistance(rule)) {
			return "~E_BAD_ARGS: rule " + rule + " does not exist";
		}
		
		if(get) {
			boolean enabled = handler.getRenderingRules().isEnabled(rule);
			
			return "Rule " + rule + " is currently " + (enabled ? "enabled" : "disabled");
		}
		
		if(!handler.getRenderingRules().set(rule, action)) {
			return "~E_???: failed to change rule";
		}
		
		return "Renderer rule " + rule + " " + actionStr + "d";
	}
	
	@SuppressWarnings("unchecked")
	@Command(description = "Spawns specified entity",
			args = "<entityName>",
			argsHint = {"Name of entity to spawn"})
	public static String spawn(DebugPanel panel, String... args) throws ClassNotFoundException {
		if(args.length < SPAWN)
			return missingArgsString(args, SPAWN);
		
		String entityName = args[0];
		Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(Modules.ENTITIES + entityName);
		
		boolean placed = handler.getPlayer().placeEntity(entityClass);
		
		if(!placed)
			return "E_???: failed to spawn entity";
		
		return NULL;
	}
	
	@Command(description = "Kills specified entity",
			args = "<enityID>",
			argsHint = {"ID (hash code) of entity to kill"})
	public static String kill(DebugPanel panel, String... args) {
		if(args.length < KILL)
			return missingArgsString(args, KILL);
		
		int entityID = Integer.parseInt(args[0]);
		
		Entity entity = handler.getWorld().getEntityManager().getEntity(entityID);
		if(entity == null)
			return "~E_BAD_ARGS: entity does not exist";
		
		entity.vanish();
		
		return NULL;
	}
	
	@Command(description = "Kills currently visible or all entities of specified class",
			args = "<enityClass> <range>",
			argsHint = {"Name of entities' class to kill", "Range of killing (all - world; here - visible)"})
	public static String killAll(DebugPanel panel, String... args) {
		if(args.length < KILL_ALL)
			return missingArgsString(args, KILL_ALL);
		
		String className = args[0];
		String range = args[1];
		
		int killed = 0;
		
		if(range.equals("all")) {
			List<Entity> toKill = handler.getWorld().getEntityManager().getEntities(className);
			toKill.forEach(entity -> entity.vanish());
			
			killed = toKill.size();
		} else if(range.equals("here")) {
			List<Entity> toKill = handler.getWorld().getEntityManager().getEntities(className).stream()
				.filter(entity -> entity.getDistanceFromPlayer() < EntityManager.RENDER_DISTANCE).collect(Collectors.toList());
			
			toKill.forEach(entity -> entity.vanish());
			
			killed = toKill.size();
		} else {
			return "~E_BAD_ARGS: wrong range provided (all / here)";
		}
		
		return "Killed " + killed + " entities";
	}
	
	@Command(description = "Returns nearest location of specified object (only in loaded chunks for entity / only in 100 km range at looking direction for biome)",
			args = "<type> <name>",
			argsHint = {"Type of object to find (entity, biome)", "Object's name or ID (hash code)"})
	public static String find(DebugPanel panel, String... args) {
		if(args.length < FIND)
			return missingArgsString(args, FIND);
		
		String type = args[0];
		
		assertArg(type, "entity", "biome");
		if(assertError()) return argAssertErrorMsg;
		
		String name = args[1];
		int id = 0;
		
		boolean findByID = false;
		try {
			id = Integer.parseInt(name);
			
			findByID = true;
		} catch(NumberFormatException e) {}
		
		if(type.equals("entity")) {
			if(findByID) {
				Entity found = handler.getWorld().getEntityManager().getEntity(id);
				if(found == null)
					return "Entity not found!";
				
				return "Found at: " + found.getX() + ", " + found.getY();
			}
			
			List<Entity> found = handler.getWorld().getEntityManager().getEntities(name);
			if(found.isEmpty())
				return "Entity not found!";
			
			Entity nearest = found.get(0);
			for(Entity entity : found) {
				if(entity.getDistanceFromPlayer() < nearest.getDistanceFromPlayer())
					nearest = entity;
			}
			
			return "Found at: " + nearest.getX() / Tile.SIZE + ", " + nearest.getY() / Tile.SIZE;
		} else if(type.equals("biome")) {
//			int currentX = (int) (handler.getPlayer().getWorldX()), currentY = (int) (handler.getPlayer().getWorldY());
//			int direction = 0; // 0 = right; 1 = down; 2 = left; 3 = up
//			int moves = 1, movesLeft = 1;
//			
//			int i = 0;
//			while(moves < 1000) {	
//				if(direction == 0)
//					currentX += 1;
//				else if(direction == 1)
//					currentY += 1;
//				else if(direction == 2)
//					currentX -= 1;
//				else
//					currentY -= 1;
//				
//				if(handler.getWorld().getChunkManager().getLoadedChunks().size() > 128) {
//					handler.getWorld().getChunkManager().unloadFarChunks(handler.getPlayer().getChunkX(), handler.getPlayer().getChunkY(), 1);
//				}
//				
//				Tile tile = handler.getWorld().getChunkManager().getTile(currentX, currentY);
//				
//				if(tile != null && tile.getBiome().equals(name))
//					return "Found at: " + currentX + ", " + currentY;
//				
//				i++;
//				
//				movesLeft--;
//				if(movesLeft <= 0) {
//					if(i % 2 == 0)
//						moves += 1;
//					
//					movesLeft = moves;
//					
//					direction = (direction + 1) % 4;
//				}
//			}
			
			if(!handler.getWorld().getBiomes().containsKey(name)) {
				return "Invalid biome name provided!";
			}
			
			String result = "Biome not found!";
			
			int playerWorldX = (int) handler.getPlayer().getWorldX();
			int playerWorldY = (int) handler.getPlayer().getWorldY();
			
			byte lookingDirection = handler.getPlayer().getLookingDirection();
			boolean xAxis = lookingDirection == Entity.LEFT || lookingDirection == Entity.RIGHT;
			int start = xAxis ? playerWorldX : playerWorldY;
			int current = start;

			while(Math.abs(start - current) < 10_000) {
				if(lookingDirection == Entity.LEFT || lookingDirection == Entity.UP) {
					current--;
				} else {
					current++;
				}
				
				Tile tile = xAxis ? handler.getWorld().getChunkManager().getTile(current, playerWorldY) : handler.getWorld().getChunkManager().getTile(playerWorldX, current);
			
				if(tile != null && tile.getBiome().equals(name)) {
					result = "Found at: " + (xAxis ? current : playerWorldX) + ", " + (xAxis ? playerWorldY : current);
					break;
				}
			}
			
			handler.getWorld().getChunkManager().unloadFarChunks(handler.getPlayer().getChunkX(), handler.getPlayer().getChunkY());
			
			return result;
		}
		
		return NULL;
	}
	
	@Command(description = "Adds or subtracts player's XP points",
			args = "<action> <amount>",
			argsHint = {"Action: add, addLevel", "Amount of XP or levels"})
	public static String xp(DebugPanel panel, String... args) {
		if(args.length < XP)
			missingArgsString(args, XP);
		
		String action = args[0];
		
		assertArg(action, "add", "addLevel");
		if(assertError()) return argAssertErrorMsg;
		
		int amount = Integer.parseInt(args[1]);
		
		if(action.equals("add")) {
			handler.getPlayer().addExperiencePoints(amount);
			
			return "Added " + amount + " XP";
		}
		
		if(action.equals("addLevel")) {
			handler.getPlayer().addLevel(amount);
			
			return "Added " + amount + " levels";
		}
		
		return NULL;
	}
	
	private static void assertArg(String arg, String... expected) {
		List<String> expectedList = List.of(expected);
		
		if(!expectedList.contains(arg)) {
			argAssertError = true;
			argAssertErrorMsg = "~E_BAD_ARGS: argument \"" + arg + "\" is invalid. Expected: " + String.join(", ", expected);
		}
	}
	
	private static boolean assertError() {
		if(argAssertError) {
			argAssertError = false;
			
			return true;
		}
		
		return false;
	}
	
	private static String missingArgsString(String[] args, byte required) {
		return "~E_MISSING_ARGS: provided " + args.length + " of " + required + " args";
	}
	
	public static void setHandler(Handler handler) {
		DebugCommands.handler = handler;
	}
}
