package quitebetter.mixin.core;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quitebetter.core.tileentity.TileEntityCrate;
import quitebetter.core.util.IBasketContents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Mixin(
	value = {TileEntityBasket.class},
	remap = false
)
public class TileEntityBasketMixin implements IBasketContents {
	@Shadow
	@Final
	private Object2IntMap<TileEntityBasket.BasketEntry> contents;

	@Shadow
	private void updateNumUnits() {}

	@Override
	public void quitebetter$transferContents(UnaryOperator<ItemStack> transfer) {
		List<TileEntityBasket.BasketEntry> toRemove = new ArrayList<>();
		for (Object2IntMap.Entry<TileEntityBasket.BasketEntry> entry : this.contents.object2IntEntrySet()) {
			TileEntityBasket.BasketEntry basketEntry = entry.getKey();
			ItemStack stack = new ItemStack(basketEntry.id(), entry.getIntValue(), basketEntry.metadata(), basketEntry.tag());
			ItemStack leftover = transfer.apply(stack);
			if (leftover == null) {
				continue;
			}
			if (leftover.stackSize > 0) {
				this.contents.put(basketEntry, leftover.stackSize);
			} else {
				toRemove.add(basketEntry);
			}
		}
		for (TileEntityBasket.BasketEntry basketEntry : toRemove) {
			this.contents.removeInt(basketEntry);
		}
		this.updateNumUnits();
	}

	@Inject(
		method = {"importItemStack"},
		at = @At("HEAD"),
		cancellable = true
	)
	private void importItemStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		TileEntityBasket tile = (TileEntityBasket)(Object)this;
		World world = tile.worldObj;
		if (world == null) return;
		TilePos belowPos = new TilePos(tile.tilePos.x, tile.tilePos.y - 1, tile.tilePos.z);
		if (world.getTileEntity(belowPos) instanceof TileEntityCrate crate) {
			if (crate.canTakeStack(stack) && crate.pushStack(stack)!=null) {
				cir.setReturnValue(false);
			}
		}
	}
}
