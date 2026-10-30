package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;

public class BlockLogicCorrugatedIron  extends BlockLogicAxisAligned implements IPaintable {
	public BlockLogicCorrugatedIron(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(this, 1, meta)};
	}

	@Override
	public String getLanguageKey(int meta) {
		return this.block.getKey();
	}

	@Override
	public void setColor(World world, TilePosc tilePos, DyeColor color) {
		int meta = world.getBlockData(tilePos);
		world.setBlockTypeData(tilePos, ModBlocks.BLOCK_IRON_CORRUGATED_PAINTED,meta);
		((BlockLogicCorrugatedIronPainted)ModBlocks.BLOCK_IRON_CORRUGATED_PAINTED.getLogic()).setColor(world, tilePos, color);
	}
}
