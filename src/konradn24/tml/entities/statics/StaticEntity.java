package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;

public abstract class StaticEntity extends Entity {

	public StaticEntity(Handler handler, double x, double y) {
		super(handler, x, y);
	}
	
	public StaticEntity(Handler handler, double x, double y, float width, float height) {
		super(handler, x, y, width, height);
	}
}
