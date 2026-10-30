package quitebetter.client;

import quitebetter.client.blockmodel.BlockModels;
import quitebetter.client.itemmodel.ItemModels;
import quitebetter.client.tileentityrender.TileEntityRendererPedestal;
import quitebetter.core.tileentity.TileEntityPedestal;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;
import quitebetter.core.ModCore;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Key key = Key.of(ModCore.MOD_ID);
		ClientEvents.BLOCK_MODEL_RELOAD.listen(key, BlockModels::Setup);
		ClientEvents.ITEM_MODEL_RELOAD.listen(key, ItemModels::Setup);
		ClientEvents.TILE_ENTITY_RENDERER_RELOAD.listen(key, d -> d.assignRenderer(TileEntityPedestal.class, new TileEntityRendererPedestal()));
	}
}
