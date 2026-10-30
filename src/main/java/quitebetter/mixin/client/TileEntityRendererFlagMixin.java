package quitebetter.mixin.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FlagRenderer;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.client.render.tileentity.TileEntityRendererFlag;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.useless.dragonfly.models.entity.StaticEntityModel;

@Mixin(
	value = {TileEntityRendererFlag.class},
	remap = false
)
public abstract class TileEntityRendererFlagMixin extends TileEntityRenderer<TileEntityFlag> {
	@Shadow
	@Final
	private FlagRenderer flagRenderer;
	@Shadow
	@Final
	public Minecraft mc;
	@Shadow
	private long lastTick;

	@Unique
	private static final float rotationValue = 15F;
	@Unique
	private static final float pushValue = 0.4F;

	@ModifyVariable(
		method = "doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	private boolean modifyShortPole(boolean shortPole, TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPoleArg, float windDirection, float windIntensity) {
		World theWorld = this.mc.currentWorld;
		return shortPole && theWorld.getBlockData(tileEntityFlag.tilePos.down(new TilePos())) == 0;
	}

	@Inject(
		method = {"doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V"},
		at = @At("HEAD"),
		cancellable = true
	)
	private void doRender(TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPole, float windDirection, float windIntensity, CallbackInfo ci) {
		World theWorld = this.mc.currentWorld;
		int meta = theWorld.getBlockData(tileEntityFlag.tilePos);
		Direction dir = Direction.fromId(meta);
		if (!dir.isHorizontal()) {
			return;
		}

		if (theWorld.getWorldTime() != lastTick) {
			lastTick = theWorld.getWorldTime();
			flagRenderer.tick();
		}

		float swayInterpolated = (float)tileEntityFlag.sway + partialTick;
		float sway = (float)((double)swayInterpolated / (double)1.5F % (Math.PI * 2D));
		sway = (float)(Math.sin((double)sway) * (double)windIntensity * (double)2.0F);
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate((float)x + 0.5F - dir.offsetX()*pushValue, (float)y + 0.75F, (float)z + 0.5F - dir.offsetZ()*pushValue);
		GLRenderer.modelM4f().rotate(rotationValue * MathHelper.DEG_TO_RAD, dir.offsetZ(), 0.0F, -dir.offsetX());
		GLRenderer.modelM4f().translate(0.0F, -0.75F, 0.0F);
		GLRenderer.modelM4f().scale(0.0625F, 0.0625F, -0.0625F);
		this.bindTexture("/assets/minecraft/textures/entity/flag.png");
		StaticEntityModel flagModel = this.getModel("pole.long");
		int angle = 0;
		switch (dir) {
			case NORTH:
				angle = 3;
				break;
			case EAST:
				angle = 0;
				break;
			case SOUTH:
				angle = 1;
				break;
			case WEST:
				angle = 2;
				break;
		}
		flagModel.getTransform("base").visible = false;
		flagModel.getTransform("flag").rotY = (angle*90 + sway) * MathHelper.DEG_TO_RAD;
		flagModel.render();
		flagModel.getTransform("base").visible = true;
		flagModel.translateToBone("flag");
		this.bindTexture("/assets/minecraft/textures/entity/flag_ui.png");
		if (tileEntityFlag.getFlipped()) {
			this.getModel("cloth.mirror").render();
			GLRenderer.enableState(State.BLEND);
			GLRenderer.setBlendFunc(BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.SRC_COLOR);
			this.renderDispatcher.textureManager.bindTexture(flagRenderer.getTexture(tileEntityFlag));
			this.getModel("cloth.mirror").render();
		} else {
			this.getModel("cloth").render();
			GLRenderer.enableState(State.BLEND);
			GLRenderer.setBlendFunc(BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.SRC_COLOR);
			this.renderDispatcher.textureManager.bindTexture(flagRenderer.getTexture(tileEntityFlag));
			this.getModel("cloth").render();
		}

		GLRenderer.disableState(State.BLEND);
		GLRenderer.popFrame();

		ci.cancel();
	}
}
