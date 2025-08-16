package konradn24.tml.worlds.generator;

import java.util.List;

import konradn24.tml.entities.Entity;
import konradn24.tml.tiles.Tile;

public class GenerationData {

	public Tile tile;
	public List<Entity> entities;
	
	public GenerationData(Tile tile, List<Entity> entities) {
		this.tile = tile;
		this.entities = entities;
	}
}
