package konradn24.tml.saving;

import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;

public class SavesManager {

	public static final String SAVES_LOCATION = "saves";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
	
	private Handler handler;
	
	public SavesManager(Handler handler) {
		this.handler = handler;
	}
	
	public boolean save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Save save = new Save(handler);
		String saveName = "save_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".json";
		
		try(Writer writer = new FileWriter(SAVES_LOCATION + "/" + saveName)) {
			gson.toJson(save, writer);
			
			Logging.info("Saves Manager: saved game as " + saveName);
			
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to save game as \"" + saveName + "\"");
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean load(String saveName) {
		Gson gson = new Gson();
		
		try(Reader reader = new FileReader(SAVES_LOCATION + "/" + saveName + ".json")) {
			Save save = gson.fromJson(reader, Save.class);
			
			Logging.info("v=" + save.getVersion() + "  seed=" + save.getSeed() + "  x=" + save.getPlayerX() + "  y=" + save.getPlayerY());
		
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to load save \"" + saveName + "\"");
			e.printStackTrace();
			
			return false;
		}
	}
}
