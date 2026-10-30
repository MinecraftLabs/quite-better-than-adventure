package quitebetter.mixin.core;

import quitebetter.core.achivement.ModAchievements;
import quitebetter.core.item.ItemFishingRodLoot;
import quitebetter.core.item.ModItems;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(
	value = {EntityFishingBobber.class},
	remap = false,
	priority = 10001
)
public class FishingBobberMixin {
	@Shadow
	private int ticksCatchable;
	@Shadow
	@Nullable
	private TilePos tilePos;
	@Shadow
	public Player owner;
	@Shadow
	private int ticksInAir;
	@Shadow
	public Entity hookedEntity;
	@Shadow
	private int lerpSteps;
	@Shadow
	private double lerpX;
	@Shadow
	private double lerpY;
	@Shadow
	private double lerpZ;
	@Shadow
	private double lerpYRot;
	@Shadow
	private double lerpXRot;
	@Shadow
	private double velocityX;
	@Shadow
	private double velocityY;
	@Shadow
	private double velocityZ;
	@Unique
	private boolean lavafishing=false;

	@Unique
	private static boolean isItemFishingRod(Item item) {
		Item[] rods = {Items.TOOL_FISHINGROD, ModItems.TOOL_STEEL_FISHINGROD};
		return item != null && Arrays.asList(rods).contains(item);
	}

