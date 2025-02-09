package konradn24.tml.entities;

import java.util.Random;

import konradn24.tml.Handler;
import konradn24.tml.worlds.generator.World;

public class SpawnArea {

	private World world;
	private int interval, limit = -1;
	private int x, y, width, height;
	private Handler handler;
	
	//Timer
	private long timer, lastTime = System.currentTimeMillis();
	
	public SpawnArea(World world, int interval, int x, int y, int width, int height, Handler handler) {
		this.world = world;
		this.interval = interval;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.handler = handler;
	}
	
	public void tick(Entity entity) {
		if(limit == 0)
			return;
		
		timer = System.currentTimeMillis();
		
		if(timer - lastTime >= interval) {
			float x = this.x - handler.getGameCamera().getxOffset();
			float y = this.y - handler.getGameCamera().getyOffset();
			
			Random r = new Random();
			entity.x = r.nextInt(width) + x;
			entity.y = r.nextInt(height) + y;
		
			world.getEntityManager().addEntity(entity);
			
			lastTime = System.currentTimeMillis();
		}
	}
}
