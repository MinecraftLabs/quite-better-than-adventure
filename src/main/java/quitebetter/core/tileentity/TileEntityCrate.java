package quitebetter.core.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.BlockParticleHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.ICarrySource;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;
import quitebetter.core.block.BlockLogicCrate;
import quitebetter.core.item.ModItems;
import quitebetter.core.util.BaseUtil;
import quitebetter.core.util.IBasketContents;
import quitebetter.core.util.PlayerUtil;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.entity.player.Player;

import java.util.*;

public class TileEntityCrate extends TileEntityGlued implements ICarrySource {
	public final static int maxSlots = 48;
	public final static int maxCrates = 8;
	public Integer maxItemCount; //Max item amount in single instance
	public Integer itemCount = 0; //Current item amount in single instance
	public Integer itemID; //Item ID
	public Integer itemMeta; //Item Meta
	public Integer itemMaxStack; //Item stack max size
	public Integer upperHeight; //Y cord of upper crate
	public Integer lowerHeight; //Y cord of lower crate
	public boolean glued = false; //Is glued

	private boolean isCrateConnectable(int yo) {
		assert worldObj != null;
		TilePos pos = new TilePos(tilePos.x, yo, tilePos.z);
		return (
			worldObj.getBlockType(pos).id() == worldObj.getBlockType(tilePos).id() &&
			worldObj.getBlockData(pos) == worldObj.getBlockData(tilePos) &&
			worldObj.getTileEntity(pos) != null &&
			!((TileEntityCrate) worldObj.getTileEntity(pos)).glued
		);
	}

	//DATA

	public boolean hasEqualDataTo(TileEntityCrate crate) {
		return this.hasItemData() &&
			this.itemID.equals(crate.itemID) &&
			this.itemMeta.equals(crate.itemMeta);
	}

	public boolean hasEqualItemDataTo(TileEntityCrate crate) {
		return this.hasItemData() &&
			this.itemID.equals(crate.itemID) &&
			this.itemMeta.equals(crate.itemMeta);
	}

	public boolean hasItemData() {
		return
			this.itemID != null &&
				this.itemMeta != null &&
				this.itemMaxStack != null;
	}

	public void passDataTo(TileEntityCrate passcrate) {
		passcrate.itemMeta = this.itemMeta;
		passcrate.itemID = this.itemID;
		passcrate.itemMaxStack = this.itemMaxStack;
		passcrate.maxItemCount = this.maxItemCount;
		passcrate.upperHeight = this.upperHeight;
		passcrate.lowerHeight = this.lowerHeight;
		this.sendPacketsForCrates();
	}

	//VISUAL

	public void emitParticles(ItemStack stack) {
		Random rand = new Random();
		if (stack != null) {
			if (stack.getItem() instanceof ItemBlock) {
				for (int j = 0; j < 5; ++j) {
					BaseUtil.spawnParticle(worldObj,
						"block",
						this.tilePos.x + rand.nextFloat(),
						this.tilePos.y + rand.nextFloat(),
						this.tilePos.z + rand.nextFloat(),
						(double) (rand.nextInt(11) - 5) / 10,
						(double) (rand.nextInt(11) - 5) / 10,
						(double) (rand.nextInt(11) - 5) / 10,
						BlockParticleHelper.encodeBlockData(stack.itemID, stack.getMetadata(), Side.NORTH));
				}
			} else {
				for (int j = 0; j < 5; ++j) {
					BaseUtil.spawnParticle(worldObj,
						"item",
						this.tilePos.x + rand.nextFloat(),
						this.tilePos.y + rand.nextFloat(),
						this.tilePos.z + rand.nextFloat(),
						(double) (rand.nextInt(11) - 5) / 10,
						(double) (rand.nextInt(11) - 5) / 10,
						(double) (rand.nextInt(11) - 5) / 10,
						stack.itemID);
				}
			}
		} else {
			for (int j = 0; j < 3; ++j) {
				BaseUtil.spawnParticle(worldObj, "smoke", this.tilePos.x + rand.nextFloat(), this.tilePos.y + rand.nextFloat(), this.tilePos.z + rand.nextFloat(), (double) (rand.nextInt(11) - 5) / 100, (double) (rand.nextInt(11) - 5) / 100, (double) (rand.nextInt(11) - 5) / 100, 0);
			}
		}
	}

