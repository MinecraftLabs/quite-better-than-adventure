package quitebetter.core.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectilePebble;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.IDispensable;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import quitebetter.core.block.ModBlocks;

import java.util.Random;

public class ItemSeashell extends Item implements IDispensable {
	public ItemSeashell(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
		this.maxStackSize = 64;
	}

	@Override
	public boolean onUseOnBlock(ItemStack itemstack, World world, Player player, TilePosc blockPos, Side side, double xPlaced, double yPlaced) {
		int blockX = blockPos.x();
		int blockY = blockPos.y();
		int blockZ = blockPos.z();
		TilePos pos = new TilePos(blockX, blockY, blockZ);
		Block<?> block = world.getBlockType(pos);
		int id = block.id();
		int meta = world.getBlockData(pos);
		if (
			id != ModBlocks.OVERLAY_SEASHELL.id() &&
			block.hasTag(BlockTags.PLACE_OVERWRITES)
		) {
			id = 0;
			meta = 0;
		}

		if (itemstack.stackSize <= 0) {
			return false;
		} else if (blockY == world.getHeightBlocks() - 1 && ModBlocks.OVERLAY_SEASHELL.getMaterial().isSolid()) {
			return false;
		} else {
			if (id == ModBlocks.OVERLAY_SEASHELL.id() && side == Side.TOP) {
				int newMeta = meta + 1;
				if (!world.isBlockOpaqueCube(pos.down(new TilePos()))) {
					return false;
				}

				if (newMeta < 3) {
					world.setBlockTypeDataNotify(pos, ModBlocks.OVERLAY_SEASHELL, newMeta);
					world.playBlockSoundEffect(player, (double)((float)blockX + 0.5F), (double)((float)blockY + 0.5F), (double)((float)blockZ + 0.5F), ModBlocks.OVERLAY_SEASHELL, EnumBlockSoundEffectType.PLACE);
					itemstack.consumeItem(player);
					return true;
				}
			}

			if (id != 0) {
				blockX += side.offsetX();
				blockY += side.offsetY();
				blockZ += side.offsetZ();
				pos.set(blockX, blockY, blockZ);
				id = world.getBlockType(pos).id();
				meta = world.getBlockData(pos);
			}

			if (id == ModBlocks.OVERLAY_SEASHELL.id()) {
				int newMeta = meta + 1;
				AABBd bbBox = new AABBd((double)blockX, (double)blockY, (double)blockZ, (double)((float)blockX + 1.0F), (double)((float)blockY + (float)(2 * (newMeta + 1)) / 16.0F), (double)((float)blockZ + 1.0F));
				if (!world.checkIfAABBIsClear(bbBox) || !world.isBlockOpaqueCube(pos.down(new TilePos()))) {
					return false;
				}

				if (newMeta < 3) {
					world.setBlockTypeDataNotify(pos, ModBlocks.OVERLAY_SEASHELL, newMeta);
					world.playBlockSoundEffect(player, (double)((float)blockX + 0.5F), (double)((float)blockY + 0.5F), (double)((float)blockZ + 0.5F), ModBlocks.OVERLAY_SEASHELL, EnumBlockSoundEffectType.PLACE);
					itemstack.consumeItem(player);
					return true;
				}
			}

			if (world.canBlockIdBePlacedAt(ModBlocks.OVERLAY_SEASHELL.id(), pos, false, side) && world.isBlockOpaqueCube(pos.down(new TilePos())) && world.setBlockTypeDataNotify(pos, ModBlocks.OVERLAY_SEASHELL, 0)) {
				ModBlocks.OVERLAY_SEASHELL.onPlacedByMob(world, pos, side, player, xPlaced, yPlaced);
				world.playBlockSoundEffect(player, (double)((float)blockX + 0.5F), (double)((float)blockY + 0.5F), (double)((float)blockZ + 0.5F), ModBlocks.OVERLAY_SEASHELL, EnumBlockSoundEffectType.PLACE);
				itemstack.consumeItem(player);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void onUseByActivator(ItemStack itemStack, World world, TileEntityActivator activator, Random random, TilePosc blockPos, Direction direction, double offX, double offY, double offZ) {
		ProjectilePebble projectilePebble = new ProjectilePebble(world, (double)blockPos.x() + offX, (double)blockPos.y() + offY, (double)blockPos.z() + offZ);
		projectilePebble.setHeading((double)direction.offsetX() * 0.6, direction.offsetY() == 0 ? 0.1 : (double)direction.offsetY() * 0.6, (double)((float)direction.offsetZ() * 0.6F), 1.1F, 6.0F);
		world.entityJoinedWorld(projectilePebble);
		--itemStack.stackSize;
	}

	@Override
	public void onDispensed(ItemStack itemStack, World world, Random random, Direction direction, double x, double y, double z) {
		int xOffset = direction.offsetX();
		int yOffset = direction.offsetY();
		int zOffset = direction.offsetZ();
		ProjectilePebble projectilePebble = new ProjectilePebble(world, x, y, z);
		projectilePebble.setHeading((double)xOffset, (double)yOffset + 0.1, (double)zOffset, 1.1F, 6.0F);
		world.entityJoinedWorld(projectilePebble);
	}
}
