package quitebetter.client.tileentityrender;

import quitebetter.core.tileentity.TileEntityPedestal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.pos.TilePos;

@Environment(EnvType.CLIENT)
public class TileEntityRendererPedestal extends TileEntityRenderer<TileEntityPedestal> {
	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntityPedestal tileEntity, double x, double y, double z, float renderPartialTicks) {
		if (tileEntity.displayItem != null) {
			GLRenderer.pushFrame();
			float bobbingOffset = MathHelper.sin(((float)tileEntity.ticksRan + renderPartialTicks) / 10.0F) * 0.1F + 0.1F;
			float yaw = (float)Math.toDegrees((double)(((float)tileEntity.ticksRan + renderPartialTicks) / 20.0F));
			GLRenderer.modelM4f().translate((float)x + 0.5F, (float)y + bobbingOffset + 0.25F, (float)z + 0.5F);
			ItemModelDispatcher.getInstance()
				.getDispatch(tileEntity.displayItem)
				.renderItemEntity(
					tessellator,
					tileEntity.displayItem,
					GameSettings.ITEMS_3D.value,
					1,
					tileEntity.ticksRan,
					yaw,
					Minecraft.getMinecraft().currentWorld.getLightIndex(new TilePos(tileEntity.tilePos.x, tileEntity.tilePos.y, tileEntity.tilePos.z), 0),
					renderPartialTicks
				);
			GLRenderer.popFrame();
		}
	}
}
