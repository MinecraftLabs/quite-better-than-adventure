package quitebetter.core.projectile;

import net.minecraft.core.block.BlockLogicTNT;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.EntityPrimedTNT;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import quitebetter.core.block.ModBlocks;
import quitebetter.core.item.ModItems;

public class ProjectileArrowTorch extends ProjectileArrow {

	public ProjectileArrowTorch(World world, Mob entityliving, boolean doesArrowBelongToPlayer) {
		super(world, entityliving, doesArrowBelongToPlayer, 0);
	}

	public ProjectileArrowTorch(World world, double x, double y, double z) {
		super(world, x, y, z, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.tickCount > 1 && this.tickCount % 3 == 0) {
			world.spawnParticle("flame", this.x, this.y, this.z, 0, 0, 0, 0, false);
		}
	}

	@Override
	public void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (hitResult instanceof HitResult.Entity hitEntity) {
			hitEntity.entity.remainingFireTicks = 25;
			world.playSoundAtEntity(null, this, "fire.ignite", 1F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
			if (hitEntity.entity instanceof EntityPrimedTNT tnt) {
				tnt.fuse = 0;
			}
		}
	}

	@Override
	protected void inGroundAction() {
		this.remove();
		boolean spawned = false;
		int x;
		int y;
		int z;
		//CENTERED
		x = this.tilePos.x;
		y = this.tilePos.y;
		z = this.tilePos.z;
		TilePos pos = new TilePos(x, y, z);
		//TNT BLOCK
		if (world.getBlockType(pos).equals(Blocks.TNT)) {
			((BlockLogicTNT) world.getBlockType(pos).getLogic()).ignite(world,pos,true);
			return;
		}
		if (this.tilePos.y<(int) this.y) {
			y=(int)this.y;
		} else if (this.tilePos.x<(int) this.x) {
			x=(int)this.x;
		} else if (this.tilePos.x>(int) this.x) {
			x=(int)this.x;
		} else if (this.tilePos.z<(int) this.z) {
			z=this.tilePos.z+1;
		} else if (this.tilePos.z>=(int) this.z) {
			z=this.tilePos.z-1;
		}

		pos.set(x, y, z);
		if ((world.getBlockType(pos).hasTag(BlockTags.PLACE_OVERWRITES) && !(world.getBlockType(pos).hasTag(BlockTags.IS_LAVA)) && !(world.getBlockType(pos).hasTag(BlockTags.IS_WATER)) )) {
			if (ModBlocks.ARROW_TORCH.canPlaceAt(world,pos)) {
				world.setBlockTypeNotify(pos, ModBlocks.ARROW_TORCH);
				world.playSoundAtEntity(null, this, "fire.ignite", 1F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
				for (int i=1; i < 5; i++) {
					world.spawnParticle("smoke", x+0.5, y+0.5, z+0.5, random.nextFloat()*(random.nextInt(2)==0 ? -0.3 : 0.3), random.nextFloat()*(random.nextInt(2)==0 ? -0.3 : 0.3), random.nextFloat()*(random.nextInt(2)==0 ? -0.3 : 0.3), 0, false);
				}
				spawned = true;
			}
		}
		if (!spawned) {
			out:
			for (int xi=-1; xi < 1; xi++) {
				for (int yi = -1; yi < 1; yi++) {
					for (int zi = -1; zi < 1; zi++) {
						x = (int) (this.x + xi);
						y = (int) (this.y + yi);
						z = (int) (this.z + zi);
						pos.set(x, y, z);
						if ((world.getBlockType(pos).hasTag(BlockTags.PLACE_OVERWRITES) && !(world.getBlockType(pos).hasTag(BlockTags.IS_LAVA)) && !(world.getBlockType(pos).hasTag(BlockTags.IS_WATER)) )) {
							if (ModBlocks.ARROW_TORCH.canPlaceAt(world, pos)) {
								world.setBlockTypeNotify(pos, ModBlocks.ARROW_TORCH);
								world.playSoundAtEntity(null, this, "fire.ignite", 1F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
								for (int i = 1; i < 5; i++) {
									world.spawnParticle("smoke", x + 0.5, y + 0.5, z + 0.5, random.nextFloat() * (random.nextInt(2) == 0 ? -0.3 : 0.3), random.nextFloat() * (random.nextInt(2) == 0 ? -0.3 : 0.3), random.nextFloat() * (random.nextInt(2) == 0 ? -0.3 : 0.3), 0, false);
								}
								spawned = true;
								break out;
							}
						}
					}
				}
			}
		}

		if (!spawned) {
			world.entityJoinedWorld(
				new EntityItem(world, this.x, this.y, this.z, new ItemStack(ModItems.AMMO_ARROW_TORCH))
			);
		}
	}
}
