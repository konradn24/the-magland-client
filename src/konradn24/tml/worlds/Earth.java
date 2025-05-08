package konradn24.tml.worlds;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.statics.JungleTree1;
import konradn24.tml.entities.statics.TaigaTree1;
import konradn24.tml.entities.statics.TaigaTree2;
import konradn24.tml.entities.statics.TaigaTree3;
import konradn24.tml.entities.statics.Tree1;
import konradn24.tml.entities.statics.Tree2;
import konradn24.tml.entities.statics.Tree3;
import konradn24.tml.tiles.DeepWater;
import konradn24.tml.tiles.Dirt;
import konradn24.tml.tiles.Grass;
import konradn24.tml.tiles.Gravel;
import konradn24.tml.tiles.Ice;
import konradn24.tml.tiles.RainforestGrass;
import konradn24.tml.tiles.Sand;
import konradn24.tml.tiles.Snow;
import konradn24.tml.tiles.TaigaGrass;
import konradn24.tml.tiles.Tile;
import konradn24.tml.tiles.Tundra;
import konradn24.tml.tiles.Water;
import konradn24.tml.worlds.generator.World;

public class Earth extends World {

	//Spawn areas
//	private SpawnArea[] zombieSpawns;
	
	public static final String FROZEN_OCEAN = "frozen_ocean";
	public static final String HALF_FROZEN_OCEAN = "half_frozen_ocean";
	public static final String OCEAN = "ocean";
	public static final String FROZEN_SEA = "frozen_sea";
	public static final String HALF_FROZEN_SEA = "half_frozen_sea";
	public static final String SEA = "sea";
	public static final String TUNDRA_BEACH = "tundra_beach";
	public static final String GRAVEL_BEACH = "gravel_beach";
	public static final String BEACH = "beach";
	public static final String SNOW_DESERT = "snow_desert";
	public static final String TUNDRA = "tundra";
	public static final String DRY_TAIGA = "dry_taiga";
	public static final String TAIGA = "taiga";
	public static final String PLAINS = "plains";
	public static final String HILLS = "hills";
	public static final String FOREST = "forest";
	public static final String DESERT = "desert";
	public static final String SUBTROPICAL_RAINFOREST = "subtropical_rainforest";
	public static final String RAINFOREST = "rainforest";
	public static final String DENSE_RAINFOREST = "dense_rainforest";
	
	public Earth(long seed, Handler handler) {
		super(handler);
		super.seed = seed;
		
		worldName = "Earth";
		
		generate(seed, 120, 150);
		
//		if(getTile(spawnTileX, spawnTileY).hasAttribute(Tile.ATTRIB_NO_SPAWN))
//			setTile(spawnTileX, spawnTileY, Tile.getTile(Sand.class));
		
//		entityManager.addEntity(new House(handler, 64 * 16, 64 * 7));
	}
	
	protected void generation(int x, int y, float altitude, float temperature, float humidity) {
		/*
		 * ALTITUDE
		 * -1.0 -0.15	ocean
		 * -0.15 0		sea
		 * 0 0.1		beach
		 * 0.1 0.6		plains
		 * 0.6 1.0		hills
		 * 
		 * TEMPERATURE
		 * -1.0 -0.8 	extreme ice desert
		 * -0.8 -0.65 	ice desert
		 * -0.65 -0.5	tundra
		 * -0.5 -0.15	taiga
		 * -0.15 0.4	moderate
		 * 0.4 0.7		desert/subtropical
		 * 0.7 1.0		rainforest
		 * 
		 * HUMIDITY
		 * -1.0 -0.3	desert
		 * -0.3 0.25	grassy
		 * 0.25 1.0		forests
		 */
		
		// OCEAN
		if(altitude < -0.15) {
			if(temperature < -0.8) {
				setTile(x, y, Tile.getTile(Ice.class).set(FROZEN_OCEAN));
			} else if(temperature < -0.65)
				if(random.nextDouble() < 0.5)
					setTile(x, y, Tile.getTile(Ice.class).set(HALF_FROZEN_OCEAN));
				else
					setTile(x, y, Tile.getTile(DeepWater.class).set(HALF_FROZEN_OCEAN));
			else
				setTile(x, y, Tile.getTile(DeepWater.class).set(OCEAN));
		
		// SEA
		} else if(altitude < 0) {
			if(temperature < -0.65)
				setTile(x, y, Tile.getTile(Ice.class).set(FROZEN_SEA));
			else if(temperature < -0.5)
				if(random.nextDouble() < 0.35)
					setTile(x, y, Tile.getTile(Ice.class).set(HALF_FROZEN_SEA));
				else
					setTile(x, y, Tile.getTile(Water.class).set(HALF_FROZEN_SEA));
			else
				setTile(x, y, Tile.getTile(Water.class).set(SEA));
			
		// BEACH
		} else if(altitude < 0.1) {
			if(temperature < -0.5)
				setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA_BEACH));
			else if(temperature < -0.3)
				setTile(x, y, Tile.getTile(Gravel.class).set(GRAVEL_BEACH));
			else
				setTile(x, y, Tile.getTile(Sand.class).set(BEACH));
			
