package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class Fir1 extends StaticEntity {

	public Fir1(Handler handler, double x, double y) {
		super(handler, x, y);
	}

	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 12, transform.size.y - 24, 32, 24);
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
