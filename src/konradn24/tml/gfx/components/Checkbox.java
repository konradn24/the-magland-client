package konradn24.tml.gfx.components;

import konradn24.tml.Handler;
import konradn24.tml.gfx.images.Assets;

public class Checkbox extends Switch {

	public Checkbox(int x, int y, int width, int height, Handler handler) {
		super(Assets.getAnimation("iconUnchecked"), Assets.getAnimation("iconChecked"), x, y, width, height, handler);
	}
}
