package quitebetter.core.item;


import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;

public class ItemHook extends Item {


	public ItemHook(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public boolean onUseOnBlock(ItemStack itemstack, World world, @Nullable Player player, TilePosc blockPos, Side side, double xPlaced, double yPlaced) {
		Block<?> block = world.getBlockType(blockPos);
		//check if block can be climbed
		double distance = player.distanceTo(blockPos.x()+0.5, blockPos.y()+0.5, blockPos.z()+0.5);
		if (block.isCubeShaped() && side != Side.BOTTOM && side != Side.TOP &&
			blockPos.y() < player.y && player.yd < 0.1
			&& distance <= 1.8
		) {
			if (block == Blocks.WOOL) {
				player.swingItem();
				world.playSoundAtEntity(player, player, "step.cloth", 0.1F, 2F / (itemRand.nextFloat() * 0.4F + 0.8F));
				return true;
			} else {
				world.playBlockEvent(player, blockPos, 2001, block.id());
				player.yd = 0.5;
				player.fallDistance = 0;
				player.swingItem();
				itemstack.damageItem(1, player);
				return true;
			}
		}
		return false;
	}
}
