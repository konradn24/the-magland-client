package konradn24.tml.entities.buildings;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.Entity;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.utils.Modules;

public abstract class Building extends Entity {

	private static String[] buildings = new String[256];
	
	protected String name;
	
	protected static int requiredLevel;
	protected static Map<Class<? extends Item>, Integer> requiredMaterials;
	protected static int rewardExperiencePoints;
	
	protected boolean underConstruction;
	
	protected Map<Class<? extends Item>, Integer> storage;
	
	public Building(Handler handler, int width, int height) {
		super(handler, 0, 0, width, height);
		loadName();
	}
	
	public Building(Handler handler, float x, float y, int width, int height) {
		super(handler, x, y, width, height);
		loadName();
	}
	
	public Building(Handler handler, float x, float y, int width, int height, boolean instantConstruction) {
		super(handler, x, y, width, height);
		loadName();
		
		if(instantConstruction) {
			underConstruction = false;
			
			if(handler.getPlayer() != null)
				handler.getPlayer().addExperiencePoints(rewardExperiencePoints);
		}
	}
	
	public String getInfo() {
		return "In development";
	}
	
	private void loadName() {
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		name = new String(c);
	}
	
	public static void init() {
		registerBuilding("Shelter");
		registerBuilding("House");
		
		Logging.info(declaredBuildingsAmount() + " buildings initialized");
	}
	
	public void construct() {
		underConstruction = false;
		
		handler.getPlayer().addExperiencePoints(rewardExperiencePoints);
	}
	
	public boolean isUnlocked() {
		return handler.getPlayer().getExperienceLevel() >= requiredLevel;
	}
	
	protected void setRequiredMaterials(Map<Class<? extends Item>, Integer> requiredMaterials) {
		Building.requiredMaterials = new LinkedHashMap<>(requiredMaterials);
		this.storage = new LinkedHashMap<>();
		
		for(Class<? extends Item> item : requiredMaterials.keySet())
			storage.put(item, 0);
	}
	
	private static void registerBuilding(String className) {
		for(int i = 0; i < buildings.length; i++) {
			if(buildings[i] == null || buildings[i].isEmpty()) {
				buildings[i] = className;
				return;
			}
		}
		
		Logging.error("Register building " + className + " failed - registry is full!");
	}
	
	@SuppressWarnings("unused")
	private static void registerBuilding(String className, int id) {
		if(id < 0 || id >= buildings.length) {
			Logging.error("Register building " + className + " failed - provided ID is out of range!");
			return;
		}
		
		if(buildings[id] != null && !buildings[id].isEmpty()) {
			Logging.error("Register building " + className + " failed - provided ID is already used!");
			return;
		}
		
		buildings[id] = className;
	}
	
	public static int declaredBuildingsAmount() {
		int amount = 0;
		for(String building : buildings)
			if(building != null && !building.isEmpty())
				amount++;
		
		return amount;
	}
	
	// GETTERS AND SETTERS
	
	public static Class<?> getBuilding(int id) {
		if(id < 0 || id >= buildings.length)
			return null;
		
		if(buildings[id] == null)
			return null;
		
		try {
			Class<?> buildingClass = Class.forName(Modules.BUILDINGS + buildings[id]);
			
			return buildingClass;
		} catch (ClassNotFoundException e) {
			Logging.error("Cannot get building of ID " + id + " - class not found!");
			return null;
		}
	}
	
	public static Class<?> getBuilding(String id) {
		try {
			Class<?> buildingClass = Class.forName(Modules.BUILDINGS + id);
			
			return buildingClass;
		} catch (ClassNotFoundException e) {
			Logging.error("Cannot get building of ID " + id + " - class not found!");
			return null;
		}
	}
	
	public static Building getBuilding(String id, Handler handler) {
		try {
			Class<?> buildingClass = Class.forName(Modules.BUILDINGS + id);
			
			return (Building) buildingClass.getConstructor(Handler.class).newInstance(handler);
		} catch (ClassNotFoundException e) {
			Logging.error("Cannot get building of ID " + id + " - class not found!");
			return null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			Logging.error("Cannot create new instance of building " + id);
			return null;
		}
	}
	
	public static String[] getBuildings() {
		String[] buildings = new String[declaredBuildingsAmount()];
		for(int i = 0; i < buildings.length; i++) {
			if(Building.buildings[i] == null || Building.buildings[i].isEmpty())
				return buildings;
			
			buildings[i] = Building.buildings[i];
		}
		
		return buildings;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayedName() {
		String displayedName = "";
		
		int lastIndex = 0;
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			
			if(i == name.length() - 1) {
				displayedName += (lastIndex == 0 ? "" : " ") + name.substring(lastIndex, i + 1);
				return displayedName.substring(0, 1).toUpperCase() + displayedName.substring(1);
			}
			
			if(Character.isUpperCase(c)) {
				displayedName += (lastIndex == 0 ? "" : " ") + name.substring(lastIndex, i);
				lastIndex = i;
			}
		}
		
		return displayedName.substring(0, 1).toUpperCase() + displayedName.substring(1);
	}
	
	public int getRequiredLevel() {
		return requiredLevel;
	}

	public Map<Class<? extends Item>, Integer> getRequiredMaterials() {
		return requiredMaterials;
	}

	public int getRewardExperiencePoints() {
		return rewardExperiencePoints;
	}

	public boolean isUnderConstruction() {
		return underConstruction;
	}
}
