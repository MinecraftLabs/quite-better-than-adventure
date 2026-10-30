package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBdc;
import quitebetter.core.item.ModItems;

public class BlockLogicOverlaySeashells extends BlockLogic {

	public BlockLogicOverlaySeashells(Block<?> block, Material material) {
		super(block, material);
		this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
	}

	@Override
	public boolean canPlaceAt(World world, TilePosc tilePos) {
		Block<?> block = world.getBlockType(tilePos.down(new TilePos()));
		return block.getMaterial().isSolid();
	}

	@Override
	public boolean canStay(World world, TilePosc tilePos) {
		return this.canPlaceAt(world, tilePos);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public AABBdc getCollisionAABB(WorldSource world, TilePosc tilePos) {
		return null;
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		if (!this.canStay(world, tilePos)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, world.getBlockData(tilePos), null, null);
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
		}

	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return dropCause == EnumDropCause.PICK_BLOCK ? new ItemStack[]{new ItemStack(ModItems.SEASHELL, 1)} : new ItemStack[]{new ItemStack(ModItems.SEASHELL, meta + 1)};
	}
}
