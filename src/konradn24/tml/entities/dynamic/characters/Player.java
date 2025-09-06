package konradn24.tml.entities.dynamic.characters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.glfw.GLFW.*;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.dynamic.DynamicEntity;
import konradn24.tml.graphics.Assets;
import konradn24.tml.graphics.renderer.Animation;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.gui.graphics.Style;
import konradn24.tml.gui.graphics.components.ProgressBar;
import konradn24.tml.gui.graphics.renderers.AssetsRenderer;
import konradn24.tml.gui.graphics.widgets.msgbox.MessageBox;
import konradn24.tml.inventory.Crafting;
import konradn24.tml.inventory.Inventory;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.items.Item;
import konradn24.tml.items.Stick;
import konradn24.tml.items.Stone;
import konradn24.tml.states.State;
import konradn24.tml.tiles.Tile;
import konradn24.tml.utils.Logging;
import konradn24.tml.utils.Utils;

public class Player extends DynamicEntity {
	
	// TODO: Exp progress bar
	
	public static final float FLYING_SPEED = 1500f;
	
	public static final int DEFAULT_DAMAGE = 5;
	public static final int DEFAULT_HEALTH = 50;
	public static final int DEFAULT_THIRST = 50;
	public static final int DEFAULT_HUNGER = 50;
	public static final int DEFAULT_RANGE = 100;
	public static final int DEFAULT_CARRY_LIMIT = 25;
	
	private static final int SEARCH_GROUND_COOLDOWN = 3 * 1000;
	private static final int MATH_BASE_XP = 25;
	
	public static final List<Integer> LEVELS_THRESHOLD = new ArrayList<>();
	public static final int MAX_LEVEL = 128;
	
	private static final float STAT_BAR_WIDTH = 192;
	private static final float STAT_BAR_HEIGHT = 24;
	private static final float STAT_BAR_MARGIN_X = 0;
	private static final float STAT_BAR_MARGIN_Y = 16;
	
	private static final float CURRENT_ITEM_SLOT_SIZE = 100;
	
	// Animations
	private Animation animDown, animUp, animLeft, animRight;
	
	// Modules
//	private BuildingsMenu buildingsMenu;
//	private BuildingsPlacer buildingPlacer;
	
	// Stats
	private int experiencePoints, totalExperiencePoints, experienceLevel, nextLevelThreshold;
	
	private float maxSpeed;
	
	private ProgressBar healthBar, thirstBar, hungerBar;
	private int thirst, hunger;
	
	// Timers
	private long lastSearchGroundTime;
	
	public Player(Handler handler, double x, double y) {
		super(handler, x, y, DynamicEntity.DEFAULT_CREATURE_WIDTH, DynamicEntity.DEFAULT_CREATURE_HEIGHT);
	}
	
	@Override
	protected void init() {
		// Bounds
		setBounds(transform.size.x / 2 - 4, transform.size.y - 8, 8, 8);
		
		setOrigin(Origin.CENTER);
		
		// Animations
		animDown = Assets.getAnimation("playerDown");
		animUp = Assets.getAnimation("playerUp");
		animLeft = Assets.getAnimation("playerLeft");
		animRight = Assets.getAnimation("playerRight");
		
		// Modules
		inventory = new Inventory(this, "Player", false, DEFAULT_CARRY_LIMIT, handler);
		inventory.attachCrafting(new Crafting(Item.ATTRIB_HAND_CRAFTABLE));
		
//		buildingsMenu = new BuildingsMenu("playerBuildings", handler);
//		buildingPlacer = new BuildingsPlacer(handler);
		
		// Stats
		setHealth(DEFAULT_HEALTH);
		thirst = DEFAULT_THIRST;
		hunger = DEFAULT_HUNGER;
		
		maxSpeed = DEFAULT_SPEED;
		lookingDirection = 3;
		
		for(int i = 0; i < MAX_LEVEL; i++)
			LEVELS_THRESHOLD.add(MATH_BASE_XP * i * (1 + i));
		
		experiencePoints = 0;
		experienceLevel = 0;
		nextLevelThreshold = LEVELS_THRESHOLD.get(experienceLevel + 1) - LEVELS_THRESHOLD.get(experienceLevel);
		
		healthBar = new ProgressBar(STAT_BAR_MARGIN_X, Display.LOGICAL_HEIGHT - STAT_BAR_MARGIN_Y * 3 - STAT_BAR_HEIGHT * 3, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_HEALTH, handler);
		thirstBar = new ProgressBar(STAT_BAR_MARGIN_X, Display.LOGICAL_HEIGHT - STAT_BAR_MARGIN_Y * 2 - STAT_BAR_HEIGHT * 2, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_THIRST, handler);
		hungerBar = new ProgressBar(STAT_BAR_MARGIN_X, Display.LOGICAL_HEIGHT - STAT_BAR_MARGIN_Y - STAT_BAR_HEIGHT, STAT_BAR_WIDTH, STAT_BAR_HEIGHT, DEFAULT_HUNGER, handler);
		
		// Log
		Logging.info("Player initialized");
	}
	
