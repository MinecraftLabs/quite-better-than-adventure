package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import quitebetter.core.util.IFalling;

import java.util.Random;

public class BlockLogicMudSlab extends BlockLogicSlab implements IFalling {
	private final boolean isHardened;
	private final Block<?> altBlock;

	public BlockLogicMudSlab(Block<?> block, Block<?> modelBlock, boolean isHardened, Block<?> altBlock) {
		super(block, modelBlock);
		this.isHardened = isHardened;
		this.altBlock = altBlock;
	}

	@Override
	public int tickDelay() {
		return 5;
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
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(this, 1, meta)};
	}

	private void tryToFall(World world, TilePosc tilePos) {
		if (!isHardened) {
			int x = tilePos.x();
			int y = tilePos.y();
			int z = tilePos.z();
			int meta = world.getBlockData(tilePos);
			if (canFallBelow(world, tilePos.down(new TilePos()), this.block.id(), meta) && y >= 0) {
				byte byte0 = 32;
				if (!world.areBlocksLoaded(new TilePos(x - byte0, y - byte0, z - byte0), new TilePos(x + byte0, y + byte0, z + byte0))) {
					world.setBlockTypeNotify(tilePos, Blocks.AIR);

					TilePos queryPos = new TilePos(x, y, z);
					while(canFallBelow(world, queryPos.set(x, y - 1, z), this.block.id(), meta) && y > 0) {
						--y;
					}

					if (y > 0) {
						world.setBlockTypeNotify(queryPos.set(x, y, z), this.block);
					}
				} else {
					EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, this.block.id(), world.getBlockData(tilePos), null);
					world.entityJoinedWorld(entityFallingBlock);
				}
			}

		}
	}

	public static boolean canFallBelow(World world, TilePosc tilePos, int id, int meta) {
		Block<?> block = world.getBlockType(tilePos);
		return meta == 2 || (block.id() == id && world.getBlockData(tilePos)!=1) || block == Blocks.AIR || block == Blocks.FIRE || Blocks.hasTag(block, BlockTags.IS_WATER) || Blocks.hasTag(block, BlockTags.IS_LAVA );
	}

	@Override
	public boolean FellOnGround(EntityFallingBlock fallingBlock, TilePosc tilePos) {
		World world = fallingBlock.world;
		TilePos belowPos = tilePos.down(new TilePos());
		if (world.getBlockType(belowPos).id()==block.id() && world.getBlockData(belowPos)!=1) {
			world.setBlockTypeDataNotify(belowPos, block,1);
			fallingBlock.remove();
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
			return false;
		} else if (fallingBlock.carriedBlock.metadata==2) {
			world.setBlockDataNotify(tilePos,0);
			fallingBlock.remove();
			return false;
		}
		return true;
	}

	@Override
	public void updateTick(World world, TilePosc tilePos, Random rand, boolean isRandomTick) {
		if (this.isHardened && !BlockLogicMudBlock.isLavaNearby(world, tilePos) && BlockLogicMudBlock.canBecomeWet(world, tilePos)) {
			world.setBlockTypeDataNotify(tilePos, altBlock, world.getBlockData(tilePos));
		} else if (!this.isHardened && (BlockLogicMudBlock.isLavaNearby(world, tilePos) || world.dimension == Dimension.NETHER)) {
			world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, (double)tilePos.x() + 0.5, (double)tilePos.y() + 0.5, (double)tilePos.z() + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

			for(int l = 0; l < 8; ++l) {
				world.spawnParticle("largesmoke", (double)tilePos.x() + world.rand.nextDouble(), (double)tilePos.y() + world.rand.nextDouble(), (double)tilePos.z() + world.rand.nextDouble(), 0.0, 0.0, 0.0, 0, false);
			}

			world.setBlockTypeDataNotify(tilePos, altBlock, world.getBlockData(tilePos));
		} else {
			this.tryToFall(world, tilePos);
		}
	}
}
