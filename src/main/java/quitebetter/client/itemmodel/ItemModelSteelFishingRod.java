package quitebetter.client.itemmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelFishingRod;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.Item;

import static quitebetter.core.ModCore.MOD_ID;

@Environment(EnvType.CLIENT)
public class ItemModelSteelFishingRod extends ItemModelFishingRod {
	public ItemModelSteelFishingRod(Item item) {
		super(item, false);
		this.rodCast = TextureRegistry.getTexture(MOD_ID + ":item/tool_steel_fishingrod_cast");
		this.setIcon(MOD_ID + ":item/tool_steel_fishingrod");
	}
}
