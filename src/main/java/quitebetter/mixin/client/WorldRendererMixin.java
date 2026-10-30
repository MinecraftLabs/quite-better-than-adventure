package quitebetter.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.enums.HumanArmorShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import quitebetter.core.item.ModItems;
import quitebetter.core.util.PlayerUtil;

@Mixin(
	value = {WorldRenderer.class},
	remap = false
)
public class WorldRendererMixin {
	@Shadow
	public Minecraft mc;

	@ModifyExpressionValue(
		method = "getMouseOver",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/player/gamemode/Gamemode;getBlockReachDistance()F"
		)
	)
	private float addGloveReach(float original) {
		if (PlayerUtil.isArmorItemThis(mc.thePlayer, HumanArmorShape.CHEST, ModItems.GLOVE_LEATHER)) {
			return original + 3F;
		}
		return original;
	}
}
