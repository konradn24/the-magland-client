package konradn24.tml.entities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.joml.Matrix4f;

import konradn24.tml.Handler;
import konradn24.tml.entities.actions.Action;
import konradn24.tml.graphics.Assets;
import konradn24.tml.graphics.renderer.BatchRenderer;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.items.Item;
import konradn24.tml.utils.Logging;
import konradn24.tml.worlds.generator.Chunk;

public class EntityManager {
	
	public static final int RENDER_DISTANCE = 1500;
	public static final int VISIBLE_DISTANCE = 640;
	
	public static final int ACTION_ICON_SIZE = 48;
	
	private static final String VANISH_REMOVE_STACK_TAG = "#VANISH";
	
	private Handler handler;
	
	private ArrayList<Entity> entities;
	private HashMap<Entity, String> removeStack;
	
	public EntityManager(Handler handler) {
		this.handler = handler;
		
		entities = new ArrayList<>();
		removeStack = new HashMap<>();
		
		addEntity(handler.getPlayer());
		
		Logging.info("Entity Manager initialized");
	}
	
	public void update(float dt) {
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			// Check for uncaught entity dead from previous tick
			entity.checkHealth(null);
			entity.update(dt);
			
			// Action
			updateActions(entity);
		}
		
		updateRemoveStack();
	}
	
	private void updateActions(Entity entity) {
		Item currentItem = handler.getPlayer().getInventory().getSelectedItem();
		Class<? extends Item> currentItemClass = currentItem != null ? currentItem.getClass() : null;
		
		setCursorToActionIcon(currentItem,  currentItemClass, entity);
		
		if(!entity.leftClicked())
			return;
		
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
	
	private void updateRemoveStack() {
		if(removeStack.isEmpty()) {
			return;
		}
		
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
			handler.getPlayGUI().getDebugPanel().print(textToPrint);
		}
		
		removeStack.clear();
	}
	
	public void render(Matrix4f viewMatrix) {
		// Sorting entities to make good-looking depth illusion
//		sortedEntities.clear();
//		
//		for(int i = 0; i < entities.size(); i++) {
//			Entity entity = entities.get(i);
//			
//			if(entity.getDistanceFromPlayer() > RENDER_DISTANCE)
//				continue;
//			
//			sortedEntities.add(entity);
//		}
//		
//		try {
//			sortedEntities.sort(renderSorter);
//		} catch(IllegalArgumentException e) {}
		
		BatchRenderer.render(entities, handler.getCamera().getViewMatrix());
		
		for(Entity entity : entities) {
			entity.render();
			entity.renderDebug();
		}
		
//		handler.getPlayer().getBuildingPlacer().render();
//		handler.getPlayer().renderCurrentItemSlot();
		
		for(InventoryProperty property : handler.getPlayer().getInventory().getItems()) {
			property.item.render();
		}
		
//		handler.getPlayer().getBuildingsMenu().render(g);
	}
	
	public void renderGUI(long vg) {
		for(Entity entity : entities) {
			entity.renderGUI(vg);
		}
	}
	
	private void setCursorToActionIcon(Item currentItem, Class<? extends Item> currentItemClass, Entity entity) {
		if(!entity.hover())
			return;
		
		List<Integer> actionsAttributes = new ArrayList<>(entity.actionsByAttribute.keySet());
		
		// When actionsAttributes has -1, show hand icon
		if(actionsAttributes.contains(-1)) {
			handler.getGame().getDisplay().setCursor(Assets.getTexture("handIcon"));
			
			return;
		}
		
		// When player has specified item, show item's texture as action icon
		if(currentItemClass != null && entity.actionsByItem.containsKey(currentItemClass)) {
			Texture icon = entity.getCustomActionIcon() != null ? entity.getCustomActionIcon() : currentItem.getTexture();
			
			handler.getGame().getDisplay().setCursor(icon);
			return;
		}
		
		// When player's current item has specified attribute, show item's texture as action icon
		if(currentItem != null && currentItem.hasAttributeAny(actionsAttributes)) {
			Texture icon = entity.getCustomActionIcon() != null ? entity.getCustomActionIcon() : currentItem.getTexture();
			
			handler.getGame().getDisplay().setCursor(icon);
			
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
	
	public void addRandomEntity(Entity[] entities, float[] chances) {
		Entity entity = Entity.random(entities, chances);
		addEntity(entity);
	}
	
	public void addRandomEntity(Random random, Entity[] entities, float[] chances) {
		Entity entity = Entity.random(random, entities, chances);
		addEntity(entity);
	}
	
	public void addEntitiesFromChunk(Chunk chunk) {
		for(Entity entity : chunk.entities) {
			addEntity(entity);
		}
	}
	
	public void removeEntitiesFromChunk(Chunk chunk) {
		for(Entity entity : chunk.entities) {
			vanish(entity);
		}
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
		List<Entity> found = entities.stream().filter(entity -> entity.getClass().equals(entityClass) && entity.getWorldX() == x && entity.getWorldY() == y)
				.collect(Collectors.toList());
		if(found.isEmpty())
			return null;
		
		return found.get(0);
	}
	
	public List<Entity> getEntities(String className) {
		return entities.stream().filter(entity -> entity.getClass().getName().contains(className)).collect(Collectors.toList());
	}
	
	//Getters and setters

	Handler getHandler() {
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
		} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logging.error("Entity Manager: failed to create " + entityClass.getSimpleName() + " entity instance");
			Logging.error(e);
			
			return null;
		}
	}
}
