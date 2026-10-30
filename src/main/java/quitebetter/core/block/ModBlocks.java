package quitebetter.core.block;

import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.item.block.ItemBlockPainted;
import net.minecraft.core.sound.BlockSounds;
import quitebetter.core.ModConfig;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryRegistry;

import java.util.ArrayList;
import java.util.List;

import static quitebetter.core.ModCore.MOD_ID;

public class ModBlocks {
	public static Block<?> HAZARD;
	public static Block<?> BLOCK_BONE;
	public static Block<?> OBSIDIAN_POLISHED;
	public static Block<?> HAZARD_PAINTED;
	public static Block<?> TILE_SEASHELL;
	public static Block<BlockLogicStairs> STAIRS_TILE_SEASHELL;
	public static Block<BlockLogicSlab> SLAB_TILE_SEASHELL;
	public static Block<?> FAN;
	public static Block<?> ACTIVE_FAN;
	public static Block<?> IN_FAN;
	public static Block<?> ACTIVE_IN_FAN;
	public static Block<?> BRICK_OLIVINE;
	public static Block<BlockLogicStairs> STAIRS_BRICK_OLIVINE;
	public static Block<BlockLogicSlab> SLAB_BRICK_OLIVINE;
	public static Block<?> SUPPORT_STEEL;
	public static Block<?> SUPPORT_IRON;
	public static Block<?> SUPPORT_WOOD;
	public static Block<?> MUSHROOM_GLOWING;
	public static Block<?> PEDESTAL;
	public static Block<?> BRICK_SNOW;
	public static Block<BlockLogicStairs> STAIRS_BRICK_SNOW;
	public static Block<BlockLogicSlab> SLAB_BRICK_SNOW;
	public static Block<?> ARROW_TORCH;
	public static Block<?> CRATE;
	public static Block<?> CRATE_PAINTED;
	public static Block<?> GEODE;

	public static Block<?> BRICK_OBSIDIAN;
	public static Block<BlockLogicStairs> STAIRS_BRICK_OBSIDIAN;
	public static Block<BlockLogicSlab> SLAB_BRICK_OBSIDIAN;

	public static Block<?> LANTERN_COAL;
	public static Block<?> LANTERN_REDSTONE;
	public static Block<?> LANTERN_MUSHROOM;
	public static Block<?> OVERLAY_SEASHELL;

	public static Block<?> BRICK_MUD;
	public static Block<BlockLogicStairs> STAIRS_BRICK_MUD;
	public static Block<BlockLogicSlab> SLAB_BRICK_MUD;

	public static Block<?> BRICK_MUD_BAKED;
	public static Block<BlockLogicStairs> STAIRS_BRICK_MUD_BAKED;
	public static Block<BlockLogicSlab> SLAB_BRICK_MUD_BAKED;

	public static Block<?> BRICK_CLAY_MOSSY;

	public static Block<?> BLOCK_IRON_CORRUGATED;
	public static Block<?> BLOCK_IRON_CORRUGATED_PAINTED;

	public static int startingBlockId = ModConfig.startingBlockId;

	public static Integer id(String name) {
		try {
			return ModConfig.cfg.getInt(ModConfig.BlockIDs+"."+name);
		}catch (NullPointerException e) {
			return startingBlockId++;
		}
	}

