package konradn24.tml.entities.creatures.characters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.building.BuildingsMenu;
import konradn24.tml.building.BuildingsPlacer;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.creatures.Creature;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.ProgressBar;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.images.ImageLoader;
import konradn24.tml.gfx.widgets.Animation;
import konradn24.tml.gfx.widgets.msg.MessageBox;
import konradn24.tml.inventory.Inventory;
import konradn24.tml.inventory.items.Stick;
import konradn24.tml.inventory.items.Stone;
import konradn24.tml.inventory.tools.Tool;
import konradn24.tml.states.GameState;
import konradn24.tml.states.State;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Utils;

public class Player extends Creature {
	
	// TODO: Exp progress bar
	
	public static final float FLYING_SPEED = 25f;
	
	public static final int DEFAULT_DAMAGE = 5;
	public static final int DEFAULT_HEALTH = 50;
	public static final int DEFAULT_THIRST = 50;
	public static final int DEFAULT_HUNGER = 50;
	public static final int DEFAULT_RANGE = 100;
	public static final int DEFAULT_CARRY_LIMIT = 25;
	
	private static final int SEARCH_GROUND_COOLDOWN = 3 * 1000;
	private static final int MATH_BASE_XP = 25;
	
	public static final List<Integer> LEVELS_THRESHOLD = new ArrayList<>();;
	public static final int MAX_LEVEL = 128;
	
	private static final int STAT_BAR_WIDTH = 192;
	private static final int STAT_BAR_HEIGHT = 24;
	private static final int STAT_BAR_MARGIN_X = 48;
	private static final int STAT_BAR_MARGIN_Y = 16;
	
	// Animations
	private Animation animDown, animUp, animLeft, animRight;
	
	// Modules
	private Inventory inventory;
	private BuildingsMenu buildingsMenu;
	private BuildingsPlacer buildingPlacer;
	
	// View
	private BufferedImage view;
	
	// Stats
	private int experiencePoints, totalExperiencePoints, experienceLevel, nextLevelThreshold;
	
	private float maxSpeed;
	private float carryLimit;
	
	private ProgressBar healthBar, thirstBar, hungerBar;
	private int thirst, hunger;
	
	// Timers
	private long lastSearchGroundTime;
	
