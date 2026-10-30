package quitebetter.core.block;

import quitebetter.core.item.ModItems;
import quitebetter.core.tileentity.TileEntityPedestal;
import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class BlockLogicPedestal extends BlockLogic {
	public BlockLogicPedestal(Block<?> block, Material material) {
		super(block, material);
		this.setBlockBounds(0,0,0,1,0.1,1);
		block.withEntity(TileEntityPedestal::new);
	}

	@Override
	public boolean onInteracted(World world, TilePosc tilePos, Player player, Side side, double xPlaced, double yPlaced) {
		TileEntityPedestal tile = (TileEntityPedestal)world.getTileEntity(tilePos);
		ItemStack heldItem = player.getHeldItem();
		boolean flag = tile.setDisplayItem(player, heldItem);
		if (heldItem != null && heldItem.stackSize <= 0) {
			player.inventory.setItem(player.inventory.getCurrentSlot(), (ItemStack)null);
		}

		return flag;
	}

	public boolean mayPlaceOn(int blockId) {
		Block[] excepts = {
			ModBlocks.SUPPORT_IRON,
			ModBlocks.SUPPORT_STEEL,
			ModBlocks.SUPPORT_WOOD,
			ModBlocks.FAN,
			ModBlocks.ACTIVE_FAN,
			ModBlocks.IN_FAN,
			ModBlocks.ACTIVE_IN_FAN,
			Blocks.PISTON_BASE,
			Blocks.PISTON_HEAD,
			Blocks.BRAZIER_INACTIVE,
			Blocks.BRAZIER_ACTIVE, //Wanted to remove, but it looks cool.
			Blocks.MESH,
			Blocks.MESH_GOLD,
			Blocks.ICE,
			Blocks.PERMAICE
		};
		return Blocks.solid[blockId] || Arrays.asList(excepts).contains(Blocks.getBlock(blockId));
	}

	@Override
	public boolean canStay(World world, TilePosc tilePos) {
		if (tilePos.y() >= 0 && tilePos.y() < world.getHeightBlocks()) {
			return this.mayPlaceOn(world.getBlockType(tilePos.down(new TilePos())).id());
		} else {
			return false;
		}
	}

	@Override
	public boolean canPlaceAt(World world, TilePosc tilePos) {
		return super.canPlaceAt(world, tilePos) && this.mayPlaceOn(world.getBlockType(tilePos.down(new TilePos())).id());
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		super.onNeighborChanged(world, tilePos, block);
		if (!this.canStay(world, tilePos)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, world.getBlockData(tilePos), (TileEntity)null, (Player)null);
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
		}
	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean isEmittingSignal(WorldSource worldSource, TilePosc tilePos, Side side) {
		return ((TileEntityPedestal) worldSource.getTileEntity(tilePos)).displayItem != null;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(ModItems.PEDESTAL)};
	}

	public static void updateNeighbours(World world, TilePosc tilePos, Block block) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		world.notifyBlocksOfNeighborChange(new TilePos(x, y - 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y + 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x - 1, y, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x + 1, y, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y, z - 1), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y, z + 1), block);

		world.notifyBlocksOfNeighborChange(new TilePos(x, y - 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y - 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x - 1, y - 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x + 1, y - 1, z), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y - 1, z - 1), block);
		world.notifyBlocksOfNeighborChange(new TilePos(x, y - 1, z + 1), block);
	}
}
