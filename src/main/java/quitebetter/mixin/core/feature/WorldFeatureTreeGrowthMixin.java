package quitebetter.mixin.core.feature;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureCherryTreeFancy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeCaatinga;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeEucalyptus;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreePalm;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShapeSwamp;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaBushy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTall;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.feature.WorldFeatureGlowingMushrooms;

import java.util.Random;

@Mixin(
	value = {
		WorldFeatureCherryTreeFancy.class,
		WorldFeatureTreeCaatinga.class,
		WorldFeatureTreeEucalyptus.class,
		WorldFeatureTreeFancy.class,
		WorldFeatureTreePalm.class,
		WorldFeatureTreeShapeSwamp.class,
		WorldFeatureTreeShrub.class,
		WorldFeatureTreeTaigaBushy.class,
		WorldFeatureTreeTall.class
	},
	remap = false
)
public class WorldFeatureTreeGrowthMixin {
	@Inject(
		method = {"place"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/generate/feature/tree/WorldFeatureTree;onTreeGrown(Lnet/minecraft/core/world/World;III)V"
		)
	)
	private void place(World world, Random random, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		WorldFeatureGlowingMushrooms.PlaceNearTree(world, random, x, y, z, world.getBlockBiome(new TilePos(x, y, z)));
	}
}
