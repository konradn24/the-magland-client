package konradn24.tml.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import konradn24.tml.Launcher;

public final class Logging {
	
	public static final String PATH = "logs/";
	public static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
	
	private static File file;
	private static FileWriter fileWriter;
	private static boolean fileWriterStop;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
	
	public static void init() {
		try {
			if(fileWriter != null)
				fileWriter.close();
			
			file = new File(PATH + FILE_NAME_FORMAT.format(new Date()) + ".txt");
			file.createNewFile();
			
			fileWriter = new FileWriter(file, true);
			fileWriter.append("!!! LOGS FILE CREATED AT " + now() + " !!!");
			fileWriter.append("\n------------------------------------------\n");
			fileWriter.append("Game version: " + Launcher.VERSION + "\n"
				 	+ "Date and time: " + Logging.now() + "\n"
				 	+ "Uptime: " + (System.currentTimeMillis() - Launcher.time) / 1000 / 60 + " minutes\n"
		 			+ "Java version: " + System.getProperty("java.version") + "\n"
 					+ "OS: " + System.getProperty("os.name") + "\n"
					+ "OS architecture: " + System.getProperty("os.arch") + "\n"
					+ "OS version: " + System.getProperty("os.version") + "\n"
					+ "OS user: " + System.getProperty("user.name"));
			fileWriter.append("\n------------------------------------------\n");
			fileWriter.flush();
			
			startFileWriter();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Creating logging files failed!");
			e.printStackTrace();
		}
	}
	
	public static void info(String str) {
		System.out.println("<" + now() + "> INFO: " + str);
		
		if(fileWriterStop) return;
		
		try {
			fileWriter.append("\n<" + now() + "> INFO: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing to file failed!");
			e.printStackTrace();
		}
	}
	
	public static void warning(String str) {
		System.out.println("<" + now() + "> WARNING: " + str);

		if(fileWriterStop) return;
		
		try {
			fileWriter.append("\n<" + now() + "> WARNING: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing to file failed!");
			e.printStackTrace();
		}
	}
	
	public static void error(String str) {
		System.err.println("<" + now() + "> ERROR: " + str);

		if(fileWriterStop) return;
		
		try {
			fileWriter.append("\n<" + now() + "> ERROR: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing to file failed!");
			e.printStackTrace();
		}
	}
	
	public static String now() {
		return sdf.format(new Date());
	}
	
	public static boolean isFileWriterStopped() {
		return fileWriterStop;
	}
	
	public static void startFileWriter() {
		fileWriterStop = false;
	}
	
	public static void stopFileWriter() {
		fileWriterStop = true;
	}
	
	public static File getFile() {
		return file;
	}
	
	public static FileWriter getFileWriter() {
		return fileWriter;
	}
}
