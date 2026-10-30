package quitebetter.client.blockmodel;

import quitebetter.core.block.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.*;
import net.minecraft.client.render.block.model.generic.BlockModelGenericTorch;
import net.minecraft.core.util.helper.Side;

import static quitebetter.core.ModCore.MOD_ID;

@Environment(EnvType.CLIENT)
public class BlockModels {
	private static String PathTo(String texture) {
		return MOD_ID+":block/"+texture;
	}
	private static String PathTo(String folder, String texture) {
		return MOD_ID+":block/"+folder+"/"+texture;
	}
	private static String PathTo(String namespace, String folder, String texture) {
		return namespace+":block/"+folder+"/"+texture;
	}

	public static void Setup(BlockModelDispatcher d) {
		//BONEBLOCK
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BLOCK_BONE).setAllTextures(PathTo("block_bone")));
		//OBSIDIAN POLISHED
		d.addDispatch(new BlockModelStandard<>(ModBlocks.OBSIDIAN_POLISHED).setAllTextures(PathTo("obsidian_polished")));
		//OLIVINEBRICKS
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_OLIVINE).setAllTextures(PathTo("brick_olivine")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_BRICK_OLIVINE).setAllTextures(PathTo("brick_olivine")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_BRICK_OLIVINE).setAllTextures(PathTo("brick_olivine")));
		//SEASHELLTILES
		d.addDispatch(new BlockModelStandard<>(ModBlocks.TILE_SEASHELL).setAllTextures(PathTo("tile_seashell")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_TILE_SEASHELL).setAllTextures(PathTo("tile_seashell")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_TILE_SEASHELL).setAllTextures(PathTo("tile_seashell")));
		//HAZARD BLOCK
		d.addDispatch(new BlockModelStandard<>(ModBlocks.HAZARD).setAllTextures(PathTo("hazard_block","default")));
		d.addDispatch(new BlockModelHazardPainted<>(ModBlocks.HAZARD_PAINTED));
		//FAN
		d.addDispatch(new BlockModelFan<>(ModBlocks.FAN,false,false));
		d.addDispatch(new BlockModelFan<>(ModBlocks.ACTIVE_FAN,true,false));
		d.addDispatch(new BlockModelFan<>(ModBlocks.IN_FAN,false,true));
		d.addDispatch(new BlockModelFan<>(ModBlocks.ACTIVE_IN_FAN,true,true));
		//SUPPORTS
		d.addDispatch(new BlockModelSupports<>(ModBlocks.SUPPORT_STEEL).setTex(PathTo("support","steel_top"), Side.TOP, Side.BOTTOM).setTex(PathTo("support","steel"), Side.NORTH, Side.EAST, Side.SOUTH, Side.WEST));
		d.addDispatch(new BlockModelSupports<>(ModBlocks.SUPPORT_IRON).setTex(PathTo("support","iron_top"), Side.TOP, Side.BOTTOM).setTex(PathTo("support","iron"), Side.NORTH, Side.EAST, Side.SOUTH, Side.WEST));
		d.addDispatch(new BlockModelSupports<>(ModBlocks.SUPPORT_WOOD).setTex(PathTo("support","wood_top"), Side.TOP, Side.BOTTOM).setTex(PathTo("support","wood"), Side.NORTH, Side.EAST, Side.SOUTH, Side.WEST));
		//GLOWING MUSHROOM
		d.addDispatch(new BlockModelCrossedSquares<>(ModBlocks.MUSHROOM_GLOWING).setAllTextures(PathTo("mushroom_glowing")));
		//PEDESTAL
		d.addDispatch(new BlockModelPedestal<>(ModBlocks.PEDESTAL));
		//SNOW BRICKS
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_SNOW).setAllTextures(PathTo("brick_snow")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_BRICK_SNOW).setAllTextures(PathTo("brick_snow")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_BRICK_SNOW).setAllTextures(PathTo("brick_snow")));
		//ARROW TORCH
		d.addDispatch(new BlockModelGenericTorch<>(ModBlocks.ARROW_TORCH, MOD_ID + ":block/arrow_torch").render3D(false));
		//CRATE
		d.addDispatch(new BlockModelStandard<>(ModBlocks.CRATE).setAllTextures(PathTo("crate","default")));
		d.addDispatch(new BlockModelCratePainted<>(ModBlocks.CRATE_PAINTED));
		//GEODE
		d.addDispatch(new BlockModelStandard<>(ModBlocks.GEODE).setAllTextures(PathTo("geode")));
		//BRICK OBSIDIAN
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_OBSIDIAN).setAllTextures(PathTo("brick_obsidian")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_BRICK_OBSIDIAN).setAllTextures(PathTo("brick_obsidian")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_BRICK_OBSIDIAN).setAllTextures(PathTo("brick_obsidian")));
		//LANTERN
		d.addDispatch(new BlockModelLantern<>(ModBlocks.LANTERN_COAL).setAllTextures(PathTo("lantern_coal")));
		d.addDispatch(new BlockModelLantern<>(ModBlocks.LANTERN_REDSTONE).setAllTextures(PathTo("lantern_redstone")));
		d.addDispatch(new BlockModelLantern<>(ModBlocks.LANTERN_MUSHROOM).setAllTextures(PathTo("lantern_mushroom")));
		//SEASHELL
		d.addDispatch(new BlockModelOverlaySeashell<>(ModBlocks.OVERLAY_SEASHELL));
		//BRICKS MUD
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_MUD).setAllTextures(PathTo("brick_mud")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_BRICK_MUD).setAllTextures(PathTo("brick_mud")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_BRICK_MUD).setAllTextures(PathTo("brick_mud")));
		//BRICKS MUD BAKED
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_MUD_BAKED).setAllTextures(PathTo("brick_mud_baked")));
		d.addDispatch(new BlockModelStairs<>(ModBlocks.STAIRS_BRICK_MUD_BAKED).setAllTextures(PathTo("brick_mud_baked")));
		d.addDispatch(new BlockModelSlab<>(ModBlocks.SLAB_BRICK_MUD_BAKED).setAllTextures(PathTo("brick_mud_baked")));
		//BRICKS CLAY MOSSY
		d.addDispatch(new BlockModelStandard<>(ModBlocks.BRICK_CLAY_MOSSY).setAllTextures(PathTo("brick_clay_mossy")));
		//CORRUGATED IRON BLOCK
		d.addDispatch(new BlockModelAxisAligned<>(ModBlocks.BLOCK_IRON_CORRUGATED).setAllTextures(PathTo("corrugated_iron","default")));
		d.addDispatch(new BlockModelAxisAlignedPainted<>(ModBlocks.BLOCK_IRON_CORRUGATED_PAINTED));
	}
}
