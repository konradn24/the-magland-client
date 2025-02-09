package konradn24.tml.gfx.components;

import java.util.ArrayList;
import java.util.List;

public class Select { // TODO

	private int selected;
	private String layoutID;
	
	private List<AdvancedLabel> options;
	
	public Select(String layoutID) {
		this.layoutID = layoutID;
		this.options = new ArrayList<>();
	}

	public int getSelected() {
		return selected;
	}

	public void select(int option) {
		this.selected = option;
	}

	public String getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(String layoutID) {
		this.layoutID = layoutID;
	}

	public List<AdvancedLabel> getOptions() {
		return options;
	}
}
