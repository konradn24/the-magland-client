package konradn24.tml.entities.dynamic;

import konradn24.tml.Handler;
import konradn24.tml.collisions.Collision;
import konradn24.tml.collisions.SweepResult;
import konradn24.tml.entities.Entity;
import konradn24.tml.tiles.Tile;

public abstract class DynamicEntity extends Entity {
	
	public static final float DEFAULT_SPEED = 150.0f;
	public static final int DEFAULT_CREATURE_WIDTH = 1,
							DEFAULT_CREATURE_HEIGHT = 1;
//	private static final int COLLISION_OFFSET = 1;
	
	protected float speed;
	protected float xMove, yMove;
	
//	protected AIController ai;

	public DynamicEntity(Handler handler, float x, float y, int width, int height) {
		super(handler, x, y, width, height);
		speed = DEFAULT_SPEED;
		xMove = 0;
		yMove = 0;
	}

	public void move(float dt) {
		if(xMove == 0f && yMove == 0f) {
			return;
		}
		
		float dx = xMove * dt;
		float dy = yMove * dt;
		
		SweepResult rx = Collision.sweep(this, handler.getWorld().getEntityManager().getEntities(), dx, 0);
		SweepResult ry = Collision.sweep(this, handler.getWorld().getEntityManager().getEntities(), 0, dy);
		
        if (rx.collided && !rx.overlap) {
            transform.position.x += dx * rx.t;
            
            float push = 0.001f;
            transform.position.x += rx.nx * push;
        } else if (rx.overlap) {
        	transform.position.x += dx;
        } else {
        	transform.position.x += dx;
        }
        
        if (ry.collided && !ry.overlap) {
            transform.position.y += dy * ry.t;
            
            float push = 0.001f;
            transform.position.y += ry.ny * push;
        } else if (ry.overlap) {
            transform.position.y += dy;
        } else {
        	transform.position.y += dy;
        }
	}
	
//	public void moveX(float dt) {
//		if(xMove > 0){ // Moving right
//			int tx = (int) (transform.position.x + xMove + bounds.position.x + bounds.size.x) / Tile.SIZE;
//			
//			if(!collisionWithTile(tx, (int) (transform.position.y + bounds.position.y) / Tile.SIZE) &&
//					!collisionWithTile(tx, (int) (transform.position.y + bounds.position.y + bounds.size.y) / Tile.SIZE)){
//				transform.position.x += xMove * dt;
//			} else {
//				transform.position.x = tx * Tile.SIZE - bounds.position.x - bounds.size.x - COLLISION_OFFSET;
//			}
//			
//		}else if(xMove < 0) { // Moving left
//			int tx = (int) (transform.position.x + xMove + bounds.position.x) / Tile.SIZE;
//			
//			if(!collisionWithTile(tx, (int) (transform.position.y + bounds.position.y) / Tile.SIZE) &&
//					!collisionWithTile(tx, (int) (transform.position.y + bounds.position.y + bounds.size.y) / Tile.SIZE)){
//				transform.position.x += xMove * dt;
//			} else {
//				transform.position.x = tx * Tile.SIZE + bounds.position.x + bounds.size.x + COLLISION_OFFSET;
//			}
//		}
//	}
//	
//	public void moveY(float dt){
//		if(yMove < 0) { // Up
//			int ty = (int) (transform.position.y + yMove + bounds.position.y) / Tile.SIZE;
//			
//			if(!collisionWithTile((int) (transform.position.x + bounds.position.x) / Tile.SIZE, ty) &&
//					!collisionWithTile((int) (transform.position.x + bounds.position.x + bounds.size.x) / Tile.SIZE, ty)){
//				transform.position.y += yMove * dt;
//			} else {
//				transform.position.y = ty * Tile.SIZE + Tile.SIZE - bounds.position.y;
//			}
//			
//		}else if(yMove > 0){//Down
//			int ty = (int) (transform.position.y + yMove + bounds.position.y + bounds.size.y) / Tile.SIZE;
//			
//			if(!collisionWithTile((int) (transform.position.x + bounds.position.x) / Tile.SIZE, ty) &&
//					!collisionWithTile((int) (transform.position.x + bounds.position.x + bounds.size.x) / Tile.SIZE, ty)){
//				transform.position.y += yMove * dt;
//			} else {
//				transform.position.y = ty * Tile.SIZE - bounds.position.y - bounds.size.y - COLLISION_OFFSET;
//			}
//		}
//	}
	
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
