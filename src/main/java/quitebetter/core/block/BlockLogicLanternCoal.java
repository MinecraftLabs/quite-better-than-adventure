package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockLogicLanternCoal extends BlockLogic {

	public BlockLogicLanternCoal(Block<?> block, Material material) {
		super(block, material);
		this.setBlockBounds(0.2F,0F,0.2F,0.8F,1F,0.8F);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public boolean canStay(World world, TilePosc tilePos) {
		return !world.isAirBlock(tilePos.up(new TilePos())) || !world.isAirBlock(tilePos.down(new TilePos()));
	}

	@Override
	public boolean canPlaceAt(World world, TilePosc tilePos) {
		return super.canPlaceAt(world, tilePos) && this.canStay(world, tilePos);
	}

	@Override
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block) {
		super.onNeighborChanged(world, tilePos, block);
		if (!this.canStay(world, tilePos)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, world.getBlockData(tilePos), null, null);
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
		}
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(this.block)};
	}

	@Override
	public int getPistonPushReaction(World world, TilePosc tilePos) {
		return Material.PISTON_DESTROY_ON_PUSH;
	}

	@Override
	public void animationTick(World world, TilePosc tilePos, Random rand) {
		double xPos = (double)tilePos.x() + 0.5;
		double yPos = (double)tilePos.y() + 0.6;
		double zPos = (double)tilePos.z() + 0.5;
		world.spawnParticle("smoke", xPos, yPos, zPos, 0.0, 0.0, 0.0, 0, false);
		world.spawnParticle("flame", xPos, yPos, zPos, 0.0, 0.0, 0.0, 0, false);
	}
}
