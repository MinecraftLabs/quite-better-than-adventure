package quitebetter.mixin.core.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFlag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
	value = {ItemFlag.class},
	remap = false
)
public class ItemFlagMixin {
	@Inject(
		method = {"onUseOnBlock"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/world/World;setBlockType(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/block/Block;)Z",
			shift = At.Shift.AFTER
		)
	)
	private void onUseOnBlockDirection(ItemStack stack, World world, @Nullable Player entityplayer, TilePosc blockPos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		if (world.getBlockType(blockPos)!=null && world.getBlockType(blockPos).equals(Blocks.FLAG)) {
			if ( side.isHorizontal()) {
				world.setBlockData(blockPos, side.direction().id);
			}
		}
	}
}
