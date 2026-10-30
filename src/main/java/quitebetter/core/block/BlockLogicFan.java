package quitebetter.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.tileentity.TileEntityCrate;
import quitebetter.core.tileentity.TileEntityFan;
import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BlockLogicFan extends BlockLogicVeryRotatable {
	public static final int MASK_DIRECTION = 0b00000111;
	public static final int MASK_SIGNALOVERRIDE = 0b00001000;
	public boolean isActive;
	public boolean isInverted;

	private int setbit(int data, int pos) { pos -= 1; data = data | (1 << pos); data |= 1 << pos; return data;}
	private int unsetbit(int data, int pos) { pos -= 1; data = data & ~(1 << pos); data &= ~(1 << pos); return data;}

	public boolean isSignalOverriden(World world, TilePosc tilePos) {
		return (world.getBlockData(tilePos) & MASK_SIGNALOVERRIDE) != 0;
	}

	public void overrideSignal(World world, TilePosc tilePos) {
		world.setBlockDataNotify(tilePos,
			this.setbit(world.getBlockData(tilePos),4)
		);
	}

	public static boolean isInverted(Block fan) {
		return fan.equals(ModBlocks.IN_FAN) || fan.equals(ModBlocks.ACTIVE_IN_FAN);
	}

	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(ModBlocks.FAN)} : null;
	}

	public static void toggleInverted(World world, TilePosc tilePos) {
		Block<?> fan = world.getBlockType(tilePos);
		if (fan != null) {
			BlockLogicFan logic = (BlockLogicFan) fan.getLogic();
			world.setBlockTypeDataNotify(tilePos,
				(!isInverted(fan) ? (logic.isActive ? ModBlocks.ACTIVE_IN_FAN : ModBlocks.IN_FAN) : (logic.isActive ? ModBlocks.ACTIVE_FAN : ModBlocks.FAN)),
				world.getBlockData(tilePos));
		}
	}

	public static boolean isFan(Block block) {
		Block[] fans = {ModBlocks.FAN,ModBlocks.ACTIVE_FAN,ModBlocks.IN_FAN,ModBlocks.ACTIVE_IN_FAN};
		return block != null && Arrays.asList(fans).contains(block);
	}

	public BlockLogicFan(Block<?> block, Material material, boolean isActive, boolean isIn) {
		super(block, material);
		this.isActive = isActive;
		this.isInverted = isIn;
		block.withEntity(TileEntityFan::new);
	}

	private int getSightRange(World world, TilePosc tilePos, Direction facing) {
		if (facing == Direction.NONE) {
			return 0;
		} else {
			int range = 4;
			TilePos queryPos = new TilePos();
			for(int i = 1; i <= range; ++i) {
				int x1 = MathHelper.round(tilePos.x() + (double)(facing.offsetX() * i));
				int y1 = MathHelper.round(tilePos.y() + (double)(facing.offsetY() * i));
				int z1 = MathHelper.round(tilePos.z() + (double)(facing.offsetZ() * i));
				int id = world.getBlockType(queryPos.set(x1, y1, z1)).id();
				if (Blocks.solid[id]) {
					return i - 1;
				}
			}
			return range;
		}
	}

	@Override
	public void animationTick(World world, TilePosc tilePos, Random rand) {
		if (this.isActive) {
			if (this.isModifierFire(world,tilePos)) {
				Direction dir = getDirection(world.getBlockData(tilePos));
				int len = this.getSightRange(world, tilePos, dir);
				for (int i=1;i<5;i++) {
					double xPos = (double) tilePos.x() + (double) rand.nextFloat();
					double yPos = (double) tilePos.y() + (double) rand.nextFloat() * (double) 0.5F;
					double zPos = (double) tilePos.z() + (double) rand.nextFloat();
					world.spawnParticle("flame", xPos + dir.offsetX()*getSightRange(world,tilePos,dir)*rand.nextFloat(), yPos + dir.offsetY()*getSightRange(world,tilePos,dir)*rand.nextFloat(), zPos + dir.offsetZ()*getSightRange(world,tilePos,dir)*rand.nextFloat(), 0, 0, 0, 0, false);
				}
			} else {
				Direction dir = getDirection(world.getBlockData(tilePos));
				int len = this.getSightRange(world, tilePos, dir);
				double xPos = (double) tilePos.x() + (double) rand.nextFloat();
				double yPos = (double) tilePos.y() + (double) rand.nextFloat() * (double) 0.5F;
				double zPos = (double) tilePos.z() + (double) rand.nextFloat();
				world.spawnParticle("smoke", xPos + dir.offsetX() * 1.3, yPos + dir.offsetY() * 1.3, zPos + dir.offsetZ() * 1.3, 0, 0, 0, 0, false);
			}
		}
	}


	public void setActive(World world, TilePosc tilePos, boolean isActive) {
		world.setBlockTypeDataNotify(tilePos,(( isSignalOverriden(world, tilePos) || isActive ) ? (isInverted(block) ? ModBlocks.ACTIVE_IN_FAN : ModBlocks.ACTIVE_FAN) : (isInverted(block) ? ModBlocks.IN_FAN : ModBlocks.FAN)), world.getBlockData(tilePos));
		if (isActive) {
			world.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(),"fire.ignite", 0.1F, 2F);
		}
	}

	public static Direction getDirection(int data) {
		data = data & MASK_DIRECTION;
		if (data==0) {
			return Direction.UP;
		} else if (data==1) {
			return Direction.DOWN;
		}
		return Direction.fromId(data);
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		this.setActive(world, tilePos, this.checkIfActive(world, tilePos));
	}

	public boolean checkIfActive(World world, TilePosc tilePos) {
		Direction direction = getDirection(world.getBlockData(tilePos));
		TilePos queryPos = new TilePos();
		if (direction != Direction.DOWN && world.hasSignal(tilePos.down(queryPos), Side.BOTTOM)) {
			return true;
		} else if (direction != Direction.UP && world.hasSignal(tilePos.up(queryPos), Side.TOP)) {
			return true;
		} else if (direction != Direction.NORTH && world.hasSignal(tilePos.add(0, 0, -1, queryPos), Side.NORTH)) {
			return true;
		} else if (direction != Direction.SOUTH && world.hasSignal(tilePos.add(0, 0, 1, queryPos), Side.SOUTH)) {
			return true;
		} else if (direction != Direction.EAST && world.hasSignal(tilePos.add(1, 0, 0, queryPos), Side.EAST)) {
			return true;
		} else if (direction != Direction.WEST && world.hasSignal(tilePos.add(-1, 0, 0, queryPos), Side.WEST)) {
			return true;
		} else if (world.hasSignal(tilePos, Side.BOTTOM)) {
			return true;
		} else if (direction != Direction.UP && world.hasSignal(tilePos.add(0, 2, 0, queryPos), Side.TOP)) {
			return true;
		} else if (world.hasSignal(tilePos.add(0, 1, -1, queryPos), Side.NORTH)) {
			return true;
		} else if (world.hasSignal(tilePos.add(0, 1, 1, queryPos), Side.SOUTH)) {
			return true;
		} else {
			return world.hasSignal(tilePos.add(-1, 1, 0, queryPos), Side.WEST) ? true : world.hasSignal(tilePos.add(1, 1, 0, queryPos), Side.EAST);
		}
	}

	@Override
	public void onPlacedOnSide(World world, TilePosc tilePos, @NotNull Side side, double xPlaced, double yPlaced) {
		if (side.direction() == Direction.UP) {
			world.setBlockDataNotify(tilePos, 0);
		} else if (side.direction() == Direction.DOWN) {
			world.setBlockDataNotify(tilePos, 1);
		} else {
			world.setBlockDataNotify(tilePos, setDirection(0, side.direction()));
		}
		this.setActive(world, tilePos, this.checkIfActive(world, tilePos));
	}

	@Override
	public void onPlacedByMob(World world, TilePosc tilePos, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		Direction direction = mob.getPlacementDirection(side).opposite();
		if (direction == Direction.UP) {
			world.setBlockDataNotify(tilePos, 0);
		} else if (direction == Direction.DOWN) {
			world.setBlockDataNotify(tilePos, 1);
		} else {
			world.setBlockDataNotify(tilePos, setDirection(0, direction));
		}
		this.setActive(world, tilePos, this.checkIfActive(world, tilePos));
	}

	public static int setDirection(int meta, Direction direction) {
		return meta & -8 | direction.id & 7;
	}

	public Block getModifierBlock(World world, TilePosc tilePos) {
		Direction dir = BlockLogicFan.getDirection(world.getBlockData(tilePos));
		return world.getBlockType(tilePos.add(dir, new TilePos()));
	}

	public boolean isModifierFire(World world, TilePosc tilePos) {
		Block block = getModifierBlock(world, tilePos);
		return this.getModifierBlock(world, tilePos) != null && (this.getModifierBlock(world, tilePos).equals(Blocks.FIRE) || this.getModifierBlock(world, tilePos).equals(Blocks.BRAZIER_ACTIVE));
	}

}
