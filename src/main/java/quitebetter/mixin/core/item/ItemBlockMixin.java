package quitebetter.mixin.core.item;

import quitebetter.core.block.BlockLogicFan;
import quitebetter.core.block.ModBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(
	value = {ItemBlock.class},
	remap = false
)
public class ItemBlockMixin {
	@Inject(
		method = {"onUseOnBlock"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void onUseOnBlock(ItemStack itemstack, World world, @Nullable Player entityplayer, TilePosc blockPos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		Block<?> block = world.getBlockType(blockPos);
		if (block!=null) {
			if (itemstack.getItem().equals(Blocks.COBBLE_NETHERRACK.asItem())) {
				if (block.equals(ModBlocks.FAN)) {
					BlockLogicFan logic = (BlockLogicFan) block.getLogic();
					logic.overrideSignal(world, blockPos);
					logic.setActive(world, blockPos, true);
					cir.setReturnValue(true);
				}
			} else if (itemstack.getItem().equals(Blocks.TORCH_REDSTONE_ACTIVE.asItem()) && (entityplayer == null || !entityplayer.isSneaking())) {
				if (BlockLogicFan.isFan(block)) {
					BlockLogicFan logic = (BlockLogicFan) block.getLogic();
					logic.toggleInverted(world, blockPos);
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(
		method = {"onUseByActivator"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void onUseByActivator(ItemStack itemStack, World world, TileEntityActivator activator, Random random, TilePosc blockPos, Direction direction, double offX, double offY, double offZ, CallbackInfo cir) {
		TilePos targetPos = blockPos.add(direction, new TilePos());
		Block<?> block = world.getBlockType(targetPos);
		if (itemStack.getItem().equals(Blocks.TORCH_REDSTONE_ACTIVE.asItem())) {
			if (BlockLogicFan.isFan(block)) {
				BlockLogicFan logic = (BlockLogicFan) block.getLogic();
				logic.toggleInverted(world, targetPos);
				cir.cancel();
			}
		}
	}
}
