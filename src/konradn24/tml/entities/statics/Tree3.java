package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class Tree3 extends StaticEntity {

	public Tree3(Handler handler, float x, float y) {
		super(handler, x, y, 2, 3);
	}

	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 8, transform.size.y - 16, 16, 16);
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
