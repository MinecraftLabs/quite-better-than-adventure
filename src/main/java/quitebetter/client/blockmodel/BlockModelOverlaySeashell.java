package quitebetter.client.blockmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import static quitebetter.core.ModCore.MOD_ID;

@Environment(EnvType.CLIENT)
public class BlockModelOverlaySeashell<T extends BlockLogic> extends BlockModelStandard<T> {
	protected IconCoordinate[] seashells = new IconCoordinate[]{
		TextureRegistry.getTexture(MOD_ID+":block/seashells/0"),
		TextureRegistry.getTexture(MOD_ID+":block/seashells/1"),
		TextureRegistry.getTexture(MOD_ID+":block/seashells/2")};

	public BlockModelOverlaySeashell(Block<T> block) {
		super(block);
	}

	@Override
	public boolean render(TessellatorGeneral tessellator, WorldSource worldSource, TilePosc tilePos) {
		byte lightIndex = LightIndexHelper.max(this.block.getLightIndex(worldSource, tilePos), this.block.getLightIndex(worldSource, tilePos.down(new TilePos())));
		tessellator.setLightmapCoord1i(lightIndex);
		tessellator.setColorOpaque3f(1.0F, 1.0F, 1.0F);
		renderBlocks.renderTopFace(tessellator, this.block.getBoundsFromState(worldSource, tilePos), tilePos.x(), tilePos.y(), tilePos.z(), this.getBlockTextureFromSideAndMetadata(Side.TOP, worldSource.getBlockData(tilePos)));
		return true;
	}

	@Override
	public boolean shouldItemRender3d() {
		return false;
	}

	@Override
	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int data) {
		return this.seashells[MathHelper.clamp(data, 0, 2)];
	}
}
