package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;

public abstract class StaticEntity extends Entity {

	public StaticEntity(Handler handler, float x, float y, int width, int height) {
		super(handler, x, y, width, height);
	}
}
