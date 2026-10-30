package quitebetter.mixin.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlag;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
	value = {BlockLogicFlag.class},
	remap = false
)
public abstract class BlockLogicFlagMixin extends BlockLogic {
	public BlockLogicFlagMixin(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public AABBdc getBoundsFromState(WorldSource world, TilePosc tilePos) {
		Direction dir = Direction.fromId(world.getBlockData(tilePos));
		if (dir.isHorizontal()) {
			float f = 0.125F;
			float f1 = 1.0F;
			float dx = dir.offsetX()*0.4F;
			float dz = dir.offsetZ()*0.4F;
			return new AABBd(
				(double)(0.5F - f)-dx,
				0.0,
				(double)(0.5F - f)-dz,
				(double)(0.5F + f)-dx,
				(double)f1,
				(double)(0.5F + f)-dz
			);
		}
		return super.getBoundsFromState(world, tilePos);
	}
}