	@Override
	public void update(float dt) {
		// Movement
		if(handler.getGameRules().flying && handler.getKeyManager().isPressed(GLFW_KEY_LEFT_SHIFT))
			speed = FLYING_SPEED;
		else if(speed == FLYING_SPEED) speed = maxSpeed;
		
		getInput();
		move(dt);
		
		// Animation
		super.updateAnimationDirection(animUp, animDown, animLeft, animRight);
		super.updateAnimation();
		
		handler.getCamera().centerOnEntity(this);
		
		// Modules
		inventory.update(dt);
//		buildingsMenu.update(dt);
//		buildingPlacer.update(dt);
		
		// Stats
		healthBar.setValue(health);
		thirstBar.setValue(thirst);
		hungerBar.setValue(hunger);
		
		healthBar.update(dt);
		thirstBar.update(dt);
		hungerBar.update(dt);
	}
	
	private void getInput(){
		xMove = 0;
		yMove = 0;
		
		boolean up = handler.getKeyManager().isPressed(GLFW_KEY_W);
		boolean down = handler.getKeyManager().isPressed(GLFW_KEY_S);
		boolean left = handler.getKeyManager().isPressed(GLFW_KEY_A);
		boolean right = handler.getKeyManager().isPressed(GLFW_KEY_D);
		
		// UP + LEFT + RIGHT
		if(up && left && right) {
			lookingDirection = UP;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = 0;
					
		// UP + LEFT
		} else if(up && left) {
			lookingDirection = LEFT;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = (float) (-speed * (1 / Math.sqrt(2)));
			
		// UP + RIGHT
		} else if(up && right) {
			lookingDirection = RIGHT;
			yMove = (float) (-speed * (1 / Math.sqrt(2)));
			xMove = (float) (speed * (1 / Math.sqrt(2)));
		
		// UP + DOWN
		} else if(up && down) {
			lookingDirection = DOWN;
			yMove = 0;
			xMove = 0;
		
		// UP
		} else if(up) {
			lookingDirection = UP;
			yMove = -speed;
		
		// DOWN + LEFT + RIGHT
		} else if(down && left && right) {
			lookingDirection = DOWN;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = 0;
				
		// DOWN + RIGHT
		} else if(down && right) {
			lookingDirection = RIGHT;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = (float) (speed * (1 / Math.sqrt(2)));
			
		// DOWN + LEFT
		} else if(down && left) {
			lookingDirection = LEFT;
			yMove = (float) (speed * (1 / Math.sqrt(2)));
			xMove = (float) (-speed * (1 / Math.sqrt(2)));
		
		// DOWN
		} else if(down) {
			lookingDirection = DOWN;
			yMove = speed;
		
		// LEFT + RIGHT
		} else if(left && right) {
			lookingDirection = DOWN;
			yMove = 0;
			xMove = 0;
		} else if(left) {
			lookingDirection = LEFT;
			xMove = -speed;
		} else if(right) {
			lookingDirection = RIGHT;
			xMove = speed;
		}
		
		// Searching ground
		if(handler.getKeyManager().isPressed(GLFW_KEY_SPACE) && lastSearchGroundTime + SEARCH_GROUND_COOLDOWN < System.currentTimeMillis()) {
			searchGround();
		}
		
		// Inventory
		if(handler.getKeyManager().isPressed(GLFW_KEY_E)) {
			handler.getKeyManager().lockKey(GLFW_KEY_E);
			handler.getPlayGUI().getContextPanel().toggleContext(inventory);
		}
	}
	
