package quitebetter.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import net.minecraft.server.world.ServerPlayerController;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
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

	@Unique
	private boolean handleLeftClick(World world, TilePos tilePos, Side side) {
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof ILeftClickable logic && logic.preventsBreaking(world, tilePos, this.playerEntity, side)) {
			logic.onBlockLeftClicked(world, tilePos, this.playerEntity, side);
			TileEntity tile = world.getTileEntity(tilePos);
			if (tile != null) {
				this.playerEntity.playerNetServerHandler.sendPacket(new PacketTileEntityData(tile));
			}
			return true;
		}
		return false;
	}

	@Unique
	private boolean handleRightClick(World world, TilePos tilePos, Side side) {
		Block<?> block = world.getBlockType(tilePos);
		if (block.getLogic() instanceof IRightClickable logic && logic.preventsInteraction(world, tilePos, this.playerEntity, side)) {
			logic.onBlockRightClicked(world, tilePos, this.playerEntity, side);
			TileEntity tile = world.getTileEntity(tilePos);
			if (tile != null) {
				this.playerEntity.playerNetServerHandler.sendPacket(new PacketTileEntityData(tile));
			}
			return true;
		}
		return false;
	}

	@WrapOperation(
		method = {"handlePlayerAction"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerPlayerController;startMining(IIILnet/minecraft/core/util/helper/Side;)V"
		)
	)
	public void startMining(ServerPlayerController controller, int x, int y, int z, Side side, Operation<Void> original) {
		//LOGGER.info("Captured {} with action {}. Checking ILeftClickable.", packet.getClass().getName(),action);
		TilePos tilePos = new TilePos(x, y, z);
		WorldServer world = mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (this.handleLeftClick(world, tilePos, side)) {
			this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, world));
		} else {
			original.call(controller, x, y, z, side);
		}
	}

	@WrapOperation(
		method = {"handlePlayerAction"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerPlayerController;hitBlock(IIILnet/minecraft/core/util/helper/Side;DD)V"
		)
	)
	public void hitBlock(ServerPlayerController controller, int x, int y, int z, Side side, double xHit, double yHit, Operation<Void> original) {
		TilePos tilePos = new TilePos(x, y, z);
		WorldServer world = mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (this.handleLeftClick(world, tilePos, side)) {
			this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, world));
		} else {
			original.call(controller, x, y, z, side, xHit, yHit);
		}
	}

	@WrapOperation(
		method = {"handlePlayerAction"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerPlayerController;destroyBlock(IIILnet/minecraft/core/util/helper/Side;)Z"
		)
	)
	public boolean destroyBlock(ServerPlayerController controller, int x, int y, int z, Side side, Operation<Boolean> original) {
		TilePos tilePos = new TilePos(x, y, z);
		WorldServer world = mcServer.getDimensionWorld(this.playerEntity.dimension);
		if (this.handleLeftClick(world, tilePos, side)) {
			return false;
		}
		return original.call(controller, x, y, z, side);
	}

	@WrapOperation(
		method = {"handleUseOrPlaceItem"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerPlayerController;useOrPlaceItemStackOnTile(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;Lnet/minecraft/core/item/ItemStack;IIILnet/minecraft/core/util/helper/Side;DD)Z"
		)
	)
	public boolean useOrPlaceItemStackOnTile(ServerPlayerController controller, Player player, World world, ItemStack itemStack, int x, int y, int z, Side side, double xPlaced, double yPlaced, Operation<Boolean> original) {
		//LOGGER.info("Captured {} with type {}. Checking IRightClickable.", packet.getClass().getName(),type);
		TilePos tilePos = new TilePos(x, y, z);
		if (this.handleRightClick(world, tilePos, side)) {
			return true;
		}
		return original.call(controller, player, world, itemStack, x, y, z, side, xPlaced, yPlaced);
	}

	@WrapOperation(
		method = {"handleUseOrPlaceItem"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/world/ServerPlayerController;placeItemStackOnTile(Lnet/minecraft/core/entity/player/Player;Lnet/minecraft/core/world/World;Lnet/minecraft/core/item/ItemStack;IIILnet/minecraft/core/util/helper/Side;DD)Z"
		)
	)
	public boolean placeItemStackOnTile(ServerPlayerController controller, Player player, World world, ItemStack itemStack, int x, int y, int z, Side side, double xPlaced, double yPlaced, Operation<Boolean> original) {
		TilePos tilePos = new TilePos(x, y, z);
		if (this.handleRightClick(world, tilePos, side)) {
			return true;
		}
		return original.call(controller, player, world, itemStack, x, y, z, side, xPlaced, yPlaced);
	}
}
