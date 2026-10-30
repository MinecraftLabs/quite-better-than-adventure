package quitebetter.client.blockmodel;

import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelAxisAligned;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicAxisAligned;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBdc;

public class BlockModelSupports<T extends BlockLogic> extends BlockModelAxisAligned<T> {

	public BlockModelSupports(Block<T> block) {
		super(block);
	}

	public boolean shouldRenderSide(WorldSource worldSource, TilePosc tilePos, Side side, int meta) {
		Block<?> frontblock = worldSource.getBlockType(tilePos.add(side.opposite(), new TilePos()));
		return (!getRotatedSide(side, meta).isHorizontal() || (!frontblock.isCubeShaped()) );
	}

	@Override
	public void renderStandalone(TessellatorGeneral tessellator, int metadata, byte lightIndex) {
		tessellator.offsetTranslation(-0.5, -0.5, -0.5);
		tessellator.startDrawingQuads();
		tessellator.setLightmapCoord1i(lightIndex);
		tessellator.setColor1i(this.getStandaloneTintColor(metadata));
		AABBdc bounds = this.getBlockBoundsForItemRender();

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderBlocks.renderTopFace(tessellator, bounds, 0.0, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.TOP, metadata));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderBlocks.renderNorthFace(tessellator, bounds, 0.0, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.NORTH, metadata));
		renderBlocks.renderNorthFace(tessellator, bounds, 0.0, 0.0, (double)0.99F, this.getBlockTextureFromSideAndMetadata(Side.NORTH, metadata));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderBlocks.renderSouthFace(tessellator, bounds, 0.0, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.SOUTH, metadata));
		renderBlocks.renderSouthFace(tessellator, bounds, 0.0, 0.0, (double)-0.99F, this.getBlockTextureFromSideAndMetadata(Side.SOUTH, metadata));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderBlocks.renderWestFace(tessellator, bounds, 0.0, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.WEST, metadata));
		renderBlocks.renderWestFace(tessellator, bounds, (double)0.99F, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.WEST, metadata));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderBlocks.renderEastFace(tessellator, bounds, 0.0, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.EAST, metadata));
		renderBlocks.renderEastFace(tessellator, bounds, (double)-0.99F, 0.0, 0.0, this.getBlockTextureFromSideAndMetadata(Side.EAST, metadata));
		tessellator.draw();
		tessellator.offsetTranslation(0.5, 0.5, 0.5);
	}

	@Override
	public boolean render(TessellatorGeneral tessellator, WorldSource worldSource, TilePosc tilePos) {
		int meta = worldSource.getBlockData(tilePos);
		Axis axis = BlockLogicAxisAligned.metaToAxis(meta);
		switch (axis) {
			case Y:
				renderBlocks.uvRotateEast = 0;
				renderBlocks.uvRotateWest = 0;
				renderBlocks.uvRotateSouth = 0;
				renderBlocks.uvRotateNorth = 0;
				break;
			case Z:
				renderBlocks.uvRotateSouth = 1;
				renderBlocks.uvRotateNorth = 1;
				break;
			case X:
				renderBlocks.uvRotateEast = 1;
				renderBlocks.uvRotateWest = 1;
				renderBlocks.uvRotateTop = 1;
				renderBlocks.uvRotateBottom = 1;
		}
		AABBdc bounds = this.block.getBounds();
		int color = BlockColorDispatcher.getInstance().getDispatch(this.block).getWorldColor(worldSource, tilePos, 0);
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		renderBlocks.enableAO = true;
		renderBlocks.cache.setupCache(this.block, worldSource, tilePos);
		//OUTSIDE
		for(Side side : Side.sides) {
			Block<?> frontblock = worldSource.getBlockType(tilePos.add(side, new TilePos()));
			if (!getRotatedSide(side, meta).isHorizontal() || (!frontblock.isCubeShaped()) ) {
				renderBlocks.renderSide(tessellator, worldSource, this, bounds, tilePos, r, g, b, side, meta);
			}
		}
		//INSIDE
		double in=0.995;
		Side side;
		side = Side.SOUTH;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderSouthFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		side = Side.WEST;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderWestFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		side = Side.NORTH;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderNorthFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		side = Side.EAST;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderEastFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		side = Side.TOP;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderTopFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		side = Side.BOTTOM;
		if (shouldRenderSide(worldSource, tilePos, side, meta)) { renderBlocks.renderBottomFace(tessellator, bounds, tilePos.x()-side.offsetX()*in,tilePos.y()-side.offsetY()*in,tilePos.z()-side.offsetZ()*in, getBlockTextureFromSideAndMetadata(side,meta)); }
		renderBlocks.enableAO = false;
		renderBlocks.resetRenderBlocks();
		return true;
	}

}
