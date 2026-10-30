package quitebetter.mixin.core;

import net.minecraft.core.block.BlockLogicMushroom;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(
	value = {BlockLogicMushroom.class},
	remap = false
)
public class BlockLogicMushroomMixin {
	@Inject(
		method = {"updateTick"},
		at = @At("TAIL")
	)
	public void updateTick(World world, TilePosc tilePos, Random rand, boolean isRandomTick, CallbackInfo ci) {
		if (world.getBlockType(tilePos.down(new TilePos())).equals(Blocks.COBBLE_STONE_MOSSY)) {
			if (rand.nextInt(2)==0) {
				BlockLogicMushroom that = (BlockLogicMushroom)(Object)this;
				TilePos checkPos = new TilePos();

				for (int i=1;i<=3;i++) {
					int x1 = tilePos.x() + rand.nextInt(3) - 1;
					int randY = tilePos.y() + rand.nextInt(2) - rand.nextInt(2);
					int z1 = tilePos.z() + rand.nextInt(3) - 1;
					if (world.isAirBlock(checkPos.set(x1, randY, z1)) && that.canGrowOn(world, checkPos)) {
						int x2 = tilePos.x() + rand.nextInt(3) - 1;
						int z2 = tilePos.z() + rand.nextInt(3) - 1;
						if (world.isAirBlock(checkPos.set(x2, randY, z2)) && that.canGrowOn(world, checkPos)) {
							world.setBlockTypeNotify(checkPos, that.block);
						}
					}
				}
			}
		}
	}
}
