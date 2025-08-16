package konradn24.tml.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Vector4f;

import konradn24.tml.Handler;
import konradn24.tml.entities.actions.Action;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.entities.statics.Pouch;
import konradn24.tml.graphics.Assets;
import konradn24.tml.graphics.renderer.Animation;
import konradn24.tml.graphics.renderer.BatchSprite;
import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.inventory.Inventory;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Logging;
import konradn24.tml.utils.Modules;
import konradn24.tml.utils.Transform;
import konradn24.tml.worlds.generator.Chunk;

public abstract class Entity {

	public enum Origin {
		TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT,
		LEFT, TOP, RIGHT, BOTTOM, CENTER
	}
	
	public static final int DEFAULT_HEALTH = 10, DEFAULT_VIEWFINDER_SIZE = 32;
	public static final Color DEFAULT_VIEWFINDER_INFO_STRING_COLOR = Color.red;
	public static final byte UP = 1, RIGHT = 2, DOWN = 3, LEFT = 4;
	
	protected Transform transform;
	protected float originX, originY;

	protected float boundsX, boundsY, boundsWidth, boundsHeight;
	
	protected Texture texture, customActionIcon;
	protected Animation animation;
	protected BatchSprite sprite;
	
	protected int health, maxHealth;
	protected boolean visible, dead, killable;
	
	protected Map<Class<? extends Item>, Action> actionsByItem = new HashMap<>();
	protected Map<Integer, Action> actionsByAttribute = new HashMap<>();
	
	protected List<InventoryProperty> loot = new ArrayList<>(); // TODO: dropping items on death
	protected Inventory inventory;
	
	protected Handler handler;
	
	/** 1 - north; 2 - east; 3 - south; 4 - west **/
	protected byte lookingDirection;
	
	// REAL (X, Y) CONSTRUCTORS
	
	public Entity(Handler handler, float x, float y) {
		this.handler = handler;
		health = DEFAULT_HEALTH;
		maxHealth = DEFAULT_HEALTH;
		killable = true;
		
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		String className = new String(c);
		
		if(Assets.textureExists(className)) {
			this.texture = Assets.getTexture(className);
		}
		
		if(Assets.animationExists(className)) {
			this.animation = Assets.getAnimation(className);
			this.texture = this.animation.getCurrentFrame();
		}
		
		this.transform = new Transform();
		this.transform.size.x = texture.width;
		this.transform.size.y = texture.height;
		
		init();
		
		this.transform.position.x = x * Tile.SIZE - originX;
		this.transform.position.y = y * Tile.SIZE - originY;
		this.transform.calculateZIndex();
		
		if(texture == null) {
			this.sprite = new BatchSprite(new Vector4f(0, 0, 0, 1), transform);
		} else {
			this.sprite = new BatchSprite(texture, transform);
		}
	}
	
	public Entity(Handler handler, float x, float y, float width, float height) {
		this.handler = handler;
		this.transform = new Transform();
		this.transform.size.x = width * Tile.SIZE;
		this.transform.size.y = height * Tile.SIZE;
		health = DEFAULT_HEALTH;
		maxHealth = DEFAULT_HEALTH;
		killable = true;
		
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		String className = new String(c);
		
		if(Assets.textureExists(className)) {
			this.texture = Assets.getTexture(className);
		}
		
		if(Assets.animationExists(className)) {
			this.animation = Assets.getAnimation(className);
			this.texture = this.animation.getCurrentFrame();
		}
		
		init();
		
		this.transform.position.x = (int) (x * Tile.SIZE - originX);
		this.transform.position.y = (int) (y * Tile.SIZE - originY);
		this.transform.calculateZIndex();
		
		if(texture == null) {
			this.sprite = new BatchSprite(new Vector4f(0, 0, 0, 1), transform);
		} else {
			this.sprite = new BatchSprite(texture, transform);
		}
	}
	
