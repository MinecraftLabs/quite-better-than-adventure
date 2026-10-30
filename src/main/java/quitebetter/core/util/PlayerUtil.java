package quitebetter.core.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerInventory;

public class PlayerUtil {
	public static boolean hasItemInHotbar(Player player, Item item) {
		for (int i=0; i<ContainerInventory.HOTBAR_SIZE; i++) {
			if (player.inventory.getItem(i)!=null && player.inventory.getItem(i).getItem().equals(item)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasItemOutOfHotbar(Player player, Item item) {
		for (int i=ContainerInventory.HOTBAR_SIZE; i<ContainerInventory.MAIN_INVENTORY_SIZE; i++) {
			if (player.inventory.getItem(i)!=null && player.inventory.getItem(i).getItem().equals(item)) {
				return true;
			}
		}
		return false;
	}

	public static ItemStack getQuiver(Player player) {
		ItemStack quiverSlot = player.inventory.armorItemInSlot(HumanArmorShape.CHEST);
		if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER_GOLD.id && quiverSlot.getMetadata() < quiverSlot.getMaxDamage()) {
			return quiverSlot;
		} else if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER.id && quiverSlot.getMetadata() < quiverSlot.getMaxDamage()) {
			return quiverSlot;
		}
		return null;
	}

	public static boolean consumeQuiverAmmo(Player player) {
		ItemStack quiver = getQuiver(player);
		if (quiver!=null) {
			if (quiver.getItem().equals(Items.ARMOR_QUIVER)) { quiver.damageItem(1, player); }
			return true;
		}
		return false;
	}

	public static void consumeBowAmmo(Player player) {
		Item item = player.getNextArrow();
		if (item == null) {
			return;
		}
		if (hasItemInHotbar(player, item) || !consumeQuiverAmmo(player)) {
			player.inventory.consumeInventoryItem(item.id);
		}
	}

	public static Integer findSlotInInventory(Player player, Item item, int meta) {
		ContainerInventory inv = player.inventory;
		for(int i = 0; i < inv.mainInventory.length; ++i) {
			ItemStack slot = player.inventory.getItem(i);
			if (slot!=null && slot.getItem().equals(item) && (!slot.getItem().getHasSubtypes() || slot.getMetadata() == meta)) {
				return i;
			}
		}
		return null;
	}
}
