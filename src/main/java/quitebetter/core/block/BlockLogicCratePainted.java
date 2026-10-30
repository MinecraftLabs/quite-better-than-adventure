package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.tileentity.TileEntityCrate;

public class BlockLogicCratePainted extends BlockLogicCrate implements IPainted {
	public BlockLogicCratePainted(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public int getPlacedData(@Nullable Player player, ItemStack stack, World world, TilePosc tilePos, Side side, double xPlaced, double yPlaced) {
		return stack.getMetadata() & 15;
	}

	@Override
	public DyeColor fromMetadata(int meta) {
		return DyeColor.colorFromBlockMeta(meta);
	}

	@Override
	public int toMetadata(DyeColor color) {
		return color.blockMeta;
	}

	@Override
	public int stripColorFromMetadata(int meta) {
		return 0;
	}

	@Override
	public void removeDye(World world, TilePosc tilePos) {
		world.setBlockTypeNotify(tilePos, ModBlocks.CRATE);
	}

	@Override
	public void setColor(World world, TilePosc tilePos, DyeColor color) {
		world.setBlockDataNotify(tilePos,toMetadata(color));
		world.notifyBlockChange(tilePos,block);
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		if (tile!=null) {
			tile.updateStorageHeights();
		}
	}
}
