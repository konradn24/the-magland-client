package konradn24.tml.gui.graphics.components;

import konradn24.tml.Handler;

public class Checkbox extends Button {

	protected boolean checked;
	
	public Checkbox(Handler handler) {
		super(handler);
		
		super.label.setContent("x");
		super.label.setInvisible(true);
		
		super.onLeftClick = () -> setChecked(!checked);
	}
	
	public Checkbox(int x, int y, int size, Handler handler) {
		super("", x, y, size, size, handler);

		super.label.setContent("x");
		super.label.setInvisible(true);
		
		super.onLeftClick = () -> setChecked(!checked);
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		
		if(checked) {
			super.label.setInvisible(false);
		} else {
			super.label.setInvisible(true);
		}
	}
}
