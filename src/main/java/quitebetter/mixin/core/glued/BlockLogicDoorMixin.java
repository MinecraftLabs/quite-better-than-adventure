package quitebetter.mixin.core.glued;

import quitebetter.core.tileentity.TileEntityGlued;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(
	value = {BlockLogicDoor.class},
	remap = false
)
public class BlockLogicDoorMixin {
	@Inject(
		method = {"<init>"},
		at = @At("TAIL")
	)
	public void BlockLogicDoor(Block<?> block, Material material, boolean isTop, boolean requireTool, Supplier<Item> droppedItem, CallbackInfo ci) {
		block.withEntity(TileEntityGlued::new);
	}

	@Inject(
		method = {"onInteracted"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void onInteracted(World world, TilePosc tilePos, Player player, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> ci) {
		if (world.getTileEntity(tilePos) instanceof TileEntityGlued glued && glued.isGlued()) { ci.setReturnValue(false); }
	}
	@Inject(
		method = {"onActivatorInteracted"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void onActivatorInteracted(World world, TilePosc tilePos, TileEntityActivator activator, Direction direction, CallbackInfo ci) {
		if (world.getTileEntity(tilePos) instanceof TileEntityGlued glued && glued.isGlued()) { ci.cancel(); }
	}
	@Inject(
		method = {"onNeighborChanged"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void onNeighborChanged(World world, TilePosc tilePos, Block<?> block, CallbackInfo ci) {
		if (world.getTileEntity(tilePos) instanceof TileEntityGlued glued && glued.isGlued()) { ci.cancel(); }
	}
}
