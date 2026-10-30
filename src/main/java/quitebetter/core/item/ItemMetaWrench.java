package quitebetter.core.item;


import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.Nullable;

public class ItemMetaWrench extends Item {


	public ItemMetaWrench(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public boolean onUseOnBlock(ItemStack itemstack, World world, @Nullable Player player, TilePosc blockPos, Side side, double xPlaced, double yPlaced) {
		Block<?> block = world.getBlockType(blockPos);
		player.swingItem();
		world.playBlockEvent(player, blockPos, 2001, block.id());
		int oldmeta = world.getBlockData(blockPos);
		int change = player.isSneaking() ? -1 : 1;
		world.setBlockDataNotify(blockPos, oldmeta + change);
		player.sendMessage(I18n.getInstance().translateKey("item.quitebetter.meta.wrench.info").replace("*", ""+oldmeta) + " "+world.getBlockData(blockPos));
		return true;
	}
}
