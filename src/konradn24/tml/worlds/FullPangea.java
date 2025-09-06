package konradn24.tml.worlds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joml.Vector3f;

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
import konradn24.tml.graphics.shaders.Shader;
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

public class FullPangea extends World {

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
	
	public FullPangea(long seed, float smoothness, float biomeSize, Handler handler) throws IOException {
		super(
			20004000, 20004000, handler, 
			new Shader("res/shaders/chunk_vertex_shader.glsl", "res/shaders/chunk_fragment_shader.glsl"),
			new Shader("res/shaders/batch_vertex_shader.glsl", "res/shaders/batch_fragment_shader.glsl")
		);
		
		super.seed = seed;
		super.smoothness = smoothness;
		super.biomeSize = biomeSize;
		
		worldName = "Earth";
		
		biomes = Map.ofEntries(
			Map.entry(FROZEN_OCEAN,        	new Vector3f(180, 200, 255)),
			Map.entry(HALF_FROZEN_OCEAN,   	new Vector3f(150, 180, 240)),
			Map.entry(OCEAN,               	new Vector3f(0,   80,  160)),
			Map.entry(FROZEN_SEA,          	new Vector3f(170, 190, 255)),
			Map.entry(HALF_FROZEN_SEA,     	new Vector3f(130, 170, 240)),
			Map.entry(SEA,                	new Vector3f(0,  120,  200)),
			Map.entry(TUNDRA_BEACH,       	new Vector3f(102, 128, 102)),
			Map.entry(GRAVEL_BEACH,        	new Vector3f(128, 128, 128)),
			Map.entry(BEACH,               	new Vector3f(208, 217, 93)),
			Map.entry(SNOW_DESERT,         	new Vector3f(240, 240, 255)),
			Map.entry(TUNDRA,              	new Vector3f(102, 102, 102)),
			Map.entry(DRY_TAIGA,           	new Vector3f(160, 190, 160)),
			Map.entry(TAIGA,               	new Vector3f(114, 242, 124)),
			Map.entry(PLAINS,              	new Vector3f(135, 255, 79)),
			Map.entry(HILLS,               	new Vector3f(100, 170,  80)),
			Map.entry(FOREST,              	new Vector3f(40,  120,  40)),
			Map.entry(DESERT,              	new Vector3f(240, 220, 130)),
			Map.entry(SUBTROPICAL_RAINFOREST, new Vector3f(60, 140,  60)),
			Map.entry(RAINFOREST,          	new Vector3f(30,  100,  30)),
			Map.entry(DENSE_RAINFOREST,    	new Vector3f(10,   80,  20))
		);
		
//		if(getTile(spawnTileX, spawnTileY).hasAttribute(Tile.ATTRIB_NO_SPAWN))
//			setTile(spawnTileX, spawnTileY, Tile.getTile(Sand.class));
		
//		entityManager.addEntity(new House(handler, 64 * 16, 64 * 7));
	}
	
	protected GenerationData generation(int x, int y, float altitude, float temperature, float humidity) {
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
        String biome = BIOME_NULL;
        float bestScore = 0f;
        
        for (var entry : biomeScores.entrySet()) {
        	float score = entry.getValue();
        	if (score <= 0f) continue;
        	
        	if(entry.getValue() > bestScore) {
        		bestScore = entry.getValue();
        		biome = entry.getKey();
        	}
        	
            Vector3f c = biomes.get(entry.getKey());
            r += c.x * score;
            g += c.y * score;
            b += c.z * score;
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
        	
        	if(random.nextFloat() < 0.01f)
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
