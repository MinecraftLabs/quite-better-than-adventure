package quitebetter.core.tileentity;
import com.mojang.nbt.tags.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.net.packet.PacketEntityFling;
import net.minecraft.core.net.packet.PacketMovePlayer;
import net.minecraft.server.entity.player.PlayerServer;
import quitebetter.core.block.BlockLogicFan;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.List;

public class TileEntityFan extends TileEntity {
	private int getSightRange(World world, TilePosc tilePos, Direction facing) {
		if (facing == Direction.NONE) {
			return 0;
		} else {
			int range = 4;
			TilePos queryPos = new TilePos();
			for(int i = 1; i <= range; ++i) {
				int x1 = MathHelper.round(tilePos.x() + (double)(facing.offsetX() * i));
				int y1 = MathHelper.round(tilePos.y() + (double)(facing.offsetY() * i));
				int z1 = MathHelper.round(tilePos.z() + (double)(facing.offsetZ() * i));
				int id = world.getBlockType(queryPos.set(x1, y1, z1)).id();
				if (Blocks.solid[id]) {
					return i - 1;
				}
			}
			return range;
		}
	}

	private AABBd getDetectionBox(TilePosc tilePos, Direction facing, int range) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		double x1 = (double)x + (double)facing.offsetX();
		double y1 = (double)y + (double)facing.offsetY();
		double z1 = (double)z + (double)facing.offsetZ();
		double x2 = (double)x + (double)(facing.offsetX() * range);
		double y2 = (double)y + (double)(facing.offsetY() * range);
		double z2 = (double)z + (double)(facing.offsetZ() * range);
		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2) + (double)1.0F;
		double maxY = Math.max(y1, y2) + (double)1.0F;
		double maxZ = Math.max(z1, z2) + (double)1.0F;
		return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void tick() {
		if (this.worldObj != null && !this.worldObj.isClientSide && ((BlockLogicFan) this.getBlock().getLogic()).isActive() && !((BlockLogicFan) this.getBlock().getLogic()).isSignalOverriden(worldObj,this.tilePos)) {
			int id = this.worldObj.getBlockType(this.tilePos).id();
			Block block = this.worldObj.getBlockType(this.tilePos);
			int meta = this.worldObj.getBlockData(this.tilePos);
			Direction facing = BlockLogicFan.getDirection(meta);
			int effectiveRange = this.getSightRange(this.worldObj, this.tilePos, facing);
			if (effectiveRange > 0) {
				AABBd detectionBox = this.getDetectionBox(this.tilePos, facing, effectiveRange);
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, detectionBox);
				for(int i = 0; i < list.size(); ++i) {
					Entity entity = (Entity)list.get(i);
					if (entity.canInteract()) {
						double xd = (double) facing.offsetX() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						double yd = (double) facing.offsetY() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						double zd = (double) facing.offsetZ() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						if (EnvironmentHelper.isMultiplayerServer() && entity instanceof PlayerServer) {
							entity.xd += (double) facing.offsetX() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
							entity.yd += (double) facing.offsetY() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
							entity.zd += (double) facing.offsetZ() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
							entity.fallDistance = 0.0F;
							PlayerServer player = (PlayerServer) entity;
							player.playerNetServerHandler.sendPacket(new PacketEntityFling(player.id, entity.xd, entity.yd, entity.zd, 0, 0));
						} else {
							entity.fling(xd,yd,zd,0);
						}
						//FIRE
						if ( ((BlockLogicFan) this.getBlock().getLogic()).isModifierFire(worldObj,this.tilePos) ) {
							entity.remainingFireTicks += 300;
						}
					}
				}
			}
		}
	}

	@Override
	public void readAdditionalData(CompoundTag compoundTag) {
	}

	@Override
	public void writeAdditionalData(CompoundTag compoundTag) {
	}
}
