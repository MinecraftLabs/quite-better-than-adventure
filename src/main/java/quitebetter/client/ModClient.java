package quitebetter.client;

import quitebetter.client.blockmodel.BlockModels;
import quitebetter.client.itemmodel.ItemModels;
import quitebetter.client.tileentityrender.TileEntityRendererPedestal;
import quitebetter.core.ModCore;
import quitebetter.core.achivement.ModAchievements;
import quitebetter.core.tileentity.TileEntityPedestal;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.achievements.data.AchievementPages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Key key = Key.of(ModCore.MOD_ID);
		ClientEvents.BLOCK_MODEL_RELOAD.listen(key, BlockModels::Setup);
		ClientEvents.ITEM_MODEL_RELOAD.listen(key, ItemModels::Setup);
		ClientEvents.TILE_ENTITY_RENDERER_RELOAD.listen(key, d -> d.assignRenderer(TileEntityPedestal.class, new TileEntityRendererPedestal()));
		ClientEvents.AFTER_CLIENT_START.listen(key, ModClient::initAchievements);
	}

	private static void initAchievements() {
		if (!FabricLoader.getInstance().isModLoaded("stardew")) {
			AchievementPages.init();
			AchievementPages.netherPage.addAchievement(ModAchievements.LAVA_FISHING, -1, 0);
		}
	}
}
