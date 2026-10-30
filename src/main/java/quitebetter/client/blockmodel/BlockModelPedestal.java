package quitebetter.client.blockmodel;

import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

import static quitebetter.core.ModCore.MOD_ID;

public class BlockModelPedestal<T extends BlockLogic> extends BlockModelStandard<T> {
	private final @NotNull IconCoordinate topTexture;
	private final @NotNull IconCoordinate sideTexture;
	private final @NotNull IconCoordinate bottomTexture;

	public BlockModelPedestal(Block<T> block) {
		super(block);
		this.topTexture = TextureRegistry.getTexture(MOD_ID+":block/pedestal/top");
		this.sideTexture = TextureRegistry.getTexture(MOD_ID+":block/pedestal/side");
		this.bottomTexture = TextureRegistry.getTexture(MOD_ID+":block/pedestal/bottom");
	}

	@Override
	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int data) {
		return side == Side.TOP ? this.topTexture : this.bottomTexture;
	}

	@Override
	public boolean shouldItemRender3d() { return false; }

	@Override
	public boolean render(TessellatorGeneral tessellator, WorldSource worldSource, TilePosc tilePos) {
		double x = tilePos.x();
		double y = tilePos.y();
		double z = tilePos.z();
		renderBlocks.renderStandardBlock(tessellator, worldSource, this, this.block.getBounds(), tilePos);

		double minU = this.sideTexture.getIconUMin();
		double maxU = this.sideTexture.getIconUMax();
		double minV = this.sideTexture.getIconVMin();
		double maxV = this.sideTexture.getIconVMax();
		double w = 0.1;
		//WEST
		tessellator.setLightmapCoord1i(this.block.getLightIndex(worldSource, tilePos.add(-1, 0, 0, new TilePos())));
		tessellator.setColorOpaque3f(0.6F, 0.6F, 0.6F);

		tessellator.addVertexWithUV(x, y+0.1, z, minU, minV);
		tessellator.addVertexWithUV(x-w, y-1+0.1, z, minU, maxV);
		tessellator.addVertexWithUV(x-w, y-1+0.1, z+1, maxU, maxV);
		tessellator.addVertexWithUV(x, y+0.1, z+1, maxU, minV);

		//EAST
		tessellator.setLightmapCoord1i(this.block.getLightIndex(worldSource, tilePos.add(1, 0, 0, new TilePos())));
		tessellator.setColorOpaque3f(0.6F, 0.6F, 0.6F);

		tessellator.addVertexWithUV(x+1, y+0.1, z+1, minU, minV);
		tessellator.addVertexWithUV(x+1+w, y-1+0.1, z+1, minU, maxV);
		tessellator.addVertexWithUV(x+1+w, y-1+0.1, z, maxU, maxV);
		tessellator.addVertexWithUV(x+1, y+0.1, z, maxU, minV);

		//SOUTH
		tessellator.setLightmapCoord1i(this.block.getLightIndex(worldSource, tilePos.add(0, 0, 1, new TilePos())));
		tessellator.setColorOpaque3f(0.8F, 0.8F, 0.8F);
		tessellator.addVertexWithUV(x, y+0.1, z+1, minU, minV);
		tessellator.addVertexWithUV(x, y -1+w, z+1 +w, minU, maxV);
		tessellator.addVertexWithUV(x+1, y -1+w, z+1 +w, maxU, maxV);
		tessellator.addVertexWithUV(x+1, y+0.1, z+1, maxU, minV);

		//NORTH
		tessellator.setLightmapCoord1i(this.block.getLightIndex(worldSource, tilePos.add(0, 0, -1, new TilePos())));
		tessellator.setColorOpaque3f(0.8F, 0.8F, 0.8F);
		tessellator.addVertexWithUV(x+1, y+0.1, z, minU, minV);
		tessellator.addVertexWithUV(x+1, y -1+0.1, z -w, minU, maxV);
		tessellator.addVertexWithUV(x, y -1+0.1, z -w, maxU, maxV);
		tessellator.addVertexWithUV(x, y+0.1, z, maxU, minV);
		return true;
	}
}
