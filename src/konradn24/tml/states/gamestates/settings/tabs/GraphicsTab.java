package konradn24.tml.states.gamestates.settings.tabs;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.components.Checkbox;
import konradn24.tml.gui.graphics.components.FormField;
import konradn24.tml.gui.graphics.components.Select;
import konradn24.tml.gui.graphics.layouts.GridLayout;
import konradn24.tml.gui.graphics.widgets.tabs.Tab;
import konradn24.tml.states.gamestates.settings.SettingsState;

@SuppressWarnings("rawtypes")
public class GraphicsTab extends Tab {

	private GridLayout<FormField> fields;
	
	public GraphicsTab(float x, float y, float width, float height, Handler handler) {
		super(x, y, width, height, handler);
		
		float columnWidth = width / SettingsState.TAB_COLUMNS;
		
		fields = new GridLayout<FormField>(FormField.class, 2);
		
		for(int i = 0; i < fields.getComponents().length; i++) {
			int column = i % SettingsState.TAB_COLUMNS;
			int row = i / SettingsState.TAB_COLUMNS;
			
			float fieldX = column * columnWidth;
			float fieldY = row * SettingsState.ROW_HEIGHT;
			
			switch(i) {
				case 0: { // Fullscreen
					FormField<Checkbox> field = new FormField<Checkbox>(
							Checkbox.class, "Fullscreen:", 
							fieldX, fieldY, columnWidth, SettingsState.ROW_HEIGHT, 
							SettingsState.CHECKBOX_SIZE, SettingsState.CHECKBOX_SIZE, handler
					);
					
					if(handler.getSettings().isFullscreen()) {
						field.getInput().setChecked(true);
					}
					
					field.setHoverOffsetX(x);
					field.setHoverOffsetY(y);
					
					fields.getComponents()[0] = field;
					
					break;
				}
				
				case 1: { // FPS limit
					FormField<Select> field = new FormField<Select>(
							Select.class, "FPS limit:", 
							fieldX, fieldY, columnWidth, SettingsState.ROW_HEIGHT, 
							SettingsState.SELECT_WIDTH, SettingsState.SELECT_HEIGHT, handler
					);
					
					field.getInput().setOptions("15", "30", "60", "120");
					field.getInput().setSelected(Integer.toString(handler.getSettings().getFpsLimit()));
					
					field.setHoverOffsetX(x);
					field.setHoverOffsetY(y);
					
					fields.getComponents()[1] = field;
					
					break;
				}
			}
		}
	}

	@Override
	public void update(float dt) {
		fields.update(dt);
	}

	@Override
	public void renderGUI(long vg) {
		fields.renderGUI(vg);
	}
	
	@Override
	public String getTitle() {
		return "Graphics";
	}
	
	public boolean getFullscreen() {
		return ((Checkbox) fields.getComponents()[0].getInput()).isChecked();
	}
	
	public int getFpsLimit() {
		return Integer.parseInt(((Select) fields.getComponents()[1].getInput()).getSelected());
	}
}