	//AURAL

	public void emitSound(boolean success) {
		assert worldObj != null;
		if (success) {
			worldObj.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "random.equip", 0.5F, 2F);
			worldObj.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "step.wood", 0.5F, 0.5F);
		} else {
			worldObj.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "step.wood", 0.5F, 2F);
		}
	}

	//STORAGE DATA

	public int getFreeSpace() {
		return getFreeSpace(null);
	}

	public int getFreeSpace(@Nullable Integer reqAmount) {
		if (reqAmount == null) {
			return this.maxItemCount - this.itemCount;
		} else {
			int a = this.maxItemCount - this.itemCount - reqAmount;
			return (a > 0 ? 0 : -a);
		}
	}

	public int getOverallFreeSpace() {
		assert worldObj != null;
		int out = 0;
		this.checkStorageHeights();
		for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
			TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
			if (isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
				if (tile.hasItemData()) {
					out += tile.maxItemCount;
					out -= tile.itemCount;
				}
			}
		}
		return out;
	}

	public int getOverallMaxSpace() {
		assert worldObj != null;
		int out = 0;
		this.checkStorageHeights();
		for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
			TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
			if (isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
				if (tile.maxItemCount != null) {
					out += tile.maxItemCount;
				}
			}
		}
		return out;
	}

	public int getOverallFullness() {
		assert worldObj != null;
		int out = 0;
		this.checkStorageHeights();
		for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
			TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
			if (isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
				if (tile.itemCount != null) {
					out += tile.itemCount;
				}
			}
		}
		return out;
	}

	public Integer getCrateCount() {
		this.checkStorageHeights();
		return this.upperHeight - this.lowerHeight + 1;
	}

	public TileEntityCrate getHighestCrate() {
		assert worldObj != null;
		return (TileEntityCrate) worldObj.getTileEntity(new TilePos(tilePos.x, upperHeight, tilePos.z));
	}

	//HEIGHTS

	public void checkStorageHeights() {
		if (this.upperHeight == null) {
			this.upperHeight = this.tilePos.y;
		}
		if (this.lowerHeight == null) {
			this.lowerHeight = this.tilePos.y;
		}
	}

	public void updateStorageHeights() {
		assert worldObj != null;
		checkStorageHeights();
		int yi;
		boolean success;
		//ADDING
		yi = this.tilePos.y;
		this.upperHeight = yi;
		do {
			success = false;
			if (isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(new TilePos(tilePos.x, yi, tilePos.z));
				if (!tile.hasItemData() || tile.hasEqualDataTo(this)) {
					if (this.upperHeight - this.lowerHeight + 1 < maxCrates) {
						success = true;
					}
				}
			}
			if (success) {
				this.upperHeight = yi;
			}
			yi += 1;
		} while (success);
		yi = this.tilePos.y;
		this.lowerHeight = yi;
		do {
			success = false;
			if (isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(new TilePos(tilePos.x, yi, tilePos.z));
				if (!tile.hasItemData() || tile.hasEqualDataTo(this)) {
					if (this.upperHeight - this.lowerHeight + 1 < maxCrates) {
						success = true;
					}
				}
			}
			if (success) {
				this.lowerHeight = yi;
			}
			yi -= 1;
		} while (success);
		this.notifyCrates();
	}

	public void notifyCrates() {
		for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
			TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
			if (worldObj != null && isCrateConnectable(yi)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
				if (tile.hasEqualItemDataTo(this) || !tile.hasItemData()) {
					this.passDataTo(tile);
				}
			}
		}
		sendPacketsForCrates();
	}

	//BREAK RESULT

	public ArrayList<ItemStack> getBreakResult() {
		ArrayList<ItemStack> result = new ArrayList<>();
		if (itemID == null) {
			return result;
		}
		int maxStackSize = (new ItemStack(itemID, 1, itemMeta)).getMaxStackSize();
		int curItemCount = itemCount;
		for (int i = 1; Math.round((float) itemCount / maxStackSize) >= i; i++) {
			result.add(new ItemStack(itemID, (Math.min(curItemCount, maxStackSize)), itemMeta));
			curItemCount -= (Math.min(curItemCount, maxStackSize));
		}
		return result;
	}

	//CARRYING

	@Nullable
	@Override
	public ICarriable pickup(World world, Entity holder, TilePosc tilePos) {
		return super.pickup(world, holder, tilePos);
	}

	@Override
	public boolean tryPlace(World world, Entity holder, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		this.upperHeight = null;
		this.lowerHeight = null;
		boolean placed = super.tryPlace(world, holder, blockX, blockY, blockZ, side, xPlaced, yPlaced);
		if (placed) {
			this.setChanged();
		}
		return placed;
	}

	//INTERACTION

	public static boolean isStackPushable(ItemStack stack) {
		return stack == null || (stack.getItem().getItemStackLimit(stack) > 1 && canUseToTake(stack));
	}

	public static boolean canUseToTake(ItemStack stack) {
		Integer[] itemids = {Items.BASKET.id, ModItems.PEDESTAL.id, Items.PAINTBRUSH.id};
		return stack == null || !Arrays.asList(itemids).contains(stack.itemID) && (!(stack.getItem() instanceof ItemBlock) || !(BlockLogicCrate.isCrate(((ItemBlock) stack.getItem()).getBlock())));
	}

	public ItemStack pushStack(ItemStack stack) {
		if (stack != null && isStackPushable(stack) && (this.itemID == null || this.itemMeta == null)) {
			this.itemID = stack.itemID;
			this.itemMeta = stack.getMetadata();
			this.itemMaxStack = stack.getMaxStackSize();
			this.maxItemCount = maxSlots * this.itemMaxStack;
		}
		if (this.itemID == null || this.itemMeta == null) {
			return null;
		}
		if (stack != null && stack.itemID == this.itemID && stack.getMetadata() == this.itemMeta) {
			this.checkStorageHeights();
			boolean success = false;
			loop:
			for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
				if (worldObj != null && isCrateConnectable(yi)) {
					TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
					TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
					if (!tile.hasItemData() || tile.getFreeSpace() > 0) {
						tile.itemID = stack.itemID;
						tile.itemMeta = stack.getMetadata();
						tile.itemMaxStack = Item.getItem(tile.itemID).getDefaultStack().getMaxStackSize();
						tile.maxItemCount = maxSlots * tile.itemMaxStack;
						int countcorr = tile.getFreeSpace(stack.stackSize);
						tile.itemCount += stack.stackSize - countcorr;
						stack.stackSize = Math.max(countcorr, 0);
						success = true;
						sendTilePacket(pos);
						if (stack.stackSize > 0) {
							continue loop;
						}
					}
				}
			}
			if (success) {
				emitParticles(stack);
				emitSound(true);
			} else {
				emitParticles(null);
				emitSound(false);
			}
			sendPacketsForCrates();
			return stack;
		}
		return null;
	}

	public void pushItems(Player player) {
		ItemStack heldstack = player.getCurrentEquippedItem();
		//INTERACTION
		if (this.glued) {
			return;
		}
		if (!this.hasItemData() && heldstack != null && heldstack.getItem().equals(Items.SLIMEBALL) && player.isSneaking()) {
			Random rand = new Random();
			for (int j = 0; j < 4; ++j) {
				BaseUtil.spawnParticle(worldObj, "item", tilePos.x + rand.nextFloat(), tilePos.y + rand.nextFloat(), tilePos.z + rand.nextFloat(), (double) (rand.nextInt(11) - 5) / 10, (double) (rand.nextInt(11) - 5) / 10, (double) (rand.nextInt(11) - 5) / 10, Items.SLIMEBALL.id);
			}
			worldObj.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "mob.slimeattack", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
			this.glued = true;
			sendTilePacket(this.tilePos);
			return;
		}
		//INIT
		if (!player.isSneaking() && heldstack != null && isStackPushable(heldstack) && (this.itemID == null || this.itemMeta == null)) {
			this.itemID = heldstack.itemID;
			this.itemMeta = heldstack.getMetadata();
			this.itemMaxStack = heldstack.getMaxStackSize();
			this.maxItemCount = maxSlots * this.itemMaxStack;
		}
		if (this.itemID == null || this.itemMeta == null) {
			return;
		}
		player.swingItem();
		if (!player.isSneaking()) {
			//SINGLE STACK
			Integer index = PlayerUtil.findSlotInInventory(player, Item.getItem(this.itemID), this.itemMeta);
			if (index != null) {
				ItemStack stack = player.inventory.getItem(index);
				pushStack(player.getGamemode() == Gamemodes.CREATIVE ? stack.copy() : stack);
				if (player.getGamemode() != Gamemodes.CREATIVE) {
					PlayerUtil.inventorySetSlot(player, index, (ItemStack) (
						stack.stackSize > 0 ? stack : null
					));
				}
			}
		} else {
			Integer index;
			ItemStack stack;
			index = PlayerUtil.findSlotInInventory(player, Item.getItem(this.itemID), this.itemMeta);
			if (index != null) {
				stack = player.inventory.getItem(index);
				boolean success;
				this.checkStorageHeights();
				do {
					success = false;
					loop:
					for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
						if (worldObj != null && isCrateConnectable(yi)) {
							TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
							TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
							if (!tile.hasItemData() || tile.getFreeSpace() > 0) {
								tile.itemID = stack.itemID;
								tile.itemMeta = stack.getMetadata();
								tile.itemMaxStack = Item.getItem(tile.itemID).getDefaultStack().getMaxStackSize();
								tile.maxItemCount = maxSlots * tile.itemMaxStack;
								int countcorr = tile.getFreeSpace(stack.stackSize);
								tile.itemCount += stack.stackSize - countcorr;
								stack.stackSize = Math.max(countcorr, 0);
								if (stack.stackSize > 0) {
									continue loop;
								}
								sendTilePacket(pos);
								tile.emitParticles(stack);
								success = true;
							}
						}
					}
					PlayerUtil.inventorySetSlot(player, index, (ItemStack) (
						stack.stackSize > 0 ? stack : null
					));
					index = PlayerUtil.findSlotInInventory(player, Item.getItem(this.itemID), this.itemMeta);
					if (index != null) {
						stack = player.inventory.getItem(index);
					} else {
						stack = null;
					}
				} while (stack != null && success);
				sendPacketsForCrates();
				emitSound(true);
				player.inventory.setChanged();
				return;
			}
		}
		player.inventory.setChanged();
		sendPacketsForCrates();
		emitSound(false);
	}

	public boolean canTakeStack(ItemStack stack) {
		return this.itemID == null || this.getFreeSpace()>0 && isStackPushable(stack) && stack != null && stack.itemID == this.itemID && stack.getMetadata() == this.itemMeta;
	}

	public ItemStack takeStack() {
		this.checkStorageHeights();
		if (this.glued) {
			return null;
		}
		for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
			TilePos pos = new TilePos(tilePos.x, yi, tilePos.z);
			if (isCrateConnectable(yi) && worldObj.getTileEntity(pos) instanceof TileEntityCrate && (((TileEntityCrate) worldObj.getTileEntity(pos)).itemCount > 0)) {
				TileEntityCrate tile = (TileEntityCrate) worldObj.getTileEntity(pos);
				int maxStackSize = (new ItemStack(tile.itemID, 1, tile.itemMeta)).getMaxStackSize();
				int amount = Math.min(tile.itemCount, maxStackSize);
				tile.itemCount -= amount;
				ItemStack stack = new ItemStack(tile.itemID, amount, tile.itemMeta);
				emitParticles(stack);
				sendTilePacket(pos);
				emitSound(true);
				takeOutOfBasket();
				return stack;
			}
		}
		sendPacketsForCrates();
		emitSound(false);
		return null;
	}

	//NBT

	@Override
	public void readAdditionalData(CompoundTag compoundTag) {
		super.readAdditionalData(compoundTag);
		if (Item.getItem(compoundTag.getInteger("ItemId")) != null) {
			this.itemCount = compoundTag.getInteger("ItemCount");
			this.itemID = compoundTag.getInteger("ItemId");
			this.itemMeta = compoundTag.getInteger("ItemMeta");
			this.itemMaxStack = Item.getItem(this.itemID).getDefaultStack().getMaxStackSize();
			this.maxItemCount = maxSlots * this.itemMaxStack;
			this.upperHeight = compoundTag.getInteger("UpperHeight");
			this.lowerHeight = compoundTag.getInteger("LowerHeight");
			this.glued = compoundTag.getBoolean("Glued");
		}
	}

	@Override
	public void writeAdditionalData(CompoundTag compoundTag) {
		super.writeAdditionalData(compoundTag);
		if (this.itemCount != null) {
			compoundTag.putInt("ItemCount", this.itemCount);
		}
		if (this.itemID != null) {
			compoundTag.putInt("ItemId", this.itemID);
		}
		if (this.itemMeta != null) {
			compoundTag.putInt("ItemMeta", this.itemMeta);
		}
		if (this.upperHeight != null) {
			compoundTag.putInt("UpperHeight", this.upperHeight);
		}
		if (this.lowerHeight != null) {
			compoundTag.putInt("LowerHeight", this.lowerHeight);
		}
		compoundTag.putBoolean("Glued", this.glued);
	}

	//BASKET INTERACTION

	public void takeOutOfBasket() {
		TileEntityCrate tile = this.getHighestCrate();
		if (tile.itemID!=null && tile.getFreeSpace()<=0) {
			return;
		}
		TilePos basketPos = new TilePos(tile.tilePos.x, tile.tilePos.y + 1, tile.tilePos.z);
		if (this.worldObj.getTileEntity(basketPos) != null && this.worldObj.getTileEntity(basketPos) instanceof TileEntityBasket) {
			TileEntityBasket basket = (TileEntityBasket) this.worldObj.getTileEntity(basketPos);
			((IBasketContents) basket).quitebetter$transferContents(stack -> {
				ItemStack stackcorr = tile.pushStack(stack);
				if (stackcorr != null) {
					tile.updateStorageHeights();
				}
				return stackcorr;
			});
			tile.sendPacketsForCrates();
		}
	}

	//GLUABLE

	@Override
	public void applyGlue() {
		Random rand = new Random();
		assert worldObj != null;
		if (!this.glued) {
			this.glued = true;
			this.setChanged();

			for (int j = 0; j < 4; ++j) {
				worldObj.spawnParticle("item", tilePos.x + rand.nextFloat(), tilePos.y + rand.nextFloat(), tilePos.z + rand.nextFloat(), (double) (rand.nextInt(11) - 5) / 10, (double) (rand.nextInt(11) - 5) / 10, (double) (rand.nextInt(11) - 5) / 10, Items.SLIMEBALL.id, false);
			}
			worldObj.playSoundEffect((Entity) null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "mob.slimeattack", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
		}
	}

	//SERVER-SIDE

	public void sendTilePacket(TilePosc pos) {
		if (worldObj != null && !worldObj.isClientSide) {
			TileEntity tile = worldObj.getTileEntity(pos);
			if (tile != null) {
				tile.setChanged();
			}
		}
	}

	public void sendPacketsForCrates() {
		if (this.upperHeight!=null && this.lowerHeight!=null) {
			assert worldObj != null;
			for (int yi = this.upperHeight; yi >= this.lowerHeight; yi--) {
				sendTilePacket(new TilePos(tilePos.x, yi, tilePos.z));
			}
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
