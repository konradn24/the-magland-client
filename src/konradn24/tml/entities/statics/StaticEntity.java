package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;

public abstract class StaticEntity extends Entity {

	public StaticEntity(Handler handler, float x, float y) {
		super(handler, x, y);
	}
	
	public StaticEntity(Handler handler, float x, float y, float width, float height) {
		super(handler, x, y, width, height);
	}
}
