package konradn24.tml;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import konradn24.tml.utils.Logging;

public class Launcher {
public static final String VERSION = "ALPHA3";
	
	public static long time;
	
	private static Game game;

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> { exceptionHandler(throwable); });
		Runtime.getRuntime().addShutdownHook(new Thread(() -> { shutdownHandler(); }));
		
		time = System.currentTimeMillis();
		
		Logging.init();
		Logging.info("Start time: " + time);
		
		if(!initLogs()) {
			Logging.error("Failed to initialize logs directory");
		}
		
		if(!initSaves()) {
			Logging.error("Failed to initialize saves directory");
		}
		
		game = new Game("The Magland");
		game.start();
		
		Logging.info("Main thread started. Elapsed time: " + (System.currentTimeMillis() - time) + "ms");
	}
	
	private static boolean initLogs() {
		File logs = new File("logs");
		logs.mkdir();
		
		return logs.exists();
	}
	
	private static boolean initSaves() {
		File saves = new File("saves");
		saves.mkdir();
		
		return saves.exists();
	}
	
	private static void exceptionHandler(Throwable throwable) {
		Logging.error("FATAL ERROR OCCURRED!");
		Logging.error(throwable);
		
		try {
			showCrashDialog(throwable);
		} catch(Exception e) {
			Logging.error("CANNOT SHOW CRASH DIALOG");
			Logging.error(e);
			
			System.exit(1);
		}
	}
	
	private static void shutdownHandler() {
		Logging.info("Runtime shutdown...");
		
//		if(State.getState() != null && State.getState().equals(game.gameState)) {
//			if(game.tryAutoSave()) {
//				Logging.info("Runtime shutdown: auto save success");
//			} else {
//				Logging.error("Runtime shutdown: auto save failed");
//			}
//		}
		
		if(Logging.saveLog()) {
			Logging.info("Runtime shutdown: save log success");
		} else {
			Logging.error("Runtime shutdown: save log failed");
		}
		
		Logging.info("Runtime shutdown: closing log file writer");
		Logging.closeFileWriter();
		
		System.out.println("Runtime shutdown completed");
	}
	
	private static void showCrashDialog(Throwable throwable) {
        String message = "Unexpected error occurred:\n" + throwable.toString();

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        JTextArea textArea = new JTextArea(message + "\n\n" + stackTrace);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JButton openLogButton = new JButton("Open log file");
        openLogButton.addActionListener(e -> openLogFile());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(openLogButton, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, panel, "Fatal error", JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }
	
	private static void openLogFile() {
        try {
            File logFile = new File("logs/latest.log");
            
            if (!logFile.exists()) {
                JOptionPane.showMessageDialog(null, "File latest.log does not exist.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Desktop.getDesktop().open(logFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to open latest.log file:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