	public static void Setup() {
		//BONEBLOCK
		BLOCK_BONE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.2F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL)
			.build("block_bone", id("BLOCK_BONE"), b -> new BlockLogicBoneBlock(b, Materials.MARBLE) {});
		//OBSIDIAN POLISHED
		OBSIDIAN_POLISHED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(10.0F).setResistance(2000.0F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.PISTON_CRUSHING)
			.build("obsidian_polished", id("OBSIDIAN_POLISHED"), b -> new BlockLogic(b, Materials.STONE) {})
			.withOverrideColor(MaterialColor.paintedBlack);
		//SEASHELLTILES
		TILE_SEASHELL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F).setResistance(2.0F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("tile_seashell", id("TILE_SEASHELL"), b -> new BlockLogic(b, Materials.SLATE) {});
		STAIRS_TILE_SEASHELL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F).setResistance(2.0F)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("stairs_tile_seashell", id("STAIRS_TILE_SEASHELL"), b -> new BlockLogicStairs(b, TILE_SEASHELL) {});
		SLAB_TILE_SEASHELL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F).setResistance(2.0F)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("slab_tile_seashell", id("SLAB_TILE_SEASHELL"), b -> new BlockLogicSlab(b, TILE_SEASHELL) {});
		//HAZARD
		HAZARD = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F).setResistance(25.0F)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE)
			.build("hazard", id("HAZARD"), b -> new BlockLogicHazard(b, Materials.STONE) {})
			.setBlockItem(b -> new ItemBlock(HAZARD));
		HAZARD_PAINTED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F).setResistance(25.0F)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.setBlockItem(b -> new ItemBlockPainted(b, false))
			.build("hazard_painted", id("HAZARD_PAINTED"), b -> new BlockLogicHazardPainted(b, Materials.STONE) {});
		CreativeInventoryRegistry.INSTANCE.register(HAZARD_PAINTED, misc().setCustomSupplier(() -> {
			List<ItemStack> list = new ArrayList<>();
			for (int i = 0; i < 16; ++i) {
				list.add(new ItemStack(HAZARD_PAINTED, 1, i));
			}
			return list;
		}));
		//FAN
		FAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(25.0F)
			.setTicking(true)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.MINEABLE_BY_AXE)
			.build("fan", id("FAN"), b -> new BlockLogicFan(b, Materials.WOOD, false, false) {});
		ACTIVE_FAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(25.0F)
			.setTicking(true)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.MINEABLE_BY_AXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.build("active_fan", id("ACTIVE_FAN"), b -> new BlockLogicFan(b, Materials.WOOD, true, false) {});
		IN_FAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(25.0F)
			.setTicking(true)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.MINEABLE_BY_AXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.build("in_fan", id("IN_FAN"), b -> new BlockLogicFan(b, Materials.WOOD, false, true) {});
		ACTIVE_IN_FAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(25.0F)
			.setTicking(true)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.MINEABLE_BY_AXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.build("active_in_fan", id("ACTIVE_IN_FAN"), b -> new BlockLogicFan(b, Materials.WOOD, true, true) {});
		//OLIVINE BRICKS
		BRICK_OLIVINE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(3F).setResistance(10.0F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("brick_olivine", id("BRICK_OLIVINE"), b -> new BlockLogic(b, Materials.STONE) {});
		STAIRS_BRICK_OLIVINE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(3F).setResistance(10.0F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("stairs_brick_olivine", id("STAIRS_BRICK_OLIVINE"), b -> new BlockLogicStairs(b, BRICK_OLIVINE) {});
		SLAB_BRICK_OLIVINE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(3F).setResistance(10.0F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("slab_brick_olivine", id("SLAB_BRICK_OLIVINE"), b -> new BlockLogicSlab(b, BRICK_OLIVINE) {});
		//SUPPORTS
		SUPPORT_STEEL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(1.0F)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("support_steel", id("SUPPORT_STEEL"), b -> new BlockLogicPillar(b, Materials.STEEL) {});
		SUPPORT_IRON = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(1.0F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL)
			.build("support_iron", id("SUPPORT_IRON"), b -> new BlockLogicPillar(b, Materials.METAL) {});
		SUPPORT_WOOD = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.3F)
			.setTags(BlockTags.MINEABLE_BY_AXE)
			.setBlockSound(BlockSounds.WOOD)
			.build("support_wood", id("SUPPORT_WOOD"), b -> new BlockLogicPillar(b, Materials.WOOD) {});
		//GLOWING MUSHROOM
		MUSHROOM_GLOWING = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setTags(BlockTags.PLANTABLE_IN_JAR)
			.setBlockSound(BlockSounds.GRASS)
			.build("mushroom_glowing", id("MUSHROOM_GLOWING"), b -> new BlockLogicGlowingMushroom(b) {}).withLightEmission(0.95F);
		//PEDESTAL
		PEDESTAL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setTags(BlockTags.NOT_IN_CREATIVE_MENU)
			.build("pedestal", id("PEDESTAL"), b -> new BlockLogicPedestal(b, Materials.CLOTH) {});
		//SNOW BRICKS
		BRICK_SNOW = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.3F)
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.MINEABLE_BY_SHOVEL)
			.build("brick_snow", id("BRICK_SNOW"), b -> new BlockLogic(b, Materials.SNOW) {});
		STAIRS_BRICK_SNOW = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.3F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.MINEABLE_BY_SHOVEL)
			.build("stairs_brick_snow", id("STAIRS_BRICK_SNOW"), b -> new BlockLogicStairs(b, BRICK_SNOW) {});
		SLAB_BRICK_SNOW = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.3F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.MINEABLE_BY_SHOVEL)
			.build("slab_brick_snow", id("SLAB_BRICK_SNOW"), b -> new BlockLogicSlab(b, BRICK_SNOW) {});
		//ARROW TORCH BLOCK
		ARROW_TORCH = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setTags(BlockTags.NOT_IN_CREATIVE_MENU)
			.build("arrow_torch", id("ARROW_TORCH"), b -> new BlockLogicArrowTorch(b) {}).withLightEmission(1F);
		//CRATE
		CRATE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F).setResistance(25.0F)
			.build("crate", id("CRATE"), b -> new BlockLogicCrate(b, Materials.WOOD) {});
		CRATE_PAINTED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc().setCustomSupplier(() -> {
				List<ItemStack> list = new ArrayList<>();
				for (int i = 1; i < 16; ++i) {
					list.add(new ItemStack(CRATE_PAINTED, 1, i));
				}
				list.add(CRATE_PAINTED.getDefaultStack());
				return list;
			}))
			.setHardness(1F).setResistance(25.0F)
			.setBlockItem(b -> new ItemBlockPainted<>(b, false))
			.build("crate_painted", id("CRATE_PAINTED"), b -> new BlockLogicCratePainted(b, Materials.WOOD) {});
		//GEODE
		GEODE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(10F).setResistance(25.0F)
			.build("geode", id("GEODE"), b -> new BlockLogicGeode(b, Materials.BASALT) {});
		//BRICK OBSIDIAN
		BRICK_OBSIDIAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(10F).setResistance(2000.0F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.PISTON_CRUSHING)
			.setBlockSound(BlockSounds.STONE)
			.build("brick_obsidian", id("BRICK_OBSIDIAN"), b -> new BlockLogic(b, Materials.STONE) {})
			.withOverrideColor(MaterialColor.paintedBlack);
		STAIRS_BRICK_OBSIDIAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(10F).setResistance(2000.0F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.PISTON_CRUSHING)
			.build("stairs_brick_obsidian", id("STAIRS_BRICK_OBSIDIAN"), b -> new BlockLogicStairs(b, BRICK_OBSIDIAN) {});
		SLAB_BRICK_OBSIDIAN = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(10F).setResistance(2000.0F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE,BlockTags.PISTON_CRUSHING)
			.build("slab_brick_obsidian", id("SLAB_BRICK_OBSIDIAN"), b -> new BlockLogicSlab(b, BRICK_OBSIDIAN) {});
		//LANTERN
		LANTERN_COAL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.1F)
			.setTicking(true)
			.setTags(BlockTags.CAN_HANG_OFF,BlockTags.BROKEN_BY_FLUIDS)
			.build("lantern_coal", id("LANTERN_COAL"), b -> new BlockLogicLanternCoal(b, Materials.DECORATION) {})
			.withLightEmission(1F);
		LANTERN_REDSTONE = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.1F)
			.setTicking(true)
			.setTags(BlockTags.CAN_HANG_OFF,BlockTags.BROKEN_BY_FLUIDS)
			.build("lantern_redstone", id("LANTERN_REDSTONE"), b -> new BlockLogicLanternRedstone(b, Materials.DECORATION) {})
			.withLightEmission(0.5F);
		LANTERN_MUSHROOM = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.1F)
			.setTicking(true)
			.setTags(BlockTags.CAN_HANG_OFF,BlockTags.BROKEN_BY_FLUIDS)
			.build("lantern_mushroom", id("LANTERN_MUSHROOM"), b -> new BlockLogicLanternMushroom(b, Materials.DECORATION) {})
			.withLightEmission(1F);
		//OVERLAY SEASHELL
		OVERLAY_SEASHELL = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setTags(BlockTags.BROKEN_BY_FLUIDS,BlockTags.NOT_IN_CREATIVE_MENU)
			.build("overlay_seashell", id("OVERLAY_SEASHELL"), b -> new BlockLogicOverlaySeashells(b, Materials.DECORATION) {});
		//BRICKS MUD
		BRICK_MUD = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.6F)
			.setBlockSound(BlockSounds.GRAVEL)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_CACTI, BlockTags.GROWS_TREES, BlockTags.GROWS_SUGAR_CANE)
			.build("brick_mud", id("BRICK_MUD"), b -> new BlockLogicMudBlock(b, Materials.DIRT, false, BRICK_MUD_BAKED) {})
			.withOverrideColor(MaterialColor.dirt);
		STAIRS_BRICK_MUD = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.6F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.GRAVEL)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("stairs_brick_mud", id("STAIRS_BRICK_MUD"), b -> new BlockLogicMudStairs(b, BRICK_MUD, false, STAIRS_BRICK_MUD_BAKED) {});
		SLAB_BRICK_MUD = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(0.6F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.GRAVEL)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("slab_brick_mud", id("SLAB_BRICK_MUD"), b -> new BlockLogicMudSlab(b, BRICK_MUD, false, SLAB_BRICK_MUD_BAKED) {});
		//BRICKS MUD BACKED
		BRICK_MUD_BAKED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_CACTI, BlockTags.GROWS_TREES, BlockTags.GROWS_SUGAR_CANE)
			.build("brick_mud_baked", id("BRICK_MUD_BAKED"), b -> new BlockLogicMudBlock(b, Materials.STEEL, true, BRICK_MUD) {})
			.withOverrideColor(MaterialColor.dirt);
		STAIRS_BRICK_MUD_BAKED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("stairs_brick_mud_baked", id("STAIRS_BRICK_MUD_BAKED"), b -> new BlockLogicMudStairs(b, BRICK_MUD_BAKED, true, STAIRS_BRICK_MUD) {});
		SLAB_BRICK_MUD_BAKED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1.5F)
			.setUseInternalLight()
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("slab_brick_mud_baked", id("SLAB_BRICK_MUD_BAKED"), b -> new BlockLogicMudSlab(b, BRICK_MUD_BAKED, true, SLAB_BRICK_MUD) {});
		BRICK_CLAY_MOSSY = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(1F)
			.setResistance(10.0F)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("brick_clay_mossy", id("BRICK_CLAY_MOSSY"), b -> new BlockLogic(b, Materials.STONE) {});
		//CORRUGATED IRON BLOCK
		BLOCK_IRON_CORRUGATED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(2F).setResistance(25.0F)
			.setTags(BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE)
			.build("block_iron_corrugated", id("BLOCK_IRON_CORRUGATED"), b -> new BlockLogicCorrugatedIron(b, Materials.METAL) {})
			.setBlockItem(b -> new ItemBlock<>(BLOCK_IRON_CORRUGATED));
		BLOCK_IRON_CORRUGATED_PAINTED = new BlockBuilder(MOD_ID)
			.setCreativeInventoryPlacement(misc())
			.setHardness(2F).setResistance(25.0F)
			.setTags(BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.setBlockItem(b -> new ItemBlockPainted<>(b, true))
			.build("block_iron_corrugated_painted", id("BLOCK_IRON_CORRUGATED_PAINTED"), b -> new BlockLogicCorrugatedIronPainted(b, Materials.METAL) {});
		CreativeInventoryRegistry.INSTANCE.register(BLOCK_IRON_CORRUGATED_PAINTED, misc().setCustomSupplier(() -> {
			List<ItemStack> list = new ArrayList<>();
			for (int i = 0; i < 16; ++i) {
				list.add(new ItemStack(BLOCK_IRON_CORRUGATED_PAINTED, 1, i << 4));
			}
			return list;
		}));
	}

	private static CreativeInventoryPlacement misc() {
		return new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS);
	}
}
