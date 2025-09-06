package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class SaguaroCactus1 extends StaticEntity {

	public SaguaroCactus1(Handler handler, double x, double y) {
		super(handler, x, y, 1, 2);
	}

	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 8, transform.size.y - 16, 16, 16);
		setOrigin(Origin.BOTTOM);
		setHealth(100);
		
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
