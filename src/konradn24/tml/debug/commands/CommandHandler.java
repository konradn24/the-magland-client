package konradn24.tml.debug.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.gui.panels.DebugPanel;

public class CommandHandler {
	
	private static boolean error, warning;
	
	public static void init(Handler handler) {
		DebugCommands.setHandler(handler);
	}
	
	public static String handle(DebugPanel panel, String cmd) {
		String command;
		String[] args;
		
		if(cmd.indexOf(' ') == -1) {
			command = cmd;
			args = new String[0];
		} else {
			command = cmd.substring(0, cmd.indexOf(' '));
			
			String[] cmdSplitted = cmd.split(" ");
			
			if(cmd.contains("\"")) {
				List<String> tempCmdSplitted = new ArrayList<>();
				
				int stringStart = -1, stringEnd = -1;
				for(int i = 0; i < cmdSplitted.length; i++) {
					if(!cmdSplitted[i].contains("\"") && stringStart == -1) {
						tempCmdSplitted.add(cmdSplitted[i]);
					}
					
					if(cmdSplitted[i].startsWith("\"")) {
						stringStart = i;
						cmdSplitted[i] = cmdSplitted[i].substring(1);
					}
					
					if(cmdSplitted[i].endsWith("\"")) {
						stringEnd = i;
						cmdSplitted[i] = cmdSplitted[i].substring(0, cmdSplitted[i].length() - 1);
					}
					
					if(stringStart > -1 && stringEnd > -1) {
						tempCmdSplitted.add(String.join(" ", Arrays.copyOfRange(cmdSplitted, stringStart, stringEnd + 1)));
						stringStart = -1;
						stringEnd = -1;
					}
				}
				
				cmdSplitted = new String[tempCmdSplitted.size()];
				for(int i = 0; i < tempCmdSplitted.size(); i++) {
					cmdSplitted[i] = tempCmdSplitted.get(i);
				}
			}
			
			args = Arrays.copyOfRange(cmdSplitted, 1, cmdSplitted.length);
		}
		
		try {
			Method method = DebugCommands.class.getMethod(command, DebugPanel.class, String[].class);
			
			if(!method.isAnnotationPresent(Command.class))
				throw new NoSuchMethodException("@Command annotation not found");
			
			String response = method.invoke(null, panel, args).toString();
			
			if(response.startsWith(DebugCommands.ERROR)) {
				response = errorString(response);
			} else error = false;
			
			if(response.startsWith(DebugCommands.WARNING))
				warning = true;
			else warning = false;
			
			return response;
		} catch(SecurityException e) {
			e.printStackTrace();
			return errorString("~E_SECURITY: " + e.getLocalizedMessage());
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
			return errorString("~E_NO_SUCH_METHOD: " + e.getLocalizedMessage());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return errorString("~E_ILLEGAL_ARG: " + e.getLocalizedMessage());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return errorString("~E_ILLEGAL_ACCESS: " + e.getLocalizedMessage());
		} catch(InvocationTargetException e) {
			e.printStackTrace();
			return errorString("~E_INVOCATION_TARGET: " + e.getTargetException());
		}
	}
	
	public static String errorString(String msg) {
		error = true;
		return "Error occurred! " + msg;
	}

	public static String warningString(String msg) {
		warning = true;
		return "Warning! " + msg;
	}
	
	public static boolean isError() {
		return error;
	}

	public static boolean isWarning() {
		return warning;
	}
}
