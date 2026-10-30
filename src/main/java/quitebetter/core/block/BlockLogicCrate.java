package quitebetter.core.block;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityMeshGold;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import org.jetbrains.annotations.NotNull;
import quitebetter.core.tileentity.TileEntityCrate;
import quitebetter.core.util.ILeftClickable;
import quitebetter.core.util.IRightClickable;
import quitebetter.core.util.PlayerUtil;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.IPaintable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.util.ServerUtil;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static quitebetter.core.ModCore.LOGGER;

public class BlockLogicCrate extends BlockLogic implements IPaintable, ILeftClickable, IRightClickable {
	public BlockLogicCrate(Block<?> block, Material material) {
		super(block, material);
		block.withEntity(TileEntityCrate::new);
	}

	public static boolean isCrate(Block block) {
		Block[] fans = {ModBlocks.CRATE,ModBlocks.CRATE_PAINTED};
		return block != null && Arrays.asList(fans).contains(block);
	}

	//ACTIVATOR

	@Override
	public void onActivatorInteracted(World world, TilePosc tilePos, TileEntityActivator activator, Direction direction) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		TilePos frontPos = tilePos.add(direction, new TilePos());
		Block front = world.getBlockType(frontPos);
		TileEntity frontTile = world.getTileEntity(frontPos);
		if (tile!=null) {
			ItemStack activeStack = activator.getItem(activator.stackSelector);
			if (tile.hasItemData()) {
				if (TileEntityCrate.canUseToTake(activeStack) && (front==null || front.hasTag(BlockTags.PLACE_OVERWRITES) || front.equals(Blocks.MESH))) {
					ItemStack stack = tile.takeStack();
					if (stack!=null) {
						world.entityJoinedWorld(new EntityItem(world, frontPos.x(),frontPos.y(),frontPos.z(), stack));
					}
				}
			}
		}
	}

	//BREAKING

	@Override
	public boolean preventsBreaking(World world, TilePosc tilePos, Player player, Side side) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		return tile!=null && !tile.glued && tile.itemID !=null && !player.isSneaking();
	}

	@Override
	public void onBlockLeftClicked(World world, TilePosc tilePos, Player player, Side side) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		if (tile!=null && (player.getHeldItem()==null || TileEntityCrate.canUseToTake(player.getHeldItem())) ) {
			ItemStack stack = tile.takeStack();
			if (stack != null) {
				if (PlayerUtil.storeItemStack(player, stack) == -1) {
					world.entityJoinedWorld(new EntityItem(world, player.x, player.y, player.z, stack));
				}
			}
		}
	}

	//INTERACTION

	@Override
	public boolean preventsInteraction(World world, TilePosc tilePos, Player player, Side side) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		return tile!=null && !tile.glued && (tile.itemID !=null || player.getHeldItem()!=null && player.getHeldItem().getItem().equals(Items.SLIMEBALL) || !player.isSneaking()) && (player.getHeldItem()!=null && TileEntityCrate.canUseToTake(player.getHeldItem()));
	}

	@Override
	public void onBlockRightClicked(World world, TilePosc tilePos, Player player, Side side) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		if (tile != null) {
			if (EnvironmentHelper.isSingleplayerClient() || EnvironmentHelper.isMultiplayerServer()) {
				tile.pushItems(player);
			} else {
				if (!tile.glued) {
					player.swingItem();
				}
			}
		}
	}

	//WORLD

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		ArrayList<ItemStack> result = new ArrayList<>();
		if (tileEntity!=null) {
			result = ((TileEntityCrate)tileEntity).getBreakResult();
		}
		result.add(new ItemStack(this.block, 1, meta));
		EntityItem.enableItemClumping = true;
		return result.toArray(new ItemStack[]{});
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		TileEntityCrate tile = (TileEntityCrate) world.getTileEntity(tilePos);
		if (tile!=null) {
			tile.updateStorageHeights();
			tile.takeOutOfBasket();
		}
	}

	//COLOR

	@Override
	public void setColor(World world, TilePosc tilePos, DyeColor color) {
		world.setBlockTypeRaw(tilePos, ModBlocks.CRATE_PAINTED);
		((BlockLogicCratePainted)ModBlocks.CRATE_PAINTED.getLogic()).setColor(world, tilePos, color);
		world.notifyBlockChange(tilePos,block);
	}
}
