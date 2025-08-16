package konradn24.tml.entities.creatures;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;
import konradn24.tml.tiles.Tile;

public abstract class Creature extends Entity {
	
	public static final float DEFAULT_SPEED = 150.0f;
	public static final int DEFAULT_CREATURE_WIDTH = 1,
							DEFAULT_CREATURE_HEIGHT = 1;
	private static final int COLLISION_OFFSET = 1;
	
	protected float speed;
	protected float xMove, yMove;
	
//	protected AIController ai;

	public Creature(Handler handler, float x, float y, int width, int height) {
		super(handler, x, y, width, height);
		speed = DEFAULT_SPEED;
		xMove = 0;
		yMove = 0;
	}

	@Override
	public void update(float dt) {
//		if(ai != null) ai.update(dt);
	}
	
	public void move(float dt) {
		if(!collidesWithAny(xMove, 0f))
			moveX(dt);
		if(!collidesWithAny(0f, yMove))
			moveY(dt);
	}
	
	public void moveX(float dt) {
		if(xMove > 0){ // Moving right
			int tx = (int) (transform.position.x + xMove + boundsX + boundsWidth) / Tile.SIZE;
			
			if(!collisionWithTile(tx, (int) (transform.position.y + boundsY) / Tile.SIZE) &&
					!collisionWithTile(tx, (int) (transform.position.y + boundsY + boundsHeight) / Tile.SIZE)){
				transform.position.x += xMove * dt;
			} else {
				transform.position.x = tx * Tile.SIZE - boundsX - boundsWidth - COLLISION_OFFSET;
			}
			
		}else if(xMove < 0) { // Moving left
			int tx = (int) (transform.position.x + xMove + boundsX) / Tile.SIZE;
			
			if(!collisionWithTile(tx, (int) (transform.position.y + boundsY) / Tile.SIZE) &&
					!collisionWithTile(tx, (int) (transform.position.y + boundsY + boundsHeight) / Tile.SIZE)){
				transform.position.x += xMove * dt;
			} else {
				transform.position.x = tx * Tile.SIZE + boundsX + boundsWidth + COLLISION_OFFSET;
			}
		}
	}
	
	public void moveY(float dt){
		if(yMove < 0) { // Up
			int ty = (int) (transform.position.y + yMove + boundsY) / Tile.SIZE;
			
			if(!collisionWithTile((int) (transform.position.x + boundsX) / Tile.SIZE, ty) &&
					!collisionWithTile((int) (transform.position.x + boundsX + boundsWidth) / Tile.SIZE, ty)){
				transform.position.y += yMove * dt;
			} else {
				transform.position.y = ty * Tile.SIZE + Tile.SIZE - boundsY;
			}
			
		}else if(yMove > 0){//Down
			int ty = (int) (transform.position.y + yMove + boundsY + boundsHeight) / Tile.SIZE;
			
			if(!collisionWithTile((int) (transform.position.x + boundsX) / Tile.SIZE, ty) &&
					!collisionWithTile((int) (transform.position.x + boundsX + boundsWidth) / Tile.SIZE, ty)){
				transform.position.y += yMove * dt;
			} else {
				transform.position.y = ty * Tile.SIZE - boundsY - boundsHeight - COLLISION_OFFSET;
			}
		}
	}
	
	protected boolean collisionWithTile(int x, int y){
		return handler.getWorld().getChunkManager().getTile(x, y).hasAttribute(Tile.ATTRIB_NOT_PASSABLE);
	}
	
	//GETTERS SETTERS

	public float getxMove() {
		return xMove;
	}

	public void setxMove(float xMove) {
		this.xMove = xMove;
	}

	public float getyMove() {
		return yMove;
	}

	public void setyMove(float yMove) {
		this.yMove = yMove;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
}
