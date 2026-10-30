package quitebetter.mixin.core.item;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemSlimeball;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
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
import quitebetter.core.tileentity.TileEntityCrate;
import quitebetter.core.tileentity.TileEntityGlued;

import java.util.Random;

@Mixin(
	value = {ItemSlimeball.class},
	remap = false
)
public class ItemSlimeballMixin {
	@Inject(
		method = {"onUseOnBlock"},
		at = @At("RETURN")
	)
	public void onUseOnBlock(ItemStack itemstack, World world, @Nullable Player entityplayer, TilePosc blockPos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		TileEntity tile = world.getTileEntity(blockPos);
		if (tile instanceof TileEntityGlued || tile instanceof TileEntityCrate) {
			if (entityplayer != null && entityplayer.getCurrentEquippedItem()!=null && entityplayer.getCurrentEquippedItem().getItem().equals(Items.SLIMEBALL)) {
				((TileEntityGlued) tile).applyGlue();
			}
		}
	}

	@Inject(
		method = {"onUseByActivator"},
		at = @At("RETURN")
	)
	public void onUseByActivator(ItemStack itemStack, World world, TileEntityActivator activator, Random random, TilePosc blockPos, Direction direction, double offX, double offY, double offZ, CallbackInfo ci) {
		TileEntity tile = world.getTileEntity(blockPos.add(direction, new TilePos()));
		if (tile instanceof TileEntityGlued || tile instanceof TileEntityCrate) {
			if (activator.getItem(activator.stackSelector)!=null && activator.getItem(activator.stackSelector).getItem().equals(Items.SLIMEBALL)) {
				((TileEntityGlued) tile).applyGlue();
			}
		}
	}
}
