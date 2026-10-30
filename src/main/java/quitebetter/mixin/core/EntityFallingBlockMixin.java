package quitebetter.mixin.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.EntityFallingBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quitebetter.core.util.IFalling;

@Mixin(
	value = {EntityFallingBlock.class},
	remap = false
)
public class EntityFallingBlockMixin {
	@Inject(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/block/Block;disableNormalEntityLogic:Z",
			opcode = Opcodes.PUTSTATIC,
			ordinal = 0
		),
		cancellable = true
	)
	private void tick(CallbackInfo ci) {
		EntityFallingBlock ent = (EntityFallingBlock)(Object)this;
		Block<?> block = ent.carriedBlock.block();
		if (block.getLogic() instanceof IFalling falling) {
			if (!falling.FellOnGround(ent, ent.tilePosApprox())) {
				ci.cancel();
			}
		}
	}
}
