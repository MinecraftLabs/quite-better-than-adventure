package quitebetter.core.tileentity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.pos.TilePos;

import java.util.Random;

public class TileEntityGlued extends TileEntity {
	private Boolean glued = false;

	public boolean isGlued() {return this.glued;}

	public void applyGlue() {
		Random rand = new Random();
		assert worldObj != null;
		Block<?> block = worldObj.getBlockType(this.tilePos);
		assert block != null;
		if (!this.isGlued()) {
			this.glued = true;

			for(int j = 0; j < 4; ++j) {
				worldObj.spawnParticle("item", tilePos.x + rand.nextFloat(), tilePos.y + rand.nextFloat(), tilePos.z + rand.nextFloat(), (double)(rand.nextInt(11)-5)/10, (double)(rand.nextInt(11)-5)/10, (double)(rand.nextInt(11)-5)/10, Items.SLIMEBALL.id, false);
			}
			worldObj.playSoundEffect((Entity)null, SoundCategory.WORLD_SOUNDS, tilePos.x, tilePos.y, tilePos.z, "mob.slimeattack", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);

			if (block.getLogic() instanceof BlockLogicDoor) {
				int off = ((BlockLogicDoor) block.getLogic()).isTop ? -1 : 1;
				TileEntityGlued tile = (TileEntityGlued) worldObj.getTileEntity(tilePos.add(0, off, 0, new TilePos()));
				if (tile!=null) { tile.applyGlue(); }
			}
		}
	}

	@Override
	public void readAdditionalData(CompoundTag compoundTag) {
		this.glued = compoundTag.getBoolean("Glued");
	}

	@Override
	public void writeAdditionalData(CompoundTag compoundTag) {
		if (this.glued != null) {
			compoundTag.putBoolean("Glued", this.glued);
		}

	}
}
