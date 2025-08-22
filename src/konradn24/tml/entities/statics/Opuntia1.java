package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class Opuntia1 extends StaticEntity {

	public Opuntia1(Handler handler, float x, float y) {
		super(handler, x, y, 1, 1);
	}

	@Override
	protected void init() {
		setOrigin(Origin.BOTTOM);
		setHealth(80);
		
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
