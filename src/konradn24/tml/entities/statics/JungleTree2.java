package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.items.tools.Tool;

public class JungleTree2 extends StaticEntity {

	public JungleTree2(Handler handler, float x, float y) {
		super(handler, x, y);
	}
	
	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 48, transform.size.y - 32, 104, 32);
		setOrigin(Origin.BOTTOM);
		setHealth(240);
		
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
