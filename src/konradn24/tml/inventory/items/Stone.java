package konradn24.tml.inventory.items;

public class Stone extends Item {

	public Stone() {
		super();
		
		setAttributes(Item.ATTRIB_CRAFTABLE);
		
		weight = 0.8f;
	}

	@Override
	public void init() {
		
	}
	
	@Override
	public void update(float dt) {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void renderGUI(long vg) {
		
	}

	public String getInfo() {
		return "Stones can be found on ground. This resource can be used to craft some simple items.";
	}
}
