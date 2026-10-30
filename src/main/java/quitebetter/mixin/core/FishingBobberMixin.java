package quitebetter.mixin.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import quitebetter.core.achivement.ModAchievements;
import quitebetter.core.item.ItemFishingRodLoot;
import quitebetter.core.item.ModItems;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Dimension;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	public Player owner;
	@Unique
	private boolean lavafishing=false;

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/item/ItemStack;getItem()Lnet/minecraft/core/item/Item;"
		)
	)
	private Item keepSteelRod(Item item) {
		return item == ModItems.TOOL_STEEL_FISHINGROD ? Items.TOOL_FISHINGROD : item;
	}

	@ModifyConstant(
		method = "tick",
		constant = @Constant(intValue = 500)
	)
	private int modifyCatchRate(int catchRate) {
		ItemStack heldItem = this.owner.getCurrentEquippedItem();
		return 3000 - (heldItem != null && heldItem.getItem() == ModItems.TOOL_STEEL_FISHINGROD ? 100 : 0);
	}

	@Inject(
		method = "tick",
		at = {
			@At(value = "RETURN", ordinal = 0),
			@At("TAIL")
		},
		require = 2
	)
	private void tickLava(CallbackInfo ci) {
		EntityFishingBobber bob = (EntityFishingBobber)(Object)this;
		Random random = bob.world.rand;
		int k = 5;
		double d5 = 0.0;
		lavafishing = false;

		if (bob.world.dimension.equals(Dimension.NETHER)) {
			for (int l = 0; l < k; ++l) {
				double d8 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * (double)l / (double)k - 0.125 + 0.125;
				double d9 = bob.bb.minY + (bob.bb.maxY - bob.bb.minY) * (double)(l + 1) / (double)k - 0.125 + 0.125;
				AABBd axisalignedbb1 = new AABBd(bob.bb.minX, d8, bob.bb.minZ, bob.bb.maxX, d9, bob.bb.maxZ);
				if (bob.world.isAABBInMaterial(axisalignedbb1, Materials.LAVA)) {
					d5 += 1.0 / (double)k;
					ItemStack heldItem = bob.owner.getCurrentEquippedItem();
					if (heldItem != null && heldItem.getItem() == ModItems.TOOL_STEEL_FISHINGROD) {
						lavafishing = true;
					} else {
						bob.remove();
						bob.owner.bobberEntity = null;
						return;
					}
				}
			}

			if (d5 > 0.0 && !bob.world.isClientSide) {
				if (ticksCatchable > 0) {
					--ticksCatchable;
				} else {
					int catchRate = 600;

					if (random.nextInt(catchRate) == 0) {
						ticksCatchable = random.nextInt(30) + 10;
						bob.yd -= 0.2;
						bob.world.playSoundAtEntity(null, bob, "liquid.lavapop", 0.5F, 0.3F + (random.nextFloat() - random.nextFloat()) * 0.4F);
						double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
						double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
						bob.world.spawnParticle("lava", bob.x + xOff, bob.y, bob.z + zOff, xOff, 1, zOff, 0, true);
					} else {
						if (random.nextInt(20) == 0) {
							double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
							double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
							bob.world.spawnParticle("smoke", bob.x + xOff, bob.y, bob.z + zOff, xOff, 0.3, zOff, 0, true);
						}
					}
				}
			}

			if (lavafishing) {
				if (ticksCatchable > 0) {
					bob.yd -= (double)(random.nextFloat() * random.nextFloat() * random.nextFloat()) * 0.2;
				}

				double d7 = d5 * 2.0 - 1.0;
				bob.yd += (d5 > 0 ? 0.08 : 0.04) * d7;
				if (d5 > 0.0) {
					bob.yd *= 0.8;
				}
			}
		}
	}

	@Inject(
		method = {"yoink()I"},
		at = @At("HEAD"),
		cancellable = true
	)
	private void yoinkLava(CallbackInfoReturnable<Integer> cir) {
		EntityFishingBobber bob = (EntityFishingBobber)(Object)this;
		if (lavafishing && ticksCatchable > 0 && !bob.world.isClientSide) {
			Random random = bob.world.rand;
			for (int i = 1; i < 10; i++) {
				double xOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
				double zOff = (double)((random.nextFloat() * 2.0F - 1.0F) * bob.bbWidth);
				bob.world.spawnParticle("lava", bob.x + xOff, bob.y, bob.z + zOff, xOff, 1, zOff, 0, true);
			}
			bob.world.playSoundAtEntity(null, bob, "random.splash", 0.25F, 0.3F + (random.nextFloat() - random.nextFloat()) * 0.4F);
			EntityItem entityitem = new EntityItem(bob.world, bob.x, bob.y + 1.5F, bob.z, new ItemStack(ItemFishingRodLoot.GetLavaLoot()));
			double dx = bob.owner.x - bob.x;
			double dy = bob.owner.y - bob.y;
			double dz = bob.owner.z - bob.z;
			double distance = (double)MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
			double scale = 0.1;
			entityitem.xd = dx * scale;
			entityitem.yd = (dy - 2) * scale + (double)MathHelper.sqrt(distance) * 0.08;
			entityitem.zd = dz * scale;
			bob.world.entityJoinedWorld(entityitem);
			bob.owner.addStat(StatList.fishCaughtStat, 1);
			bob.remove();
			bob.owner.bobberEntity = null;
			cir.setReturnValue(1);
			bob.owner.triggerAchievement(ModAchievements.LAVA_FISHING);
		}
	}

	@ModifyArg(
		method = "yoink",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/item/ItemStack;<init>(Lnet/minecraft/core/item/Item;)V"
		),
		index = 0
	)
	private Item replaceWaterLoot(Item item) {
		return ItemFishingRodLoot.GetWaterLoot();
	}
}
