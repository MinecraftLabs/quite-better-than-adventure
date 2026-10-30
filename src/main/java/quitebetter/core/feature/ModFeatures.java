package quitebetter.core.feature;

import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.pos.TilePos;
import quitebetter.core.util.WorldUtil;

import java.util.Arrays;
import java.util.Random;

public class ModFeatures {
	public static void Setup() {
		CommandHelper.registerWorldFeatureClass(WorldFeatureMaze.class);
	}

	public static void ChunkDecoration(World world, Chunk chunk, int chunkX, int chunkZ, Random ChunkRandom, int x, int y, int z, Random random) {
		Biome biome = world.getBlockBiome(new TilePos(x,y,z));
		if (Arrays.asList(WorldFeatureMaze.allowedBiomes).contains(biome)) {
			if (random.nextInt(65)==0) {
				int topBlock = world.getHeightValue(x, z);
				if (topBlock > WorldUtil.getOceanLevel(world)+5) {
					(new WorldFeatureMaze()).place(world, ChunkRandom, new TilePos(x, topBlock, z));
				}
			}
		}
	}
}
