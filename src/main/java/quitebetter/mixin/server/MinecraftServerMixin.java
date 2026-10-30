package quitebetter.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.ModCore;

@Mixin(
	value = {MinecraftServer.class},
	remap = false
)
public class MinecraftServerMixin {
	@Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/achievement/stat/StatList;init()V"))
	public void initStats(CallbackInfoReturnable<Boolean> cir) {
		ModCore.onStatInitialize();
	}
}
