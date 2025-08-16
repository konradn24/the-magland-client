package konradn24.tml.camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import konradn24.tml.display.Display;
import konradn24.tml.entities.Entity;
import konradn24.tml.utils.Logging;

public class GameCamera {
	
	private Vector2f position;
	private Matrix4f viewMatrix;
	
	public GameCamera(Vector2f position) {
		this.position = position;
		this.viewMatrix = new Matrix4f();
		
		Logging.info("Game Camera initialized");
	}
	
	public void centerOnEntity(Entity e){
		position.set(e.getX() - Display.x(.5f), e.getY() - Display.y(.5f));
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	public void move(float dx, float dy) {
		position.add(dx, dy);
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Matrix4f getViewMatrix() {
		viewMatrix.identity();
		viewMatrix.translate(-position.x, -position.y, 0);
		return viewMatrix;
	}
}
