package quitebetter.mixin.core.feature;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancyRainforest;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.feature.WorldFeatureGlowingMushrooms;

import java.util.Random;

@Mixin(
	value = {WorldFeatureTreeFancyRainforest.class},
	remap = false
)
public class TreeFancyRainforestMixin {
	@Inject(
		method = {"place"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/generate/feature/tree/WorldFeatureTreeFancyRainforest;func_521_a()V")
	)
	private void place(World world, Random random, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		WorldFeatureGlowingMushrooms.PlaceNearTree(world, random, x, y, z, world.getBlockBiome(new TilePos(x, y, z)));
	}
}
