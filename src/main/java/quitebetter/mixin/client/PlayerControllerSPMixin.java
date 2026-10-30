package quitebetter.mixin.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.util.ILeftClickable;

@Mixin(
	value = {PlayerControllerSP.class},
	remap = false
)
public abstract class PlayerControllerSPMixin extends PlayerController {
	public PlayerControllerSPMixin(Minecraft minecraft) {
		super(minecraft);
	}

	@Inject(
		method = {"destroyBlock"},
		at = @At("HEAD"),
		cancellable = true
	)
	//NON-CREATIVE CASE
	public void startDestroyBlock(TilePosc tilePos, Side side, CallbackInfoReturnable<Boolean> cir) {
		World world = this.mc.currentWorld;
		Player player = this.mc.thePlayer;
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof ILeftClickable logic && logic.preventsBreaking(world,tilePos,player,side)) {
			cir.cancel();
		}
	}

}
