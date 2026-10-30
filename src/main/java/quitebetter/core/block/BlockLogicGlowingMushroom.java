package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.BlockLogicMushroom;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockLogicGlowingMushroom extends BlockLogicMushroom {
	public BlockLogicGlowingMushroom(Block<?> block) {
		super(block);
		float f = 0.2F;
		this.setBlockBounds((double)(0.5F - f), (double)0.0F, (double)(0.5F - f), (double)(0.5F + f), (double)(f * 2.0F), (double)(0.5F + f));
		this.canBeBonemealed = true;
		block.setTicking(true);
	}

	@Override
	public void updateTick(World world, TilePosc tilePos, Random rand, boolean isRandomTick) {
		if (rand.nextInt(25) == 0) {
			int x1 = tilePos.x() + rand.nextInt(3) - 1;
			int randY = tilePos.y() + rand.nextInt(2) - rand.nextInt(2);
			int z1 = tilePos.z() + rand.nextInt(3) - 1;
			TilePos queryPos = new TilePos();
			if (world.isAirBlock(queryPos.set(x1, randY, z1)) && this.canStay(world, queryPos)) {
				int x2 = tilePos.x() + rand.nextInt(3) - 1;
				int z2 = tilePos.z() + rand.nextInt(3) - 1;
				if (world.isAirBlock(queryPos.set(x2, randY, z2)) && this.canStay(world, queryPos)) {
					world.setBlockTypeNotify(queryPos.set(x2, randY, z2), this.block);
				}
			}
		}
		TilePos belowPos = tilePos.down(new TilePos());
		if (world.getBlockType(belowPos)!=null && world.getBlockType(belowPos).equals(Blocks.COBBLE_STONE_MOSSY)) {
			if (rand.nextInt(2)==0) {
				BlockLogicMushroom that = (BlockLogicMushroom)(Object)this;
				that.updateTick(world, tilePos, rand, false);
			}
		}
	}

	@Override
	protected boolean mayPlaceOn(Block<?> block) {
		return block.solid();
	}

	@Override
	public boolean canStay(World world, TilePosc tilePos) {
		if (tilePos.y() >= 0 && tilePos.y() < world.getHeightBlocks()) {
			return this.mayPlaceOn(world.getBlockType(tilePos.down(new TilePos())));
		} else {
			return false;
		}
	}

	@Override
	public boolean onBonemealUsed(ItemStack itemstack, @Nullable Player player, World world, TilePosc tilePos, Side side, double xPlaced, double yPlaced) {
		Random rand = world.rand;
		if (!world.isClientSide && this.canBeBonemealed) {
			if (player == null || player.getGamemode().hasBlockConsumption()) {
				--itemstack.stackSize;
			}

			TilePos queryPos = new TilePos();
			label38:
			for(int j1 = 0; j1 < 32; ++j1) {
				int _x = tilePos.x();
				int _y = tilePos.y();
				int _z = tilePos.z();

				for(int j2 = 0; j2 < j1 / 16; ++j2) {
					_x += rand.nextInt(3) - 1;
					_y += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
					_z += rand.nextInt(3) - 1;
					if (!this.mayPlaceOn(world.getBlockType(queryPos.set(_x, _y - 1, _z)))) {
						continue label38;
					}
				}

				if (world.getBlockType(queryPos.set(_x, _y, _z)) == Blocks.AIR && (double)rand.nextFloat() > 0.85) {
					world.setBlockTypeNotify(queryPos.set(_x, _y, _z), this.block);
				}
			}

			return true;
		} else {
			return this.canBeBonemealed;
		}
	}
}
