package konradn24.tml.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import konradn24.tml.Handler;
import konradn24.tml.entities.actions.Action;
import konradn24.tml.entities.creatures.characters.Player;
import konradn24.tml.entities.statics.Pouch;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.inventory.items.Item;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Modules;

public abstract class Entity {

	public static final int DEFAULT_HEALTH = 10, DEFAULT_VIEWFINDER_SIZE = 32;
	public static final Color DEFAULT_VIEWFINDER_INFO_STRING_COLOR = Color.red;
	public static final byte UP = 1, RIGHT = 2, DOWN = 3, LEFT = 4;
	
	protected float x, y;
	protected int originX, originY;
	protected int width, height;
	protected int playerXCalibrated, playerYCalibrated;
	
	protected BufferedImage texture, customActionIcon;
	protected int health, maxHealth;
	protected boolean visible, dead, killable;
	protected Rectangle bounds;
	
	protected Map<Class<? extends Item>, Action> actionsByItem = new HashMap<>();
	protected Map<Integer, Action> actionsByAttribute = new HashMap<>();
	
	protected Map<Class<? extends Item>, Integer> items = new HashMap<>(); // TODO: dropping items on death
	
	protected Handler handler;
	
	/** 1 - north; 2 - east; 3 - south; 4 - west **/
	protected byte lookingDirection;
	
	public Entity(Handler handler, float x, float y, int width, int height) {
		this.handler = handler;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		health = DEFAULT_HEALTH;
		maxHealth = DEFAULT_HEALTH;
		killable = true;
		
		bounds = new Rectangle(0, 0, width, height);
		
		char[] c = this.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		
		this.texture = Assets.getTexture(new String(c));
		
		onInit();
	}
	
	// Abstracts
	protected abstract void onInit();
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract void onDead();
	
	// Additional methods
	public void renderGUI(Graphics g) {}
	
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
	}
	
	public void vanish() {
		handler.getWorld().getEntityManager().vanish(this);
	}
	
	public void drop(Map<Class<? extends Item>, Integer> items) {
		Pouch pouch = new Pouch(handler, x + width / 2 - Pouch.WIDTH / 2, y + height / 2 - Pouch.HEIGHT / 2);
		pouch.setItems(items);
		
		handler.getWorld().getEntityManager().addEntity(pouch);
	}
	
	//Mouse staff
	public boolean hover() {
		int screenX = getScreenX();
		int screenY = getScreenY();
		
		if(handler.getMouseManager().getMouseX() >= screenX && handler.getMouseManager().getMouseX() <= screenX + width &&
		   handler.getMouseManager().getMouseY() >= screenY && handler.getMouseManager().getMouseY() <= screenY + height)
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
		if(bounds == null)
			return false;
		
		for(Entity entity : handler.getWorld().getEntityManager().getEntities()) {
			if(entity.equals(this) || entity.bounds == null)
				continue;
			
			if(entity.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
				return true;
		}
		
		return false;
	}
	
	public boolean collidesWith(Entity entity, float xOffset, float yOffset) {
		if(bounds == null)
			return false;
		
		if(entity.equals(this) || entity.bounds == null)
			return false;
		
		if(entity.getCollisionBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
			return true;
		
		return false;
	}
	
	public boolean isPlayerOn() {
		Rectangle player = new Rectangle((int) handler.getPlayer().getX(), (int) handler.getPlayer().getY(), handler.getPlayer().getWidth(), handler.getPlayer().getHeight());
		
		return player.intersects(x, y, width, height);
	}
	
	public int getDistanceFromPlayer() {
		int distance = (int) Math.hypot(x - handler.getPlayer().getX(), y - handler.getPlayer().getY());
		
		return distance;
	}
	
	//Rendering stuff on debug mode
	public void renderDebugMode(Graphics g) {
		//Collision boxes
		if(bounds != null) {
			g.setColor(Color.black);
			g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
					(int) (y + bounds.y - handler.getGameCamera().getyOffset()),
					bounds.width, bounds.height);
		}
		
		g.setColor(Color.magenta);
		g.setFont(Presets.FONT_GLOBAL);
		g.drawString(getDebugAddress(), (int) getScreenX(), (int) getScreenY());
	}
	
	public String getDebugAddress() {
		String classAddress = this.getClass().getName();
		classAddress = classAddress.replace(Modules.ENTITIES, "") + "#" + this.hashCode();
		
		return classAddress;
	}
	
	public Rectangle getCollisionBounds(float xOffset, float yOffset) {
		if(bounds == null)
			return null;
		
		return new Rectangle((int) (x + bounds.x + xOffset), (int) (y + bounds.y + yOffset), bounds.width, bounds.height);
	}
	
	public float getX() {
		return x;
	}
	
	public int getTileX() {
		return (int) x / Tile.TILE_WIDTH;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void setTileX(float x) {
		this.x = x * Tile.TILE_WIDTH;
	}

	public float getY() {
		return y;
	}
	
	public int getTileY() {
		return (int) y / Tile.TILE_HEIGHT;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setTileY(float y) {
		this.y = y * Tile.TILE_HEIGHT;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getScreenX() {
		return (int) (x - handler.getGameCamera().getxOffset());
	}

	public int getScreenY() {
		return (int) (y - handler.getGameCamera().getyOffset());
	}

	public BufferedImage getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
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
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public int getOriginX() {
		return originX;
	}

	public void setOriginX(int originX) {
		this.originX = originX;
	}

	public int getOriginY() {
		return originY;
	}

	public void setOriginY(int originY) {
		this.originY = originY;
	}

	public BufferedImage getCustomActionIcon() {
		return customActionIcon;
	}

	public void setCustomActionIcon(BufferedImage customActionIcon) {
		this.customActionIcon = customActionIcon;
	}
	
	public Action getAction(int attribute) {
		return actionsByAttribute.get(attribute);
	}
	
	public Action getAction(Class<? extends Item> itemClass) {
		return actionsByItem.get(itemClass);
	}

	public Tile standsOnTile() {
		return handler.getWorld().getTile((int) (x / Tile.TILE_WIDTH), (int) (y / Tile.TILE_HEIGHT));
	}
}
