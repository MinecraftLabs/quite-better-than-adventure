package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.PlacementMode;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCorrugatedIronPainted extends BlockLogicAxisAlignedPainted implements IPainted {
	public BlockLogicCorrugatedIronPainted(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public int getPlacedData(@Nullable Player player, ItemStack stack, World world, TilePosc tilePos, Side side, double xPlaced, double yPlaced) {
		return stack.getMetadata();
	}

	@Override
	public void onPlacedByMob(World world, TilePosc tilePos, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		int meta = world.getBlockData(tilePos);
		Axis axis = mob.getPlacementDirection(side, PlacementMode.SIDE).axis();
		world.setBlockDataNotify(tilePos, axisToMeta(meta, axis));
	}

	@Override
	public void onPlacedOnSide(World world, TilePosc tilePos, @NotNull Side side, double xPlaced, double yPlaced) {
		int meta = world.getBlockData(tilePos);
		Axis axis = side.axis();
		world.setBlockDataNotify(tilePos, axisToMeta(meta, axis));
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(this, 1, this.stripAxisFromMetadata(meta))};
	}

	@Override
	public void removeDye(World world, TilePosc tilePos) {
		int meta = world.getBlockData(tilePos);
		world.setBlockTypeDataNotify(tilePos, ModBlocks.BLOCK_IRON_CORRUGATED, this.stripColorFromMetadata(meta));
	}
}
