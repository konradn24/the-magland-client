package konradn24.tml.tiles;

public class TileData {

	public int x, y;
	public Tile tile;
	
	public TileData(int x, int y, Tile tile) {
		this.x = x;
		this.y = y;
		this.tile = tile;
	}
	
	public String toString() {
		return "Tile data: X=" + x + " Y=" + y + " Name=" + (tile == null ? "NULL" : tile.getClass().getSimpleName());
	}
}