	// Abstracts
	protected abstract void init();
	public abstract void update(float dt);
	public abstract void render();
	public abstract void onDead();
	
	// Additional methods
	public void renderGUI(long vg) {}
	
	protected void updateSprite() {
		sprite.update(transform);
	}
	
	protected void updateAnimation() {
		animation.update();
		
		this.texture = animation.getCurrentFrame();
	}
	
	protected void updateAnimationDirection(Animation up, Animation down, Animation left, Animation right) {
		switch(lookingDirection) {
			case UP: {
				animation = up;
			}
			
			case RIGHT: {
				animation = right;
			}
			
			case LEFT: {
				animation = left;
			}
			
			default: {
				animation = down;
			}
		}
	}
	
	@Deprecated
	public void distantDamage(Item item) {
		if(item == null)
			return;

		int distance = getDistanceFromPlayer();
		int damage = Player.DEFAULT_DAMAGE;
		int range = Player.DEFAULT_RANGE;
		
		if(item.isTool()) {
			Tool tool = (Tool) item;
			
			damage = tool.getDamage();
			range = tool.getRange();
		}
		
		if(distance > range)
			return;
		
		damage(damage);
	}
	
	public void damage(int damage) {
		if(!killable)
			return;
		
		health -= damage;
		
		checkHealth(null);
	}
	
	public void damage(Item item) {
		if(!killable)
			return;
		
		String killedBy = "bare hands";
		
		if(item != null && item.isTool()) {
			Tool tool = (Tool) item;
			
			health -= tool.getDamage();
			killedBy = tool.getName();
		} else {
			health -= Player.DEFAULT_DAMAGE;
		}
		
		checkHealth("killed using " + killedBy);
	}
	
	public void heal(int heal) {
		health = Math.min(health + heal, maxHealth);
	}
	