		// FLAT TERRAIN
		} else if(altitude < 0.6) {
			// Snowy
			if(temperature < -0.65) {
				setTile(x, y, Tile.getTile(Snow.class).set(SNOW_DESERT));
			
			// Tundra
			} else if(temperature < -0.5) {
				if(random.nextDouble() < 0.25)
					setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
				else
					setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
			
			// Low density taiga or taiga
			} else if(temperature < -0.15) {
				if(humidity < -0.3) { // Low humidity = low density taiga
					if(random.nextDouble() < 0.125)
						setTile(x, y, Tile.getTile(Dirt.class).set(DRY_TAIGA));
					else
						setTile(x, y, Tile.getTile(TaigaGrass.class).set(DRY_TAIGA));
					
					if(random.nextDouble() < 0.075)
						entityManager.addRandomEntity(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 });
				} else { // Better humidity = taiga
					if(random.nextDouble() < 0.125)
						setTile(x, y, Tile.getTile(Dirt.class).set(TAIGA));
					else
						setTile(x, y, Tile.getTile(TaigaGrass.class).set(TAIGA));
					
					if(random.nextDouble() < 0.125)
						entityManager.addRandomEntity(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 });
				}
			
			// Plains or forest
			} else if(temperature < 0.4) {
				if(humidity < -0.3) { // Low humidity = plains
					setTile(x, y, Tile.getTile(Grass.class).set(PLAINS));
				} else { // Better humidity = forest
					setTile(x, y, Tile.getTile(Grass.class).set(FOREST));
					
					if(random.nextDouble() < 0.225)
						entityManager.addRandomEntity(random, new Entity[]{ new Tree1(handler, x, y), new Tree2(handler, x, y), new Tree3(handler, x, y) }, new double[]{ 0.5, 0.75, 1 });
				}
			
			// Desert, low density rainforest or rainforest
			} else if(temperature < 0.7) {
				if(humidity < -0.45) { // Very low humidity = desert 
					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
				} else if(humidity < -0.25) { // Low humidity = low density rainforest
					if(random.nextDouble() < 0.25)
						setTile(x, y, Tile.getTile(Grass.class).set(SUBTROPICAL_RAINFOREST));
					else
						setTile(x, y, Tile.getTile(RainforestGrass.class).set(SUBTROPICAL_RAINFOREST));
					
					if(random.nextDouble() < 0.1)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				} else { // Better and high humidity = rainforest
					if(random.nextDouble() < 0.25)
						setTile(x, y, Tile.getTile(Grass.class).set(RAINFOREST));
					else
						setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
					
					if(random.nextDouble() < 0.18)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				}
			
			// Rainforest or dense rainforest
			} else {
				if(humidity < -0.3) { // Low humidity = rainforest
					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
					
					if(random.nextDouble() < 0.275)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				} else {
					setTile(x, y, Tile.getTile(RainforestGrass.class).set(DENSE_RAINFOREST));
					
					if(random.nextDouble() < 0.375) // Better or high humidity = dense rainforest
						entityManager.addEntity(new JungleTree1(handler, x, y));
				}
			}
			
		// MIDDLE
		} else {
			// Snowy
			if(temperature < -0.65) {
				setTile(x, y, Tile.getTile(Snow.class).set(SNOW_DESERT));
			
			// Tundra
			} else if(temperature < -0.5) {
				if(random.nextDouble() < 0.5)
					setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
				else
					setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
			
			// Tundra or taiga
			} else if(temperature < -0.15) {
				if(humidity < 0.25) { // Low humidity = tundra
					if(random.nextDouble() < 0.5)
						setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
					else
						setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
				} else { // Better humidity = taiga
					if(random.nextDouble() < 0.125)
						setTile(x, y, Tile.getTile(Dirt.class).set(TAIGA));
					else
						setTile(x, y, Tile.getTile(TaigaGrass.class).set(TAIGA));
					
					if(random.nextDouble() < 0.2)
						entityManager.addRandomEntity(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 });
				}
			
			// Plains or forest
			} else if(temperature < 0.4) {
				if(humidity < 0.25) { // Low humidity = plains
					if(random.nextDouble() < 0.025)
						setTile(x, y, Tile.getTile(Gravel.class).set(HILLS));
					else
						setTile(x, y, Tile.getTile(Grass.class).set(HILLS));
					
					if(random.nextDouble() < 0.005)
						entityManager.addEntity(new Tree1(handler, x, y));
				} else { // Better humidity = forest
					setTile(x, y, Tile.getTile(Grass.class).set(FOREST));
					
					if(random.nextDouble() < 0.225)
						entityManager.addRandomEntity(random, new Entity[]{ new Tree1(handler, x, y), new Tree2(handler, x, y), new Tree3(handler, x, y) }, new double[]{ 0.5, 0.75, 1 });
				}
			
			// Desert, low density rainforest or rainforest
			} else if(temperature < 0.7) {
				if(humidity < -0.3) { // Low humidity = desert
					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
				} else if(humidity < 0.25) { // Medium humidity = low density rainforest
					if(random.nextDouble() < 0.3)
						setTile(x, y, Tile.getTile(RainforestGrass.class).set(SUBTROPICAL_RAINFOREST));
					else
						setTile(x, y, Tile.getTile(Grass.class).set(SUBTROPICAL_RAINFOREST));
					
					if(random.nextDouble() < 0.05)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				} else { // High humidity = rainforest
					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
					
					if(random.nextDouble() < 0.225)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				}
			
			// Desert, rainforest or dense rainforest
			} else {
				if(humidity < -0.3) { // Low humidity = desert
					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
				} else if(humidity < 0.25) { // Medium humidity = rainforest
					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
					
					if(random.nextDouble() < 0.275)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				} else { // High humidity = dense rainforest
					setTile(x, y, Tile.getTile(RainforestGrass.class).set(DENSE_RAINFOREST));
					
					if(random.nextDouble() < 0.375)
						entityManager.addEntity(new JungleTree1(handler, x, y));
				}
			}
		}
	}
	
	protected void tick() {
//		for(SpawnArea spawn : zombieSpawns)
//			spawn.tick(new Zombie(handler, 0, 0));
	}
}
