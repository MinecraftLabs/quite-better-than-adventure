package quitebetter.core.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import quitebetter.core.block.BlockLogicPedestal;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.Nullable;

public class TileEntityPedestal extends TileEntity  {
	public @Nullable ItemStack displayItem;
	public int ticksRan = 0;

	@Override
	public void tick() {
		if (this.ticksRan == 0) {
			this.ticksRan = this.worldObj.rand.nextInt(360);
		}

		++this.ticksRan;
	}

	public boolean setDisplayItem(Player player, @Nullable ItemStack stack) {
		boolean success = false;
		boolean wasNull = this.displayItem == null;
		if (this.displayItem != null && player.getGamemode().hasBlockConsumption()) {
			player.inventory.insertItem(this.displayItem, true);
			if (this.displayItem.stackSize > 0) {
				player.dropPlayerItem(this.displayItem);
			}

			success = true;
		}

		if (stack != null) {
			stack.consumeItem(player);
			this.displayItem = new ItemStack(stack.getItem(), 1, stack.getMetadata(), stack.getData());
			this.setChanged();
			BlockLogicPedestal.updateNeighbours(this.worldObj, this.tilePos, this.getBlock());
			return true;
		} else {
			this.displayItem = null;
			this.setChanged();
			BlockLogicPedestal.updateNeighbours(this.worldObj, this.tilePos, this.getBlock());
			return success | !wasNull;
		}
	}

	@Override
	public void readAdditionalData(CompoundTag compoundTag) {
		if (compoundTag.getBoolean("hasItem")) {
			this.displayItem = ItemStack.readItemStackFromNbt(compoundTag.getCompound("item"));
		} else {
			this.displayItem = null;
		}

	}

	@Override
	public void writeAdditionalData(CompoundTag compoundTag) {
		if (this.displayItem != null) {
			compoundTag.putCompound("item", this.displayItem.writeToNBT(new CompoundTag()));
			compoundTag.putBoolean("hasItem", true);
		}

	}

	@Override
	public void dropContents(World world, int x, int y, int z) {
		super.dropContents(world, x, y, z);
		if (this.displayItem != null) {
			EntityItem item = world.dropItem(new TilePos(x, y, z), this.displayItem);
			item.xd *= (double)0.5F;
			item.yd *= (double)0.5F;
			item.zd *= (double)0.5F;
			item.pickupDelay = 0;
		}

	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