	public void heal(Item item) {
		if(item == null || !item.isTool())
			return;
		
		Tool tool = (Tool) item;
		
		health = Math.min(health + tool.getHeal(), maxHealth);
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	public void checkHealth(String textOnDeath) {
		if(isDead())
			kill(textOnDeath);
	}
	
	public void kill(String text) {
		handler.getWorld().getEntityManager().kill(this, text);
		dropAll();
	}
	
	public void vanish() {
		handler.getWorld().getEntityManager().vanish(this);
	}
	
	public void dropLoot() {
		if(loot.isEmpty()) {
			return;
		}
		
		Pouch pouch = new Pouch(handler, getWorldX(), getWorldY());
		pouch.getInventory().add(loot);
		
		handler.getWorld().getEntityManager().addEntity(pouch);
	}
	
	public void dropAll() {
		dropLoot();
		
		if(inventory != null) {
			inventory.dropAll();
		}
	}
	
	public boolean hover() {
		float screenX = getScreenX();
		float screenY = getScreenY();
		
		if(handler.getMouseManager().getMouseX() >= screenX && handler.getMouseManager().getMouseX() <= screenX + transform.size.x &&
		   handler.getMouseManager().getMouseY() >= screenY && handler.getMouseManager().getMouseY() <= screenY + transform.size.y)
			return true;
		else
			return false;
	}
	
	public boolean leftClicked() {
		if(handler.getMouseManager().isLeftReleased() && hover())
			return true;
		else
			return false;
	}
	
	public boolean rightClicked() {
		if(handler.getMouseManager().isRightReleased() && hover())
			return true;
		else
			return false;
	}
	
	//Checking collisions
	public boolean collidesWithAny(float xOffset, float yOffset) {
		if(!hasBounds())
			return false;
		
		for(Entity entity : handler.getWorld().getEntityManager().getEntities()) {
			if(entity.equals(this) || !entity.hasBounds())
				continue;
			
			if(collidesWith(entity, xOffset, yOffset))
				return true;
		}
		
		return false;
	}
	
	public boolean collidesWith(Entity entity, float xOffset, float yOffset) {
		if(!hasBounds())
			return false;
		
		if(entity.equals(this) || !entity.hasBounds())
			return false;
		
		float boundsX = this.boundsX + xOffset;
		float boundsY = this.boundsY + yOffset;
		
		return (boundsX >= entity.boundsX && boundsX <= entity.boundsX + entity.boundsWidth && boundsY >= boundsY && entity.boundsY <= entity.boundsY + entity.boundsHeight);
	}
	
	public boolean isPlayerOn() {
		float playerX = handler.getPlayer().getX();
		float playerY = handler.getPlayer().getY();
		float playerWidth = handler.getPlayer().getWidth();
		float playerHeight = handler.getPlayer().getHeight();
		
		return (transform.position.x >= playerX && transform.position.x <= playerX + playerWidth && transform.position.y >= playerY && transform.position.y <= playerY + playerHeight);
	}
	
	public int getDistanceFromPlayer() {
		int distance = (int) Math.hypot(transform.position.x - handler.getPlayer().getX(), transform.position.y - handler.getPlayer().getY());
		
		return distance;
	}
	
	public void renderDebug() {
		//Collision boxes
//		if(handler.getRenderingRules().collisionBoxes && bounds != null) {
//			g.setColor(Color.black);
//			g.fillRect((int) (x + bounds.x - handler.getCamera().getxOffset()),
//					(int) (y + bounds.y - handler.getCamera().getyOffset()),
//					bounds.width, bounds.height);
//		}
//		
//		if(handler.getRenderingRules().tags) {
//			g.setColor(Color.magenta);
//			g.setFont(Presets.FONT_GLOBAL);
//			g.drawString(getDebugAddress(), (int) getScreenX(), (int) getScreenY());
//		}
	}
	
	public String getDebugAddress() {
		String classAddress = this.getClass().getName();
		classAddress = classAddress.replace(Modules.ENTITIES, "") + "#" + this.hashCode();
		
		return classAddress;
	}
	
	public float getX() {
		return transform.position.x + originX;
	}
	
	public float getWorldX() {
		return (transform.position.x + originX) / Tile.SIZE;
	}

	public void setX(float x) {
		this.transform.position.x = x - originX;
	}
	
	public void setWorldX(float x) {
		this.transform.position.x = x * Tile.SIZE - originX;
	}

	public float getY() {
		return transform.position.y + originY;
	}
	
	public float getWorldY() {
		return (transform.position.y + originY) / Tile.SIZE;
	}

	public void setY(float y) {
		this.transform.position.y = y - originY;
	}
	
	public void setWorldY(float y) {
		this.transform.position.y = y * Tile.SIZE - originY;
	}
	
	public float getRealX() {
		return transform.position.x;
	}
	
	public void setRealX(float x) {
		this.transform.position.x = x;
	}
	
	public float getRealY() {
		return transform.position.y;
	}
	
	public void setRealY(float y) {
		this.transform.position.y = y;
	}
	
	public int getChunkX() {
		return Math.floorDiv((int) getWorldX(), Chunk.SIZE);
	}
	
	public int getChunkY() {
		return Math.floorDiv((int) getWorldY(), Chunk.SIZE);
	}

	public float getWidth() {
		return transform.size.x;
	}

	public void setWidth(float width) {
		this.transform.size.x = width;
	}

	public float getHeight() {
		return transform.size.y;
	}

	public void setHeight(float height) {
		this.transform.size.y = height;
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public float getScreenX() {
		return transform.position.x - handler.getCamera().getPosition().x;
	}

	public float getScreenY() {
		return transform.position.y - handler.getCamera().getPosition().y;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	BatchSprite getSprite() {
		return sprite;
	}

	public boolean isKillable() {
		return killable;
	}

	public void setKillable(boolean killable) {
		this.killable = killable;
	}
	
	public byte getLookingDirection() {
		return lookingDirection;
	}
	
	public void setLookingDirection(byte lookingDirection) {
		this.lookingDirection = lookingDirection;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		this.maxHealth = health;
	}
	
	public void setHealth(int health, boolean ignoreMaxHealth) {
		if(ignoreMaxHealth) {
			this.health = health;
			return;
		}
		
		setHealth(health);
	}
	
	public float getBoundsX() {
		return boundsX;
	}
	
	public float getBoundsY() {
		return boundsY;
	}
	
	public float getBoundsWidth() {
		return boundsWidth;
	}
	
	public float getBoundsHeight() {
		return boundsHeight;
	}

	public void setBounds(float boundsX, float boundsY, float boundsWidth, float boundsHeight) {
		this.boundsX = boundsX;
		this.boundsY = boundsY;
		this.boundsWidth = boundsWidth;
		this.boundsHeight = boundsHeight;
	}
	
	public boolean hasBounds() {
		return boundsWidth > 0 && boundsHeight > 0;
	}

	public void setOrigin(Origin origin) {
		switch(origin) {
			case TOP_LEFT: {
				originX = 0;
				originY = 0;
				break;
			}
			
			case TOP_RIGHT: {
				originX = transform.size.x;
				originY = 0;
				break;
			}
			
			case BOTTOM_RIGHT: {
				originX = transform.size.x;
				originY = transform.size.y;
				break;
			}
			
			case BOTTOM_LEFT: {
				originX = 0;
				originY = transform.size.y;
				break;
			}
			
			case LEFT: {
				originX = 0;
				originY = transform.size.y / 2;
				break;
			}
			
			case TOP: {
				originX = transform.size.x / 2;
				originY = 0;
				break;
			}
			
			case RIGHT: {
				originX = transform.size.x;
				originY = transform.size.y / 2;
				break;
			}
			
			case BOTTOM: {
				originX = transform.size.x / 2;
				originY = transform.size.y;
				break;
			}
			
			default: {
				originX = transform.size.x / 2;
				originY = transform.size.y / 2;
				break;
			}
		}
	}
	
	public float getOriginX() {
		return originX;
	}

	public void setOriginX(float originX) {
		this.originX = originX;
	}

	public float getOriginY() {
		return originY;
	}

	public void setOriginY(float originY) {
		this.originY = originY;
	}

	public Texture getCustomActionIcon() {
		return customActionIcon;
	}

	public void setCustomActionIcon(Texture customActionIcon) {
		this.customActionIcon = customActionIcon;
	}
	
	public Action getAction(int attribute) {
		return actionsByAttribute.get(attribute);
	}
	
	public Action getAction(Class<? extends Item> itemClass) {
		return actionsByItem.get(itemClass);
	}

	public Tile standsOnTile() {
		return handler.getWorld().getChunkManager().getTile((int) getWorldX(), (int) getWorldY());
	}

	public List<InventoryProperty> getLoot() {
		return loot;
	}

	public void setLoot(List<InventoryProperty> loot) {
		this.loot = loot;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public static Entity random(Entity[] entities, float[] chances) {
		if(entities.length != chances.length) {
			Logging.error("Cannot return random entity: entities and chances do not have the same length!");
			return entities[0];
		}
		
		Random random = new Random();
		double r = random.nextFloat();
		
		for(int i = 0; i < entities.length; i++) {
			if(r < chances[i]) {
				return entities[i];
			}
		}
		
		return entities[entities.length - 1];
	}
	
	public static Entity random(Random random, Entity[] entities, float[] chances) {
		if(entities.length != chances.length) {
			Logging.error("Cannot return random entity: entities and chances do not have the same length!");
			return entities[0];
		}

		double r = random.nextFloat();
		
		for(int i = 0; i < entities.length; i++) {
			if(r < chances[i]) {
				return entities[i];
			}
		}
		
		return entities[entities.length - 1];
	}
}
