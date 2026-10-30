package quitebetter.mixin.server;

import net.minecraft.core.block.Block;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketPlayerAction;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.net.packet.PacketUseOrPlaceItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quitebetter.core.util.ILeftClickable;
import quitebetter.core.util.IRightClickable;

@Mixin(
	value = {PacketHandlerServer.class},
	remap = false
)
public class PacketHandlerServerMixin {
	@Shadow
	@Final
	private MinecraftServer mcServer;
	@Shadow
	private PlayerServer playerEntity;

	@Inject(
		method = {"handlePlayerAction"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void handlePlayerAction(PacketPlayerAction packet, CallbackInfo ci) {
		int action = packet.action;
		TilePos tilePos = new TilePos(packet.xPosition, packet.yPosition, packet.zPosition);
		Side side = packet.side;
		WorldServer world = mcServer.getDimensionWorld(this.playerEntity.dimension);

		if (action == PacketPlayerAction.ACTION_DIG_START ||
			action == PacketPlayerAction.ACTION_DIG_CONTINUED ||
			action == PacketPlayerAction.ACTION_DIG_COMPLETE) {
			//LOGGER.info("Captured {} with action {}. Checking ILeftClickable.", packet.getClass().getName(),action);
			Block<?> block = world.getBlockType(tilePos);
			if (block!=null && (block.getLogic() instanceof ILeftClickable)) {
				ILeftClickable logic = (ILeftClickable) block.getLogic();
				if (logic.preventsBreaking(world,tilePos,playerEntity,side)) {
					logic.onBlockLeftClicked(world,tilePos,playerEntity,side);
					playerEntity.playerNetServerHandler.sendPacket(new PacketTileEntityData(world.getTileEntity(tilePos)));
					playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(tilePos.x, tilePos.y, tilePos.z, world));
					ci.cancel();
				}
			}
		}
	}

	@Inject(
		method = {"handleUseOrPlaceItem"},
		at = @At("HEAD"),
		cancellable = true
	)
	public void handleUseOrPlaceItem(PacketUseOrPlaceItemStack packet, CallbackInfo ci) {
		TilePos tilePos = new TilePos(packet.xPosition, packet.yPosition, packet.zPosition);
		Direction direction = packet.direction;
		WorldServer world = mcServer.getDimensionWorld(this.playerEntity.dimension);

		//LOGGER.info("Captured {} with type {}. Checking IRightClickable.", packet.getClass().getName(),packet.type);
		Block<?> block = world.getBlockType(tilePos);
		if (block!=null && (block.getLogic() instanceof IRightClickable)) {
			IRightClickable logic = (IRightClickable) block.getLogic();
			if (logic.preventsInteraction(world,tilePos,playerEntity,direction.side())) {
				logic.onBlockRightClicked(world,tilePos,playerEntity,direction.side());
				playerEntity.playerNetServerHandler.sendPacket(new PacketTileEntityData(world.getTileEntity(tilePos)));
				playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(tilePos.x, tilePos.y, tilePos.z, world));
				ci.cancel();
			}
		}
	}
}
