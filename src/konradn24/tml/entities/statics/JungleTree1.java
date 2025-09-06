package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class JungleTree1 extends StaticEntity {

	public JungleTree1(Handler handler, double x, double y) {
		super(handler, x, y);
	}
	
	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 32, transform.size.y - 32, 64, 32);
		setOrigin(Origin.BOTTOM);
		setHealth(200);
		
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
