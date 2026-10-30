package quitebetter.core.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import quitebetter.core.block.BlockLogicFan;

import java.util.List;

public class TileEntityFan extends TileEntity {
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
		double maxX = Math.max(x1, x2) + 1.0;
		double maxY = Math.max(y1, y2) + 1.0;
		double maxZ = Math.max(z1, z2) + 1.0;
		return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public void tick() {
		if (this.worldObj != null && !this.worldObj.isClientSide && ((BlockLogicFan) this.getBlock().getLogic()).isActive() && !((BlockLogicFan) this.getBlock().getLogic()).isSignalOverriden(worldObj,this.tilePos)) {
			Block<?> block = this.worldObj.getBlockType(this.tilePos);
			int meta = this.worldObj.getBlockData(this.tilePos);
			Direction facing = BlockLogicFan.getDirection(meta);
			int effectiveRange = BlockLogicFan.getSightRange(this.worldObj, this.tilePos, facing);
			if (effectiveRange > 0) {
				AABBd detectionBox = this.getDetectionBox(this.tilePos, facing, effectiveRange);
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);
				for(int i = 0; i < list.size(); ++i) {
					Entity entity = list.get(i);
					if (entity.canInteract()) {
						double xd = (double) facing.offsetX() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						double yd = (double) facing.offsetY() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						double zd = (double) facing.offsetZ() /6.5 /(BlockLogicFan.isInverted(block) ? -1 : 1);
						entity.fling(xd,yd,zd,0);
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
