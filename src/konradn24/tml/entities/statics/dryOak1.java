package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.inventory.tools.Tool;

public class DryOak1 extends StaticEntity {

	public DryOak1(Handler handler, float x, float y) {
		super(handler, x, y);
	}

	@Override
	protected void init() {
		boundsX = transform.size.x / 2 - 8;
		boundsY = transform.size.y - 10;
		boundsWidth = 10;
		boundsHeight = 10;
		
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
