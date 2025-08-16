package konradn24.tml.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.utils.Logging;

public class Settings {
	
	// Graphics
	private boolean fullscreen;
	private int fpsLimit;
	private int chunkLoadRadius;
	
	// Gameplay
	private int autoSaveInterval;
	
	private transient Handler handler;
	
	public Settings(Handler handler) {
		// Graphics
		fullscreen = false;
		fpsLimit = 60;
		chunkLoadRadius = 1;
		
		// Gameplay
		autoSaveInterval = 1;
		
		this.handler = handler;
	}
	
	public void load(boolean refreshGame) throws IOException {
		if(SettingsIO.fileExists()) {
			Logging.info("Settings Manager: file found, reading file...");
			set(SettingsIO.readFile(), refreshGame);
		} else {
			Logging.warning("Settings Manager: file not found, creating new file...");
			set(SettingsIO.createFile(), refreshGame);
		}
	}
	
	public void save() throws IOException {
		Logging.info("Settings Manager: saving settings...");
		SettingsIO.writeFile(this);
	}
	
	public void set(Settings settings, boolean refreshGame) {
		List<String> changed = new ArrayList<>();
		
		// Graphics
		
		if(fullscreen != settings.fullscreen) {
			fullscreen = settings.fullscreen;
			changed.add("fullscreen");
		}
		
		if(fpsLimit != settings.fpsLimit) {
			fpsLimit = settings.fpsLimit <= 0 ? fpsLimit : settings.fpsLimit;
			changed.add("fps");
		}
		
		if(chunkLoadRadius != settings.chunkLoadRadius) {
			chunkLoadRadius = settings.chunkLoadRadius <= 0 ? chunkLoadRadius : settings.chunkLoadRadius;
			changed.add("chunk_load_radius");
		}
		
		// Gameplay
		
		if(autoSaveInterval != settings.autoSaveInterval) {
			autoSaveInterval = settings.autoSaveInterval;
			changed.add("autosave");
		}
		
		if(handler != null && refreshGame) {
			handler.getGame().refreshSettings(changed);
		}
		
		Logging.info("Settings Manager: settings object has been set");
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public int getFpsLimit() {
		return fpsLimit;
	}

	public void setFpsLimit(int fpsLimit) {
		this.fpsLimit = fpsLimit;
	}

	public int getChunkLoadRadius() {
		return chunkLoadRadius;
	}

	public void setChunkLoadRadius(int chunkLoadRadius) {
		this.chunkLoadRadius = chunkLoadRadius;
	}

	public int getAutoSaveInterval() {
		return autoSaveInterval;
	}

	public void setAutoSaveInterval(int autoSaveInterval) {
		this.autoSaveInterval = autoSaveInterval;
	}
}
