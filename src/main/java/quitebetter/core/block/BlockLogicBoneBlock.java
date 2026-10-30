package quitebetter.core.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFallingBlock;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.util.IFalling;

import java.util.Random;

public class BlockLogicBoneBlock extends BlockLogicFallingBlock implements IFalling {
	public BlockLogicBoneBlock(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean FellOnGround(EntityFallingBlock fallingBlock, TilePosc tilePos) {
		assert fallingBlock.world!=null;
		Random rand = fallingBlock.world.rand;
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
		fallingBlock.world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(), "mob.skeletonhurt", 0.25F, 1F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
		return true;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		Random rand = world.rand;
		return dropCause == EnumDropCause.SILK_TOUCH || dropCause == EnumDropCause.PICK_BLOCK ? new ItemStack[]{new ItemStack(this.block)} : new ItemStack[]{new ItemStack(Items.BONE,rand.nextInt(4))};
	}

	@Override
	public void onDestroyedByPlayer(World world, TilePosc tilePos, Side side, int meta, Player player, Item item) {
		Random rand = world.rand;
		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, tilePos.x(), tilePos.y(), tilePos.z(), "mob.skeletonhurt", 0.5F, 0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
	}
}