	public Player(Handler handler, float x, float y) {
		super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);
	}
	
	@Override
	protected void onInit() {
		// Bounds
		bounds.x = 22;
		bounds.y = 44;
		bounds.width = 19;
		bounds.height = 19;
		
		// Animations
		animDown = new Animation(500, Assets.getAnimation("playerDown"), true);
		animUp = new Animation(500, Assets.getAnimation("playerUp"), true);
		animLeft = new Animation(500, Assets.getAnimation("playerLeft"), true);
		animRight = new Animation(500, Assets.getAnimation("playerRight"), true);
		
		// Modules
		inventory = new Inventory("playerInventory", handler);
		buildingsMenu = new BuildingsMenu("playerBuildings", handler);
		buildingPlacer = new BuildingsPlacer(handler);
		
		// View
		view = ImageLoader.loadImage("/textures/view.png");
		
		// Stats
		setHealth(DEFAULT_HEALTH);
		thirst = DEFAULT_THIRST;
		hunger = DEFAULT_HUNGER;
		
		maxSpeed = DEFAULT_SPEED;
		carryLimit = DEFAULT_CARRY_LIMIT;
		lookingDirection = 3;
		
		for(int i = 0; i < MAX_LEVEL; i++)
			LEVELS_THRESHOLD.add(MATH_BASE_XP * i * (1 + i));
		
		experiencePoints = 0;
		experienceLevel = 0;
		nextLevelThreshold = LEVELS_THRESHOLD.get(experienceLevel + 1) - LEVELS_THRESHOLD.get(experienceLevel);
		
		healthBar = new ProgressBar(STAT_BAR_MARGIN_X, handler.getStyle().getScreenHeight() - STAT_BAR_MARGIN_Y * 3 - STAT_BAR_HEIGHT * 3, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_HEALTH);
		thirstBar = new ProgressBar(STAT_BAR_MARGIN_X, handler.getStyle().getScreenHeight() - STAT_BAR_MARGIN_Y * 2 - STAT_BAR_HEIGHT * 2, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_THIRST);
		hungerBar = new ProgressBar(STAT_BAR_MARGIN_X, handler.getStyle().getScreenHeight() - STAT_BAR_MARGIN_Y - STAT_BAR_HEIGHT, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_HUNGER);
		
		// Log
		Logging.info("Player initialized");
	}
	
	@Override
	public void tick() {
		// Animations
		animDown.tick();
		animUp.tick();
		animLeft.tick();
		animRight.tick();
		
		// Movement
		if(inventory.isChangedCurrent() && inventory.getCurrentItem() != null) {
			speed = maxSpeed;
			speed -= inventory.getCurrentItem().getWeight() / 10;
		}
		
		if(handler.getGameRules().flying && handler.getKeyManager().getKeys()[KeyEvent.VK_SHIFT])
			speed = FLYING_SPEED;
		else if(speed == FLYING_SPEED) speed = maxSpeed;
		
		if(!inventory.isOpened()) {
			getInput();
			move();
		}
		
		handler.getGameCamera().centerOnEntity(this);
		
		// Modules
		inventory.tick();
		buildingsMenu.tick();
		buildingPlacer.tick();
		
		// Stats
		healthBar.tick(health);
		thirstBar.tick(thirst);
		hungerBar.tick(hunger);
	}
	
	private void getInput(){
		xMove = 0;
		yMove = 0;
		
		// UP + LEFT + RIGHT
		if(handler.getKeyManager().up && handler.getKeyManager().left && handler.getKeyManager().right) {
			lookingDirection = UP;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = 0;
					
		// UP + LEFT
		} else if(handler.getKeyManager().up && handler.getKeyManager().left) {
			lookingDirection = LEFT;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = (float) (-speed * (1 / Math.sqrt(2)));
			
		// UP + RIGHT
		} else if(handler.getKeyManager().up && handler.getKeyManager().right) {
			lookingDirection = RIGHT;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = (float) (speed * (1 / Math.sqrt(2)));
		
		// UP + DOWN
		} else if(handler.getKeyManager().up && handler.getKeyManager().down) {
			lookingDirection = DOWN;
			yMove = 0;
			xMove = 0;
		
		// UP
		} else if(handler.getKeyManager().up) {
			lookingDirection = UP;
			yMove = -speed;
		
		// DOWN + LEFT + RIGHT
		} else if(handler.getKeyManager().down && handler.getKeyManager().left && handler.getKeyManager().right) {
			lookingDirection = DOWN;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = 0;
				
		// DOWN + RIGHT
		} else if(handler.getKeyManager().down && handler.getKeyManager().right) {
			lookingDirection = RIGHT;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = (float) (speed * (1 / Math.sqrt(2)));
			
		// DOWN + LEFT
		} else if(handler.getKeyManager().down && handler.getKeyManager().left) {
			lookingDirection = LEFT;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = (float) (-speed * (1 / Math.sqrt(2)));
		
		// DOWN
		} else if(handler.getKeyManager().down) {
			lookingDirection = DOWN;
			yMove = speed;
		
		// LEFT + RIGHT
		} else if(handler.getKeyManager().left && handler.getKeyManager().right) {
			lookingDirection = DOWN;
			yMove = 0;
			xMove = 0;
		} else if(handler.getKeyManager().left) {
			lookingDirection = LEFT;
			xMove = -speed;
		} else if(handler.getKeyManager().right) {
			lookingDirection = RIGHT;
			xMove = speed;
		}
		
		//Searching ground
		if(handler.getKeyManager().getKeys()[KeyEvent.VK_SPACE] && lastSearchGroundTime + SEARCH_GROUND_COOLDOWN < System.currentTimeMillis()) {
			searchGround();
			lastSearchGroundTime = System.currentTimeMillis();
		}
	}
	
	private void searchGround() {
		Tile tile = handler.getWorld().getTile((int) (x / Tile.TILE_WIDTH), (int) (y / Tile.TILE_HEIGHT));
	
		if(tile.hasAttribute(Tile.ATTRIB_SEARCHABLE)) {
			if(handler.getWorld().getRandom().nextDouble() < 0.33) {
				if(handler.getWorld().getRandom().nextDouble() < 0.65)
					inventory.add(Stick.class, 1);
				else
					inventory.add(Stone.class, 1);
			}
		} else {
			Logging.warning("Not here! <TODO>");
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
		
//		g.setColor(Color.black);
//		g.fillRect((int) (getTileAheadX() * Tile.TILE_WIDTH - handler.getGameCamera().getxOffset()), (int) (getTileAheadY() * Tile.TILE_HEIGHT - handler.getGameCamera().getyOffset()), Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
		
		if(GameState.debugMode) {
			//Range oval
			int playerXCalibrated = (int) (x - handler.getGameCamera().getxOffset());
			int playerYCalibrated = (int) (y - handler.getGameCamera().getyOffset());
			
			if(inventory.getCurrentItem() != null && inventory.getCurrentItem().isTool()) {
				Tool currentItem = (Tool) inventory.getCurrentItem();
				
				g.setColor(Color.red);
				g.drawOval(playerXCalibrated - currentItem.getRange() / 2 + width / 2, playerYCalibrated - currentItem.getRange() / 2 + height / 2, currentItem.getRange(), currentItem.getRange());
			}
		}
	}
	
	public void renderView(Graphics g) {
		g.drawImage(view, (int) (x - handler.getGameCamera().getxOffset() - 460 - (1025)), (int) (y - handler.getGameCamera().getyOffset() - 360 - (1100)), 3000, 3000, null);
	}
	
	// TODO: render stats' icons
	public void renderStats(Graphics g) {
		healthBar.render(g);
		thirstBar.render(g);
		hungerBar.render(g);
	}
	
	public void renderInventory(Graphics g) {
		if(inventory.getCurrentItem() == null) {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
			g.setColor(Color.black);
			g.drawString("[SPACE] to search ground...", handler.getWidth() / 2 - 230 / 2, handler.getHeight() - 80);
		}
		
		if(lastSearchGroundTime + SEARCH_GROUND_COOLDOWN > System.currentTimeMillis())
			inventory.renderCooldown(g, SEARCH_GROUND_COOLDOWN, lastSearchGroundTime + SEARCH_GROUND_COOLDOWN);
		
		inventory.render(g);
	}
	
	@Override
	public void onDead() {
		
	}
	
	public void placeEntity(Entity entity) {
		entity.setTileX(getTileAheadX() + 0.5f);
		entity.setTileY(getTileAheadY() + 1f);
		
		handler.getWorld().getEntityManager().addEntity(entity);
	}
	
	public boolean placeEntity(Class<? extends Entity> entityClass) {
		Entity entity = handler.getWorld().getEntityManager().getEntityInstance(entityClass, 0, 0);
		
		if(entity == null) {
			Logging.error("Player: cannot place entity - creating instance error occurred!");
			
			return false;
		}
		
		placeEntity(entity);
		
		return true;
	}
	
	public Tile getTileAhead() {
		float x = 0;
		float y = 0;
		
		if(lookingDirection == UP) {
			x = this.x + this.width / 2;
			y = this.y - Tile.TILE_HEIGHT;
		} else if(lookingDirection == DOWN) {
			x = this.x + this.width / 2;
			y = this.y + this.height + Tile.TILE_HEIGHT;
		} else if(lookingDirection == LEFT) {
			x = this.x - Tile.TILE_WIDTH;
			y = this.y + this.height / 2;
		} else {
			x = this.x + this.width + Tile.TILE_WIDTH;
			y = this.y + this.height / 2;
		}
		
		int tileX = (int) Math.floor(x / Tile.TILE_WIDTH);
		int tileY = (int) Math.floor(y / Tile.TILE_HEIGHT);
		
		return handler.getWorld().getTile(tileX, tileY);
	}
	
	public int getTileAheadX() {
		float x = 0;
		
		if(lookingDirection == UP) {
			x = this.x + this.width / 2;
		} else if(lookingDirection == DOWN) {
			x = this.x + this.width / 2;
		} else if(lookingDirection == LEFT) {
			x = this.x - Tile.TILE_WIDTH;
		} else {
			x = this.x + this.width + Tile.TILE_WIDTH;
		}
		
		int tileX = (int) Math.floor(x / Tile.TILE_WIDTH);
		
		return tileX;
	}
	
	public int getTileAheadY() {
		float y = 0;
		
		if(lookingDirection == UP) {
			y = this.y - Tile.TILE_HEIGHT;
		} else if(lookingDirection == DOWN) {
			y = this.y + this.height + Tile.TILE_HEIGHT;
		} else if(lookingDirection == LEFT) {
			y = this.y + this.height / 2;
		} else {
			y = this.y + this.height / 2;
		}
		
		int tileY = (int) Math.floor(y / Tile.TILE_HEIGHT);
		
		return tileY;
	}
	
	private BufferedImage getCurrentAnimationFrame() {
		switch(lookingDirection) {
		case UP: {
			return animUp.getCurrentFrame();
		}
		
		case RIGHT: {
			return animRight.getCurrentFrame();
		}
		
		case DOWN: {
			return animDown.getCurrentFrame();
		}
		
		default: {
			return animLeft.getCurrentFrame();
		}
		}
	}
	
	public void addExperiencePoints(int amount) {
		if(experiencePoints + amount >= 0) {
			this.totalExperiencePoints += amount;
			this.experiencePoints += amount;
		} else {
			this.totalExperiencePoints -= experiencePoints;
			this.experiencePoints = 0;
		}
		
		int level = 0;
		
		for(int i = 0; i < LEVELS_THRESHOLD.size(); i++) {
			if(totalExperiencePoints < LEVELS_THRESHOLD.get(i)) {
				level = i - 1;
				break;
			}
		}
		
		if(level <= experienceLevel)
			return;
		
		int oldLevel = experienceLevel;
		
		while(level > experienceLevel) {
			experienceLevel++;
			
			MessageBox msg = new MessageBox(MessageBox.TYPE_OK, "Next level!", "You have reached level " + experienceLevel + "!", handler);
			msg.setBarColor(Presets.COLOR_EXP_WINDOW_BAR);
			msg.setWindowColor(Presets.COLOR_EXP_WINDOW);
			
			msg.setTitleTextColor(Presets.COLOR_EXP_WINDOW_TEXT);
			msg.setTextColor(Presets.COLOR_EXP_WINDOW_TEXT.brighter());
			
			msg.getButtonOk().setColor(Presets.COLOR_EXP_WINDOW_BUTTON);
			msg.getButtonOk().getLabel().setColor(Presets.COLOR_EXP_WINDOW_TEXT.brighter());
			
			State.getState().getDialogsManager().showMessageBox(msg);
		}
		
		experiencePoints -= LEVELS_THRESHOLD.get(experienceLevel) - LEVELS_THRESHOLD.get(oldLevel);
		nextLevelThreshold = LEVELS_THRESHOLD.get(experienceLevel + 1) - LEVELS_THRESHOLD.get(experienceLevel);
	}
	
	public void addLevel(int amount) {
		if(experienceLevel + amount < 0) {
			this.experienceLevel = 0;
			this.experiencePoints = 0;
			this.totalExperiencePoints = 0;
			return;
		}
		
		int toAdd = LEVELS_THRESHOLD.get(experienceLevel + amount) - LEVELS_THRESHOLD.get(experienceLevel);
		
		addExperiencePoints(toAdd);
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public BuildingsMenu getBuildingsMenu() {
		return buildingsMenu;
	}

	public BuildingsPlacer getBuildingPlacer() {
		return buildingPlacer;
	}

	public float getCarryLimit() {
		return carryLimit;
	}
	
	public void setCarryLimit(float carryLimit) {
		this.carryLimit = carryLimit;
	}
	
	public int getExperiencePoints() {
		return experiencePoints;
	}
	
	public int getExperienceLevel() {
		return experienceLevel;
	}

	public int getThirst() {
		return thirst;
	}

	public int getHunger() {
		return hunger;
	}

	public String toString() {
		return "X: " + x / Tile.TILE_WIDTH 
				+ " | Y: " + y / Tile.TILE_HEIGHT + "\n"
				+ "Health: " + health
				+ " | Speed: " + speed
				+ " | Looking direction: " + Utils.directionString(lookingDirection) + "\n"
				+ "Weight limit: " + carryLimit
				+ " | Current weight: " + inventory.getWeight()
				+ " | Different items: " + inventory.getItemsAndTools().size()
				+ " | All items: " + inventory.getItemsAndTools().values().stream().reduce(0, Integer::sum) + "\n"
				+ " Level: " + experienceLevel
				+ " | XP: " + experiencePoints + " / " + nextLevelThreshold
				+ " | Total XP: " + totalExperiencePoints;
	}
}
