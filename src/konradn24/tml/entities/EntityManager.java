package konradn24.tml.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.actions.Action;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.states.GameState;

public class EntityManager {
	
	public static final int RENDER_DISTANCE = 1024;
	public static final int VISIBLE_DISTANCE = 640;
	
	public static final int ACTION_ICON_SIZE = 48;
	
	private static final String VANISH_REMOVE_STACK_TAG = "#VANISH";
	
	private Handler handler;
	
	private ArrayList<Entity> entities;
	private ArrayList<Entity> sortedEntities;
	
	private HashMap<Entity, String> removeStack;
	
	private Comparator<Entity> renderSorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity a, Entity b) {
			if(a.getY() + a.getHeight() < b.getY() + b.getHeight())
				return -1;
			return 1;
		}
	};
	
	public EntityManager(Handler handler) {
		this.handler = handler;
		
		entities = new ArrayList<>();
		sortedEntities = new ArrayList<>();
		
		removeStack = new HashMap<>();
		
		addEntity(handler.getPlayer());
		
		Logging.info("Entity Manager initialized");
	}
	
	public void tick() {
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			// Check for uncaught entity dead from previous tick
			entity.checkHealth(null);
			
			entity.tick();

			// Action
			tickActions(entity);
		}
		
		tickRemoveStack();
	}
	
	private void tickActions(Entity entity) {
		if(!entity.leftClicked())
			return;
		
		Item currentItem = handler.getPlayer().getInventory().getCurrentItem();
		Class<? extends Item> currentItemClass = currentItem != null ? currentItem.getClass() : null;
		
		// First, try to get action associated with -1
		Action action = entity.getAction(-1);
		
		if(action != null) {
			Logging.info("Entity Manager: performing action (by key -1) with item " 
					+ (currentItemClass == null ? null : currentItemClass.getSimpleName()) + " on entity " 
					+ entity.getClass().getSimpleName());
			
			action.perform(currentItem, handler);
			
			return;
		}
		
		// Then try to get action associated with current item
		action = entity.getAction(currentItemClass);
		
		if(action != null) {
			Logging.info("Entity Manager: performing action (by item) with item " 
					+ (currentItemClass == null ? null : currentItemClass.getSimpleName()) + " on entity " 
					+ entity.getClass().getSimpleName());
			
			action.perform(currentItem, handler);
			
			return;
		}
		
		// Then try to get action associated with any of current item's attributes
		if(currentItem != null) {
			for(int attribute : currentItem.getAttributes()) {
				action = entity.getAction(attribute);
				
				if(action != null) {
					Logging.info("Entity Manager: performing action (by attribute) with item " 
							+ (currentItemClass == null ? null : currentItemClass.getSimpleName()) + " on entity " 
							+ entity.getClass().getSimpleName());
					
					action.perform(currentItem, handler);
					
					return;
				}
			}
		}
	}
	
	private void tickRemoveStack() {
		for(Map.Entry<Entity, String> entry : removeStack.entrySet()) {
			Entity entity = entry.getKey();
			String text = entry.getValue();
			
			if(!entities.remove(entity) || text == VANISH_REMOVE_STACK_TAG)
				continue;
			
			entity.onDead();
			
			String textToPrint = "Entity #" + entity.hashCode() + " died";
			
			if(text != null)
				textToPrint += ": " + text;
			
			Logging.info("Entity Manager: " + textToPrint);
			GameState.getDebugConsole().print(textToPrint);
		}
		
		removeStack.clear();
	}
	
	public void render(Graphics g) {
		// Sorting entities to make good-looking depth illusion
		sortedEntities.clear();
		
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			if(entity.getDistanceFromPlayer() > RENDER_DISTANCE)
				continue;
			
			sortedEntities.add(entity);
		}
		
		try {
			sortedEntities.sort(renderSorter);
		} catch(IllegalArgumentException e) {}
		
		for(Entity entity : sortedEntities) {
			int x = entity.getScreenX();
			int y = entity.getScreenY();
			
			g.drawImage(entity.texture, x, y, entity.width, entity.height, null);
			entity.render(g);
			
			if(GameState.debugMode)
				entity.renderDebugMode(g);
		}
		
		// Action (second loop to render on top of entities' textures)
		for(Entity entity : sortedEntities) {
			renderActionIcon(g, entity);
			entity.renderGUI(g);
		}
		
		handler.getPlayer().getBuildingPlacer().render(g);
		handler.getPlayer().renderStats(g);
		handler.getPlayer().renderInventory(g);
		handler.getPlayer().getBuildingsMenu().render(g);
	}
	
	private void renderActionIcon(Graphics g, Entity entity) {
		if(!entity.hover())
			return;
		
		Item currentItem = handler.getPlayer().getInventory().getCurrentItem();
		Class<? extends Item> currentItemClass = currentItem != null ? currentItem.getClass() : null;
		
		List<Integer> actionsAttributes = new ArrayList<>(entity.actionsByAttribute.keySet());
		
		// When actionsAttributes has -1, show hand icon
		if(actionsAttributes.contains(-1)) {
			g.drawImage(Assets.handIcon, handler.getMouseManager().getMouseX() + 8, 
					handler.getMouseManager().getMouseY(), 
					ACTION_ICON_SIZE, ACTION_ICON_SIZE, null);
			
			return;
		}
		
		// When player has specified item, show item's texture as action icon
		if(currentItemClass != null && entity.actionsByItem.containsKey(currentItemClass)) {
			BufferedImage icon = entity.getCustomActionIcon() != null ? entity.getCustomActionIcon() : currentItem.getTexture();
			
			g.drawImage(icon, handler.getMouseManager().getMouseX() + 8, 
					handler.getMouseManager().getMouseY(), 
					ACTION_ICON_SIZE, ACTION_ICON_SIZE, null);
			
			return;
		}
		
		// When player's current item has specified attribute, show item's texture as action icon
		if(currentItem != null && currentItem.hasAttributeAny(actionsAttributes)) {
			BufferedImage icon = entity.getCustomActionIcon() != null ? entity.getCustomActionIcon() : currentItem.getTexture();
			
			g.drawImage(icon, handler.getMouseManager().getMouseX() + 8, 
					handler.getMouseManager().getMouseY(), 
					ACTION_ICON_SIZE, ACTION_ICON_SIZE, null);
			
			return;
		}
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	public boolean addEntity(Class<? extends Entity> entityClass, float x, float y) {
		Entity entity = getEntityInstance(entityClass, x, y);
		
		if(entity == null) {
			Logging.error("Cannot add new entity: instance creating error occurred!");
			
			return false;
		}
		
		return entities.add(entity);
	}
	
	public void addRandomEntity(Entity[] entities, double[] chances) {
		if(entities.length != chances.length) {
			Logging.error("Cannot add random entity: entities and chances do not have the same length!");
			return;
		}
		
		Random random = new Random();
		double r = random.nextDouble();
		
		for(int i = 0; i < entities.length; i++) {
			if(r < chances[i]) {
				this.entities.add(entities[i]);
				return;
			}
		}
		
		this.entities.add(entities[entities.length - 1]);
	}
	
	public void addRandomEntity(Random random, Entity[] entities, double[] chances) {
		if(entities.length != chances.length) {
			Logging.error("Cannot add random entity: entities and chances do not have the same length!");
			return;
		}

		double r = random.nextDouble();
		
		for(int i = 0; i < entities.length; i++) {
			if(r < chances[i]) {
				this.entities.add(entities[i]);
				return;
			}
		}
		
		this.entities.add(entities[entities.length - 1]);
	}
	
	public void kill(Entity entity, String text) {
		removeStack.put(entity, text);
	}
	
	public void vanish(Entity entity) {
		removeStack.put(entity, VANISH_REMOVE_STACK_TAG);
	}
	
	public Entity getEntity(int hashCode) {
		List<Entity> found = entities.stream().filter(entity -> entity.hashCode() == hashCode).collect(Collectors.toList());
		if(found.isEmpty())
			return null;
		
		return found.get(0);
	}
	
	public Entity getEntityByPosition(Class<? extends Entity> entityClass, float x, float y) {
		List<Entity> found = entities.stream().filter(entity -> entity.getClass().equals(entityClass) && entity.getX() == x && entity.getY() == y)
				.collect(Collectors.toList());
		if(found.isEmpty())
			return null;
		
		return found.get(0);
	}
	
	public List<Entity> getEntities(String className) {
		return entities.stream().filter(entity -> entity.getClass().getName().contains(className)).collect(Collectors.toList());
	}
	
	//Getters and setters

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	public Entity getEntityInstance(Class<? extends Entity> entityClass, float x, float y) {
		try {
			Entity entity = entityClass.getConstructor(Handler.class, float.class, float.class).newInstance(handler, x, y);
		
			return entity;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			
			return null;
		}
	}
}
