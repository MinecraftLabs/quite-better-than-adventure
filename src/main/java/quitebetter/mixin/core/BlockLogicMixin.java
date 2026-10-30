package quitebetter.mixin.core;

import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
	value = {BlockLogic.class},
	remap = false
)
public class BlockLogicMixin {
	@Inject(
		method = {"getBoundsFromState"},
		at = @At("RETURN"),
		cancellable = true
	)
	public void getBoundsFromState(WorldSource world, TilePosc tilePos, CallbackInfoReturnable<AABBdc> cir) {
		if ((Object) this instanceof BlockLogicFlag) {
			if (world.getBlockType(tilePos) == Blocks.FLAG) {
				Direction dir = Direction.fromId(world.getBlockData(tilePos));
				if (dir.isHorizontal()) {
					float f = 0.125F;
					float f1 = 1.0F;
					float dx = dir.offsetX()*0.4F;
					float dz = dir.offsetZ()*0.4F;
					cir.setReturnValue(new AABBd(
						(double)(0.5F - f)-dx,
						(double)0.0F,
						(double)(0.5F - f)-dz,
						(double)(0.5F + f)-dx,
						(double)f1,
						(double)(0.5F + f)-dz
					));
				}
			}
		}
	}
}
