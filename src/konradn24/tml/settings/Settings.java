package konradn24.tml.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.utils.Logging;

public class Settings {
	
	// Graphics
	public static final transient boolean DEFAULT_FULLSCREEN = false;
	public static final transient int DEFAULT_FPS_LIMIT = 60;
	
	private boolean fullscreen;
	private int fpsLimit;
	
	// Gameplay
	public static final transient int DEFAULT_AUTO_SAVE_INTERVAL = 1;
	
	private int autoSaveInterval;
	
	// Controls
	private Controls controls;
	
	private transient Handler handler;
	
	public Settings(Handler handler) {
		// Graphics
		fullscreen = false;
		fpsLimit = 60;
		
		// Gameplay
		autoSaveInterval = 1;
		
		// Controls
		controls = new Controls();
		
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
		
		// Gameplay
		
		if(autoSaveInterval != settings.autoSaveInterval) {
			autoSaveInterval = settings.autoSaveInterval;
			changed.add("autosave");
		}
		
		// Controls
		
		if(!controls.equals(settings.controls)) {
			controls = settings.controls;
			changed.add("controls");
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

	public int getAutoSaveInterval() {
		return autoSaveInterval;
	}

	public void setAutoSaveInterval(int autoSaveInterval) {
		this.autoSaveInterval = autoSaveInterval;
	}

	public Controls getControls() {
		return controls;
	}
}
