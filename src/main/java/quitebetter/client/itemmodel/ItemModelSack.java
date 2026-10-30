package quitebetter.client.itemmodel;

import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.font.FontRenderer;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ItemModelSack extends ItemModelStandard {
	public ItemModelSack(Item item, boolean defaultTextureLookup) {
		super(item, defaultTextureLookup);
	}

	@Override
	public void renderItemOverlayIntoGUI(@NotNull TessellatorGeneral tessellator, @NotNull FontRenderer fontRenderer, @NotNull TextureManager textureManager, @NotNull ItemStack itemstack, int x, int y, @Nullable String override, float alpha) {
		GLRenderer.pushFrame();
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		int a = (int)(alpha * 255.0F);
		if (itemstack.stackSize != 1 || override != null) {
			String s = override != null ? override : "" + itemstack.stackSize;
			GLRenderer.disableState(State.DEPTH_TEST);
			fontRenderer.render((CharSequence)s, x + 19 - 2 - fontRenderer.stringWidth(s), y + 6 + 3).setShadow().setColor(16777215 | a << 24).call();
			GLRenderer.enableState(State.DEPTH_TEST);
		}

		int durability = 64-itemstack.getItemDamageForDisplay();
		int barWidth = (int)Math.round((double)13.0F - (double)durability * (double)13.0F / (double)itemstack.getMaxDamage());
		int progress = (int)Math.round((double)255.0F - (double)durability * (double)255.0F / (double)itemstack.getMaxDamage());
		GLRenderer.pushFrame();
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setShader(Shaders.COLOR);
		int colorFG = Color.HSBtoRGB((float)progress / 255.0F / 3.0F, 1.0F, 1.0F);
		int colorBG = (255 - progress) / 4 << 16 | 16128;
		tessellator.startDrawingQuads();
		this.renderColoredQuad(tessellator, x + 2, y + 13, 13, 2, 0, a);
		this.renderColoredQuad(tessellator, x + 2, y + 13, 12, 1, colorBG, a);
		this.renderColoredQuad(tessellator, x + 2, y + 13, barWidth, 1, colorFG, a);
		tessellator.draw();
		GLRenderer.popFrame();

		GLRenderer.popFrame();
	}
}
