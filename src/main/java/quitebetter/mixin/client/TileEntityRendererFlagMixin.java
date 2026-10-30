package quitebetter.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRendererFlag;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
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
public class TileEntityRendererFlagMixin {
	@Unique
	private static final float rotationValue = 15F;
	@Unique
	private static final float pushValue = 0.4F;

	@Unique
	private static Direction wallDirection(TileEntityFlag flag) {
		if (flag.worldObj == null) {
			return null;
		}
		Direction dir = Direction.fromId(flag.worldObj.getBlockData(flag.tilePos));
		return dir.isHorizontal() ? dir : null;
	}

	@ModifyVariable(
		method = "doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	private boolean modifyShortPole(boolean shortPole, TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPoleArg, float windDirection, float windIntensity) {
		if (wallDirection(tileEntityFlag) != null) {
			return false;
		}
		if (tileEntityFlag.worldObj == null) {
			return shortPole;
		}
		return shortPole && tileEntityFlag.worldObj.getBlockData(tileEntityFlag.tilePos.down(new TilePos())) == 0;
	}

	@ModifyVariable(
		method = "doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 1
	)
	private float modifyWindDirection(float windDirection, TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPole, float windDirectionArg, float windIntensity) {
		Direction dir = wallDirection(tileEntityFlag);
		if (dir == null) {
			return windDirection;
		}
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
		return angle * 90;
	}

	@Inject(
		method = {"doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V"},
		at = @At(
			value = "INVOKE",
			target = "Lorg/joml/Matrix4f;scale(FFF)Lorg/joml/Matrix4f;",
			shift = At.Shift.BEFORE
		)
	)
	private void applyWallTransform(TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPole, float windDirection, float windIntensity, CallbackInfo ci) {
		Direction dir = wallDirection(tileEntityFlag);
		if (dir != null) {
			GLRenderer.modelM4f().translate(-dir.offsetX()*pushValue, 0.75F, -dir.offsetZ()*pushValue);
			GLRenderer.modelM4f().rotate(rotationValue * MathHelper.DEG_TO_RAD, dir.offsetZ(), 0, -dir.offsetX());
			GLRenderer.modelM4f().translate(0, -0.75F, 0);
		}
	}

	@WrapOperation(
		method = {"doRender(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/block/entity/TileEntityFlag;DDDFZFF)V"},
		at = @At(
			value = "INVOKE",
			target = "Lorg/useless/dragonfly/models/entity/StaticEntityModel;render()V",
			ordinal = 0
		)
	)
	private void renderPole(StaticEntityModel model, Operation<Void> original, TessellatorGeneral tessellator, TileEntityFlag tileEntityFlag, double x, double y, double z, float partialTick, boolean shortPole, float windDirection, float windIntensity) {
		if (wallDirection(tileEntityFlag) != null) {
			model.getTransform("base").visible = false;
			original.call(model);
			model.getTransform("base").visible = true;
		} else {
			original.call(model);
		}
	}
}
