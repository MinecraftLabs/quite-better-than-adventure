package quitebetter.core.util;

import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.world.pos.TilePosc;

public interface IFalling {
	default boolean FellOnGround(EntityFallingBlock fallingBlock, TilePosc tilePos) { return true; }
}
