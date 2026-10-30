package quitebetter.mixin.client;


import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quitebetter.core.util.ILeftClickable;
import quitebetter.core.util.IRightClickable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turniplabs.halplibe.helper.EnvironmentHelper;

@Mixin(
	value = {PlayerController.class},
	remap = false
)
public class PlayerControllerMixin {
	@Final
	@Shadow
	protected Minecraft mc;

	@Inject(
		method = {"continueDestroyBlock"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void continueDestroyBlock(TilePosc tilePos, Side side, double xHit, double yHit, CallbackInfo ci) {
		World world = mc.currentWorld;
		Player player = mc.thePlayer;
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof ILeftClickable) {
			ILeftClickable logic = (ILeftClickable) block.getLogic();
			if (logic.preventsBreaking(world,tilePos,player,side)) {
				ci.cancel();
			}
		}
	}

	@Inject(
		method = {"startDestroyBlock"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/controller/PlayerController;sendStartDigPacket(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/Side;DD)V",
			shift = At.Shift.AFTER,
			ordinal = 1
		),
		cancellable = true
	)
	//NON-CREATIVE CASE
	public void startDestroyBlock(TilePosc tilePos, Side side, double xHit, double yHit, boolean repeat, CallbackInfo cir) {
		World world = mc.currentWorld;
		Player player = mc.thePlayer;
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof ILeftClickable) {
			ILeftClickable logic = (ILeftClickable) block.getLogic();
			if (logic.preventsBreaking(world,tilePos,player,side)) {
				if (EnvironmentHelper.isSingleplayerClient()) {
					logic.onBlockLeftClicked(world,tilePos,player,side);
				}
				cir.cancel();
			}
		}
	}

	@Inject(
		method = {"startDestroyBlock"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/controller/PlayerController;destroyBlock(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/Side;)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	public void startDestroyBlockWithoutAnimation(TilePosc tilePos, Side side, double xHit, double yHit, boolean repeat, CallbackInfo cir) {
		PlayerController controller = (PlayerController)(Object)this;
		World world = mc.currentWorld;
		Player player = mc.thePlayer;
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof ILeftClickable) {
			ILeftClickable logic = (ILeftClickable) block.getLogic();
			if (logic.preventsBreaking(world,tilePos,player,side)) {
				if (EnvironmentHelper.isSingleplayerClient()) {
					logic.onBlockLeftClicked(world,tilePos,player,side);
				}
				controller.sendStartDigPacket(tilePos, side, xHit, yHit);
				cir.cancel();
			}
		}
	}


	@Inject(
		method = {"placeItemStackOnTile"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void placeItemStackOnTile(Player player, World world, ItemStack itemstack, TilePosc tilePos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof IRightClickable) {
			IRightClickable logic = (IRightClickable) block.getLogic();
			if (logic.preventsInteraction(world,tilePos,player,side)) {
				logic.onBlockRightClicked(world,tilePos,player,side);
				cir.cancel();
			}
		}
	}
	@Inject(
		method = {"useOrPlaceItemStackOnTile"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void useOrPlaceItemStackOnTile(Player player, World world, ItemStack itemstack, TilePosc tilePos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof IRightClickable) {
			IRightClickable logic = (IRightClickable) block.getLogic();
			if (logic.preventsInteraction(world,tilePos,player,side)) {
				logic.onBlockRightClicked(world,tilePos,player,side);
				cir.cancel();
			}
		}
	}
}
