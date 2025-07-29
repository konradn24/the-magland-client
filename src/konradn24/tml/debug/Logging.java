package konradn24.tml.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import konradn24.tml.Launcher;

public final class Logging {
	
	public static final String PATH = "logs/";
	public static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
	private static final String FILE_NAME = FILE_NAME_FORMAT.format(new Date()) + ".log";
	
	private static File file;
	private static FileWriter fileWriter;
	private static boolean fileWriterStop;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
	
	public static void init() {
		try {
			if(fileWriter != null)
				fileWriter.close();
			
			file = new File(PATH + "latest.log");
			file.createNewFile();
			
			fileWriter = new FileWriter(file, false);
			fileWriter.append("!!! LOG FILE CREATED AT " + now() + " !!!");
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
			error(e);
		}
	}
	
	public static void info(String str) {
		System.out.println(prefix(false) + "INFO: " + str);
		
		if(fileWriter == null || fileWriterStop) return;
		
		try {
			fileWriter.append(prefix(true) + "INFO: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing log to file failed!");
			error(e);
		}
	}
	
	public static void warning(String str) {
		System.out.println(prefix(false) + "WARNING: " + str);

		if(fileWriter == null || fileWriterStop) return;
		
		try {
			fileWriter.append(prefix(true) + "WARNING: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing log to file failed!");
			error(e);
		}
	}
	
	public static void error(String str) {
		System.err.println(prefix(false) + "ERROR: " + str);

		if(fileWriter == null || fileWriterStop) return;
		
		try {
			fileWriter.append(prefix(true) + "ERROR: " + str);
			fileWriter.flush();
		} catch(IOException e) {
			stopFileWriter();
			
			warning("Writing log to file failed!");
			error(e);
		}
	}
	
	public static void error(Throwable throwable) {
		error("Error: " + throwable.getMessage());
		
		for(StackTraceElement stackTraceElement : throwable.getStackTrace()) {
			error("at " + stackTraceElement.toString());
		}
	}
	
	public static boolean saveLog() {
		Path source = Paths.get(PATH + "latest.log");
		Path destination = Paths.get(PATH + FILE_NAME);
		
		try {
			Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			info("Saved log as " + FILE_NAME);
			return true;
		} catch(IOException e) {
			error("Saving log error");
			error(e);
			return false;
		}
	}
	
	public static int[] clearLogs() {
		File dir = new File(PATH);
		int deleted = 0;
		int all = dir.listFiles().length - 1;
		
		for(File file : dir.listFiles())
			if(!file.getName().equals("latest.log"))
				if(file.delete())
					deleted++;
		
		return new int[] { deleted, all };
	}
	
	public static String now() {
		return sdf.format(new Date());
	}
	
	public static String prefix(boolean newLine) {
		if(newLine) {
			return "\n<" + now() + "> [" + Thread.currentThread().getId() + "] ";
		}
		
		return "<" + now() + "> [" + Thread.currentThread().getId() + "] ";
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
	
	public static void closeFileWriter() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			error("Cannot close file writer");
			error(e);
		}
	}
	
	public static File getFile() {
		return file;
	}
	
	public static FileWriter getFileWriter() {
		return fileWriter;
	}
}
