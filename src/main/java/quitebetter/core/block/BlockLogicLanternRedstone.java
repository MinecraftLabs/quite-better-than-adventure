package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;

import java.util.Random;

public class BlockLogicLanternRedstone extends BlockLogicLanternCoal {

	public BlockLogicLanternRedstone(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void animationTick(World world, TilePosc tilePos, Random rand) {
		double xPos = (double)tilePos.x() + 0.5;
		double yPos = (double)tilePos.y() + 0.5;
		double zPos = (double)tilePos.z() + 0.5;
		world.spawnParticle("reddust", xPos, yPos, zPos, 0.0, 0.0, 0.0, 10, false);
	}

	@Override
	public boolean isEmittingSignal(WorldSource worldSource, TilePosc tilePos, Side side) {
		return true;
	}

	@Override
	public boolean isSignalSource() {
		return true;
	}
}
