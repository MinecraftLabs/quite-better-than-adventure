package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;

import java.util.Random;

public class BlockLogicLanternMushroom extends BlockLogicLanternCoal {

	public BlockLogicLanternMushroom(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void animationTick(World world, TilePosc tilePos, Random rand) {}
}