	private void searchGround() {
		Random random = new Random();
		Tile tile = standsOnTile();
	
		if(tile.hasAttribute(Tile.ATTRIB_SEARCHABLE)) {
			if(random.nextDouble() < 0.33) {
				if(random.nextDouble() < 0.65) {
					inventory.add(new InventoryProperty(Item.getItem(Stick.class), 1));
					handler.getPlayGUI().getNotificationsPanel().add("You found a stick!");
				} else {
					inventory.add(new InventoryProperty(Item.getItem(Stone.class), 1));
					handler.getPlayGUI().getNotificationsPanel().add("You found a stone!");
				}
			}
			
			lastSearchGroundTime = System.currentTimeMillis();
		} else {
			Logging.warning("Not here! <TODO>");
		}
	}

	@Override
	public void render() {
//		g.setColor(Color.black);
//		g.fillRect((int) (getTileAheadX() * Tile.TILE_WIDTH - handler.getGameCamera().getxOffset()), (int) (getTileAheadY() * Tile.TILE_HEIGHT - handler.getGameCamera().getyOffset()), Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
		
//		if(GameState.debugMode) {
//			//Range oval
//			int playerXCalibrated = (int) (x - handler.getGameCamera().getxOffset());
//			int playerYCalibrated = (int) (y - handler.getGameCamera().getyOffset());
//			
//			if(inventory.getSelected() != null && inventory.getSelectedItem().isTool()) {
//				Tool currentItem = (Tool) inventory.getSelectedItem();
//				
//				g.setColor(Color.red);
//				g.drawOval(playerXCalibrated - currentItem.getRange() / 2 + width / 2, playerYCalibrated - currentItem.getRange() / 2 + height / 2, currentItem.getRange(), currentItem.getRange());
//			}
//		}
	}
	
	// TODO: render stats' icons
	@Override
	public void renderGUI(long vg) {
		healthBar.renderGUI(vg);
		thirstBar.renderGUI(vg);
		hungerBar.renderGUI(vg);
		
		// Search ground cooldown
		if(lastSearchGroundTime + SEARCH_GROUND_COOLDOWN > System.currentTimeMillis()) {
			float x = Style.centerX(CURRENT_ITEM_SLOT_SIZE);
			float y = Display.LOGICAL_HEIGHT - CURRENT_ITEM_SLOT_SIZE - 10;
			
			float yOffset = ((float) (lastSearchGroundTime + SEARCH_GROUND_COOLDOWN - System.currentTimeMillis()) / SEARCH_GROUND_COOLDOWN) * CURRENT_ITEM_SLOT_SIZE;
			
			nvgBeginPath(vg);
			nvgRect(vg, x, y + CURRENT_ITEM_SLOT_SIZE - yOffset, CURRENT_ITEM_SLOT_SIZE, yOffset);
			nvgFillColor(vg, Colors.COOLDOWN);
			nvgFill(vg);
		}
		
		// Current item slot
		float x = Style.centerX(CURRENT_ITEM_SLOT_SIZE);
		float y = Display.LOGICAL_HEIGHT - CURRENT_ITEM_SLOT_SIZE - 10;
		
		nvgBeginPath(vg);
		AssetsRenderer.renderTexture(vg, Assets.getTexture("currentItemSlot"), x, y, CURRENT_ITEM_SLOT_SIZE, CURRENT_ITEM_SLOT_SIZE);
		
		if(inventory.isAnyItemSelected()) {
			InventoryProperty selected = inventory.getSelected();
			
			nvgBeginPath(vg);
			AssetsRenderer.renderTexture(vg, selected.item.getTexture(), x + 6, y + 6, CURRENT_ITEM_SLOT_SIZE - 12, CURRENT_ITEM_SLOT_SIZE - 12);
			
//			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
//			g.setColor(Color.yellow);
//			if(selected.item.isTool()) {
//				Tool selectedTool = (Tool) selected.item;
//				StyleText.drawCenteredString(g, selectedTool.getDurability() + " / " + selectedTool.getMaxDurability(), x + CURRENT_ITEM_SLOT_SIZE / 2, y - 20);
//			}
		} else {
			nvgBeginPath(vg);
			nvgFontFace(vg, Fonts.GLOBAL_FONT);
			nvgFontSize(vg, Fonts.DEFAULT_SIZE);
			nvgFillColor(vg, Colors.TEXT);
			nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
			nvgText(vg, x + CURRENT_ITEM_SLOT_SIZE / 2, y - 10, "[SPACE] to search ground...");
		}
	}
	
