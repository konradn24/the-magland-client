package konradn24.tml.entities.creatures;

import java.awt.Graphics2D;

import konradn24.tml.Handler;
import konradn24.tml.AI.AI;
import konradn24.tml.gfx.images.Assets;

public class Zombie extends Creature {
	
	private AI zombieAI;
	
	public Zombie(Handler handler, float x, float y) {
		super(handler, x, y, DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT);
	}

	@Override
	protected void onInit() {
		texture = Assets.getAnimation("zombieDown")[0];
		
		bounds.x = 22;
		bounds.y = 44;
		bounds.width = 19;
		bounds.height = 19;
		
		setHealth(50);
		
		speed = 1.5f;
		zombieAI = new AI(handler, this, 400, (byte) 2);
		zombieAI.setAttackPoints(3);
	}
	
	@Override
	public void tick() {
		zombieAI.tick();
		move();
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(texture, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
	}
	
	@Override
	public void onDead() {
		if(handler.getWorld().getRandom().nextDouble() <= 0.1) {
			//TODO: Add rotten flesh
			System.out.println("Rotten flesh should be added");
		}
	}

	public AI getZombieAI() {
		return zombieAI;
	}
}
