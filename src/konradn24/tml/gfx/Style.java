package konradn24.tml.gfx;

import java.util.ArrayList;
import java.util.List;

import konradn24.tml.debug.Logging;
import konradn24.tml.display.Display;
import konradn24.tml.states.State;

public class Style {

	// TODO: Dialogs font: AMERICAN TYPEWRITER
	
	private Display display;
	private int screenWidth, screenHeight;
	
	private List<GridLayout> layouts = new ArrayList<>();
	
	public Style(Display display) {
		this.display = display;
		this.screenWidth = display.getCanvas().getWidth();
		this.screenHeight = display.getCanvas().getHeight();
	}
	
	public class GridLayout {
		public Class<? extends State> state;
		public String id;
		public int x, y, width, height;
		public int rows, columns;
		public int cellSizeX, cellSizeY;
		
		public GridLayout(Class<? extends State> state, String id, int rows, int columns) {
			this.state = state;
			this.id = id;
			this.x = 0;
			this.y = 0;
			this.width = display.getCanvas().getWidth();
			this.height = display.getCanvas().getHeight();
			this.rows = rows;
			this.columns = columns;
			this.cellSizeX = width / columns;
			this.cellSizeY = height / rows;
		}
		
		public GridLayout(Class<? extends State> state, String id, int x, int y, int sizeX, int sizeY, int rows, int columns) {
			this.state = state;
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = sizeX;
			this.height = sizeY;
			this.rows = rows;
			this.columns = columns;
			this.cellSizeX = sizeX / columns;
			this.cellSizeY = sizeY / rows;
		}
		
		public void refresh() {
			this.cellSizeX = width / columns;
			this.cellSizeY = height / rows;
		}
	}
	
	public int centerX(int size) {
		return display.getCanvas().getX() + display.getCanvas().getWidth() / 2 - size / 2;
	}
	
	public int centerY(int size) {
		return display.getCanvas().getY() + display.getCanvas().getHeight() / 2 - size / 2;
	}
	
	public GridLayout getLayout(String id) {
		Object[] result = layouts.stream().filter(layout -> layout.id == id).toArray();
		
		if(result.length <= 0) {
			Logging.warning("Layout of ID " + id + " not found!");
			
			return null;
		}
		
		return (GridLayout) result[0];
	}
	
	public GridLayout getLayout(int id) {
		return layouts.get(id);
	}
	
	public void addLayout(Class<? extends State> state, String id, int rows, int columns) {
		layouts.add(new GridLayout(state, id, rows, columns));
		
		Logging.info("Added layout of ID " + id);
	}
	
	public void addLayout(Class<? extends State> state, String id, int x, int y, int sizeX, int sizeY, int rows, int columns) {
		layouts.add(new GridLayout(state, id, x, y, sizeX, sizeY, rows, columns));
		
		Logging.info("Added layout of ID " + id);
	}
	
	public int positionX(String layoutID, int column) {
		GridLayout layout = getLayout(layoutID);
		
		return (int) (layout.x + column * layout.cellSizeX + display.getCanvas().getX());
	}
	
	public int positionY(String layoutID, int row) {
		GridLayout layout = getLayout(layoutID);
		
		return (int) (layout.y + row * layout.cellSizeY + display.getCanvas().getY());
	}
	
	public int positionCenterX(String layoutID, int column, int size) {
		GridLayout layout = getLayout(layoutID);
		
		return positionX(layoutID, column) + layout.cellSizeX / 2 - size / 2;
	}
	
	public int positionCenterY(String layoutID, int row, int size) {
		GridLayout layout = getLayout(layoutID);
		
		return positionY(layoutID, row) + layout.cellSizeY / 2 - size / 2;
	}
	
	public List<GridLayout> getLayouts() {
		return layouts;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
}
