package quitebetter.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievements.data.AchievementPages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quitebetter.core.ModCore;
import quitebetter.core.achivement.ModAchievements;

@Mixin(
	value = {Minecraft.class},
	remap = false
)
public class MinecraftMixin {
	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/achievement/stat/StatList;init()V"))
	public void initStats(CallbackInfo ci) {
		ModCore.onStatInitialize();
		if (!FabricLoader.getInstance().isModLoaded("stardew")) {
			AchievementPages.init();
			AchievementPages.netherPage.addAchievement(ModAchievements.LAVA_FISHING, -1, 0);
		}
	}
}
