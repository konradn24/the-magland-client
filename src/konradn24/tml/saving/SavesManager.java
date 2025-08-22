package konradn24.tml.saving;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import konradn24.tml.Handler;
import konradn24.tml.utils.Logging;

public class SavesManager {

	public static final String SAVES_LOCATION = "saves";
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	public static final int MAX_SAVES = 8;
	
	private Handler handler;
	private Save currentSave;
	
	public SavesManager(Handler handler) {
		this.handler = handler;
	}
	
	public boolean newSave(int slot, String name, long seed) {
		if(slot < 1 || slot > MAX_SAVES) {
			Logging.error("Saves Manager: invalid slot for new save (" + slot + ")");
			return false;
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Save save = new Save(slot, name, seed);
		String fileName = getFileName(slot);
		File file = new File(SAVES_LOCATION + "/" + fileName);
		
		try {
			file.createNewFile();
			
			Writer writer = new FileWriter(file, false);
			gson.toJson(save, writer);
			
			writer.flush();
			writer.close();
			
			currentSave = save;
			
			Logging.info("Saves Manager: created and loaded new save at slot " + slot);
			
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to create new save at slot " + slot);
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean save() {
		if(!isSaveLoaded() || currentSave.getSlot() < 1 || currentSave.getSlot() > MAX_SAVES) {
			Logging.error("Saves Manager: no current save to update");
			return false;
		}
		
		currentSave.update(handler);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String fileName = getFileName(currentSave.getSlot());
		File file = new File(SAVES_LOCATION + "/" + fileName);
		
		try {
			file.createNewFile();
			
			Writer writer = new FileWriter(file, false);
			gson.toJson(currentSave, writer);
			
			writer.flush();
			writer.close();
			
			Logging.info("Saves Manager: saved to slot " + currentSave.getSlot());
			
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to save to slot " + currentSave.getSlot());
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean load(int slot) {
		Gson gson = new Gson();
		
		try(Reader reader = new FileReader(SAVES_LOCATION + "/" + getFileName(slot))) {
			currentSave = gson.fromJson(reader, Save.class);
			Logging.info("Saves Manager: loaded save \"" + currentSave.getName() + "\" from slot " + slot);
			
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to load save from slot " + slot);
			e.printStackTrace();
			
			return false;
		}
	}
	
	public Save readSave(int slot) {
		Gson gson = new Gson();
		
		try(Reader reader = new FileReader(SAVES_LOCATION + "/" + getFileName(slot))) {
			Save save = gson.fromJson(reader, Save.class);
			
			return save;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to read save from slot " + slot);
			e.printStackTrace();
			
			return null;
		}
	}
	
	public boolean writeSave(Save save) {
		if(save == null) {
			Logging.error("Saves Manager: save to write is null");
			return false;
		}
		
		if(save.getSlot() < 1 || save.getSlot() > MAX_SAVES) {
			Logging.error("Saves Manager: invalid slot for save to write (" + save.getSlot() + ")");
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String fileName = getFileName(save.getSlot());
		File file = new File(SAVES_LOCATION + "/" + fileName);
		
		try {
			file.createNewFile();
			
			Writer writer = new FileWriter(file, false);
			gson.toJson(save, writer);
			
			writer.flush();
			writer.close();
			
			Logging.info("Saves Manager: wrote save to slot " + save.getSlot());
			
			return true;
		} catch(IOException e) {
			Logging.error("Saves Manager: failed to write save to slot " + save.getSlot());
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean deleteSave(int slot) {
		File file = new File(SAVES_LOCATION + "/" + getFileName(slot));
		
		if(file.delete()) {
			Logging.info("Saves Manager: deleted save from slot " + slot);
			
			return true;
		} else {
			Logging.error("Saves Manager: failed to delete save from slot " + slot);
			
			return false;
		}
	}
	
	public boolean isSaveLoaded() {
		return currentSave != null;
	}
	
	public Save getCurrentSave() {
		return currentSave;
	}
	
	public void clearCurrentSave() {
		currentSave = null;
	}
	
	public Save[] getSaves() {
		File savesFolder = new File(SAVES_LOCATION);
		File[] files = savesFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		
		Save[] saves = new Save[MAX_SAVES];
		
		for(int i = 0; i < Math.min(files.length, MAX_SAVES); i++) {
			Gson gson = new Gson();
			
			try(Reader reader = new FileReader(files[i].getPath())) {
				String fileName = files[i].getName().replaceFirst("[.][^.]+$", "");
				int slot = Integer.parseInt(fileName.split("_")[1]) - 1;
				
				saves[slot] = gson.fromJson(reader, Save.class);
			} catch(IOException | NumberFormatException e) {
				Logging.error("Saves Manager: failed to get save from file \"" + files[i].getName() + "\"");
				e.printStackTrace();
				
				saves[i] = null;
			}
		}
		
		return saves;
	}
	
	public String[] getSavesName(Save[] saves) {
		String[] savesName = new String[saves.length];
		
		for(int i = 0; i < saves.length; i++) {
			if(saves[i] == null) {
				continue;
			}
			
			savesName[i] = saves[i].getName();
		}
		
		return savesName;
	}
	
	public String getFileName(int slot) {
		return "Slot_" + slot + ".json";
	}
}
