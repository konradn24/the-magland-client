package konradn24.tml.worlds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import konradn24.tml.Handler;
import konradn24.tml.entities.Entity;
import konradn24.tml.entities.statics.Aloe1;
import konradn24.tml.entities.statics.DryOak1;
import konradn24.tml.entities.statics.Fir1;
import konradn24.tml.entities.statics.JungleTree1;
import konradn24.tml.entities.statics.JungleTree2;
import konradn24.tml.entities.statics.Oak1;
import konradn24.tml.entities.statics.Opuntia1;
import konradn24.tml.entities.statics.Pine1;
import konradn24.tml.entities.statics.Pine2;
import konradn24.tml.entities.statics.SaguaroCactus1;
import konradn24.tml.entities.statics.SaguaroCactus2;
import konradn24.tml.entities.statics.Spruce1;
import konradn24.tml.entities.statics.Yucca1;
import konradn24.tml.graphics.shaders.ShaderUtils;
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
import konradn24.tml.utils.Logging;
import konradn24.tml.utils.Utils;
import konradn24.tml.worlds.generator.GenerationData;
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
	
	public static final Map<String, float[]> BIOME_COLORS = Map.ofEntries(
		    Map.entry(FROZEN_OCEAN, new float[] {180, 200, 255}),
			Map.entry(HALF_FROZEN_OCEAN, new float[] {150, 180, 240}),
			Map.entry(OCEAN, new float[] {0, 80, 160}),
			Map.entry(FROZEN_SEA, new float[] {170, 190, 255}),
			Map.entry(HALF_FROZEN_SEA, new float[] {130, 170, 240}),
			Map.entry(SEA, new float[] {0, 120, 200}),
			Map.entry(TUNDRA_BEACH, new float[] {220, 230, 240}),
			Map.entry(GRAVEL_BEACH, new float[] {180, 180, 180}),
			Map.entry(BEACH, new float[] {240, 230, 180}),
			Map.entry(SNOW_DESERT, new float[] {240, 240, 255}),
			Map.entry(TUNDRA, new float[] {210, 220, 220}),
			Map.entry(DRY_TAIGA, new float[] {160, 190, 160}),
			Map.entry(TAIGA, new float[] {100, 150, 100}),
			Map.entry(PLAINS, new float[] {140, 200, 90}),
			Map.entry(HILLS, new float[] {100, 170, 80}),
			Map.entry(FOREST, new float[] {40, 120, 40}),
			Map.entry(DESERT, new float[] {240, 220, 130}),
			Map.entry(SUBTROPICAL_RAINFOREST, new float[] {60, 140, 60}),
			Map.entry(RAINFOREST, new float[] {30, 100, 30}),
			Map.entry(DENSE_RAINFOREST, new float[] {10, 80, 20})
		);
	
	public static final Set<String> WATER_BIOMES = Set.of(
	    FROZEN_OCEAN, HALF_FROZEN_OCEAN, OCEAN,
	    FROZEN_SEA, HALF_FROZEN_SEA, SEA
	);

	public static final Set<String> MIDDLE_BIOMES = Set.of(
		TUNDRA_BEACH, GRAVEL_BEACH, BEACH
	);
	
	public static final Set<String> LAND_BIOMES = Set.of(
	    SNOW_DESERT, TUNDRA, DRY_TAIGA, TAIGA,
	    PLAINS, HILLS, FOREST, DESERT,
	    SUBTROPICAL_RAINFOREST, RAINFOREST, DENSE_RAINFOREST
	);
	
	public Earth(long seed, float smoothness, float biomeSize, Handler handler) throws IOException {
		super(handler, ShaderUtils.createProgram("res/shaders/chunk_vertex_shader.glsl", "res/shaders/chunk_fragment_shader.glsl"));
		super.seed = seed;
		super.smoothness = smoothness;
		super.biomeSize = biomeSize;
		
		worldName = "Earth";
		
//		if(getTile(spawnTileX, spawnTileY).hasAttribute(Tile.ATTRIB_NO_SPAWN))
//			setTile(spawnTileX, spawnTileY, Tile.getTile(Sand.class));
		
//		entityManager.addEntity(new House(handler, 64 * 16, 64 * 7));
	}
	
