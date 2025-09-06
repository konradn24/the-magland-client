package konradn24.tml.entities.statics;

import konradn24.tml.Handler;
import konradn24.tml.graphics.Assets;
import konradn24.tml.inventory.Inventory;

public class Pouch extends StaticEntity {

	public Pouch(Handler handler, double x, double y) {
		super(handler, x, y, 0.5f, 0.5f);
	}

	@Override
	protected void init() {
		killable = false;
		
		setOrigin(Origin.CENTER);
		
		customActionIcon = Assets.getTexture("handIcon");
		
		actionsByAttribute.put(-1, (item, handler) -> {
			handler.getPlayGUI().getContextPanel().setContext(inventory);
		});
		
		inventory = new Inventory(this, "Pouch", true, Float.MAX_VALUE, handler);
	}

	@Override
	public void update(float dt) {		
		inventory.update(dt);
		
		if(inventory.getItems().isEmpty()) {
			vanish();
		}
	}

	@Override
	public void render() {
		
	}
	
	@Override
	public void onDead() {

	}
}	