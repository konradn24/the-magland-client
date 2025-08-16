package konradn24.tml.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SettingsIO {

	public static final String FILE_PATH = "config/user_settings.json";
	
	public static Settings createFile() throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		File file = new File(FILE_PATH);
		Settings settings = new Settings(null);
		
		file.createNewFile();
		
		Writer writer = new FileWriter(file, false);
		gson.toJson(settings, writer);
		
		writer.flush();
		writer.close();
		
		return settings;
	}
	
	public static Settings readFile() throws FileNotFoundException {
		Gson gson = new Gson();
		Reader reader = new FileReader(FILE_PATH);

		return gson.fromJson(reader, Settings.class);
	}
	
	public static void writeFile(Settings settings) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File file = new File(FILE_PATH);
		
		file.createNewFile();
		
		Writer writer = new FileWriter(file, false);
		gson.toJson(settings, writer);
		
		writer.flush();
		writer.close();
	}
	
	public static boolean fileExists() {
		File file = new File(FILE_PATH);
		
		return file.exists() && !file.isDirectory();
	}
}
