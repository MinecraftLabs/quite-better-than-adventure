package quitebetter.mixin.client;

import net.minecraft.client.render.block.model.generic.BlockModelGenericRope;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.block.ModBlocks;

import java.util.Arrays;

@Mixin(
	value = {BlockModelGenericRope.class},
	remap = false
)
public class BlockModelRopeMixin {
	@Unique
	public Block<?>[] connectables = {ModBlocks.LANTERN_COAL,ModBlocks.LANTERN_REDSTONE,ModBlocks.LANTERN_MUSHROOM};

	@Inject(
		method = {"canConnect"},
		at = @At("RETURN"),
		cancellable = true
	)
	public void canConnect(WorldSource worldSource, TilePosc tilePos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (direction == Direction.DOWN) {
			Block<?> block = worldSource.getBlockType(tilePos.add(direction, new TilePos()));
			if (block!=null && Arrays.asList(connectables).contains(block)) {
				cir.setReturnValue(true);
			}
		}
	}
}