	@Override
	public void onDead() {
		
	}
	
	public void placeEntity(Entity entity) {
		entity.setWorldX(getTileAheadX() + 0.5f);
		entity.setWorldY(getTileAheadY() + 1f);
		
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
		double x = 0;
		double y = 0;
		
		if(lookingDirection == UP) {
			x = this.transform.position.x + this.transform.size.x / 2;
			y = this.transform.position.y - Tile.SIZE;
		} else if(lookingDirection == DOWN) {
			x = this.transform.position.x + this.transform.size.x / 2;
			y = this.transform.position.y + this.transform.size.y + Tile.SIZE;
		} else if(lookingDirection == LEFT) {
			x = this.transform.position.x - Tile.SIZE;
			y = this.transform.position.y + this.transform.size.y / 2;
		} else {
			x = this.transform.position.x + this.transform.size.x + Tile.SIZE;
			y = this.transform.position.y + this.transform.size.y / 2;
		}
		
		int tileX = (int) Math.floor(x / Tile.SIZE);
		int tileY = (int) Math.floor(y / Tile.SIZE);
		
		return handler.getWorld().getChunkManager().getTile(tileX, tileY);
	}
	
	public int getTileAheadX() {
		double x = 0;
		
		if(lookingDirection == UP) {
			x = this.transform.position.x + this.transform.size.x / 2;
		} else if(lookingDirection == DOWN) {
			x = this.transform.position.x + this.transform.size.x / 2;
		} else if(lookingDirection == LEFT) {
			x = this.transform.position.x - Tile.SIZE;
		} else {
			x = this.transform.position.x + this.transform.size.x + Tile.SIZE;
		}
		
		int tileX = (int) Math.floor(x / Tile.SIZE);
		
		return tileX;
	}
	
	public int getTileAheadY() {
		double y = 0;
		
		if(lookingDirection == UP) {
			y = this.transform.position.y - Tile.SIZE;
		} else if(lookingDirection == DOWN) {
			y = this.transform.position.y + this.transform.size.y + Tile.SIZE;
		} else if(lookingDirection == LEFT) {
			y = this.transform.position.y + this.transform.size.y / 2;
		} else {
			y = this.transform.position.y + this.transform.size.y / 2;
		}
		
		int tileY = (int) Math.floor(y / Tile.SIZE);
		
		return tileY;
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
			msg.setBarColor(Colors.EXP_WINDOW_BAR);
			msg.setWindowColor(Colors.EXP_WINDOW);
			
			msg.getButtonOk().setColor(Colors.EXP_WINDOW_BUTTON);
			msg.getButtonOk().getLabel().setColor(Colors.brighten(Colors.EXP_WINDOW_TEXT, .25f));
			
			State.showMessageBox(msg);
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
	
//	public BuildingsMenu getBuildingsMenu() {
//		return buildingsMenu;
//	}
//
//	public BuildingsPlacer getBuildingPlacer() {
//		return buildingPlacer;
//	}

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
		return "X: " + transform.position.x / Tile.SIZE 
				+ " | Y: " + transform.position.y / Tile.SIZE + "\n"
				+ "Health: " + health
				+ " | Speed: " + speed
				+ " | Looking direction: " + Utils.directionString(lookingDirection) + "\n"
				+ "Weight limit: " + inventory.getWeightLimit()
				+ " | Current weight: " + inventory.getWeight()
				+ " | Inv. properties: " + inventory.getItems().size()
				+ " | All items: " + inventory.getItems().stream().mapToInt(property -> property.amount).sum() + "\n"
				+ " Level: " + experienceLevel
				+ " | XP: " + experiencePoints + " / " + nextLevelThreshold
				+ " | Total XP: " + totalExperiencePoints;
	}
}