//	protected void generation(int x, int y, float altitude, float temperature, float humidity) {
//		/*
//		 * ALTITUDE
//		 * -1.0 -0.15	ocean
//		 * -0.15 0		sea
//		 * 0 0.1		beach
//		 * 0.1 0.6		plains
//		 * 0.6 1.0		hills
//		 * 
//		 * TEMPERATURE
//		 * -1.0 -0.8 	extreme ice desert
//		 * -0.8 -0.65 	ice desert
//		 * -0.65 -0.5	tundra
//		 * -0.5 -0.15	taiga
//		 * -0.15 0.4	moderate
//		 * 0.4 0.7		desert/subtropical
//		 * 0.7 1.0		rainforest
//		 * 
//		 * HUMIDITY
//		 * -1.0 -0.3	desert
//		 * -0.3 0.25	grassy
//		 * 0.25 1.0		forests
//		 */
//		
//		// OCEAN
//		if(altitude < -0.15) {
//			if(temperature < -0.8) {
//				setTile(x, y, Tile.getTile(Ice.class).set(FROZEN_OCEAN));
//			} else if(temperature < -0.65)
//				if(random.nextDouble() < 0.5)
//					setTile(x, y, Tile.getTile(Ice.class).set(HALF_FROZEN_OCEAN));
//				else
//					setTile(x, y, Tile.getTile(DeepWater.class).set(HALF_FROZEN_OCEAN));
//			else
//				setTile(x, y, Tile.getTile(DeepWater.class).set(OCEAN));
//		
//		// SEA
//		} else if(altitude < 0) {
//			if(temperature < -0.65)
//				setTile(x, y, Tile.getTile(Ice.class).set(FROZEN_SEA));
//			else if(temperature < -0.5)
//				if(random.nextDouble() < 0.35)
//					setTile(x, y, Tile.getTile(Ice.class).set(HALF_FROZEN_SEA));
//				else
//					setTile(x, y, Tile.getTile(Water.class).set(HALF_FROZEN_SEA));
//			else
//				setTile(x, y, Tile.getTile(Water.class).set(SEA));
//			
//		// BEACH
//		} else if(altitude < 0.1) {
//			if(temperature < -0.5)
//				setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA_BEACH));
//			else if(temperature < -0.3)
//				setTile(x, y, Tile.getTile(Gravel.class).set(GRAVEL_BEACH));
//			else
//				setTile(x, y, Tile.getTile(Sand.class).set(BEACH));
//			
//		// FLAT TERRAIN
//		} else if(altitude < 0.6) {
//			// Snowy
//			if(temperature < -0.65) {
//				setTile(x, y, Tile.getTile(Snow.class).set(SNOW_DESERT));
//			
//			// Tundra
//			} else if(temperature < -0.5) {
//				if(random.nextDouble() < 0.25)
//					setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
//				else
//					setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
//			
//			// Low density taiga or taiga
//			} else if(temperature < -0.15) {
//				if(humidity < -0.3) { // Low humidity = low density taiga
//					if(random.nextDouble() < 0.125)
//						setTile(x, y, Tile.getTile(Dirt.class).set(DRY_TAIGA));
//					else
//						setTile(x, y, Tile.getTile(TaigaGrass.class).set(DRY_TAIGA));
//					
//					if(random.nextDouble() < 0.075)
//						entities.add(Entity.random(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 }));
//				} else { // Better humidity = taiga
//					if(random.nextDouble() < 0.125)
//						setTile(x, y, Tile.getTile(Dirt.class).set(TAIGA));
//					else
//						setTile(x, y, Tile.getTile(TaigaGrass.class).set(TAIGA));
//					
//					if(random.nextDouble() < 0.125)
//						entities.add(Entity.random(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 }));
//				}
//			
//			// Plains or forest
//			} else if(temperature < 0.4) {
//				if(humidity < -0.3) { // Low humidity = plains
//					setTile(x, y, Tile.getTile(Grass.class).set(PLAINS));
//				} else { // Better humidity = forest
//					setTile(x, y, Tile.getTile(Grass.class).set(FOREST));
//					
//					if(random.nextDouble() < 0.225)
//						entities.add(Entity.random(random, new Entity[]{ new Tree1(handler, x, y), new Tree2(handler, x, y), new Tree3(handler, x, y) }, new double[]{ 0.5, 0.75, 1 }));
//				}
//			
//			// Desert, low density rainforest or rainforest
//			} else if(temperature < 0.7) {
//				if(humidity < -0.45) { // Very low humidity = desert 
//					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
//				} else if(humidity < -0.25) { // Low humidity = low density rainforest
//					if(random.nextDouble() < 0.25)
//						setTile(x, y, Tile.getTile(Grass.class).set(SUBTROPICAL_RAINFOREST));
//					else
//						setTile(x, y, Tile.getTile(RainforestGrass.class).set(SUBTROPICAL_RAINFOREST));
//					
//					if(random.nextDouble() < 0.1)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				} else { // Better and high humidity = rainforest
//					if(random.nextDouble() < 0.25)
//						setTile(x, y, Tile.getTile(Grass.class).set(RAINFOREST));
//					else
//						setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
//					
//					if(random.nextDouble() < 0.18)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				}
//			
//			// Rainforest or dense rainforest
//			} else {
//				if(humidity < -0.3) { // Low humidity = rainforest
//					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
//					
//					if(random.nextDouble() < 0.275)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				} else {
//					setTile(x, y, Tile.getTile(RainforestGrass.class).set(DENSE_RAINFOREST));
//					
//					if(random.nextDouble() < 0.375) // Better or high humidity = dense rainforest
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				}
//			}
//			
//		// MIDDLE
//		} else {
//			// Snowy
//			if(temperature < -0.65) {
//				setTile(x, y, Tile.getTile(Snow.class).set(SNOW_DESERT));
//			
//			// Tundra
//			} else if(temperature < -0.5) {
//				if(random.nextDouble() < 0.5)
//					setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
//				else
//					setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
//			
//			// Tundra or taiga
//			} else if(temperature < -0.15) {
//				if(humidity < 0.25) { // Low humidity = tundra
//					if(random.nextDouble() < 0.5)
//						setTile(x, y, Tile.getTile(Snow.class).set(TUNDRA));
//					else
//						setTile(x, y, Tile.getTile(Tundra.class).set(TUNDRA));
//				} else { // Better humidity = taiga
//					if(random.nextDouble() < 0.125)
//						setTile(x, y, Tile.getTile(Dirt.class).set(TAIGA));
//					else
//						setTile(x, y, Tile.getTile(TaigaGrass.class).set(TAIGA));
//					
//					if(random.nextDouble() < 0.2)
//						entities.add(Entity.random(random, new Entity[] { new TaigaTree1(handler, x, y), new TaigaTree2(handler, x, y), new TaigaTree3(handler, x, y) }, new double[] { 0.9, 0.95, 1 }));
//				}
//			
//			// Plains or forest
//			} else if(temperature < 0.4) {
//				if(humidity < 0.25) { // Low humidity = plains
//					if(random.nextDouble() < 0.025)
//						setTile(x, y, Tile.getTile(Gravel.class).set(HILLS));
//					else
//						setTile(x, y, Tile.getTile(Grass.class).set(HILLS));
//					
//					if(random.nextDouble() < 0.005)
//						entityManager.addEntity(new Tree1(handler, x, y));
//				} else { // Better humidity = forest
//					setTile(x, y, Tile.getTile(Grass.class).set(FOREST));
//					
//					if(random.nextDouble() < 0.225)
//						entities.add(Entity.random(random, new Entity[]{ new Tree1(handler, x, y), new Tree2(handler, x, y), new Tree3(handler, x, y) }, new double[]{ 0.5, 0.75, 1 }));
//				}
//			
//			// Desert, low density rainforest or rainforest
//			} else if(temperature < 0.7) {
//				if(humidity < -0.3) { // Low humidity = desert
//					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
//				} else if(humidity < 0.25) { // Medium humidity = low density rainforest
//					if(random.nextDouble() < 0.3)
//						setTile(x, y, Tile.getTile(RainforestGrass.class).set(SUBTROPICAL_RAINFOREST));
//					else
//						setTile(x, y, Tile.getTile(Grass.class).set(SUBTROPICAL_RAINFOREST));
//					
//					if(random.nextDouble() < 0.05)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				} else { // High humidity = rainforest
//					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
//					
//					if(random.nextDouble() < 0.225)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				}
//			
//			// Desert, rainforest or dense rainforest
//			} else {
//				if(humidity < -0.3) { // Low humidity = desert
//					setTile(x, y, Tile.getTile(Sand.class).set(DESERT));
//				} else if(humidity < 0.25) { // Medium humidity = rainforest
//					setTile(x, y, Tile.getTile(RainforestGrass.class).set(RAINFOREST));
//					
//					if(random.nextDouble() < 0.275)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				} else { // High humidity = dense rainforest
//					setTile(x, y, Tile.getTile(RainforestGrass.class).set(DENSE_RAINFOREST));
//					
//					if(random.nextDouble() < 0.375)
//						entityManager.addEntity(new JungleTree1(handler, x, y));
//				}
//			}
//		}
//	}
	
	protected GenerationData generation(int x, int y, float altitude, float temperature, float humidity) {
		/*
		 * ALTITUDE
		 * 0.00 – 0.425    ocean
		 * 0.425 – 0.50    sea
		 * 0.50 – 0.55     beach
		 * 0.55 – 0.80     plains
		 * 0.80 – 1.00     hills
		 * 
		 * TEMPERATURE
		 * 0.00 – 0.10     extreme ice desert
		 * 0.10 – 0.175    ice desert
		 * 0.175 – 0.25    tundra
		 * 0.25 – 0.425    taiga
		 * 0.425 – 0.70    moderate
		 * 0.70 – 0.85     desert / subtropical
		 * 0.85 – 1.00     rainforest
		 * 
		 * HUMIDITY
		 * 0.00 – 0.35     desert
		 * 0.35 – 0.625    grassy
		 * 0.625 – 1.00    forests
		 */
		
		long tileSeed = (x * 73856093L) ^ (y * 19349663L);
		
		altitude = Utils.clamp01((altitude + 1f) / 2f);
		temperature = Utils.clamp01((temperature + 1f) / 2f);
        humidity = Utils.clamp01((humidity + 1f) / 2f);
        
        // Water Biomes (low altitude)
        float frozenOceanScore      = Utils.gaussian(altitude, 0.05f, 0.03f) * Utils.gaussian(temperature, 0.02f, 0.05f);
        float halfFrozenOceanScore  = Utils.gaussian(altitude, 0.05f, 0.03f) * Utils.gaussian(temperature, 0.12f, 0.06f);
        float oceanScore            = Utils.gaussian(altitude, 0.05f, 0.04f) * Utils.gaussian(temperature, 0.50f, 0.25f);

        // Sea Biomes (slightly higher altitude)
        float frozenSeaScore        = Utils.gaussian(altitude, 0.25f, 0.04f) * Utils.gaussian(temperature, 0.02f, 0.05f);
        float halfFrozenSeaScore    = Utils.gaussian(altitude, 0.25f, 0.04f) * Utils.gaussian(temperature, 0.12f, 0.06f);
        float seaScore              = Utils.gaussian(altitude, 0.25f, 0.04f) * Utils.gaussian(temperature, 0.50f, 0.25f);

        // Beach Biomes (coastal transition)
        float tundraBeachScore      = Utils.gaussian(altitude, 0.40f, 0.02f) * Utils.gaussian(temperature, 0.18f, 0.04f);
        float gravelBeachScore      = Utils.gaussian(altitude, 0.40f, 0.02f) * Utils.gaussian(humidity, 0.45f, 0.12f);
        float beachScore            = Utils.gaussian(altitude, 0.42f, 0.02f) * Utils.gaussian(temperature, 0.50f, 0.12f);

        // Cold Land Biomes (high altitude)
        float snowDesertScore       = Utils.gaussian(altitude, 0.68f, 0.10f) * Utils.gaussian(temperature, 0.12f, 0.05f) * Utils.gaussian(humidity, 0.15f, 0.08f);
        float tundraScore           = Utils.gaussian(altitude, 0.62f, 0.08f) * Utils.gaussian(temperature, 0.20f, 0.04f) * Utils.gaussian(humidity, 0.45f, 0.12f);
        float dryTaigaScore         = Utils.gaussian(altitude, 0.65f, 0.09f) * Utils.gaussian(temperature, 0.32f, 0.06f) * Utils.gaussian(humidity, 0.30f, 0.07f);
        float taigaScore            = Utils.gaussian(altitude, 0.65f, 0.09f) * Utils.gaussian(temperature, 0.32f, 0.06f) * Utils.gaussian(humidity, 0.50f, 0.10f);

        // Temperate Land Biomes
        float plainsScore           = Utils.gaussian(altitude, 0.72f, 0.09f) * Utils.gaussian(temperature, 0.52f, 0.15f) * Utils.gaussian(humidity, 0.45f, 0.12f);
        float hillsScore            = Utils.gaussian(altitude, 0.88f, 0.07f);
        float forestScore           = Utils.gaussian(altitude, 0.72f, 0.09f) * Utils.gaussian(temperature, 0.52f, 0.15f) * Utils.gaussian(humidity, 0.75f, 0.10f);

        // Hot Land Biomes
        float desertScore           = Utils.gaussian(altitude, 0.68f, 0.08f) * Utils.gaussian(temperature, 0.78f, 0.05f) * Utils.gaussian(humidity, 0.15f, 0.07f);
        float subtropicalRainforestScore = Utils.gaussian(altitude, 0.62f, 0.08f) * Utils.gaussian(temperature, 0.75f, 0.05f) * Utils.gaussian(humidity, 0.78f, 0.10f);
        float rainforestScore       = Utils.gaussian(altitude, 0.60f, 0.07f) * Utils.gaussian(temperature, 0.90f, 0.04f) * Utils.gaussian(humidity, 0.82f, 0.08f);
        float denseRainforestScore  = Utils.gaussian(altitude, 0.58f, 0.06f) * Utils.gaussian(temperature, 0.94f, 0.03f) * Utils.gaussian(humidity, 0.88f, 0.05f);

        Map<String, Float> biomeScores = Map.ofEntries(
			Map.entry(FROZEN_OCEAN, frozenOceanScore),
			Map.entry(HALF_FROZEN_OCEAN, halfFrozenOceanScore),
			Map.entry(OCEAN, oceanScore),
			Map.entry(FROZEN_SEA, frozenSeaScore),
			Map.entry(HALF_FROZEN_SEA, halfFrozenSeaScore),
			Map.entry(SEA, seaScore),
			Map.entry(TUNDRA_BEACH, tundraBeachScore),
			Map.entry(GRAVEL_BEACH, gravelBeachScore),
			Map.entry(BEACH, beachScore),
			Map.entry(SNOW_DESERT, snowDesertScore),
			Map.entry(TUNDRA, tundraScore),
			Map.entry(DRY_TAIGA, dryTaigaScore),
			Map.entry(TAIGA, taigaScore),
			Map.entry(PLAINS, plainsScore),
			Map.entry(HILLS, hillsScore),
			Map.entry(FOREST, forestScore),
			Map.entry(DESERT, desertScore),
			Map.entry(SUBTROPICAL_RAINFOREST, subtropicalRainforestScore),
			Map.entry(RAINFOREST, rainforestScore),
			Map.entry(DENSE_RAINFOREST, denseRainforestScore)
    	);
        
        float total = 0f;
        float r = 0f, g = 0f, b = 0f;
        String biome = "#null";
        float bestScore = 0f;
        
        for (var entry : biomeScores.entrySet()) {
        	float score = entry.getValue();
        	if (score <= 0f) continue;
        	
        	if(entry.getValue() > bestScore) {
        		bestScore = entry.getValue();
        		biome = entry.getKey();
        	}
        	
            float[] c = BIOME_COLORS.get(entry.getKey());
            r += c[0] * score;
            g += c[1] * score;
            b += c[2] * score;
            total += score;
        }

        if (total == 0f) {
        	Logging.error("No biome match at (" + x + "," + y + ") a=" + altitude + " t=" + temperature + " h=" + humidity);
        	return new GenerationData(Tile.getTile(Dirt.class).set(biome, new float[] { 0, 0, 0 }, tileSeed), null);
        }

        float red = Math.min(255f, Math.round(r / total)) / 255f;
        float green = Math.min(255f, Math.round(g / total)) / 255f;
        float blue = Math.min(255f, Math.round(b / total)) / 255f;
        
        Random random = new Random(tileSeed);
        Class<? extends Tile> tileClass = Grass.class;
        List<Entity> entities = new ArrayList<>();
        
        if(biome == FROZEN_OCEAN || biome == FROZEN_SEA) {
        	tileClass = Ice.class;
        } else if(biome == HALF_FROZEN_OCEAN) {
        	tileClass = random.nextFloat() < 0.4f ? Ice.class : DeepWater.class;
        } else if(biome == OCEAN) {
        	tileClass = DeepWater.class;
        } else if(biome == HALF_FROZEN_SEA) {
        	tileClass = random.nextFloat() < 0.4f ? Ice.class : Water.class;
        } else if(biome == SEA) {
        	tileClass = Water.class;
        } else if(biome == TUNDRA_BEACH) {
        	tileClass = random.nextFloat() < 0.4f ? Gravel.class : Tundra.class;
        } else if(biome == GRAVEL_BEACH) {
        	tileClass = Gravel.class;
        } else if(biome == BEACH) {
        	tileClass = Sand.class;
        } else if(biome == DESERT) {
        	tileClass = Sand.class;
        	
        	if(random.nextFloat() < 0.05f)
        		entities.add(Entity.random(random, new Entity[] { new SaguaroCactus1(handler, x, y), new SaguaroCactus2(handler, x, y), new Opuntia1(handler, x, y) }, new float[] { 0.45f, 0.9f, 1 }));
        } else if(biome == SNOW_DESERT) {
        	tileClass = Snow.class;
        } else if(biome == TUNDRA) {
        	tileClass = random.nextFloat() < 0.25f ? Snow.class : Tundra.class;
        } else if(biome == DRY_TAIGA) {
        	tileClass = TaigaGrass.class;
        	
        	if(random.nextFloat() < 0.075f)
				entities.add(Entity.random(random, new Entity[] { new Fir1(handler, x, y), new Pine2(handler, x, y), new DryOak1(handler, x, y) }, new float[] { 0.7f, 0.9f, 1 }));
        } else if(biome == TAIGA) {
        	tileClass = TaigaGrass.class;
        	
        	if(random.nextFloat() < 0.125f)
				entities.add(Entity.random(random, new Entity[] { new Fir1(handler, x, y), new DryOak1(handler, x, y), new Spruce1(handler, x, y) }, new float[] { 0.25f, 0.5f, 1 }));
        } else if(biome == PLAINS) {
        	tileClass = Grass.class;
        	
        	if(random.nextFloat() < 0.02f)
        		entities.add(Entity.random(random, new Entity[] { new Oak1(handler, x, y), new DryOak1(handler, x, y) }, new float[] { 0.75f, 1 }));
		} else if(biome == HILLS) {
        	tileClass = random.nextFloat() < 0.25f ? Dirt.class : Grass.class;
        	
        	if(random.nextFloat() < 0.03f)
        		entities.add(Entity.random(random, new Entity[] { new Spruce1(handler, x, y), new Oak1(handler, x, y) }, new float[] { 0.75f, 1 }));
        } else if(biome == FOREST) {
        	tileClass = Grass.class;
        	
        	if(random.nextFloat() < 0.225f)
        		entities.add(Entity.random(random, new Entity[] { new Oak1(handler, x, y), new Pine1(handler, x, y), new DryOak1(handler, x, y) }, new float[]{ 0.65f, 0.85f, 1 }));
    	} else if(biome == SUBTROPICAL_RAINFOREST) {
        	tileClass = random.nextFloat() < 0.4f ? Grass.class : RainforestGrass.class;
        	
        	if(random.nextFloat() < 0.2f)
				entities.add(Entity.random(random, new Entity[] { new Aloe1(handler, x, y), new Yucca1(handler, x, y), new JungleTree1(handler, x, y) }, new float[] { 0.4f, 0.8f, 1 }));
        } else if(biome == RAINFOREST) {
        	tileClass = RainforestGrass.class;
        	
        	if(random.nextFloat() < 0.275f)
				entities.add(Entity.random(random, new Entity[] { new JungleTree1(handler, x, y), new JungleTree2(handler, x, y), new Aloe1(handler, x, y), new Yucca1(handler, x, y) }, new float[] { 0.3f, 0.6f, 0.8f, 1 }));
        } else if(biome == DENSE_RAINFOREST) {
        	tileClass = RainforestGrass.class;
        	
        	if(random.nextFloat() < 0.375f)
        		entities.add(Entity.random(random, new Entity[] { new JungleTree1(handler, x, y), new JungleTree2(handler, x, y), new Yucca1(handler, x, y) }, new float[] { 0.45f, 0.9f, 1 }));
        }
        
        return new GenerationData(Tile.getTile(tileClass).set(biome, new float[] { red, green, blue }, tileSeed), entities);
	}

	protected void tick() {
//		for(SpawnArea spawn : zombieSpawns)
//			spawn.tick(new Zombie(handler, 0, 0));
	}
}
