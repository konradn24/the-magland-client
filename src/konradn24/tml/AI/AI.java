package konradn24.tml.AI;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.creatures.Creature;

public class AI {
	
	public static final int DEFAULT_VIEW_RANGE = 500, DEFAULT_ATTACK_RANGE = 100, DEFAULT_ATTACK_POINTS = 15;
	
	private Handler handler;
	private Creature entity;
	private int viewRange, attackRange, attackPoints;
	private int timeInterval;
	private byte mode;
	
	/** TIME INTERVAL IN SECONDS! mode 0 - peacefully | mode 1 - neutrally | mode 2 - hostile */
	public AI(Handler handler, Creature entity, int timeInterval, byte mode) {
		this.handler = handler;
		this.entity = entity;
		this.timeInterval = timeInterval;
		this.mode = mode;
		
		viewRange = DEFAULT_VIEW_RANGE;
		attackRange = DEFAULT_ATTACK_RANGE;
		attackPoints = DEFAULT_ATTACK_POINTS;
	}
	
	int xCalibrated, yCalibrated, playerXCalibrated, playerYCalibrated;
	long timer, lastTime = System.currentTimeMillis();
	
	public void tick() {
		switch(mode) {
		case 0: { //peaceful
			//TODO peaceful mode behavior 
			break;
		}
		
		case 1: { //neutral
			//TODO neutral mode behavior
			break;
		}
		
		case 2: { //hostile
			xCalibrated = (int) (entity.getX() - handler.getGameCamera().getxOffset());
			yCalibrated = (int) (entity.getY() - handler.getGameCamera().getyOffset());
			playerXCalibrated = (int) (handler.getPlayer().getX() - handler.getGameCamera().getxOffset());
			playerYCalibrated = (int) (handler.getPlayer().getY() - handler.getGameCamera().getyOffset());
			
			int distance = (int) Math.sqrt((xCalibrated - playerXCalibrated) * (xCalibrated - playerXCalibrated) + (yCalibrated - playerYCalibrated) * (yCalibrated - playerYCalibrated)) * 2;
		
			if(distance > viewRange) {
				entity.setxMove(0);
				entity.setyMove(0);
				return;
			}
			
			if(distance > attackRange) {
				// X axis
				if(playerXCalibrated < xCalibrated) entity.setxMove(-entity.getSpeed()); // <--
				else if(playerXCalibrated > xCalibrated) entity.setxMove(entity.getSpeed()); // -->
				
				if(playerYCalibrated < yCalibrated) entity.setyMove(-entity.getSpeed()); // ^
				else if(playerYCalibrated > yCalibrated) entity.setyMove(entity.getSpeed()); // v
			} else {
				//Attacking player
				timer = System.currentTimeMillis();
				if(timer - lastTime >= timeInterval) {
					handler.getPlayer().damage(-attackPoints);
					lastTime = System.currentTimeMillis();
				}
				
				//Stop moving
				entity.setxMove(0);
				entity.setyMove(0);
			}
			
			break;
		}
		
		default: {
			Logging.error("Mode can be only between 0-2, mode " + mode + " is incorrect!");
			mode = 0;
			Logging.error("Mode has been changed to 0");
		}
		}
	}
	
	//GETTERS AND SETTERS

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Creature entity) {
		this.entity = entity;
	}

	public int getRange() {
		return viewRange;
	}

	public void setRange(int range) {
		this.viewRange = range;
	}

	public int getAttackPoints() {
		return attackPoints;
	}

	public void setAttackPoints(int attackPoints) {
		this.attackPoints = attackPoints;
	}

	public byte getMode() {
		return mode;
	}
}
