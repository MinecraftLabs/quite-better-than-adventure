package quitebetter.mixin.core;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quitebetter.core.feature.ModFeatures;

import java.util.Random;

@Mixin(
	value = {ChunkDecoratorOverworld.class},
	remap = false
)
public class ChunkDecoratorOverworldMixin {
	@Shadow
	@Final
	private World world;

	@Inject(
		method = "decorate",
		at = @At("HEAD"),
		cancellable = true
	)
	private void decorate(Chunk chunk, CallbackInfo ci) {
		this.world.scheduledUpdatesAreImmediate = true;
		int chunkX = chunk.pos.x;
		int chunkZ = chunk.pos.z;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		int y = this.world.getHeightValue(x + 16, z + 16);
		Random random = new Random(this.world.getRandomSeed()*chunkX*chunkZ);
		Random chunkRandom = chunk.getChunkRandom(75644760L);
		ModFeatures.ChunkDecoration(this.world,chunk,chunkX,chunkZ,chunkRandom,x,y,z,random);
	}
}
