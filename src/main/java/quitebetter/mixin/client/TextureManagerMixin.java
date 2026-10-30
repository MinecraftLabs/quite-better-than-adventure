package quitebetter.mixin.client;

import net.minecraft.client.option.enums.TextureAnimation;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.dynamictexture.DynamicTexture;
import net.minecraft.client.render.dynamictexture.DynamicTextureCustom;
import net.minecraft.client.render.texture.meta.AnimationProperties;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static quitebetter.core.ModCore.MOD_ID;

@Mixin(
	value = {TextureManager.class},
	remap = false
)
public abstract class TextureManagerMixin {

	@Shadow
	protected abstract void addDynamicTexture(DynamicTexture texture);

	@Inject(
		method = "addNativeDynamicTextures",
		at = @At("HEAD")
	)
	public void addNativeDynamicTextures(TextureAnimation state, CallbackInfo ci) {
		if (state != TextureAnimation.OFF) {
			//FAN
			String[] activesides = new String[]{"top", "side", "side_inverted"};
			for(String side : activesides) {
				IconCoordinate texture = TextureRegistry.getTexture(MOD_ID + ":block/fan/active_" + side);
				this.addDynamicTexture(new DynamicTextureCustom(texture, texture.getMeta("animation", AnimationProperties.class)));
			}
		}
	}
}
