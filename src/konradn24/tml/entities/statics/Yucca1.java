package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.inventory.tools.Tool;

public class Yucca1 extends StaticEntity {

	public Yucca1(Handler handler, float x, float y) {
		super(handler, x, y, 1, 1);
	}

	@Override
	protected void init() {
		setOrigin(Origin.BOTTOM);
		setHealth(140);
		
		actionsByAttribute.put(Tool.ACTION_CHOP, (item, handler) -> {
			damage(item);
		});
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void render() {

	}

	@Override
	public void onDead() {

	}
}