	@Inject(
		method = {"tick()V"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;tick()V", shift = At.Shift.AFTER),
		cancellable = true
	)
	private void tickFishing(CallbackInfo ci) {
		Random random = new Random();
		EntityFishingBobber bob = (EntityFishingBobber)(Object)this;
		if (lerpSteps > 0) {
			double d = bob.x + (lerpX - bob.x) / lerpSteps;
			double d1 = bob.y + (lerpY - bob.y) / lerpSteps;
			double d2 = bob.z + (lerpZ - bob.z) / lerpSteps;
			double d4 = lerpYRot - bob.yRot;

			while (d4 < -180.0) {
				d4 += 360.0;
			}

			while (d4 >= 180.0) {
				d4 -= 360.0;
			}

			bob.yRot = (float)(bob.yRot + d4 / lerpSteps);
			bob.xRot = (float)(bob.xRot + (lerpXRot - bob.xRot) / lerpSteps);
			lerpSteps--;
			bob.setPos(d, d1, d2);
			bob.setRot(bob.yRot, bob.xRot);
		} else {
			if (!bob.world.isClientSide) {
				ItemStack heldPlayerItem = bob.owner.getCurrentEquippedItem();
				if (bob.owner.removed
					|| !bob.owner.isAlive()
					|| heldPlayerItem == null
					|| !isItemFishingRod(heldPlayerItem.getItem())
					|| bob.distanceToSqr(bob.owner) > 1024.0) {
					bob.remove();
					bob.owner.bobberEntity = null;
					ci.cancel();
					return;
				}

				if (bob.hookedEntity != null) {
					if (!bob.hookedEntity.removed) {
						bob.x = bob.hookedEntity.x;
						bob.y = bob.hookedEntity.bb.minY + bob.hookedEntity.bbHeight * 0.8;
						bob.z = bob.hookedEntity.z;
						if (bob.hookedEntity instanceof MobPathfinder) {
							((MobPathfinder)bob.hookedEntity).setTarget(bob.owner);
						}

						double dx = bob.owner.x - bob.x;
						double dy = bob.owner.y - bob.y;
						double dz = bob.owner.z - bob.z;
						double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
						if (distance > 10.0) {
							double scale = 0.01;
							bob.hookedEntity.fling(dx * scale, dy * scale, dz * scale, 0.0F);
						}

						ci.cancel();
						return;
					}

					bob.hookedEntity = null;
				}
			}

			if (bob.isInGround() && tilePos != null) {
				if (bob.world.getBlockType(tilePos) == Blocks.ROPE) {
					bob.x = tilePos.x + 0.5;
					bob.y = tilePos.y + 0.5;
					bob.z = tilePos.z + 0.5;
					ci.cancel();
					return;
				}

				bob.setInGround(false);
				bob.xd = bob.xd * (random.nextFloat() * 0.2F);
				bob.yd = bob.yd * (random.nextFloat() * 0.2F);
				bob.zd = bob.zd * (random.nextFloat() * 0.2F);
				ticksInAir = 0;
				ticksCatchable = 0;
			}

			ticksInAir++;
			Vector3dc currentPos = new Vector3d(bob.x, bob.y, bob.z);
			Vector3dc nextPos = new Vector3d(bob.x + bob.xd, bob.y + bob.yd, bob.z + bob.zd);
			HitResult clip = bob.world.checkBlockCollisionBetweenPoints(currentPos, nextPos);
			currentPos = new Vector3d(bob.x, bob.y, bob.z);
			nextPos = new Vector3d(bob.x + bob.xd, bob.y + bob.yd, bob.z + bob.zd);
			if (clip != null) {
				nextPos = clip.location;
				if (clip instanceof HitResult.Tile hitTile && bob.world.getBlockType(hitTile.tilePos) == Blocks.ROPE) {
					bob.setInGround(true);
					tilePos = new TilePos(hitTile.tilePos);
				}
			}

			Entity entity = null;
			AABBd checkBB = MathHelper.aabbExpand(bob.bb, bob.xd, bob.yd, bob.zd, new AABBd());
			MathHelper.aabbGrow(checkBB, 1.0, 1.0, 1.0, checkBB);
			List<Entity> list = bob.world.getEntitiesWithinAABBExcludingEntity(bob, checkBB);
			double d3 = 0.0;

			for (Entity e : list) {
				if (e.isPickable() && (e != bob.owner || ticksInAir >= 5)) {
					float f2 = 0.3F;
					AABBd aabb = MathHelper.aabbGrow(e.bb, f2, f2, f2, new AABBd());
					HitResult newHitResult = MathHelper.aabbClip(aabb, currentPos, nextPos);
					if (newHitResult != null) {
						double d6 = currentPos.distance(newHitResult.location);
						if (d6 < d3 || d3 == 0.0) {
							entity = e;
							d3 = d6;
						}
					}
				}
			}

			if (entity != null) {
				clip = new HitResult.Entity(entity);
			}

			if (clip != null && clip instanceof HitResult.Entity hitEntity && hitEntity.entity.hurt(bob.owner, 0, DamageType.COMBAT)) {
				bob.hookedEntity = hitEntity.entity;
			}

			bob.move(bob.xd, bob.yd, bob.zd);
			float f = MathHelper.sqrt(bob.xd * bob.xd + bob.zd * bob.zd);
			bob.yRot = (float)(Math.atan2(bob.xd, bob.zd) * 180.0 / Math.PI);
			bob.xRot = (float)(Math.atan2(bob.yd, f) * 180.0 / Math.PI);

			while (bob.xRot - bob.xRotO < -180.0F) {
				bob.xRotO -= 360.0F;
			}

			while (bob.xRot - bob.xRotO >= 180.0F) {
				bob.xRotO += 360.0F;
			}

			while (bob.yRot - bob.yRotO < -180.0F) {
				bob.yRotO -= 360.0F;
			}

			while (bob.yRot - bob.yRotO >= 180.0F) {
				bob.yRotO += 360.0F;
			}

			bob.xRot = bob.xRotO + (bob.xRot - bob.xRotO) * 0.2F;
			bob.yRot = bob.yRotO + (bob.yRot - bob.yRotO) * 0.2F;
			float movementScale = 0.92F;
			if (bob.onGround || bob.horizontalCollision) {
				movementScale = 0.5F;
			}

			int k = 5;
			double d5 = 0.0;

			for (int l = 0; l < k; l++) {
				double d8 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * l / k - 0.125 + 0.125;
				double d9 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * (l + 1) / k - 0.125 + 0.125;
				AABBd aabb = new AABBd(bob.bb.minX, d8, bob.bb.minZ, bob.bb.maxX, d9, bob.bb.maxZ);
				if (bob.world.isAABBInMaterial(aabb, Materials.WATER)) {
					d5 += 1.0 / k;
				}
			}

			if (d5 > 0.0 && !bob.world.isClientSide) {
				if (ticksCatchable > 0) {
					ticksCatchable--;
				} else {
					int catchRate = 3000;
					int rainRate = 0;
					int algaeRate = 0;
					int steelRate = 0;
					TilePos tileAbove = new TilePos(bob.x, bob.y + 1.0, bob.z);
					if (bob.world.isBlockBeingRainedOn(tileAbove)) {
						rainRate = 200;
					}

					if (bob.world.getBlockType(tileAbove) == Blocks.ALGAE) {
						algaeRate = 100;
					}

					if (bob.owner.getCurrentEquippedItem() != null && bob.owner.getCurrentEquippedItem().getItem() == ModItems.TOOL_STEEL_FISHINGROD) {
						steelRate = 100;
					}

					catchRate = catchRate - rainRate - algaeRate - steelRate;
					if (random.nextInt(catchRate) == 0) {
						ticksCatchable = random.nextInt(30) + 10;
						bob.yd -= 0.2;
						bob.world.playSoundAtEntity(null, bob, "random.splash", 0.25F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
						float f3 = MathHelper.floor(bob.bb.minY);

						for (int i1 = 0; i1 < 1.0F + bob.bbWidth * 20.0F; i1++) {
							double xOff = (random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth;
							double zOff = (random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth;
							bob.world
								.spawnParticle(
									"bubble",
									bob.x + xOff,
									f3 + 1.0F,
									bob.z + zOff,
									bob.xd,
									bob.yd - random.nextFloat() * 0.2F,
									bob.zd,
									bob.world.dimension.id,
									false
								);
						}

						for (int j1 = 0; j1 < 1.0F + bob.bbWidth * 20.0F; j1++) {
							double xOff = (random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth;
							double zOff = (random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth;
							bob.world.spawnParticle("splash", bob.x + xOff, f3 + 1.0F, bob.z + zOff, bob.xd, bob.yd, bob.zd, bob.world.dimension.id, false);
						}
					}
				}
			}

			if (ticksCatchable > 0) {
				bob.yd = bob.yd - random.nextFloat() * random.nextFloat() * random.nextFloat() * 0.2;
			}

			double d7 = d5 * 2.0 - 1.0;
			bob.yd += 0.04 * d7;
			if (d5 > 0.0) {
				movementScale = (float)(movementScale * 0.9);
				bob.yd *= 0.8;
			}

			bob.xd *= movementScale;
			bob.yd *= movementScale;
			bob.zd *= movementScale;
			bob.setPos(bob.x, bob.y, bob.z);
		}

		int k = 5;
		double d5 = 0.0;
		lavafishing = false;

		if (bob.world.dimension.equals(Dimension.NETHER)) {
			for(int l = 0; l < k; ++l) {
				double d8 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * (double)l / (double)k - (double)0.125F + (double)0.125F;
				double d9 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * (double)(l + 1) / (double)k - (double)0.125F + (double)0.125F;
				AABBd axisalignedbb1 = new AABBd(bob.bb.minX, d8, bob.bb.minZ, bob.bb.maxX, d9, bob.bb.maxZ);
				if (bob.world.isAABBInMaterial(axisalignedbb1, Materials.LAVA)) {
					d5 += (double)1.0F / (double)k;
					if (owner.getCurrentEquippedItem().getItem() == ModItems.TOOL_STEEL_FISHINGROD) {
						lavafishing = true;
					} else {
						bob.remove();
						bob.owner.bobberEntity = null;

						ci.cancel();
						return;
					}
				}
			}

			if (d5 > (double)0.0F && !bob.world.isClientSide) {
				if (ticksCatchable > 0) {
					--ticksCatchable;
				} else {
					int catchRate = 600;

					if (random.nextInt(catchRate) == 0) {
						ticksCatchable = random.nextInt(30) + 10;
						bob.yd -= 0.2;
						bob.world.playSoundAtEntity((Entity)null, bob, "liquid.lavapop", 0.5F, 0.3F + (random.nextFloat() - random.nextFloat()) * 0.4F);
						double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
						double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
						bob.world.spawnParticle("lava", bob.x + xOff, bob.y, bob.z + zOff,xOff, 1, zOff, 0, true);

					} else {
						if (random.nextInt(20) == 0) {
							double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
							double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
							bob.world.spawnParticle("smoke", bob.x + xOff, bob.y, bob.z + zOff,xOff, 0.3, zOff, 0, true);
						}
					}
				}
			}

			if (lavafishing) {
				if (ticksCatchable > 0) {
					bob.yd -= (double) (random.nextFloat() * random.nextFloat() * random.nextFloat()) * 0.2;
				}

				double d7 = d5 * (double) 2.0F - (double) 1.0F;
				bob.yd += (d5 > 0 ? 0.08 : 0.04) * d7;
				if (d5 > (double) 0.0F) {
					bob.yd *= 0.8;
				}
			}
		}
		ci.cancel();
	}

	@Inject(
		method = {"yoink()I"},
		at = @At("HEAD"),
		cancellable = true
	)
	private void yoink(CallbackInfoReturnable<Integer> cir) {
		int damage = 0;
		EntityFishingBobber bob = (EntityFishingBobber)(Object)this;
		Random random = new Random();
		if (ticksCatchable > 0 && !bob.world.isClientSide) {
			if (lavafishing) {
				for (int i=1;i<10;i++) {
					double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
					double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
					bob.world.spawnParticle("lava", bob.x + xOff, bob.y, bob.z + zOff,xOff, 1, zOff, 0, true);
				}
				bob.world.playSoundAtEntity((Entity)null, bob, "random.splash", 0.25F, 0.3F + (random.nextFloat() - random.nextFloat()) * 0.4F);
				//
				EntityItem entityitem = new EntityItem(bob.world, bob.x, bob.y+1.5F, bob.z, new ItemStack(ItemFishingRodLoot.GetLavaLoot()));
				double dx = bob.owner.x - bob.x;
				double dy = bob.owner.y - bob.y;
				double dz = bob.owner.z - bob.z;
				double distance = (double)MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				double scale = 0.1;
				entityitem.xd = dx * scale;
				entityitem.yd = (dy-2) * scale + (double)MathHelper.sqrt(distance) * 0.08;
				entityitem.zd = dz * scale;
				bob.world.entityJoinedWorld(entityitem);
				bob.owner.addStat(StatList.fishCaughtStat, 1);
				damage = 1;
				bob.remove();
				bob.owner.bobberEntity = null;
				cir.setReturnValue(damage);
				bob.owner.triggerAchievement(ModAchievements.LAVA_FISHING);
			} else {
				EntityItem entityitem = new EntityItem(bob.world, bob.x, bob.y, bob.z, new ItemStack(ItemFishingRodLoot.GetWaterLoot()));
				double dx = bob.owner.x - bob.x;
				double dy = bob.owner.y - bob.y;
				double dz = bob.owner.z - bob.z;
				double distance = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				double scale = 0.1;
				entityitem.xd = dx * scale;
				entityitem.yd = dy * scale + (double) MathHelper.sqrt(distance) * 0.08;
				entityitem.zd = dz * scale;
				bob.world.entityJoinedWorld(entityitem);
				bob.owner.addStat(StatList.fishCaughtStat, 1);
				damage = 1;
				bob.remove();
				bob.owner.bobberEntity = null;
				cir.setReturnValue(damage);
			}
		}
	}
}
