package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.util.IFalling;

import java.util.Random;

public class BlockLogicBoneBlock extends BlockLogic implements IFalling {
	public static boolean fallInstantly = false;

	public BlockLogicBoneBlock(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onPlacedByWorld(World world, TilePosc tilePos) {
		world.scheduleBlockUpdate(tilePos, this.block, this.tickDelay());
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		world.scheduleBlockUpdate(tilePos, this.block, this.tickDelay());
	}

	@Override
	public void updateTick(World world, TilePosc tilePos, Random rand, boolean isRandomTick) {
		this.tryToFall(world, tilePos);
	}

	private void tryToFall(World world, TilePosc tilePos) {
		Random rand = new Random();
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		if (canFallBelow(world, tilePos.down(new TilePos())) && y >= 0) {
			byte byte0 = 32;
			if (!fallInstantly && world.areBlocksLoaded(new TilePos(x - byte0, y - byte0, z - byte0), new TilePos(x + byte0, y + byte0, z + byte0))) {
				EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, (double)x + (double)0.5F, (double)y + (double)0.5F, (double)z + (double)0.5F, this.block.id(), 0, (TileEntity)null);
				world.entityJoinedWorld(entityFallingBlock);
			} else {
				world.setBlockTypeNotify(tilePos, Blocks.AIR);

				TilePos queryPos = new TilePos(x, y, z);
				while(canFallBelow(world, queryPos.set(x, y - 1, z)) && y > 0) {
					--y;
				}

				if (y > 0) {
					world.setBlockTypeNotify(queryPos.set(x, y, z), this.block);
				}
			}
		}

	}

	@Override
	public int tickDelay() {
		return 3;
	}

	public static boolean canFallBelow(World world, TilePosc tilePos) {
		Block<?> block = world.getBlockType(tilePos);
		return block == null || block.hasTag(BlockTags.PLACE_OVERWRITES);
	}

	@Override
	public boolean FellOnGround(EntityFallingBlock fallingBlock, TilePosc tilePos) {
		Random rand = new Random();
		assert fallingBlock.world!=null;
		for (int i=1;i<25;i++) {
			fallingBlock.world.spawnParticle("block",
				tilePos.x()+0.5,
				tilePos.y()+0.5,
				tilePos.z()+0.5,
				(double)(rand.nextInt(11)-5),
				(double)(rand.nextInt(11)-5),
				(double)(rand.nextInt(11)-5),
				block.id(),
				false
			);
		}
		fallingBlock.world.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(), "mob.skeletonhurt", 0.25F, 1F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
		return true;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		Random rand = new Random();
		return dropCause == EnumDropCause.SILK_TOUCH || dropCause == EnumDropCause.PICK_BLOCK ? new ItemStack[]{new ItemStack(this.block)} : new ItemStack[]{new ItemStack(Items.BONE,rand.nextInt(4))};
	}

	@Override
	public void onDestroyedByPlayer(World world, TilePosc tilePos, Side side, int meta, Player player, Item item) {
		Random rand = new Random();
		world.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(), "mob.skeletonhurt", 0.5F, 0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
	}
}
