package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

import java.util.Random;

public class BlockLogicMudBlock extends BlockLogic {
	private final boolean isHardened;
	private final Block<?> altBlock;

	public BlockLogicMudBlock(Block<?> block, Material material, boolean isHardened, Block<?> altBlock) {
		super(block, material);
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

	public static boolean isWaterNearby(World world, TilePosc tilePos, int range) {
		TilePos queryPos = new TilePos();
		for(int x1 = tilePos.x() - range; x1 <= tilePos.x() + range; ++x1) {
			for(int y1 = tilePos.y() - range; y1 <= tilePos.y() + range; ++y1) {
				for(int z1 = tilePos.z() - range; z1 <= tilePos.z() + range; ++z1) {
					if (Blocks.hasTag(world.getBlockType(queryPos.set(x1, y1, z1)), BlockTags.IS_WATER)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isLavaNearby(World world, TilePosc tilePos) {
		TilePos queryPos = new TilePos();
		for(int x1 = tilePos.x() - 1; x1 <= tilePos.x() + 1; ++x1) {
			for(int y1 = tilePos.y() - 1; y1 <= tilePos.y() + 1; ++y1) {
				for(int z1 = tilePos.z() - 1; z1 <= tilePos.z() + 1; ++z1) {
					if (Blocks.hasTag(world.getBlockType(queryPos.set(x1, y1, z1)), BlockTags.IS_LAVA)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean canBecomeWet(World world, TilePosc tilePos) {
		if (isWaterNearby(world, tilePos, 1)) {
			return true;
		} else {
			if (isWaterNearby(world, tilePos, 3)) {
				TilePos queryPos = new TilePos();
				for(int x1 = tilePos.x() - 1; x1 <= tilePos.x() + 1; ++x1) {
					for(int y1 = tilePos.y() - 1; y1 <= tilePos.y() + 1; ++y1) {
						for(int z1 = tilePos.z() - 1; z1 <= tilePos.z() + 1; ++z1) {
							int bID = world.getBlockType(queryPos.set(x1, y1, z1)).id();
							if (bID == Blocks.MUD.id() || bID == Blocks.FARMLAND_DIRT.id() && BlockLogicFarmland.isWet(world.getBlockData(queryPos))) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
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
			if (canFallBelow(world, tilePos.down(new TilePos())) && y >= 0) {
				byte byte0 = 32;
				if (!world.areBlocksLoaded(new TilePos(x - byte0, y - byte0, z - byte0), new TilePos(x + byte0, y + byte0, z + byte0))) {
					world.setBlockTypeNotify(tilePos, Blocks.AIR);

					TilePos queryPos = new TilePos(x, y, z);
					while(canFallBelow(world, queryPos.set(x, y - 1, z)) && y > 0) {
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

	public static boolean canFallBelow(World world, TilePosc tilePos) {
		Block<?> block = world.getBlockType(tilePos);
		return block == Blocks.AIR || block == Blocks.FIRE || Blocks.hasTag(block, BlockTags.IS_WATER) || Blocks.hasTag(block, BlockTags.IS_LAVA);
	}

	@Override
	public void updateTick(World world, TilePosc tilePos, Random rand, boolean isRandomTick) {
		if (this.isHardened && !isLavaNearby(world, tilePos) && canBecomeWet(world, tilePos)) {
			world.setBlockTypeDataNotify(tilePos, altBlock, world.getBlockData(tilePos));
		} else if (!this.isHardened && (isLavaNearby(world, tilePos) || world.dimension == Dimension.NETHER)) {
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
