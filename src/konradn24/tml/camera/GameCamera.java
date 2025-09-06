package konradn24.tml.camera;

import org.joml.Vector2d;

import konradn24.tml.display.Display;
import konradn24.tml.entities.Entity;
import konradn24.tml.utils.Logging;

public class GameCamera {
	
	private Vector2d position;
	
	public GameCamera(Vector2d position) {
		this.position = position;
		
		Logging.info("Game Camera initialized");
	}
	
	public void centerOnEntity(Entity e){
		position.set(e.getX() - Display.x(.5f), e.getY() - Display.y(.5f));
	}
	
	public void setPosition(double x, double y) {
		position.set(x, y);
	}
	
	public void move(double dx, double dy) {
		position.add(dx, dy);
	}
	
	public Vector2d getPosition() {
		return position;
	}
}
