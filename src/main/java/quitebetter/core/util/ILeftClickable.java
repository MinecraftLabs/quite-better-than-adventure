package quitebetter.core.util;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;

public interface ILeftClickable {

	default boolean preventsBreaking(World world, TilePosc tilePos, Player player, Side side) { return !player.isSneaking(); }

	void onBlockLeftClicked(World world, TilePosc tilePos, Player player, Side side);
}
