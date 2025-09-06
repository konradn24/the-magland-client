package konradn24.tml.entities.statics;

import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.inventory.InventoryProperty;
import konradn24.tml.items.Item;
import konradn24.tml.items.Stick;
import konradn24.tml.items.tools.Tool;

public class Tree1 extends StaticEntity {

	public Tree1(Handler handler, double x, double y) {
		super(handler, x, y, 2, 3);
	}

	@Override
	protected void init() {
		setBounds(transform.size.x / 2 - 8, transform.size.y - 16, 16, 16);
		setOrigin(Origin.BOTTOM);
		setHealth(100);
		
		actionsByAttribute.put(Tool.ACTION_CHOP, (item, handler) -> {
			damage(item);
		});
		
		loot = List.of(new InventoryProperty(Item.getItem(Stick.class), 4));
	}
	
	@Override
	public void update(float dt) {
//		System.out.println((x - handler.getGameCamera().getxOffset()) + "  " + (y - handler.getGameCamera().getyOffset()));
	}

	@Override
	public void render() {
		
	}

	@Override
	public void onDead() {
		
	}
}
