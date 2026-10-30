package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;

public class BlockLogicHazard extends BlockLogic implements IPaintable {
	public BlockLogicHazard(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public int getPlacedData(@Nullable Player player, ItemStack stack, World world, TilePosc tilePos, Side side, double xPlaced, double yPlaced) {
		return stack.getMetadata() & 15;
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
		world.setBlockType(tilePos, ModBlocks.HAZARD_PAINTED);
		((BlockLogicHazardPainted)ModBlocks.HAZARD_PAINTED.getLogic()).setColor(world, tilePos, color);
	}
}
