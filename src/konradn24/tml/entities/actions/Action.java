package konradn24.tml.entities.actions;

import konradn24.tml.Handler;
import konradn24.tml.inventory.items.Item;

public interface Action {
	public void perform(Item item, Handler handler);
}
