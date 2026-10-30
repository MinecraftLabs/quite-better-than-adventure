package quitebetter.core.item;


import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.piston.BlockLogicPistonBase;
import net.minecraft.core.block.piston.PistonCommon;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.block.BlockLogicAxisAlignedPainted;
import quitebetter.core.util.BlockUtil;

import java.util.Arrays;
import java.util.Random;


public class ItemWrench extends Item {

	static final Direction[] ZAxisDirections = {
		Direction.UP,
		Direction.EAST,
		Direction.DOWN,
		Direction.WEST
	};
	static final Direction[] XAxisDirections = {
		Direction.UP,
		Direction.NORTH,
		Direction.DOWN,
		Direction.SOUTH
	};
	static final Direction[] YAxisDirections = {
		Direction.NORTH,
		Direction.EAST,
		Direction.SOUTH,
		Direction.WEST
	};
	static final BlockUtil.Stairs.StairsDirection[] XAxisStairsDirections = {
		new BlockUtil.Stairs.StairsDirection(Direction.NORTH,Direction.DOWN),
		new BlockUtil.Stairs.StairsDirection(Direction.NORTH,Direction.UP),
		new BlockUtil.Stairs.StairsDirection(Direction.SOUTH,Direction.UP),
		new BlockUtil.Stairs.StairsDirection(Direction.SOUTH,Direction.DOWN)
	};
	static final BlockUtil.Stairs.StairsDirection[] ZAxisStairsDirections = {
		new BlockUtil.Stairs.StairsDirection(Direction.WEST,Direction.DOWN),
		new BlockUtil.Stairs.StairsDirection(Direction.EAST,Direction.DOWN),
		new BlockUtil.Stairs.StairsDirection(Direction.EAST,Direction.UP),
		new BlockUtil.Stairs.StairsDirection(Direction.WEST,Direction.UP)
	};

	static Direction rotateRotatable(Direction dir, Side side) {
		Axis dax = dir.axis();
		Axis sax = side.axis();
		if (dax!=sax && sax.equals(Axis.Y)) {
			return YAxisDirections[Arrays.asList(YAxisDirections).indexOf(dir)+1&3];
		}
		return dir;
	}

	static Direction rotateVeryRotatable(Direction dir, Side side) {
		Axis dax = dir.axis();
		Axis sax = side.axis();
		if (dax!=sax) {
			switch (sax) {
				case X:
					return XAxisDirections[Arrays.asList(XAxisDirections).indexOf(dir)+1&3];
				case Y:
					return YAxisDirections[Arrays.asList(YAxisDirections).indexOf(dir)+1&3];
				case Z:
					return ZAxisDirections[Arrays.asList(ZAxisDirections).indexOf(dir)+1&3];
				default:
					break;
			}
		}
		return dir;
	}

	static Axis rotateAxisAligned(Axis axis, Side side) {
		Axis sax = side.axis();
		if (axis!=sax) {
			switch (sax) {
				case X:
					return axis.equals(Axis.Y) ? Axis.Z : Axis.Y;
				case Y:
					return axis.equals(Axis.X) ? Axis.Z : Axis.X;
				case Z:
					return axis.equals(Axis.Y) ? Axis.X : Axis.Y;
				default:
					break;
			}
		}
		return axis;
	}

	static int rotateStairs(int meta, Side side) {
		BlockUtil.Stairs.StairsDirection dir = BlockUtil.Stairs.getDirection(meta);
		Axis sax = side.axis();
		Axis hdax = dir.hDir().axis();
		BlockUtil.Stairs.StairsDirection sdir = null;
		if (sax!=hdax) {
			switch (sax) {
				case X:
					dir = XAxisStairsDirections[Arrays.asList(XAxisStairsDirections).indexOf(dir)+1&3];
					break;
				case Y:
					dir = new BlockUtil.Stairs.StairsDirection(
						YAxisDirections[Arrays.asList(YAxisDirections).indexOf(dir.hDir())+1&3],
						dir.vDir()
					);
					break;
				case Z:
					dir = ZAxisStairsDirections[Arrays.asList(ZAxisStairsDirections).indexOf(dir)+1&3];
					break;
				default:
					break;
			}
		}
		meta = BlockUtil.Stairs.getMeta(dir);
		return meta;
	}

	public ItemWrench(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	public boolean rotateBlock(World world, TilePosc tilePos, Side side) {
		boolean success = false;

		Block<?> block = world.getBlockType(tilePos);
		BlockLogic logic = block.getLogic();
		int meta = world.getBlockData(tilePos);

		if (logic instanceof BlockLogicRotatable) {
			world.setBlockDataNotify(tilePos,
				BlockLogicRotatable.setDirection(meta,
					rotateRotatable(BlockLogicRotatable.getDirectionFromMeta(meta), side)
				)
			);
			success = true;
		} else if (logic instanceof BlockLogicAxisAlignedPainted painted) {
			world.setBlockDataNotify(tilePos,
				painted.stripAxisFromMetadata(meta) |
					BlockLogicAxisAligned.axisToMeta(
						rotateAxisAligned(BlockLogicAxisAlignedPainted.metaToAxis(meta), side)
					)
			);
			success = true;
		} else if (logic instanceof BlockLogicVeryRotatable) {
			world.setBlockDataNotify(tilePos,
				BlockLogicVeryRotatable.setDirection(meta,
					rotateVeryRotatable(BlockLogicVeryRotatable.metaToDirection(meta), side)
				)
			);
			success = true;
		} else if (logic instanceof BlockLogicAxisAligned) {
			world.setBlockDataNotify(tilePos,
				BlockLogicAxisAligned.axisToMeta(
					rotateAxisAligned(BlockLogicAxisAligned.metaToAxis(meta), side)
				)
			);
			success = true;
		} else if (logic instanceof BlockLogicStairsPainted) {
			world.setBlockDataNotify(tilePos,
				(meta & 240) | rotateStairs(meta & 15, side)
			);
			success = true;
		} else if (logic instanceof BlockLogicStairs) {
			world.setBlockDataNotify(tilePos,
				rotateStairs(meta,side)
			);
			success = true;
		} else if (logic instanceof BlockLogicPistonBase) {
			int data = PistonCommon.fixLegacyBaseData(world, tilePos);
			if (!PistonCommon.IS_EXTENDED.bool(data)) {
				world.setBlockDataNotify(tilePos, PistonCommon.DIRECTION.set(data, rotateVeryRotatable(Direction.fromId(PistonCommon.DIRECTION.get(data)), side).id));
			}
			success = true;
		}
		if (success) {
			Random random = world.rand;
			world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(), "random.door_open", 0.25F, 3F + (random.nextFloat() - random.nextFloat()) * 0.4F);
		}
		return success;
	}

	@Override
	public boolean onUseOnBlock(ItemStack itemstack, World world, @Nullable Player player, TilePosc blockPos, Side side, double xPlaced, double yPlaced) {
		return rotateBlock(world, blockPos, side);
	}

	@Override
	public void onUseByActivator(ItemStack itemStack, World world, TileEntityActivator activator, Random random, TilePosc blockPos, Direction direction, double offX, double offY, double offZ) {
		rotateBlock(world, blockPos.add(direction, new TilePos()), direction.side().opposite());
	}
}
